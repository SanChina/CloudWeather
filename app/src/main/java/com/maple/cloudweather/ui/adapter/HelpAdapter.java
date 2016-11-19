package com.maple.cloudweather.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maple.cloudweather.R;
import com.maple.cloudweather.domain.History;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by San on 2016/10/13.
 */

public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpHolder> {

    private History mHistory;

    public HelpAdapter(History history) {
        mHistory = history;

    }

    @Override
    public HelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_line, parent, false);
        return new HelpHolder(view);
    }

    @Override
    public void onBindViewHolder(HelpHolder holder, int position) {
        String lsdate = mHistory.newslist.get(position).lsdate;
        String title = mHistory.newslist.get(position).title;
        holder.bindData(lsdate, title);
    }

    @Override
    public int getItemCount() {
        return mHistory.newslist != null ? 10 : 0;
    }

    class HelpHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_dialog_line_lsdate)
        TextView mLsdate;
        @BindView(R.id.item_dialog_line_title)
        TextView mTitle;

        HelpHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(String lsdate, String title) {
            mLsdate.setText(String.format("%s年 ",lsdate.substring(0,4)));
            mTitle.setText(title);
        }
    }
}
