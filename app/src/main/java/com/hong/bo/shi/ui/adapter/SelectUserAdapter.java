package com.hong.bo.shi.ui.adapter;

import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.contract.SelectContract;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by andy on 2017/1/14.
 */

public class SelectUserAdapter extends RecyclerViewBaseAdapter<Map.Entry<String, String>> {

    private SelectContract.Presenter mPresenter;

    public void setPresenter(SelectContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    public void update(){
        if(mPresenter != null) {
            mData = new ArrayList<>(mPresenter.getSelectUsers().entrySet());
            notifyDataSetChanged();
        }
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_select_contract_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {
        final Map.Entry<String, String> item = getItem(position);
        final UserInfo userInfo = RealmHelper.getUserInfo(item.getKey());
        holder.setVisibility(R.id.ivPersionIcon, !userInfo.getIsCompany());
        holder.setImageDraweeView(R.id.ivPhoto, 40, 40, userInfo.getAvaturl());
        holder.setTextView(R.id.tvName, userInfo.getName());
        holder.setOnClickListener(R.id.ivPhoto, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.operation(item.getKey());
            }
        });
    }
}
