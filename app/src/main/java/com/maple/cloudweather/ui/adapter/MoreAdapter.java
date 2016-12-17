package com.maple.cloudweather.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.uitl.UIUtil;
import com.maple.cloudweather.view.AqiView;
import com.maple.cloudweather.view.WindView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by San on 2016/11/10.
 */

public class MoreAdapter extends RecyclerView.Adapter<MoreAdapter.MoreViewHolder> {

    private Context context;
    private List<Weather> mList;
    private OnLongClickListener onLongClickListener = null;

    public void setOnLongClick(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public MoreAdapter(List<Weather> weatherList) {
        mList = weatherList;
    }

    @Override
    public MoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_more_city, parent, false);
        return new MoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoreViewHolder holder, int position) {
        final Weather weather = mList.get(position);
        holder.bindData(weather);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onLongClickListener.longClick(weather.basic.city);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MoreViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.dialog_city)
        TextView mDialogCity;
        @BindView(R.id.dialog_icon)
        ImageView mDialogIcon;
        @BindView(R.id.dialog_temp)
        TextView mDialogTemp;
        @BindView(R.id.dialog_pm25)
        TextView mDialogPm25;
        @BindView(R.id.weather_dialog_root)
        RelativeLayout mWeatherDialogRoot;
        @BindView(R.id.cardView)
        CardView mCardView;
        @BindView(R.id.wind_view)
        WindView mWindView;
        @BindView(R.id.aqi_view)
        AqiView mAqiView;
        public MoreViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(Weather weather) {
            try {
                mWindView.setData(weather); //12:17By San
                mAqiView.setData(weather);
                mDialogCity.setText(weather.basic.city);
                mDialogTemp.setText(String.format("%s℃", weather.now.tmp));
                mDialogPm25.setText(String.format("PM2.5: %s μg/m³", weather.aqi.city.pm25));
            } catch (NullPointerException ignored) {

            }

            String url = "http://files.heweather.com/cond_icon/" + weather.now.cond.code + ".png";
            Glide.with(UIUtil.getContext())
                    .load(url)
                    .asBitmap()
                    .into(mDialogIcon);
                    /*.into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mDialogIcon.setImageBitmap(resource);
                            mDialogIcon.setColorFilter(Color.WHITE);
                        }
                    });*/
            int code = Integer.valueOf(weather.now.cond.code);
            if (code == 100) {
                mCardView.setBackground(ContextCompat.getDrawable(context, R.mipmap.dialog_bg_sunny));
            } else if (code >= 300 && code < 408) {
                mCardView.setBackground(ContextCompat.getDrawable(context, R.mipmap.dialog_bg_rainy));
            } else {
                mCardView.setBackground(ContextCompat.getDrawable(context, R.mipmap.dialog_bg_cloudy));
            }
        }
    }

    public interface OnLongClickListener {
        void longClick(String city);
    }
}


