package com.maple.cloudweather.ui.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.ApiService;
import com.maple.cloudweather.domain.History;
import com.maple.cloudweather.ui.adapter.HelpAdapter;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by San on 2016/10/13.
 */

public class HelpFragment extends DialogFragment {

    private RecyclerView mRecyclerView;
    private static History mHistory = new History();
    private HelpAdapter mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromServer();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_help, null);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_help_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new HelpAdapter(mHistory);
        mRecyclerView.setAdapter(mAdapter);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle("历史的今天")
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    private void getDataFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.tianapi.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())//添加 json 转换器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//添加 RxJava 适配器
                .build();

        ApiService apiManager = retrofit.create(ApiService.class);//这里采用的是Java的动态代理模式
        apiManager.getHistory("b96b59e935eb0802f1672f843627acd1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<History>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(History history) {
                        mHistory .newslist = history.newslist;
                        System.out.println(history.newslist.get(0).title);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }
}
