package com.hong.bo.shi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.model.bean.HomeOperationBean;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

import java.util.List;

public class MenuAdapter extends BaseAdapter {

    private List<HomeOperationBean> list;
    private Context mContext;

    public MenuAdapter(List<HomeOperationBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public HomeOperationBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_item_layout, null);
        final HomeOperationBean bean = getItem(position);
        ImageDraweeView imageDraweeView = (ImageDraweeView) convertView.findViewById(R.id.ivPhoto);
        imageDraweeView.setImageDraweeView(30, 30, bean.getPicUrl());
        TextView textView = (TextView) convertView.findViewById(R.id.tvText);
        textView.setText(bean.getText());
        return convertView;
    }
}
