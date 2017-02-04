package com.hong.bo.shi.ui.adapter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.BusinessItemBean;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.qcode.CaptureActivity;
import com.hong.bo.shi.utils.UIHelper;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessAdapter extends RecyclerViewBaseAdapter<BusinessItemBean> {

    @Override
    public int layoutResId(int viewType) {
        return R.layout.fragment_business_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, final int position) {
        final BusinessItemBean item = getItem(position);
        holder.setTextView(R.id.tvText, item.getText());
        holder.setImageResource(R.id.ivIcon, item.getDrawableRes());
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
                if(position == 2){//扫一扫
                    Intent intent = new Intent(mContext, CaptureActivity.class);
                    mContext.startActivity(intent);
                }else if(!TextUtils.isEmpty(item.getmActionUrl())){
                    WebBean bean = new WebBean(item.getText(), "业务", "", 0);
                    bean.setUrl(item.getmActionUrl());
                    UIHelper.showWebView(mContext, bean);
                }
            }
        });
    }

    public boolean isChanged(int[] counts, String[] urls){
        if(getItemCount() == 4){
            for (int i = 0; i < getItemCount(); i++) {
                if(!getItem(i).getmActionUrl().equals(urls[i]) || getItem(i).getUnReadCount() != counts[i]){
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}
