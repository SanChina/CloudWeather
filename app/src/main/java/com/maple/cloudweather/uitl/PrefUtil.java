package com.maple.cloudweather.uitl;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by San on 2016/11/13.
 */

public class PrefUtil {

    public static final String CITY_NAME = "city_name";

    public static final String CLEAR_CACHE = "clear_cache";//清空缓存
    public static final String AUTO_UPDATE = "change_update_time"; //自动更新时长
    public static final String NOTIFICATION_MODEL = "notification_model";

    public static int ONE_HOUR = 1000 * 60 * 60;

    private SharedPreferences mPreferences;

    public static PrefUtil getInstance() {
        return PrefUtilHolder.INSTANCE;
    }

    private static class PrefUtilHolder {
        private static final PrefUtil INSTANCE = new PrefUtil();
    }

    private PrefUtil() {
        mPreferences = UIUtil.getContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UIUtil.getContext()).edit();
        editor.putString(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(UIUtil.getContext());
        return preferences.getString(key, defValue);
    }

    public void setCityName(String city) {
        mPreferences.edit().putString(CITY_NAME, city).apply();
    }

    public String getCityName() {
        return mPreferences.getString(CITY_NAME, "北京");
    }

    public void setAutoUpdate(int t) {
        mPreferences.edit().putInt(AUTO_UPDATE, t).apply();
    }

    public int getAutoUpdate() {
        return mPreferences.getInt(AUTO_UPDATE, 3);
    }

    //  通知栏模式 默认为常驻
    public void setNotificationModel(int t) {
        mPreferences.edit().putInt(NOTIFICATION_MODEL, t).apply();
    }

    public int getNotificationModel() {
        return mPreferences.getInt(NOTIFICATION_MODEL, Notification.FLAG_ONGOING_EVENT);
    }
}
