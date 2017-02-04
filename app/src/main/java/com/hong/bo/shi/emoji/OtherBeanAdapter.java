package com.hong.bo.shi.emoji;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.model.bean.OtherBean;

import java.util.List;


public class OtherBeanAdapter extends BaseAdapter {

    private List<OtherBean> mEmojis;
    private int mItemWidth;

    public OtherBeanAdapter(List<OtherBean> emojis, int width) {
        this.mEmojis = emojis;
        mItemWidth = width;
    }

    @Override
    public int getCount() {
        return mEmojis.size();
    }

    @Override
    public OtherBean getItem(int i) {
        return mEmojis.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.chat_bottom_more_layout_item, null);
//        LinearLayout layout = (LinearLayout) view.findViewById(R.id.item);
        ImageView ivPic = (ImageView) view.findViewById(R.id.ivIcon);
        TextView tvText = (TextView) view.findViewById(R.id.tvText);
//        //设置内边距
//        layout.setPadding(mItemWidth / 5, mItemWidth / 5, mItemWidth / 5, mItemWidth / 5);
//        AbsListView.LayoutParams params = new AbsListView.LayoutParams(mItemWidth, mItemWidth);
//        layout.setLayoutParams(params);
        ivPic.setImageResource(getItem(i).getDrawableRes());
        tvText.setText(getItem(i).getName());
        return view;
    }
}
