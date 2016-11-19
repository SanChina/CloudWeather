package com.maple.cloudweather.global;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by San on 2016/11/4.
 */

public class CloudWeatherApplication extends Application {

    private static Context sContext;
    public static RequestQueue mQueue;
    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        mQueue = Volley.newRequestQueue(sContext);
    }

    public static Context getContext() {
        return sContext;
    }
}
