package com.maple.cloudweather.ui.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.trello.rxlifecycle.components.support.RxFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends RxFragment {

    protected void setTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }
}
