package com.maple.cloudweather.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.maple.cloudweather.R;
import com.maple.cloudweather.uitl.UIUtil;

import java.util.List;

/**
 * Created by San on 2016/11/8.
 */

public class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ChooseViewHolder> {

    private List<String> mList;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public ChooseAdapter(List<String> list) {
        mList = list;
    }

    @Override
    public ChooseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(UIUtil.getContext()).inflate(R.layout.item_city,parent,false);
        ChooseViewHolder holder = new ChooseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ChooseViewHolder holder, final int position) {
        holder.bindData(mList.get(position));
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {

        return mList.size() > 0 ? mList.size() : 0;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    class ChooseViewHolder extends RecyclerView.ViewHolder {

        TextView tvCity;
        CardView mCardView;
        public ChooseViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tv_item_city);
            tvCity.setTextColor(Color.BLACK);
            mCardView = (CardView) itemView.findViewById(R.id.card_view_item_city);

        }

        public void bindData(String str) {
            tvCity.setText(str);
        }
    }
}

