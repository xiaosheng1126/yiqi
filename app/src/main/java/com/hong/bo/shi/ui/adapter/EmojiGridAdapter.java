package com.hong.bo.shi.ui.adapter;

import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.model.bean.Emoji;

import java.util.List;


public class EmojiGridAdapter extends BaseAdapter {
    private List<Emoji> mEmojis;
    private int mItemWidth;

    public EmojiGridAdapter(List<Emoji> emojis, int width) {
        this.mEmojis = emojis;
        mItemWidth = width;
    }

    @Override
    public int getCount() {
        return mEmojis.size() + 1;
    }

    @Override
    public Emoji getItem(int i) {
        return mEmojis.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView iv_emotion = new ImageView(viewGroup.getContext());
        // 设置内边距
        iv_emotion.setPadding(mItemWidth / 8, mItemWidth / 8, mItemWidth / 8, mItemWidth / 8);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(mItemWidth, mItemWidth);
        iv_emotion.setLayoutParams(params);
        //判断是否为最后一个item
        if (i == getCount() - 1) {
            iv_emotion.setImageResource(R.drawable.delete_emoji_selector);
        } else {
            iv_emotion.setImageBitmap(BitmapFactory.decodeFile(getItem(i).sourcePath));
        }
        return iv_emotion;
    }
}
