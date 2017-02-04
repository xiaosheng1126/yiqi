package com.hong.bo.shi.ui.adapter;

import android.graphics.Color;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.MainItembean;

/**
 * Created by andy on 2016/12/12.
 */

public class MainAdapter extends RecyclerViewBaseAdapter<MainItembean> {

    private int mSelectPosition = 0;
    private OnCheckSelectListener mListener;

    public void setOnCheckSelectListener(OnCheckSelectListener listener) {
        this.mListener = listener;
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_main_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, final int position) {
        MainItembean item = getItem(position);
        holder.setTextView(R.id.tvText, item.getText());
        if (mSelectPosition == position) {
            holder.setImageResource(R.id.ivIcon, item.getSelectDrawable());
            holder.setTextColor(R.id.tvText, Color.parseColor("#3399FF"));
        } else {
            holder.setImageResource(R.id.ivIcon, item.getDrawable());
            holder.setTextColor(R.id.tvText, Color.parseColor("#999999"));
        }
        if(item.getUnReadCount() > Constants.UNREAD_MAX_COUNT){
            holder.setTextView(R.id.tvUnReadCount, Constants.UNREAD_MAX_COUNT_STR);
        }else if(item.getUnReadCount() > 0){
            holder.setTextView(R.id.tvUnReadCount, String.valueOf(item.getUnReadCount()));
        }else{
            holder.setVisibility(R.id.tvUnReadCount, false);
        }
        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != mSelectPosition) {
                    mSelectPosition = position;
                    if (mListener != null) {
                        mListener.onCheckSelect(position);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    public static interface OnCheckSelectListener {
        void onCheckSelect(int position);
    }

    public void setPosition(int position) {
        this.mSelectPosition = position;
        if (mListener != null) {
            mListener.onCheckSelect(position);
        }
        notifyDataSetChanged();
    }

    public boolean isChanged(int[] counts) {
        if (counts != null && counts.length == 4 && getItemCount() == 4) {
            for (int i = 0; i < getItemCount(); i++) {
                if (getItem(i).getUnReadCount() != counts[i]) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
