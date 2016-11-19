package com.maple.cloudweather.ui.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.maple.cloudweather.MainActivity;
import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.retrofit.RetrofitSingleton;
import com.maple.cloudweather.uitl.PrefUtil;
import com.maple.cloudweather.uitl.UIUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class AutoUpdateService extends Service {
    private PrefUtil mPref;
    private CompositeSubscription mSubscription;
    private Subscription mNetSubscription;
    private boolean isUnSubscribe = true;

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = PrefUtil.getInstance();
        mSubscription = new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        synchronized (this) {
            unSubscribed();
            if (isUnSubscribe) {
                unSubscribed();
                if (mPref.getAutoUpdate() != 0) {
                    mNetSubscription = Observable.interval(mPref.getAutoUpdate(), TimeUnit.HOURS)
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    isUnSubscribe = false;
                                    fetchDataByNet();
                                }
                            });
                    mSubscription.add(mNetSubscription);
                }
            }
        }
        return START_REDELIVER_INTENT;
    }

    private void fetchDataByNet() {
        String city = mPref.getCityName();
        if (city != null) {
            city = UIUtil.replaceCity(city);
        }
        RetrofitSingleton.getInstance().fetchWeather(city)
                .subscribe(new Action1<Weather>() {
                    @Override
                    public void call(Weather weather) {
                        notification(weather);
                    }
                });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void unSubscribed() {
        isUnSubscribe = true;
        mSubscription.remove(mNetSubscription);
    }

    //通知
    private void notification(Weather weather) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder = new Notification.Builder(this);
        Notification notification = builder.setContentIntent(pendingIntent)
                .setContentTitle(weather.basic.city)
                .setContentText(String.format("%s 当前温度: %s℃ ", weather.now.cond.txt, weather.now.tmp))
                .setSmallIcon(R.mipmap.dialog_bg_cloudy)
                .build();
        notification.flags = PrefUtil.getInstance().getNotificationModel();
        notification.defaults = Notification.DEFAULT_ALL; //设置默认声音,闪光灯
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // tag和id都是可以拿来区分不同的通知的
        manager.notify(1, notification);
    }
}
