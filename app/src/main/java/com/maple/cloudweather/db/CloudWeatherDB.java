package com.maple.cloudweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maple.cloudweather.domain.City;
import com.maple.cloudweather.domain.County;
import com.maple.cloudweather.domain.Province;
import com.maple.cloudweather.uitl.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by San on 2016/9/25.
 */

public class CloudWeatherDB {

    //数据库名称
    public static final String DB_NAME = "cloud_weather.db";

    //数据库版本
    public static final int DB_VERSION = 1;

    public static CloudWeatherDB cloudWeatherDB;

    private SQLiteDatabase db;

    private CloudWeatherDB(Context context) {
        CloudWeatherOpenHelper helper = new CloudWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = helper.getWritableDatabase();
    }

    public synchronized static CloudWeatherDB getInstance(Context context) {
        if (cloudWeatherDB == null) {
            cloudWeatherDB = new CloudWeatherDB(context);
        }
        return cloudWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Province province;
            do {
                province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        UIUtil.close(cursor);
        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvince_id());
            db.insert("City", null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            City city;
            do {
                city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvince_id(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        UIUtil.close(cursor);
        return list;
    }

    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    public List<County> loadCounties(int city_id) {
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id=?", new String[]{String.valueOf(city_id)}, null, null, null);
        if (cursor.moveToFirst()) {
            County county;
            do {
                county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(city_id);
                list.add(county);
            } while (cursor.moveToNext());
        }
        UIUtil.close(cursor);
        return list;
    }

    public void saveMoreCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("more_city_name", city.getCityName());
            values.put("more_city_code", city.getCityCode());
            db.insert("MoreCity", null, values);
        }
    }

    public List<City> loadMoreCities() {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("MoreCity", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            City city;
            do {
                city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("more_city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("more_city_code")));
                list.add(city);
            } while (cursor.moveToNext());
        }
        UIUtil.close(cursor);
        return list;
    }

    public void deleteMoreCity(String city) {
        db.delete("MoreCity", "more_city_name = ?", new String[]{city});
    }
}
