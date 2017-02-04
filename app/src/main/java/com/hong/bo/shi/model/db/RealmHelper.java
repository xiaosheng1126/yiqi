package com.hong.bo.shi.model.db;


import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMember;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.HttpBean;
import com.hong.bo.shi.model.bean.UserInfo;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Description: RealmHelper
 * Creator: yxc
 * date: 2016/9/21 17:46
 */

public class RealmHelper {

    private RealmHelper() {
    }

    public static void saveGroupsList(List<GroupInfo> list, boolean isDelete) {
        Realm realm = getRealm();
        saveGroupsList(realm, list, isDelete);
    }

    public static void saveGroupsList(Realm realm, List<GroupInfo> list, boolean isDelete) {
        RealmResults<GroupInfo> groupInfos = realm.where(GroupInfo.class).findAll();
        realm.beginTransaction();
        if (isDelete) {
            groupInfos.deleteAllFromRealm();
        }
        if (list != null && list.size() > 0) {
            realm.copyToRealmOrUpdate(list);
        }
        realm.commitTransaction();
        close(realm);
    }

    public static Realm getRealm() {
        return Realm.getDefaultInstance();
    }

    public static void save(RealmObject object) {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(object);
        realm.commitTransaction();
        close(realm);
    }


    public static void save(Iterable objects) {
        Realm realm = getRealm();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(objects);
        realm.commitTransaction();
        close(realm);
    }

    public static void updateMessage(List<GroupMessage> list) {
        Realm realm = getRealm();
        realm.beginTransaction();
        for (GroupMessage message : list) {
            GroupMessage first = realm.where(GroupMessage.class).
                    equalTo(Constants.Key.GUID, message.getGuid())
                    .findFirst();
            if (first == null) {
                DownloadManager.getRequestQueue().add(message);
                realm.copyToRealm(message);
            }

        }
        realm.commitTransaction();
        close(realm);
    }

    public static HttpBean getHttpBean(String type) {
        Realm realm = getRealm();
        HttpBean httpBean = realm.where(HttpBean.class).equalTo("type", type).findFirst();
        HttpBean bean = realm.copyFromRealm(httpBean);
        close(realm);
        return bean;
    }

    public static void close(Realm realm) {
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        realm = null;
    }

    public static GroupInfo getGroupInfo(Realm realm, String groupGuid) {
        return realm.where(GroupInfo.class).equalTo(Constants.Key.GUID, groupGuid).findFirst();
    }

    public static GroupInfo getGroupInfo(String groupGuid) {
        Realm realm = getRealm();
        GroupInfo groupInfo = getGroupInfo(realm, groupGuid);
        GroupInfo info = null;
        if (groupInfo != null) {
            info = realm.copyFromRealm(groupInfo);
        }
        close(realm);
        return info;
    }

    public static void editGroupInfo(String groupGuid, String groupName) {
        Realm realm = getRealm();
        GroupInfo first = realm.where(GroupInfo.class).equalTo(Constants.Key.GUID, groupGuid).findFirst();
        if (first != null) {
            realm.beginTransaction();
            first.setGroupName(groupName);
            realm.commitTransaction();
        }
        realm.close();
    }

    public static void updateUserInfo(UserInfo userInfo) {
        Realm realm = getRealm();
        UserInfo first = realm.where(UserInfo.class).equalTo(Constants.Key.GUID, userInfo.getGuid()).findFirst();
        if (first != null) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(userInfo);
            realm.commitTransaction();
        }
        realm.close();
    }

    public static void deleteMsg(Realm realm, String guid) {
        GroupMessage first = realm.where(GroupMessage.class).equalTo(Constants.Key.GUID, guid).findFirst();
        if (first != null) {
            realm.beginTransaction();
            first.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    public static void updateGroupMessage(Realm realm, GroupMessage message) {
        GroupInfo first = realm.where(GroupInfo.class).equalTo(Constants.Key.GUID, message.getGroupGuid()).findFirst();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(message);
        if (first != null) {
            first.setLastMsg(message.getMessage());
            first.setLastMsgTime(message.getMsgTime());
            first.setCreateTime(message.getCreateTime());
            first.setMsgAttribute(message.getMsgAttribute());
            first.setMsgType(message.getMsgType());
        }
        realm.commitTransaction();
    }

    public static RealmResults<GroupMessage> getGroupMessages(Realm realm, String groupGuid) {
        return realm.where(GroupMessage.class).equalTo(Constants.Key.GROUP_GUID, groupGuid).findAllSorted(Constants.Key.CREATE_TIME, Sort.DESCENDING);
    }

    public static List<GroupMessage> getChatPics(String groupGuid){
        Realm realm = getRealm();
        RealmResults<GroupMessage> sorted = realm.where(GroupMessage.class).equalTo(Constants.Key.GROUP_GUID, groupGuid).
                equalTo("msgType", 1).findAllSorted(Constants.Key.CREATE_TIME, Sort.ASCENDING);
        List<GroupMessage> list = realm.copyFromRealm(sorted);
        close(realm);
        return list;
    }

    public static void downloadFileSuccess(String guid, String path) {
        Realm realm = getRealm();
        GroupMessage first = realm.where(GroupMessage.class).equalTo(Constants.Key.GUID, guid).findFirst();
        if(first != null){
            realm.beginTransaction();
            first.setFilePath(path);
            realm.commitTransaction();
        }
        close(realm);
    }

    public static GroupMember getGroupMember(Realm realm, String groupGuid) {
        return realm.where(GroupMember.class).equalTo(Constants.Key.GROUP_GUID, groupGuid).
                findFirst();
    }

    public static GroupMember getGroupMember(String groupGuid) {
        Realm realm = getRealm();
        GroupMember first = realm.where(GroupMember.class).equalTo(Constants.Key.GROUP_GUID, groupGuid).
                findFirst();
        GroupMember member = null;
        if (first != null) {
            member = realm.copyFromRealm(first);
        }
        close(realm);
        return member;
    }

    public static UserInfo getUserInfo(String guid) {
        Realm realm = getRealm();
        UserInfo first = realm.where(UserInfo.class).
                equalTo(Constants.Key.GUID, guid).findFirst();
        UserInfo userInfo = realm.copyFromRealm(first);
        realm.close();
        return userInfo;
    }

    public static RealmResults<UserInfo> getLocalContract(Realm realm) {
        return realm.where(UserInfo.class)
                .equalTo(Constants.Key.IS_COMPANY, 1)
                .findAll();
    }

    public static RealmResults<UserInfo> getClondContract(Realm realm) {
        return realm.where(UserInfo.class)
                .equalTo(Constants.Key.IS_COMPANY, 0)
                .findAll();
    }

    public static void updateContract(List<UserInfo> localContracts,
                                      List<UserInfo> clondContracts) {
        Realm realm = getRealm();
        if (localContracts != null && !localContracts.isEmpty()) {
            RealmResults<UserInfo> userInfos = getLocalContract(realm);
            if (userInfos.size() > 0) {
                realm.beginTransaction();
                for (UserInfo userInfo : userInfos) {
                    userInfo.setIsCompany(2);
                }
                realm.commitTransaction();
            }
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(localContracts);
            realm.commitTransaction();
        }

        if (clondContracts != null && !clondContracts.isEmpty()) {
            RealmResults<UserInfo> userInfos = getClondContract(realm);
            if (userInfos.size() > 0) {
                realm.beginTransaction();
                for (UserInfo userInfo : userInfos) {
                    userInfo.setIsCompany(2);
                }
                realm.commitTransaction();
            }
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(clondContracts);
            realm.commitTransaction();
        }
    }


}
