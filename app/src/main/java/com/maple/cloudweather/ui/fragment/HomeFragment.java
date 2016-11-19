package com.maple.cloudweather.ui.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.maple.cloudweather.MainActivity;
import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.ChangeCityEvent;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.retrofit.RetrofitSingleton;
import com.maple.cloudweather.ui.adapter.WeatherAdapter;
import com.maple.cloudweather.uitl.PrefUtil;
import com.maple.cloudweather.uitl.RxBus;
import com.maple.cloudweather.uitl.UIUtil;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements AMapLocationListener {
    private static Weather mWeather = new Weather();

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.srl)
    SwipeRefreshLayout mSrl;

    private WeatherAdapter mAdapter;
    private String mName = "北京";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    public AMapLocationClientOption mLocationOption = null;

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        /*Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            mName = bundle.getString("name","上海");
        }
        System.out.println("Pass"+ mName);*/
        RxBus.getDefault().toObservable(ChangeCityEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ChangeCityEvent>() {
                    @Override
                    public void call(ChangeCityEvent changeCityEvent) {
                        if (mSrl != null) {
                            mSrl.setRefreshing(true);
                        }
                        getDataFromNet();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_home, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mSrl != null) {
            mSrl.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mSrl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mSrl.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getDataFromNet();
                        }
                    }, 1000);
                }
            });
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WeatherAdapter(mWeather);
        mRecyclerView.setAdapter(mAdapter);

//        getDataFromNet();
        RxPermissions
                .getInstance(getActivity())
                .request(Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            location();
                        } else {
                            getDataFromNet();
                        }
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_locate_city) {
            Toast.makeText(getActivity(), "开始定位", Toast.LENGTH_SHORT).show();
            location();
            return true;
        } else if (id == R.id.action_share_weather) {
            Toast.makeText(getActivity(), "分享天气", Toast.LENGTH_SHORT).show();
            Bitmap bitmap = takeScreenShot(getActivity());
            File cacheDir = UIUtil.getContext().getCacheDir();
            File cacheFile = new File(cacheDir,"test.jpg");
//            String path = cacheFile.getAbsolutePath();
            saveScreen(bitmap, cacheFile);
            showShare();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient = null;//停止定位后，本地定位服务并不会被销毁
        mLocationClient = null;//销毁定位客户端，同时销毁本地定位服务。
    }

    private void showShare() {
//        ShareSDK.initSDK(this);
        ShareSDK.initSDK(getActivity());
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        String weatherInfo = String.format("#枫叶天气# %s,%s,%s-%s℃,%s",
                mWeather.basic.city,
                mWeather.now.cond.txt,
                mWeather.dailyForecast.get(0).tmp.min,
                mWeather.dailyForecast.get(0).tmp.max,
                mWeather.now.wind.sc);
        //oks.setText("我是分享文本");
        oks.setText(weatherInfo);
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
//        oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImagePath("/data/user/0/com.maple.cloudweather/cache/test.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
//        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
//        oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
//        oks.show(this);
        oks.show(getActivity());
    }

    private Bitmap takeScreenShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();

        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();

        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);

        // 销毁缓存信息
        view.destroyDrawingCache();

        return bmp;
    }

    private void saveScreen(Bitmap bitmap, File filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            if (null != fos) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    private void getDataFromNet() {
        mName = PrefUtil.getString("city_name", "北京");
//        mName = UIUtil.replaceCity(mName);
        Observable<Weather> observable =
                RetrofitSingleton.getInstance().fetchWeather(mName).compose(this.<Weather>bindToLifecycle());

        observable
                .doOnRequest(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mSrl.setRefreshing(true);
                    }
                })
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                })
                .doOnNext(new Action1<Weather>() {
                    @Override
                    public void call(Weather weather) {

                    }
                })
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                        mSrl.setRefreshing(false);
                    }
                })
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(getActivity(), "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺,", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "网络不好(˵¯͒⌢͗¯͒˵)", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mWeather.status = weather.status;
                        mWeather.aqi = weather.aqi;
                        mWeather.aqi.city = weather.aqi.city;

                        mWeather.basic = weather.basic;
                        mWeather.basic.update = weather.basic.update;

                        mWeather.suggestion = weather.suggestion;
                        mWeather.now = weather.now;
                        mWeather.now.cond = weather.now.cond;
                        mWeather.now.wind = weather.now.wind;

                        mWeather.dailyForecast = weather.dailyForecast;
                        mWeather.hourlyForecast = weather.hourlyForecast;

                        //给标题栏设置地区显示
                        setTitle(weather.basic.city);
                        mAdapter.notifyDataSetChanged();

                        notification(weather);
                    }
                });

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.heweather.com/x3/")
                .client(mOkHttpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();*/

        /*Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.heweather.com/x3/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器
                .build();

        ApiService apiManager = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式
        apiManager.getWeatherAPI(mName, "109ff555bd074f6b8c03594077ca4dd7")
                .subscribeOn(Schedulers.io())
                .map(new Func1<WeatherAPI, Weather>() {

                    @Override
                    public Weather call(WeatherAPI weatherAPI) {
                        Weather weather = weatherAPI.mHeWeatherDataService30s.get(0);
                        return weather;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Weather>() {
                    @Override
                    public void onCompleted() {
                        mSrl.setRefreshing(false);
                        Toast.makeText(getActivity(), "加载完毕，✺◟(∗❛ัᴗ❛ั∗)◞✺,", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "网络不好(˵¯͒⌢͗¯͒˵)", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Weather weather) {
                        mWeather.status = weather.status;
                        mWeather.aqi = weather.aqi;
                        mWeather.aqi.city = weather.aqi.city;

                        mWeather.basic = weather.basic;
                        mWeather.basic.update = weather.basic.update;

                        mWeather.suggestion = weather.suggestion;
                        mWeather.now = weather.now;
                        mWeather.now.cond = weather.now.cond;
                        mWeather.now.wind = weather.now.wind;

                        mWeather.dailyForecast = weather.dailyForecast;
                        mWeather.hourlyForecast = weather.hourlyForecast;
                        mAdapter.notifyDataSetChanged();
                    }
                });*/
    }

    private void location() {
        mSrl.setRefreshing(true);
        //初始化定位
        mLocationClient = new AMapLocationClient(UIUtil.getContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔 单位毫秒
        /*int tempTime = SharedPreferenceUtil.getInstance().getAutoUpdate();
        if (tempTime == 0) {
            tempTime = 100;
        }*/
        mLocationOption.setInterval(100*1000 * 60 * 60);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                String info = aMapLocation.getAddress();
                String city = aMapLocation.getCity();
                System.out.println("国家"+city);
//                Toast.makeText(getActivity(),info, Toast.LENGTH_SHORT).show();
                Snackbar snackbar = Snackbar.make(mSrl,info,Snackbar.LENGTH_LONG);
                setSnackbarColor(snackbar, Color.BLUE, Color.CYAN);
                snackbar.show();

                PrefUtil.putString("city_name", UIUtil.replaceCity(city));
            }else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
            getDataFromNet();
        }
    }

    public void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        View view = snackbar.getView();//获取Snackbar的view
        if(view != null){

            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));//修改view的背景色
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSnackBar));//获取Snackbar的message控件，修改字体颜色
        }
    }

    private void notification(Weather weather) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(getActivity());
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                .setSmallIcon(R.mipmap.dialog_bg_cloudy)
                .build();
        notification.flags = PrefUtil.getInstance().getNotificationModel();
        notification.defaults = Notification.DEFAULT_ALL; //设置默认声音,闪光灯
        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
