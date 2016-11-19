package com.maple.cloudweather.uitl;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.maple.cloudweather.db.CloudWeatherDB;
import com.maple.cloudweather.domain.City;
import com.maple.cloudweather.domain.County;
import com.maple.cloudweather.domain.Province;
import com.maple.cloudweather.global.CloudWeatherApplication;

/**
 * Created by San on 2016/9/28.
 */

public class Utility {

    private static Context sContext = CloudWeatherApplication.getContext();

    //处理和解析服务器返回的省级数据
    public synchronized static boolean handleProvincesResponse(CloudWeatherDB cloudWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] provinces = response.split(",");
            if (provinces != null && provinces.length > 0) {
                Province province;
                for (int i = 0; i < provinces.length; i++) {
                    Log.d("Province", provinces[i]);
                    String[] array = provinces[i].split("\\|");
                    province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    cloudWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCitiesResponse(CloudWeatherDB cloudWeatherDB, String response, int province_id) {
        if (!TextUtils.isEmpty(response)) {
            String[] cities = response.split(",");
            if (cities != null && cities.length > 0) {
                City city;
                for (int i = 0; i < cities.length; i++) {
                    Log.d("City", cities[i]);
                    String[] array = cities[i].split("\\|");
                    city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvince_id(province_id);
                    cloudWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(CloudWeatherDB cloudWeatherDB, String response, int city_id) {
        if (!TextUtils.isEmpty(response)) {
            String[] counties = response.split(",");
            if (counties != null && counties.length > 0) {
                County county;
                for (int i = 0; i < counties.length; i++) {
                    Log.d("County", counties[i]);
                    String[] array = counties[i].split("\\|");
                    county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(city_id);
                    cloudWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
