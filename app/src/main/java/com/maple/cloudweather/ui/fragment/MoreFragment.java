package com.maple.cloudweather.ui.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maple.cloudweather.R;
import com.maple.cloudweather.db.CloudWeatherDB;
import com.maple.cloudweather.domain.City;
import com.maple.cloudweather.domain.MultiUpdate;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.domain.WeatherAPI;
import com.maple.cloudweather.retrofit.RetrofitSingleton;
import com.maple.cloudweather.ui.adapter.MoreAdapter;
import com.maple.cloudweather.uitl.RxBus;
import com.maple.cloudweather.uitl.RxUtil;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends BaseFragment {

    @BindView(R.id.recycler_view_more)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl_more)
    SwipeRefreshLayout mSrl;

    private View view;
    private static List<Weather> mWeatherList;
    private MoreAdapter mAdapter;
    private CloudWeatherDB db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = CloudWeatherDB.getInstance(getActivity());
        mWeatherList = new ArrayList<>();
        RxBus.getDefault().toObservable(MultiUpdate.class)
                .subscribe(new Subscriber<MultiUpdate>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(MultiUpdate multiUpdate) {
                        System.out.println("MoreFragment+++++++++++++++++++");
                        loadData();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_more, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        loadData();
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new MoreAdapter(mWeatherList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnLongClick(new MoreAdapter.OnLongClickListener() {
            @Override
            public void longClick(final String city) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("是否删除该城市?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.deleteMoreCity(city);
                                loadData();

                            }
                        })
                        .show();
            }
        });

        if (mSrl != null) {
            mSrl.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
        }
    }

    private void loadData() {
        mWeatherList.clear();
        Observable
                .defer(new Func0<Observable<City>>() {
                    @Override
                    public Observable<City> call() {
                        List<City> list = db.loadMoreCities();
                        for (int i = 0; i < list.size(); i++) {
                            City city = list.get(i);
                            System.out.println(city.getCityName()+"+++++++++++++++");
                        }
                        return Observable.from(list);
                    }
                })
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mSrl.setRefreshing(true);
                    }
                })
                .map(new Func1<City, String>() {
                    @Override
                    public String call(City city) {
                        return city.getCityName();
                    }
                })
                .distinct()
                .flatMap(new Func1<String, Observable<Weather>>() {
                    @Override
                    public Observable<Weather> call(String s) {
                        return RetrofitSingleton
                                .getInstance()
                                .getApiService()
                                .getWeatherAPI(s, "109ff555bd074f6b8c03594077ca4dd7")
                                .map(new Func1<WeatherAPI, Weather>() {
                                    @Override
                                    public Weather call(WeatherAPI weatherAPI) {
                                        return weatherAPI.mHeWeatherDataService30s.get(0);
                                    }
                                })
                                .compose(RxUtil.<Weather>rxSchedulerHelper());
                    }
                })
                .compose(this.<Weather>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .filter(new Func1<Weather, Boolean>() {
                    @Override
                    public Boolean call(Weather weather) {
                        return !"unknown city".equals(weather.status);
                    }
                })
                .take(3)
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mSrl.setRefreshing(false);
                    }
                })
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onCompleted() {
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        mWeatherList.add(weather);
                    }
                });
    }

}
