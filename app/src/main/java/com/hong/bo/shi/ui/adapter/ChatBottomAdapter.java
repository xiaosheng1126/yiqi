package com.hong.bo.shi.ui.adapter;

import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;

/**
 * Created by andy on 2016/12/13.
 */

public class ChatBottomAdapter extends RecyclerViewBaseAdapter<String> {

    public ChatBottomAdapter() {
        super();
        mData.add("相机");
        mData.add("相册");
        mData.add("视频");

    }

    @Override
    public int layoutResId(int viewType) {
        return 0;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {

    }
}
