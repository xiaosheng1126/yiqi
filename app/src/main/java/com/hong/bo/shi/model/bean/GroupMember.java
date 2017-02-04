package com.hong.bo.shi.model.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andy on 2017/1/3.
 * 保存群所有成员对象
 */
public class GroupMember extends RealmObject{

    @PrimaryKey
    private String groupGuid;//群guid
    private String groupMemberGuids;//群成员所有guid用分隔符隔开

    public String getGroupGuid() {
        return groupGuid;
    }

    public void setGroupGuid(String groupGuid) {
        this.groupGuid = groupGuid;
    }

    public String getGroupMemberGuids() {
        return groupMemberGuids;
    }

    public void setGroupMemberGuids(String groupMemberGuids) {
        this.groupMemberGuids = groupMemberGuids;
    }

    public GroupMember(String groupGuid, String groupMemberGuids) {
        this.groupGuid = groupGuid;
        this.groupMemberGuids = groupMemberGuids;
    }

    public GroupMember() {
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "groupGuid='" + groupGuid + '\'' +
                ", groupMemberGuids='" + groupMemberGuids + '\'' +
                '}';
    }
}
