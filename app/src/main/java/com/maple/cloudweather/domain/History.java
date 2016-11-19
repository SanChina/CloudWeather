package com.maple.cloudweather.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by San on 2016/11/14.
 */

public class History {


    @SerializedName("code")
    public int code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("newslist")
    public List<NewslistBean> newslist;

    public static class NewslistBean {
        @SerializedName("lsdate")
        public String lsdate;
        @SerializedName("title")
        public String title;
    }
}
