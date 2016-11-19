package com.maple.cloudweather.retrofit;

import com.maple.cloudweather.BuildConfig;
import com.maple.cloudweather.domain.ApiService;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.domain.WeatherAPI;
import com.maple.cloudweather.uitl.RxUtil;
import com.maple.cloudweather.uitl.UIUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by San on 2016/11/12.
 */

public class RetrofitSingleton {

    public static final String BASE_URL = "https://api.heweather.com/x3/";

    private static Retrofit retrofit = null;
    private static OkHttpClient okHttpClient = null;
    private static ApiService apiService = null;

    public ApiService getApiService() {
        return apiService;
    }

    //构造方法私有化
    private RetrofitSingleton() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(loggingInterceptor);
        }

        File cacheFile = new File(UIUtil.getContext().getCacheDir(), "/net_cache");
        Cache cache = new Cache(cacheFile, 1024*1024*50);
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (!UIUtil.isNetworkConnected(UIUtil.getContext())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                if (UIUtil.isNetworkConnected(UIUtil.getContext())) {
                    int maxAge = 0;
                    // 有网络时 设置缓存超时时间0个小时
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .build();
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    response.newBuilder()
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                            .build();
                }
                return response;
            }
        };

        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);

        okHttpClient = builder.build();

        //初始化Retrofit.baseUrl("https://api.heweather.com/x3/")
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        //初始化APIService
        apiService = retrofit.create(ApiService.class);
    }

    //在访问RetrofitSingleton时创建单例
    private static class SingletonHolder {
        private static final RetrofitSingleton INSTANCE = new RetrofitSingleton();
    }

    //获取单例
    public static RetrofitSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Observable<Weather> fetchWeather(String city) {
        return  apiService.getWeatherAPI(city,"109ff555bd074f6b8c03594077ca4dd7")
                .flatMap(new Func1<WeatherAPI, Observable<WeatherAPI>>() {
                    @Override
                    public Observable<WeatherAPI> call(WeatherAPI weatherAPI) {
                        String status = weatherAPI.mHeWeatherDataService30s.get(0).status;
                        if ("no more requests".equals(status)) {
                            return Observable.error(new RuntimeException("/(ㄒoㄒ)/~~,API免费次数已用完"));
                        } else if ("unknown city".equals(status)) {
                            return Observable.error(new RuntimeException("API没有该城市"));
                        }
                        return Observable.just(weatherAPI);
                    }
                })
                .map(new Func1<WeatherAPI, Weather>() {

                    @Override
                    public Weather call(WeatherAPI weatherAPI) {
                        return weatherAPI.mHeWeatherDataService30s.get(0);
                    }
                })
                .compose(RxUtil.<Weather>rxSchedulerHelper());
    }
}
