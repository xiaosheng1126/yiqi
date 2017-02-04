package com.hong.bo.shi.ui.adapter;

import android.text.TextUtils;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.GroupMember;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andy on 2016/12/21.
 */

public class SearchContractAdapter extends RecyclerViewBaseAdapter<UserInfo> {

    private int mType = 0;// 0 表示普通搜索 1 表示搜索选择联系人
    private Map<String, String> mMap;
    private Map<String, String> mMapExits;
    private OnSelectUserListener mListener;

    public SearchContractAdapter(int mType) {
        this.mType = mType;
        if(this.mType == 1){
            mMap = new HashMap<>();
            mMapExits = new HashMap<>();
        }
    }

    public void setListener(OnSelectUserListener mListener) {
        this.mListener = mListener;
    }

    public void setSelectGuids(String guids, String groupGuid){
        if(mMap == null)return;
        String guid = App.getInstance().getUserInfo().getGuid();
        mMapExits.put(guid, guid);
        if(!TextUtils.isEmpty(groupGuid)) {
            GroupMember member = RealmHelper.getGroupMember(groupGuid);
            if (member != null) {
                String[] exits = member.getGroupMemberGuids().split(Constants.SPLIT);
                for (String exit : exits) {
                    mMapExits.put(exit, exit);
                }
            }
        }
        if(!TextUtils.isEmpty(guids)) {
            String[] split = guids.split(Constants.SPLIT);
            for (String s : split) {
                if (!TextUtils.isEmpty(s)) {
                    mMap.put(s, s);
                }
            }
        }
        if(mListener != null){
            mListener.onChanged(mMap.size());
        }
    }

    @Override
    public void setData(List<UserInfo> list) {
        if(list != null && list.size() > 0 && mMap != null){
            List<UserInfo> newList = new ArrayList<>();
            for (UserInfo userInfo : list) {
                if(mMap.containsKey(userInfo.getGuid()) || mMapExits.containsKey(userInfo.getGuid()))continue;
                newList.add(userInfo);
            }
            super.setData(newList);
        }else {
            super.setData(list);
        }
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_search_contract_item;
    }

    private boolean isSelect(String guid){
       return mMap.containsKey(guid);
    }

    private void operation(String guid){
        if(mMap.containsKey(guid)){
            mMap.remove(guid);
        }else{
            mMap.put(guid, guid);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {
        final UserInfo item = getItem(position);
        holder.setImageDraweeView(R.id.ivPhoto, 50, 50, item.getAvaturl());
        holder.setVisibility(R.id.ivPersionIcon, !item.getIsCompany());
        holder.setTextView(R.id.tvName, item.getName());
        if (mMap == null) {
            holder.setVisibility(R.id.ivSelect, false);
        } else {
            holder.setVisibility(R.id.ivSelect, true);
            holder.setImageResource(R.id.ivSelect, isSelect(item.getGuid())
                    ? R.mipmap.radio_20_sel : R.mipmap.radio_20_nor);
        }
        int rating = item.getGrade();
        holder.setVisibility(R.id.iv_start1, false);
        holder.setVisibility(R.id.iv_start2, false);
        holder.setVisibility(R.id.iv_start3, false);
        holder.setVisibility(R.id.iv_start4, false);
        holder.setVisibility(R.id.iv_start5, false);
        switch (rating) {
            case 5:
                holder.setVisibility(R.id.iv_start5, true);
            case 4:
                holder.setVisibility(R.id.iv_start4, true);
            case 3:
                holder.setVisibility(R.id.iv_start3, true);
            case 2:
                holder.setVisibility(R.id.iv_start2, true);
            case 1:
                holder.setVisibility(R.id.iv_start1, true);
        }
        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap == null) {
                    UIHelper.showUserDetails(mContext, item.getGuid(), "搜索通讯录好友");
                }else{
                    operation(item.getGuid());
                    if(mListener != null){
                        mListener.onChanged(mMap.size());
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    public String getSelectGuids(){
        if(mMap == null)return null;
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : mMap.entrySet()) {
            builder.append(entry.getKey()).append(Constants.SPLIT);
        }
        return builder.toString();
    }

    public static interface OnSelectUserListener{
        void onChanged(int count);
    }
}
