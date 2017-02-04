package com.hong.bo.shi.ui.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.chat.ChatMsgType;
import com.hong.bo.shi.dialog.CenterDialog;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.presenter.contract.ChatContract;
import com.hong.bo.shi.recorder.MediaManager;
import com.hong.bo.shi.utils.DateUtils;
import com.hong.bo.shi.utils.EmojiUtils;
import com.hong.bo.shi.utils.SystemUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

/**
 * Created by andy on 2016/12/14.
 */

public class ChatAdapter extends BaseAdapter implements ChatMsgType {

    private List<GroupMessage> mData;
    private String mMeGuid;
    private Context mContext;
    private View mAnimView;
    private boolean isSend;
    private int mMinItemWidth;
    private int mMaxItemWidth;
    private ChatContract.Presenter mPresenter;
    private CenterDialog mDialog;

    public void setPresenter(ChatContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    public ChatAdapter() {
        this.mData = new ArrayList<>();
        mMeGuid = App.getInstance().getUserInfo().getGuid();
    }

    public void add(GroupMessage message) {
        this.mData.add(message);
        notifyDataSetChanged();
    }

    public void addAll(List<GroupMessage> list) {
        if (list != null && list.size() > 0) {
            this.mData.addAll(0, list);
            notifyDataSetChanged();
        }
    }

    public void setData(List<GroupMessage> list) {
        if (!mData.isEmpty()) {
            mData.clear();
        }
        if (list != null && list.size() > 0) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    private boolean isSend(String fromGuid) {
        return mMeGuid.equals(fromGuid);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public GroupMessage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        GroupMessage item = getItem(position);
        if (item.getMsgAttribute() == 2) {//公告消息
            return PUBLIC;
        }
        switch (item.getMsgType()) {
            case 0://文本
                return isSend(item.getFromUserGuid()) ? SEND_TEXT : RECEND_TEXT;
            case 1://图片
                return isSend(item.getFromUserGuid()) ? SEND_PIC : RECEND_PIC;
            case 2://视频
                return isSend(item.getFromUserGuid()) ? SEND_VIDEO : RECEND_VIDEO;
            case 3://语音
                return isSend(item.getFromUserGuid()) ? SEND_AUDIO : RECEND_AUDIO;
            default:
                return isSend(item.getFromUserGuid()) ? SEND_LOCATION : RECEIVE_LOCATION;
        }
    }

    public void onPause() {
        if (mAnimView != null) {
            if (isSend) {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_p);
            } else {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_g);
            }
            mAnimView = null;
            MediaManager.pause();
        }
    }

    protected
    @LayoutRes
    int layoutResId(int viewType) {
        switch (viewType) {
            case SEND_TEXT:
                return R.layout.chat_send_text_item;
            case RECEND_TEXT:
                return R.layout.chat_receive_text_item;
            case SEND_PIC:
                return R.layout.chat_send_pic_item;
            case RECEND_PIC:
                return R.layout.chat_receive_pic_item;
            case SEND_AUDIO:
                return R.layout.chat_send_audio_item;
            case RECEND_AUDIO:
                return R.layout.chat_receive_audio_item;
            case SEND_VIDEO:
                return R.layout.chat_send_video_item;
            case RECEND_VIDEO:
                return R.layout.chat_receive_video_item;
            case SEND_LOCATION:
                return R.layout.chat_send_location_item;
            case RECEIVE_LOCATION:
                return R.layout.chat_receive_location_item;
            default:
                return R.layout.chat_public_item;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (mContext == null) {
            mContext = parent.getContext();
            WindowManager manager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            manager.getDefaultDisplay().getMetrics(metrics);
            mMinItemWidth = (int) (metrics.widthPixels * 0.15f);
            mMaxItemWidth = (int) (metrics.widthPixels * 0.45f);
        }
        View itemView = LayoutInflater.from(mContext).inflate(layoutResId(viewType), null);
        BaseViewHolderHelper helper = new BaseViewHolderHelper(itemView);
        helper.setViewType(viewType);
        onBindViewHolder(helper, position);
        return helper.getItemView();
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, int position) {
        final GroupMessage item = getItem(position);
        if (position > 0) {
            long time = DateUtils.stringToLong(item.getMsgTime());
            long lastTime = DateUtils.stringToLong(getItem(position - 1).getMsgTime());
            if (time - lastTime > 1000 * 60) {//1分钟间隔
                holder.setTextView(R.id.tvTime, DateUtils.getFormatSimpleDate(time));
            } else {
                holder.setVisibility(R.id.tvTime, false);
            }
        } else {
            holder.setTextView(R.id.tvTime, DateUtils.getFormatSimpleDate(
                    DateUtils.stringToLong(item.getMsgTime())));
        }
        if (holder.getViewType() == PUBLIC) {
            onBindPublicViewHolder(holder, position);
            return;
        }
        holder.setTextView(R.id.tvName, item.getFromUserName());
        holder.setImageDraweeView(R.id.ivPhoto, 40, 40, item.getFromUserAvaturl());
        switch (holder.getViewType()) {
            case SEND_TEXT:
            case RECEND_TEXT:
                onBindTextViewHolder(holder, position);
                break;
            case SEND_PIC:
            case RECEND_PIC:
                onBindPicViewHolder(holder, position);
                break;
            case SEND_AUDIO:
            case RECEND_AUDIO:
                onBindAudioViewHolder(holder, position);
                break;
            case SEND_VIDEO:
            case RECEND_VIDEO:
                onBindVideoViewHolder(holder, position);
                break;
            case SEND_LOCATION:
            case RECEIVE_LOCATION:
                onBindLocationViewHolder(holder, position);
                break;
        }
        responseToPhoto(holder, item);
        responseToLongClick(holder, position);
        responseToItemClick(holder, position);
        responseToAgain(holder, position);
    }

    private void responseToPhoto(BaseViewHolderHelper holder, final GroupMessage item) {
        View view = holder.getView(R.id.ivPhoto);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showUserDetails(mContext, item.getFromUserGuid(), "会话");
                }
            });
        }
    }

    private void responseToLongClick(BaseViewHolderHelper holder, final int position) {
        View audioItem = holder.getView(R.id.itemAudio);
        if (audioItem != null) {
            audioItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }
        View othetItem = holder.getView(R.id.rlItem);
        if (othetItem != null) {
            othetItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showMenu(position);
                    return true;
                }
            });
        }
    }

    private void responseToItemClick(final BaseViewHolderHelper holder, final int position) {
        View audioItem = holder.getView(R.id.itemAudio);
        if (audioItem != null) {
            audioItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playAudio(holder, getItem(position));
                }
            });
        }
        View othetItem = holder.getView(R.id.rlItem);
        if (othetItem != null) {
            othetItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupMessage item = getItem(position);
                    if (holder.getViewType() == SEND_PIC || holder.getViewType() == RECEND_PIC) {
                        UIHelper.peviewPic(mContext, item);
                    }else if(holder.getViewType() == RECEIVE_LOCATION || holder.getViewType() == SEND_LOCATION){
                        UIHelper.showLocation(mContext, item);
                    }else if(holder.getViewType() == RECEND_VIDEO || holder.getViewType() == SEND_VIDEO){
                        UIHelper.playVideo(mContext, item);
                    }
                }
            });
        }
    }

    private void responseToAgain(BaseViewHolderHelper holder, final int position) {
        View view = holder.getView(R.id.ivAgainSend);
        if (view != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupMessage item = getItem(position);
                    item.setStatue(2);
                    mPresenter.sendMessage(item);
                }
            });
        }
    }

    private void playAudio(BaseViewHolderHelper holder, final GroupMessage item) {
        //播放动画
        if (mAnimView != null) {
            if (isSend) {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_p);
            } else {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_g);
            }
            mAnimView = null;
        }
        mAnimView = holder.getView(R.id.view);
        isSend = isSend(item.getFromUserGuid());
        if (isSend) {
            mAnimView.setBackgroundResource(R.drawable.chat_send_audio_anim);
        } else {
            mAnimView.setBackgroundResource(R.drawable.chat_receive_audio_anim);
        }
        AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
        anim.start();
        if (!item.isDownloadSuccess()) {
            if (isSend) {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_p);
            } else {
                mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_g);
            }
            DownloadManager.getRequestQueue().add(item);
            Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
            return;
        }
        //播放音频
        MediaManager.playSound(item.getFilePath(), new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isSend) {
                    mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_p);
                } else {
                    mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_g);
                }
            }
        }, new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (isSend) {
                    mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_p);
                } else {
                    mAnimView.setBackgroundResource(R.mipmap.icon_16_soundwave_g);
                }
                Toast.makeText(mContext, "播放失败", Toast.LENGTH_SHORT).show();
                MediaManager.reset();
                return false;
            }
        });
    }

    @Override
    public void onBindTextViewHolder(BaseViewHolderHelper holder, int position) {
        TextView textView = holder.getView(R.id.tvText);
        GroupMessage item = getItem(position);
        int size = (int) (textView.getTextSize() * 13 / 8);
        textView.setText(EmojiUtils.getEmotionContent(mContext, size, item.getMessage()));
        if(holder.getViewType() == SEND_TEXT) {
            holder.setVisibility(R.id.ivAgainSend, item.getStatue() == 1);
            holder.setVisibility(R.id.pb, item.getStatue() == 2);
        }
    }

    @Override
    public void onBindPicViewHolder(BaseViewHolderHelper holder, int position) {
        final GroupMessage item = getItem(position);
        if(item.getStatue() != 0){
            holder.setImageDraweeView(R.id.ivPic, 50, 65, "file://" + item.getFilePath());
        }else{
            holder.setImageDraweeView(R.id.ivPic, 50, 65, item.getMessage());
            Log.e("PicUrl", item.getMessage());
        }
        if(holder.getViewType() == SEND_PIC){
            holder.setVisibility(R.id.ivAgainSend, item.getStatue() == 1);
            holder.setVisibility(R.id.pb, item.getStatue() == 2);
        }
    }

    @Override
    public void onBindAudioViewHolder(BaseViewHolderHelper holder, int position) {
        final GroupMessage item = getItem(position);
        ViewGroup.LayoutParams lp = holder.getView(R.id.itemAudio).getLayoutParams();
        lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f * Integer.valueOf(item.getTime())));
        holder.setTextView(R.id.tvLongTime, item.getTime() + "'");
        if(holder.getViewType() == SEND_AUDIO) {
            holder.setVisibility(R.id.ivAgainSend, item.getStatue() == 1);
            holder.setVisibility(R.id.pb, item.getStatue() == 2);
            holder.setVisibility(R.id.tvLongTime, item.getStatue() == 0);
        }
    }

    @Override
    public void onBindVideoViewHolder(BaseViewHolderHelper holder, int position) {
        final GroupMessage item = getItem(position);
        if(item.getStatue() == 0) {
            holder.setImageDraweeView(R.id.ivPic, 50, 65, item.getMessage());
            Log.e("PicUrl", item.getMessage());
        }else{
            ImageDraweeView imageDraweeView = holder.getView(R.id.ivPic);
            imageDraweeView.setImageResource(R.mipmap.default_pic);
        }
        if(holder.getViewType() == SEND_VIDEO) {
            holder.setVisibility(R.id.ivAgainSend, item.getStatue() == 1);
            holder.setVisibility(R.id.pb, item.getStatue() == 2);
        }
    }

    @Override
    public void onBindPublicViewHolder(BaseViewHolderHelper holder, int position) {
        GroupMessage item = getItem(position);
        holder.setTextView(R.id.tvText, item.getMessage());
    }

    @Override
    public void onBindLocationViewHolder(BaseViewHolderHelper holder, int position) {
        GroupMessage item = getItem(position);
        final String[] split = item.getTime().split(RequestHeader.SPLIT);
        final String decode = RequestHeader.decode(split[2]);
        holder.setTextView(R.id.tvAddress, decode);
        if(item.getStatue() != 0){
            holder.setImageDraweeView(R.id.ivPic, 200, 60, "file://" + item.getFilePath());
        }else{
            holder.setImageDraweeView(R.id.ivPic, 200, 60, item.getMessage());
            Log.e("PicUrlL", item.getMessage());
        }
    }

    private void showMenu(final int position) {
        int viewType = getItemViewType(position);
        int statue = getItem(position).getStatue();
        int menu = (viewType == SEND_TEXT || viewType == RECEND_TEXT) ?
                (statue == 0 ?  R.menu.chat_item_text_success_menu : R.menu.chat_item_text_menu )
                :( statue == 0 ? R.menu.chat_item_success_menu : R.menu.chat_item_menu);
        mDialog = new CenterDialog(mContext, menu,
                new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        GroupMessage message = getItem(position);
                        switch (item.getItemId()) {
                            case R.id.item_delete:
                                mPresenter.deleteMsg(message.getGuid());
                                break;
                            case R.id.item_copy:
                                SystemUtils.copyText(mContext, message.getMessage());
                                break;
                            case R.id.forward:
                                UIHelper.forward(mContext, message.getGuid());
                                break;
                        }
                        return true;
                    }
                });
        mDialog.show();
    }
}
