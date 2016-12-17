package com.maple.cloudweather.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maple.cloudweather.R;
import com.maple.cloudweather.db.CloudWeatherDB;
import com.maple.cloudweather.domain.ChangeCityEvent;
import com.maple.cloudweather.domain.City;
import com.maple.cloudweather.domain.County;
import com.maple.cloudweather.domain.MultiUpdate;
import com.maple.cloudweather.domain.Province;
import com.maple.cloudweather.global.CloudWeatherApplication;
import com.maple.cloudweather.ui.adapter.ChooseAdapter;
import com.maple.cloudweather.uitl.CharsetStringRequest;
import com.maple.cloudweather.uitl.PrefUtil;
import com.maple.cloudweather.uitl.RxBus;
import com.maple.cloudweather.uitl.Utility;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public class ChooseActivity extends RxAppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view_choose)
    RecyclerView mRecyclerView;

    private List<String> mList;
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;

    private ChooseAdapter mAdapter;


    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel = LEVEL_PROVINCE;

    public CloudWeatherDB cloudWeatherDB;
    private boolean isChecked;
    private CloudWeatherDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ButterKnife.bind(this);

        mToolbar.setTitle("枫叶天气");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        initView();
        db = CloudWeatherDB.getInstance(this);

        Observable
                .defer(new Func0<Observable<String>>() {
                    @Override
                    public Observable<String> call() {
                        initData();
                        return Observable.just("Hello World");
                    }
                })
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.bindToLifecycle())
                .subscribe(new Subscriber<Object>() {

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                        initRecyclerView();
                    }
                });

        Intent intent = getIntent();
        isChecked = intent.getBooleanExtra("MULTI_CHECK", false);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ChooseAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinceList.get(position);
//                    mRecyclerView.scrollToPosition(0);
                    queryCities();
                    mAdapter.notifyDataSetChanged();
                } else if (currentLevel == LEVEL_CITY) {
                    /*selectedCity = mCityList.get(position);
                    queryCounty();
                    mAdapter.notifyDataSetChanged();*/
                    String name = mCityList.get(position).getCityName();
                    System.out.println(name);
                    if (isChecked) {
                        City city = new City();
                        city.setCityName(name);

                        db.saveMoreCity(city);
                        RxBus.getDefault().post(new MultiUpdate());
                    } else {
                        PrefUtil.putString("city_name", name);
                        RxBus.getDefault().post(new ChangeCityEvent());
                    }
                    finish();
                } else if (currentLevel == LEVEL_COUNTY) {
                    String name = mCountyList.get(position).getCountyName();
                    System.out.println(name);
                    /*Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    intent.putExtras(bundle);
                    startActivity(intent);*/
                    if (isChecked) {
                        City city = new City();
                        city.setCityName(name);

                        db.saveMoreCity(city);
                        RxBus.getDefault().post(new MultiUpdate());
                    } else {
                        PrefUtil.putString("city_name", name);
                        RxBus.getDefault().post(new ChangeCityEvent());
                        /*Intent intent = new Intent(ChooseActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        intent.putExtras(bundle);
                        startActivity(intent);*/
                    }
                    finish();
                }
            }
        });
    }

    /*private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_choose);
    }*/

    private void initData() {

        mList = new ArrayList<>();
        cloudWeatherDB = CloudWeatherDB.getInstance(this);
        /*String[] provinces = new String[]{"北京", "上海", "天津", "重庆", "黑龙江", "吉林",
                "辽宁", "内蒙古", "河北", "山西", "陕西", "山东", "新疆", "西藏", "青海",
                "甘肃", "宁夏", "河南", "江苏", "湖北", "浙江", "安徽", "福建", "江西",
                "湖南", "贵州", "四川", "广东", "云南", "广西", "海南", "香港", "澳门", "台湾"};*/

//        mList.addAll(Arrays.asList(provinces));
        mAdapter = new ChooseAdapter(mList);
        queryProvinces();
    }

    private void getDataFromServer(String code, final String type) {
        String url;
        if (!TextUtils.isEmpty(code)) {
            url = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            url = "http://www.weather.com.cn/data/list3/city.xml";
        }
        CharsetStringRequest stringRequest = new CharsetStringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        boolean result = false;
                        switch (type) {
                            case "province":
                                result = Utility.handleProvincesResponse(cloudWeatherDB, response);
//                                queryProvinces();
                                break;
                            case "city":
                                result = Utility.handleCitiesResponse(cloudWeatherDB, response, selectedProvince.getId());
//                                queryCities();
                                break;
                            case "county":
                                result = Utility.handleCountyResponse(cloudWeatherDB, response, selectedCity.getId());
//                                queryCounty();
                                break;
                        }
                        if (result) {
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "county":
                                    queryCounty();
                                    break;
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Something went wrong!");
                        error.printStackTrace();
                    }
                });
        CloudWeatherApplication.mQueue.add(stringRequest);

    }

    private void queryCounty() {
        mToolbar.setTitle("选择地区");
        mCountyList = cloudWeatherDB.loadCounties(selectedCity.getId());
        if (mCountyList.size() > 0) {
            mList.clear();
            for (County county : mCountyList) {
                mList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_COUNTY;
            mRecyclerView.scrollToPosition(0);
        } else {
            getDataFromServer(selectedCity.getCityCode(), "county");
        }
    }

    private void queryCities() {
        mToolbar.setTitle("选择城市");
        mCityList = cloudWeatherDB.loadCities(selectedProvince.getId());
        if (mCityList.size() > 0) {
            mList.clear();
            for (City city : mCityList) {
                mList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_CITY;
            mRecyclerView.scrollToPosition(0);
        } else {
            getDataFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryProvinces() {
        mToolbar.setTitle("选择省份");
        mProvinceList = cloudWeatherDB.loadProvinces();
        if (mProvinceList.size() > 0) {
            mList.clear();
            for (int i = 0; i < mProvinceList.size(); i++) {
                mList.add(mProvinceList.get(i).getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            currentLevel = LEVEL_PROVINCE;
        } else {
            getDataFromServer(null, "province");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
