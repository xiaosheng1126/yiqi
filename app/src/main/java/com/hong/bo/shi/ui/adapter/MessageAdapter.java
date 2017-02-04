package com.hong.bo.shi.ui.adapter;

import android.view.View;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.utils.DateUtils;
import com.hong.bo.shi.utils.EmojiUtils;
import com.hong.bo.shi.utils.UIHelper;

/**
 * Created by andy on 2016/12/12.
 */

public class MessageAdapter extends RecyclerViewBaseAdapter<GroupInfo> {

    @Override
    public int layoutResId(int viewType) {
        return R.layout.fragment_message_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, final int position) {
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
        if(item.getUnreadCount() > Constants.UNREAD_MAX_COUNT){
            holder.setTextView(R.id.tvUnReadCount, Constants.UNREAD_MAX_COUNT_STR);
        }else if(item.getUnreadCount() > 0){
            holder.setTextView(R.id.tvUnReadCount, String.valueOf(item.getUnreadCount()));
        }else{
            holder.setVisibility(R.id.tvUnReadCount, false);
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
        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showChat(mActivity, "消息", item);
            }
        });
    }
}
