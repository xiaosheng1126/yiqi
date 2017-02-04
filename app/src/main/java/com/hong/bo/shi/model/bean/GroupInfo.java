package com.hong.bo.shi.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.utils.DateUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andy on 2016/12/10.
 * 群基本信息对象
 */
public class GroupInfo extends RealmObject implements Parcelable {

    private int unreadCount;//未读消息数量
    @PrimaryKey
    private String guid;//群GUID,
    private String groupName;//群名称
    private String mainGroupName;//群主名称
    private String mainGroupGuid;//群主GUID
    private String groupNotice;//群公告
    private int groupAttribute;//2两人群 3多人群 100(云端群)
    private int memberCount;//群成员数量
    private String mGroupAvaturls;//群头像url（多人群头像之间用逗号分隔）
    private String lastMsgTime;//群最新消息时间
    private String lastMsg;//群最新一条信息
    private int msgType;//0文本 1图片 2视频 3语音 4定位
    private int msgAttribute;//消息属性  1普通信息   2 公共信息
    private long createTime;//(群最新消息时间)群排序根据这个字段
    private String qRCode;//群二维码图片

    public GroupInfo(String data) {
        String[] split = data.split(Constants.SPLIT);
        this.unreadCount = Constants.dataToInt(split[0], 0);
        this.guid = split[1];
        this.groupName = split[2];
        this.mainGroupName = split[3];
        this.mainGroupGuid = split[4];
        this.groupNotice = split[5];
        this.groupAttribute = Constants.dataToInt(split[6], 2);
        this.memberCount = Constants.dataToInt(split[7], 2);
        this.mGroupAvaturls = split[8];
        this.lastMsgTime = split[9];
        this.lastMsg = split[10];
        this.msgType = Constants.dataToInt(split[11], 0);
        this.msgAttribute = Constants.dataToInt(split[12], 1);
        this.createTime = DateUtils.stringToLong(lastMsgTime);
        this.qRCode = split[13];
    }

    public GroupInfo() {
    }

    public String getqRCode() {
        return qRCode;
    }

    public void setqRCode(String qRCode) {
        this.qRCode = qRCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getGroupName() {
        return groupName == null ? "" : groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getMainGroupName() {
        return mainGroupName == null ? "" : mainGroupName;
    }

    public void setMainGroupName(String mainGroupName) {
        this.mainGroupName = mainGroupName;
    }

    public String getMainGroupGuid() {
        return mainGroupGuid;
    }

    public void setMainGroupGuid(String mainGroupGuid) {
        this.mainGroupGuid = mainGroupGuid;
    }

    public String getGroupNotice() {
        return groupNotice == null ? "" : groupNotice;
    }

    public void setGroupNotice(String groupNotice) {
        this.groupNotice = groupNotice;
    }

    public int getGroupAttribute() {
        return groupAttribute;
    }

    public void setGroupAttribute(int groupAttribute) {
        this.groupAttribute = groupAttribute;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String[] getGroupAvaturls_() {
        return mGroupAvaturls.split(",");
    }

    public String getGroupAvaturls() {
        return mGroupAvaturls;
    }

    public void setGroupAvaturls(String mGroupAvaturls) {
        this.mGroupAvaturls = mGroupAvaturls;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
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

    @Override
    public String toString() {
        return "GroupInfo{" +
                "unreadCount=" + unreadCount +
                ", guid='" + guid + '\'' +
                ", groupName='" + groupName + '\'' +
                ", mainGroupName='" + mainGroupName + '\'' +
                ", mainGroupGuid='" + mainGroupGuid + '\'' +
                ", groupNotice='" + groupNotice + '\'' +
                ", groupAttribute=" + groupAttribute +
                ", memberCount=" + memberCount +
                ", mGroupAvaturls='" + mGroupAvaturls + '\'' +
                ", lastMsgTime='" + lastMsgTime + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                ", msgType=" + msgType +
                ", msgAttribute=" + msgAttribute +
                ", createTime=" + createTime +
                ", qRCode=" + qRCode +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.unreadCount);
        dest.writeString(this.guid);
        dest.writeString(this.groupName);
        dest.writeString(this.mainGroupName);
        dest.writeString(this.mainGroupGuid);
        dest.writeString(this.groupNotice);
        dest.writeInt(this.groupAttribute);
        dest.writeInt(this.memberCount);
        dest.writeString(this.mGroupAvaturls);
        dest.writeString(this.lastMsgTime);
        dest.writeString(this.lastMsg);
        dest.writeInt(this.msgType);
        dest.writeInt(this.msgAttribute);
        dest.writeString(this.qRCode);
    }

    protected GroupInfo(Parcel in) {
        this.unreadCount = in.readInt();
        this.guid = in.readString();
        this.groupName = in.readString();
        this.mainGroupName = in.readString();
        this.mainGroupGuid = in.readString();
        this.groupNotice = in.readString();
        this.groupAttribute = in.readInt();
        this.memberCount = in.readInt();
        this.mGroupAvaturls = in.readString();
        this.lastMsgTime = in.readString();
        this.lastMsg = in.readString();
        this.msgType = in.readInt();
        this.msgAttribute = in.readInt();
        this.qRCode = in.readString();
    }

    public static final Parcelable.Creator<GroupInfo> CREATOR = new Parcelable.Creator<GroupInfo>() {
        @Override
        public GroupInfo createFromParcel(Parcel source) {
            return new GroupInfo(source);
        }

        @Override
        public GroupInfo[] newArray(int size) {
            return new GroupInfo[size];
        }
    };
}
