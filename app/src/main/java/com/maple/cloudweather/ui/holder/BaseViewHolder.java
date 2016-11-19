package com.maple.cloudweather.ui.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.maple.cloudweather.domain.Weather;

/**
 * Created by San on 2016/11/3.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public void bindData(Weather weather) {

    }
}
