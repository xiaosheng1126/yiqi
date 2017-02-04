package com.hong.bo.shi.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.contract.ForwardContract;
import com.hong.bo.shi.utils.DateUtils;
import com.hong.bo.shi.utils.EmojiUtils;

/**
 * Created by andy on 2017/2/4.
 */

public class ForwardGroupAdapter extends RecyclerViewBaseAdapter<GroupInfo> {

    private ForwardContract.Presenter mPresenter;

    public void setPresenter(ForwardContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_forward_group_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {
        final GroupInfo item = getItem(position);
        String[] avaturls = item.getGroupAvaturls_();
        if (avaturls.length == 1) {
            holder.setVisibility(R.id.ivSingle, true);
            holder.setVisibility(R.id.rlMulti, false);
            holder.setImageDraweeView(R.id.ivSingle, 50, 50, avaturls[0]);
        } else {
            holder.setVisibility(R.id.ivSingle, false);
            holder.setVisibility(R.id.rlMulti, true);
            if (avaturls.length == 3) {
                holder.setVisibility(R.id.ivIcon_1, false);
                holder.setImageDraweeView(R.id.ivIcon_2, 25, 25, avaturls[0]);
                holder.setImageDraweeView(R.id.ivIcon_3, 25, 25, avaturls[1]);
                holder.setImageDraweeView(R.id.ivIcon_4, 25, 25, avaturls[2]);
            } else {
                holder.setImageDraweeView(R.id.ivIcon_1, 25, 25, avaturls[0]);
                holder.setImageDraweeView(R.id.ivIcon_2, 25, 25, avaturls[1]);
                holder.setImageDraweeView(R.id.ivIcon_3, 25, 25, avaturls[2]);
                holder.setImageDraweeView(R.id.ivIcon_4, 25, 25, avaturls[3]);
            }
        }
        holder.getView(R.id.ivGroupIcon).setVisibility(item.getGroupAttribute() == 100 ?
                View.VISIBLE : View.INVISIBLE);
        holder.setTextView(R.id.tvName, item.getGroupName());
        holder.setTextView(R.id.tvTime, DateUtils.getFormatSimpleDate(
                DateUtils.stringToLong(item.getLastMsgTime())));
        TextView tvText = holder.getView(R.id.tvText);
        if (item.getMsgAttribute() == 1) {
            if (item.getMsgType() == 1) {
                tvText.setText("[图片]");
            } else if (item.getMsgType() == 2) {
                tvText.setText("[视频]");
            } else if (item.getMsgType() == 3) {
                tvText.setText("[语音]");
            }else if(item.getMsgType() == 4){
                tvText.setText("[定位]");
            }else{
                int size = (int) (tvText.getTextSize() * 13 / 10);
                tvText.setText(EmojiUtils.getEmotionContent(mContext, size, item.getLastMsg()));
            }
        } else {
            tvText.setText(item.getLastMsg());
        }
        if(mPresenter != null){
            holder.setImageResource(R.id.ivSelect, mPresenter.groupIsSelect(item.getGuid())
                    ? R.mipmap.radio_20_sel : R.mipmap.radio_20_nor);
        }
        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPresenter != null){
                    mPresenter.operationGroup(item.getGuid());
                }
            }
        });
    }
}
