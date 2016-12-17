package com.maple.cloudweather.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.Weather;
import com.maple.cloudweather.ui.holder.BaseViewHolder;
import com.maple.cloudweather.uitl.UIUtil;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by San on 2016/11/3.
 */

public class WeatherAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int TYPE_ONE = 0;
    private static final int TYPE_TWO = 1;
    private static final int TYPE_THREE = 2;
    private static final int TYPE_FOUR = 3;

    private Weather mWeather;
    private Context context;

    public WeatherAdapter(Weather weather) {
        this.mWeather = weather;
    }

    @Override
    public int getItemCount() {
        return mWeather.status != null ? 4 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TYPE_ONE) {
            return TYPE_ONE;
        } else if (position == TYPE_TWO) {
            return TYPE_TWO;
        } else if (position == TYPE_THREE) {
            return TYPE_THREE;
        } else if (position == TYPE_FOUR) {
            return TYPE_FOUR;
        }
        return super.getItemViewType(position);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ONE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_temperature, parent, false);
            return new NowViewHolder(view);
        } else if (viewType == TYPE_TWO) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_hourly, parent, false);
            return new HourlyViewHolder(view);
        } else if (viewType == TYPE_THREE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_suggestion, parent, false);
            return new SuggestionViewHolder(view);
        } else if (viewType == TYPE_FOUR) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_daily, parent, false);
            return new DailyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if (position == 0) {
            holder.bindData(mWeather);
        } else if (position == 1) {
            holder.bindData(mWeather);
        } else if (position == 2) {
            holder.bindData(mWeather);
        } else if (position == 3) {
            holder.bindData(mWeather);

        }
    }

    class NowViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_tmp)
        TextView mTvTmp;
        @BindView(R.id.tv_max)
        TextView mTvMax;
        @BindView(R.id.tv_min)
        TextView mTvMin;
        @BindView(R.id.tv_city)
        TextView mTvCity;
        @BindView(R.id.tv_desc)
        TextView mTvDesc;
        @BindView(R.id.update_time)
        TextView mUpdateTime;
        @BindView(R.id.tv_quality)
        TextView mTvQuality;
        @BindView(R.id.tv_temp_pm)
        TextView mTvTempPm;
        @BindView(R.id.tv_temp_hum)
        TextView mTvTempHum;
        @BindView(R.id.tv_temp_fl)
        TextView mTvTempFl;
        @BindView(R.id.weather_icon)
        ImageView mWeatherIcon;

        public NowViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(Weather weather) {
            try {
                mTvTmp.setText(String.format("%s℃", weather.now.tmp));
                mTvMax.setText(String.format("↑ %s ℃", weather.dailyForecast.get(0).tmp.max));
                mTvMin.setText(String.format("↓ %s ℃", weather.dailyForecast.get(0).tmp.min));
                mTvCity.setText(String.format("%s ", weather.basic.city));
                mTvDesc.setText(String.format(" %s", weather.now.cond.txt));
                mUpdateTime.setText(String.format("今天%s发布", weather.basic.update.loc.substring(11)));

                mTvQuality.setText(String.format("空气指数: %s | %s", UIUtil.safeText(weather.aqi.city.aqi), UIUtil.safeText(weather.aqi.city.qlty)));
                mTvTempPm.setText(String.format("PM2.5: %s μg/m³", UIUtil.safeText(weather.aqi.city.pm25)));
                mTvTempHum.setText(String.format("空气湿度: %s %%", weather.now.hum));
                mTvTempFl.setText(String.format("体感温度: %s℃", weather.now.fl));
                String url = "http://files.heweather.com/cond_icon/" + weather.now.cond.code + ".png";
                Picasso.with(context)
                        .load(url)
                        .placeholder(R.drawable.place_holder)
                        .error(R.drawable.error)
                        .into(mWeatherIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class HourlyViewHolder extends BaseViewHolder {
        int n = mWeather.hourlyForecast.size();
        TextView[] times = new TextView[n];
        TextView[] temps = new TextView[n];
        TextView[] hums = new TextView[n];
        TextView[] winds = new TextView[n];

        LinearLayout mLayout;

        public HourlyViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_hourly);
            for (int i = 0; i < n; i++) {
                View view = View.inflate(context, R.layout.item_hourly_line, null);
                times[i] = (TextView) view.findViewById(R.id.tv_time);
                temps[i] = (TextView) view.findViewById(R.id.tv_temp);
                hums[i] = (TextView) view.findViewById(R.id.tv_humidity);
                winds[i] = (TextView) view.findViewById(R.id.tv_wind);
                mLayout.addView(view);
            }
        }

        @Override
        public void bindData(Weather weather) {
            try {
                for (int i = 0; i < weather.hourlyForecast.size(); i++) {
                    times[i].setText(weather.hourlyForecast.get(i).date.substring(11));
                    temps[i].setText(String.format("%s℃", weather.hourlyForecast.get(i).tmp));
                    hums[i].setText(String.format("%s%%", weather.hourlyForecast.get(i).hum));
                    winds[i].setText(String.format("%s", weather.hourlyForecast.get(i).wind.sc));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SuggestionViewHolder extends BaseViewHolder {

        @BindView(R.id.tv_comf_brf)
        TextView mTvComfBrf;
        @BindView(R.id.tv_comf_txt)
        TextView mTvComfTxt;
        @BindView(R.id.tv_cw_brf)
        TextView mTvCwBrf;
        @BindView(R.id.tv_cw_txt)
        TextView mTvCwTxt;
        @BindView(R.id.tv_drsg_brf)
        TextView mTvDrsgBrf;
        @BindView(R.id.tv_drsg_txt)
        TextView mTvDrsgTxt;
        @BindView(R.id.tv_flu_brf)
        TextView mTvFluBrf;
        @BindView(R.id.tv_flu_txt)
        TextView mTvFluTxt;
        @BindView(R.id.tv_sport_brf)
        TextView mTvSportBrf;
        @BindView(R.id.tv_sport_txt)
        TextView mTvSportTxt;
        @BindView(R.id.tv_trav_brf)
        TextView mTvTravBrf;
        @BindView(R.id.tv_trav_txt)
        TextView mTvTravTxt;
        @BindView(R.id.tv_uv_brf)
        TextView mTvUvBrf;
        @BindView(R.id.tv_uv_txt)
        TextView mTvUvTxt;

        public SuggestionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindData(Weather weather) {
            try {
                mTvComfBrf.setText(String.format("舒适指数---%s", weather.suggestion.comf.brf));
                mTvComfTxt.setText(weather.suggestion.comf.txt);

                mTvCwBrf.setText(String.format("洗车指数---%s", weather.suggestion.cw.brf));
                mTvCwTxt.setText(weather.suggestion.cw.txt);

                mTvDrsgBrf.setText(String.format("穿衣指数---%s", weather.suggestion.drsg.brf));
                mTvDrsgTxt.setText(weather.suggestion.drsg.txt);

                mTvFluBrf.setText(String.format("感冒指数---%s", weather.suggestion.flu.brf));
                mTvFluTxt.setText(weather.suggestion.flu.txt);

                mTvSportBrf.setText(String.format("运动指数---%s", weather.suggestion.sport.brf));
                mTvSportTxt.setText(weather.suggestion.sport.txt);

                mTvTravBrf.setText(String.format("旅游指数---%s", weather.suggestion.trav.brf));
                mTvTravTxt.setText(weather.suggestion.trav.txt);

                mTvUvBrf.setText(String.format("紫外线指数---%s", weather.suggestion.uv.brf));
                mTvUvTxt.setText(weather.suggestion.uv.txt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class DailyViewHolder extends BaseViewHolder {

        LinearLayout mLayout;
        ImageView[] icons = new ImageView[7];
        TextView[] dates = new TextView[7];
        TextView[] descs = new TextView[7];
        TextView[] mins = new TextView[7];
        TextView[] maxs = new TextView[7];
        TextView[] infos = new TextView[7];
        TextView[] sunrises = new TextView[7];
        TextView[] sunsets = new TextView[7];

        public DailyViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_daily);
            for (int i = 0; i < 7; i++) {
                View view = View.inflate(context, R.layout.item_daily_line, null);
                icons[i] = (ImageView) view.findViewById(R.id.iv_daily_icon);
                dates[i] = (TextView) view.findViewById(R.id.tv_daily_date);
                descs[i] = (TextView) view.findViewById(R.id.tv_daily_desc);
                mins[i] = (TextView) view.findViewById(R.id.tv_daily_min);
                maxs[i] = (TextView) view.findViewById(R.id.tv_daily_max);
                infos[i] = (TextView) view.findViewById(R.id.tv_daily_info);
                sunrises[i] = (TextView) view.findViewById(R.id.tv_daily_sunrise);
                sunsets[i] = (TextView) view.findViewById(R.id.tv_daily_sunset);
                mLayout.addView(view);
            }
        }

        @Override
        public void bindData(Weather weather) {
            try {
                for (int i = 0; i < 7; i++) {
                    dates[i].setText(weather.dailyForecast.get(i).date.substring(5));
                    descs[i].setText(weather.dailyForecast.get(i).cond.txtD);
                    mins[i].setText(String.format("↓ %s ℃", weather.dailyForecast.get(i).tmp.min));
                    maxs[i].setText(String.format("↑ %s ℃", weather.dailyForecast.get(i).tmp.max));
                    infos[i].setText(String.format("%s.湿度%s%%.能见度%skm.降雨概率%s%%",
                            weather.dailyForecast.get(i).wind.sc,
                            weather.dailyForecast.get(i).hum,
                            weather.dailyForecast.get(i).vis,
                            weather.dailyForecast.get(i).pop));
                    sunrises[i].setText(weather.dailyForecast.get(i).astro.sr);
                    sunsets[i].setText(weather.dailyForecast.get(i).astro.ss);

                    String url = "http://files.heweather.com/cond_icon/" + weather.dailyForecast.get(i).cond.codeD + ".png";
                    Picasso.with(context)
                            .load(url)
                            .placeholder(R.drawable.place_holder)
                            .error(R.drawable.error)
                            .into(icons[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
