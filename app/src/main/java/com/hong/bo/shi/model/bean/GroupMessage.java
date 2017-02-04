package com.hong.bo.shi.model.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.utils.DateUtils;

import java.io.File;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andy on 2016/12/10.
 * 群会话信息
 */
public class GroupMessage extends RealmObject implements Parcelable {

    private String msgTime;//消息时间
    private String fromUserGuid;//消息发送者GUID
    private String fromUserName;//消息发送者名称
    private String fromUserAvaturl;//消息发送者头像
    private String message;//消息内容
    private int msgType;//消息体类型 0 文本 1 图片  2视频  3语音 4 定位
    private int msgAttribute;//消息属性  1普通信息   2 公共信息
    @PrimaryKey
    private String guid;//消息GUID
    private String groupGuid;//所在群guid
    private String time;//语音或者视频的时间
    private int statue;//0 成功 1 失败 2正在发送
    private String filePath;//文件路径(语音,视频)下载成功后保存到这里，发送的发送的时候保存到这里
    private long createTime;//创建时间

    public GroupMessage() {
    }

    public GroupMessage(String groupGuid, String data) {
        String[] split = data.split(Constants.SPLIT);
        this.groupGuid = groupGuid;
        this.msgTime = split[0];
        this.fromUserGuid = split[1];
        this.fromUserName = split[2];
        this.fromUserAvaturl = split[3];
        this.message = split[4];
        this.msgType = Constants.dataToInt(split[5], 0);
        this.msgAttribute = Constants.dataToInt(split[6], 1);
        this.guid = split[7];
        this.time = split[8];
        this.statue = 0;
        createTime = DateUtils.stringToLong(msgTime);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getGroupGuid() {
        return groupGuid;
    }

    public void setGroupGuid(String groupGuid) {
        this.groupGuid = groupGuid;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public String getTime() {
        return time!= null ? time.trim() : "0";
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsgTime() {
        return msgTime == null ? "" : msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getFromUserGuid() {
        return fromUserGuid;
    }

    public void setFromUserGuid(String fromUserGuid) {
        this.fromUserGuid = fromUserGuid;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserAvaturl() {
        return fromUserAvaturl;
    }

    public void setFromUserAvaturl(String fromUserAvaturl) {
        this.fromUserAvaturl = fromUserAvaturl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getMsgAttribute() {
        return msgAttribute;
    }

    public void setMsgAttribute(int msgAttribute) {
        this.msgAttribute = msgAttribute;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "msgTime='" + msgTime + '\'' +
                ", fromUserGuid='" + fromUserGuid + '\'' +
                ", fromUserName='" + fromUserName + '\'' +
                ", fromUserAvaturl='" + fromUserAvaturl + '\'' +
                ", message='" + message + '\'' +
                ", msgType=" + msgType +
                ", msgAttribute=" + msgAttribute +
                ", guid='" + guid + '\'' +
                ", groupGuid='" + groupGuid + '\'' +
                ", statue='" + statue + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msgTime);
        dest.writeString(this.fromUserGuid);
        dest.writeString(this.fromUserName);
        dest.writeString(this.fromUserAvaturl);
        dest.writeString(this.message);
        dest.writeInt(this.msgType);
        dest.writeInt(this.msgAttribute);
        dest.writeString(this.guid);
        dest.writeString(this.groupGuid);
        dest.writeString(this.time);
        dest.writeInt(this.statue);
        dest.writeString(this.filePath);
    }

    protected GroupMessage(Parcel in) {
        this.msgTime = in.readString();
        this.fromUserGuid = in.readString();
        this.fromUserName = in.readString();
        this.fromUserAvaturl = in.readString();
        this.message = in.readString();
        this.msgType = in.readInt();
        this.msgAttribute = in.readInt();
        this.guid = in.readString();
        this.groupGuid = in.readString();
        this.time = in.readString();
        this.statue = in.readInt();
        this.filePath = in.readString();
    }

    public static final Parcelable.Creator<GroupMessage> CREATOR = new Parcelable.Creator<GroupMessage>() {
        @Override
        public GroupMessage createFromParcel(Parcel source) {
            return new GroupMessage(source);
        }

        @Override
        public GroupMessage[] newArray(int size) {
            return new GroupMessage[size];
        }
    };

    public boolean isDownloadSuccess(){
        if(TextUtils.isEmpty(filePath))return false;
        File file = new File(filePath);
        if(file == null || !file.exists() || file.length() == 0)return false;
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        return guid.equals(((GroupMessage)obj).getGuid());
    }
}


