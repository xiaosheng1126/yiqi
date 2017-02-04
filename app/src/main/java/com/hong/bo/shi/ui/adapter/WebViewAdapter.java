package com.hong.bo.shi.ui.adapter;

import android.graphics.Color;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.BottomBean;

/**
 * Created by andy on 2017/1/14.
 */

public class WebViewAdapter extends RecyclerViewBaseAdapter<BottomBean> {

    private int mSelectPosition;
    private MainAdapter.OnCheckSelectListener mListener;

    public void setSelectPosition(int selectPosition) {
        if(mSelectPosition != selectPosition) {
            this.mSelectPosition = selectPosition;
            notifyDataSetChanged();
        }
    }

    public void setOnCheckSelectListener(MainAdapter.OnCheckSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_webview_bottom_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, final int position) {
        BottomBean item = getItem(position);
        holder.setTextView(R.id.tvText, item.getText());
        if (mSelectPosition == position) {
            holder.setImageDraweeView(R.id.ivIcon, 25, 25, item.getSelectUrl());
            holder.setTextColor(R.id.tvText, Color.parseColor("#3399FF"));
        } else {
            holder.setImageDraweeView(R.id.ivIcon, 25, 25, item.getUnSelectUrl());
            holder.setTextColor(R.id.tvText, Color.parseColor("#999999"));
        }

        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != mSelectPosition) {
                    mSelectPosition = position;
                    if(mListener != null){
                        mListener.onCheckSelect(mSelectPosition);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }
}
