package com.maple.cloudweather.domain;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by San on 2016/11/2.
 */

public interface ApiService {

    @GET("weather")
    Observable<WeatherAPI> getWeatherAPI(@Query("city") String city, @Query("key") String key);

    @GET("txapi/lishi/")
    Observable<History> getHistory(@Query("key") String key);

    @GET("social/")
    Observable <NewsGson> getNewsData(@Query("key")String key,@Query("num") String num,@Query("page") int page);

}
