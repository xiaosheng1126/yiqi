package com.hong.bo.shi.chat;

import com.hong.bo.shi.base.BaseViewHolderHelper;

/**
 * Created by Administrator on 2016/9/2.
 */
public interface ChatMsgType {

    public static final int PUBLIC = 0x000;
    public static final int SEND_TEXT = 0x001;
    public static final int RECEND_TEXT = 0x002;
    public static final int SEND_AUDIO = 0x003;
    public static final int RECEND_AUDIO = 0x004;
    public static final int SEND_PIC = 0x005;
    public static final int RECEND_PIC = 0x006;
    public static final int SEND_VIDEO = 0x007;
    public static final int RECEND_VIDEO = 0x008;
    public static final int SEND_LOCATION = 0x009;
    public static final int RECEIVE_LOCATION = 0x0010;

    void onBindTextViewHolder(BaseViewHolderHelper holder, int position);
    void onBindPicViewHolder(BaseViewHolderHelper holder, int position);
    void onBindAudioViewHolder(BaseViewHolderHelper holder, int position);
    void onBindVideoViewHolder(BaseViewHolderHelper holder, int position);
    void onBindPublicViewHolder(BaseViewHolderHelper holder, int position);
    void onBindLocationViewHolder(BaseViewHolderHelper holder, int position);
    void onBindViewHolder(BaseViewHolderHelper holder, int position);
}
