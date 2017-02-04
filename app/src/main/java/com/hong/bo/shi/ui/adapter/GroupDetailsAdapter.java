package com.hong.bo.shi.ui.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.contract.GroupDetailsContract;
import com.hong.bo.shi.utils.DensityUtils;
import com.hong.bo.shi.utils.UIHelper;

/**
 * Created by andy on 2016/12/14.
 */

public class GroupDetailsAdapter extends RecyclerViewBaseAdapter<UserInfo> implements View.OnClickListener{

    private int itemWidth;
    private GroupInfo mGroupInfo;
    private GroupDetailsContract.Presenter mPresenter;

    public void setPresenter(GroupDetailsContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    public void setGroupInfo(GroupInfo info) {
        this.mGroupInfo = info;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public void setActivity(BaseActivity activity) {
        super.setActivity(activity);
        itemWidth = (DensityUtils.getScreenWidth(activity) -
                DensityUtils.dp2px(mActivity, 15) * 2) / 4;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == (getItemCount() - 1)) {
            return -1;
        }
        return super.getItemViewType(position);
    }

    @Override
    public int layoutResId(int viewType) {
        if (viewType == 0) {
            return R.layout.activity_group_details_item;
        } else {
            return R.layout.activity_group_details_bottom;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {
        if (holder.getViewType() == -1) {
            onBindBottomViewHolder(holder);
        } else {
            final UserInfo item = getItem(position);
            RelativeLayout itemView = (RelativeLayout) holder.getItemView();
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = itemWidth;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            itemView.setGravity(Gravity.CENTER_HORIZONTAL);
            itemView.setLayoutParams(lp);
            if (item.getGuid() != null) {
                holder.setTextView(R.id.tvName, item.getName());
                holder.setImageDraweeView(R.id.ivPhoto, 50, 50, item.getAvaturl());
                holder.setVisibility(R.id.flOperation, false);
                holder.setVisibility(R.id.ivPersionIcon, !item.getIsCompany());
            } else {
                holder.setVisibility(R.id.flOperation, true);
                holder.setVisibility(R.id.ivPersionIcon, false);
                holder.setImageResource(R.id.ivIcon, R.mipmap.icon_24_plus_p);
                holder.setVisibility(R.id.tvName, false);
                holder.setVisibility(R.id.ivPhoto, false);
            }
            holder.setItemViewOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getGuid() == null) {
                        UIHelper.showSelectContract(mContext, mGroupInfo.getGuid(), "群详情");
                    }else{
                        UIHelper.showUserDetails(mContext, item.getGuid(), "群详情");
                    }
                }
            });
        }
    }

    private void onBindBottomViewHolder(BaseViewHolderHelper holder) {
        boolean isGroup = App.getInstance().getUserInfo().getGuid().equals(mGroupInfo.getMainGroupGuid());
        holder.setTextView(R.id.tvMemberCount, mGroupInfo.getMemberCount() + "人");
        holder.setTextView(R.id.tvGroupsName, mGroupInfo.getMainGroupName());
        holder.setTextView(R.id.tvGroupPublic, mGroupInfo.getGroupNotice());
        holder.setTextView(R.id.tvGroupName, mGroupInfo.getGroupName());
        holder.setVisibility(R.id.llDiagnosis, mGroupInfo.getGroupAttribute() == 100);
        holder.setVisibility(R.id.iv1, false);
        holder.setVisibility(R.id.iv2, false);
        holder.setVisibility(R.id.iv3, isGroup);
        holder.setVisibility(R.id.iv4, true);
        holder.setVisibility(R.id.iv5, true);
        holder.setVisibility(R.id.iv6, true);
        holder.setVisibility(R.id.iv7, true);
        if(isGroup){
            holder.setOnClickListener(R.id.llGroupName, this);
        }
        holder.setOnClickListener(R.id.llLookGroupMessage, this);
        holder.setOnClickListener(R.id.tvExit, this);
        holder.setOnClickListener(R.id.tvGetMessage, this);
        holder.setOnClickListener(R.id.llDiagnosis, this);
        holder.setOnClickListener(R.id.llQRCode, this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvExit){
            mPresenter.exit();
        }else if(v.getId() == R.id.llGroupName){
            UIHelper.showEditGroupName(mContext, mGroupInfo.getGuid(), mGroupInfo.getGroupName());
        }else if(v.getId() == R.id.tvGetMessage){
            mPresenter.getAllMessage();
        }else if(v.getId() == R.id.llDiagnosis){
            mPresenter.getData_1030();
        }else if(v.getId() == R.id.llQRCode){
            UIHelper.showBigPic(mContext, mGroupInfo.getqRCode());
        }else if(v.getId() == R.id.llLookGroupMessage){
            if(App.getInstance().getUserInfo().getGuid().equals(mGroupInfo.getMainGroupGuid())){
                UIHelper.showEditGroupPublic(mContext, mGroupInfo.getGuid(), mGroupInfo.getGroupNotice());
            }else {
                UIHelper.lookGroupPublic(mContext, mGroupInfo.getGuid(), mGroupInfo.getGroupNotice());
            }
        }
    }
}
