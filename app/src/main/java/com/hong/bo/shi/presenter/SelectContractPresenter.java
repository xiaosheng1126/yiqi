package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMember;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.SelectContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/16.
 */

public class SelectContractPresenter extends RxPresenter implements SelectContract.Presenter {

    private SelectContract.View mView;
    private Map<String, String> mSelectUsers = new HashMap<>();
    private String mGroupGuid;

    public SelectContractPresenter(SelectContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    private void init(Map<String, String> map, Realm realm) {
        RealmResults<UserInfo> localContract = RealmHelper.getLocalContract(realm);
        RealmResults<UserInfo> clondContract = RealmHelper.getClondContract(realm);
        getLocalList(realm, localContract, map);
        getClondList(realm, clondContract, map);
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        realm = null;
        map = null;
    }

    private void getLocalList(Realm realm, List<UserInfo> list, Map<String, String> exits) {
        if (list.isEmpty()) return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        for (UserInfo userInfo : list) {
            if(exits.containsKey(userInfo.getGuid()))continue;
            departmentBean = new DepartmentBean(userInfo.getDepartment());
            int indexOf = departmentList.indexOf(departmentBean);
            if (indexOf == -1) {
                departmentBean.add(realm.copyFromRealm(userInfo));
                departmentList.add(departmentBean);
            } else {
                departmentBean = departmentList.get(indexOf);
                departmentBean.add(userInfo);
            }
        }
        mView.updateLocal(departmentList);
    }

    private void getClondList(Realm realm, List<UserInfo> list, Map<String, String> exits) {
        if (list.isEmpty()) return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        for (UserInfo userInfo : list) {
            if(exits.containsKey(userInfo.getGuid()))continue;
            departmentBean = new DepartmentBean(userInfo.getOrgname());
            int indexOf = departmentList.indexOf(departmentBean);
            if (indexOf == -1) {
                departmentBean.add(realm.copyFromRealm(userInfo));
                departmentList.add(departmentBean);
            } else {
                departmentBean = departmentList.get(indexOf);
                departmentBean.add(userInfo);
            }
        }
        mView.updateClond(departmentList);
    }

    @Override
    public void operation(String guid) {
        if (isSelect(guid)) {
            mSelectUsers.remove(guid);
        }else {
            mSelectUsers.put(guid, guid);
        }
        mView.update();
    }

    @Override
    public boolean isSelect(String guid) {
        return mSelectUsers.get(guid) != null;
    }

    @Override
    public void setExitUsres(String groupGuid) {
        this.mGroupGuid = groupGuid;
        String guid = App.getInstance().getUserInfo().getGuid();
        Map<String, String> exits = new HashMap<>();
        exits.put(guid, guid);
        Realm realm = RealmHelper.getRealm();
        if(!TextUtils.isEmpty(mGroupGuid)) {
            GroupMember first = realm.where(GroupMember.class).
                    equalTo(Constants.Key.GROUP_GUID, groupGuid).findFirst();
            GroupMember member = realm.copyFromRealm(first);
            String[] split = member.getGroupMemberGuids().split(Constants.SPLIT);
            for (String s : split) {
                exits.put(s, s);
            }
        }
        init(exits, realm);
    }

    @Override
    public int selectCount() {
        return mSelectUsers.size();
    }

    @Override
    public void operation() {
        if (TextUtils.isEmpty(mGroupGuid)) {
            createGroup();
        } else {
            inviteGroupMembers();
        }
    }

    @Override
    public void createGroup() {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return requestCreate();
            }
        }, new Action1<String>() {
            @Override
            public void call(String result) {
                if (!TextUtils.isEmpty(result)) {
                    String[] split = result.split(Constants.STRIP_SPLIT);
                    GroupInfo groupInfo = new GroupInfo(split[0]);
                    RealmHelper.save(groupInfo);
                    GroupMessage message = new GroupMessage(groupInfo.getGuid(), split[1]);
                    RealmHelper.save(message);
                    mView.onSuccess(groupInfo);
                } else {
                    mView.onFailed();
                }
            }
        }));
    }

    @Override
    public void inviteGroupMembers() {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return requestInvite();
            }
        }, new Action1<String>() {
            @Override
            public void call(String result) {
                if (!TextUtils.isEmpty(result)) {
                    String[] split = result.split(Constants.STRIP_SPLIT);
                    GroupInfo groupInfo = new GroupInfo(split[0]);
                    RealmHelper.save(groupInfo);
                    GroupMessage message = new GroupMessage(groupInfo.getGuid(), split[1]);
                    RealmHelper.save(message);
                    mView.onSuccess(groupInfo);
                } else {
                    mView.onFailed();
                }
            }
        }));
    }

    @Override
    public void setSelect(String guids) {
        mSelectUsers.clear();
        if(!TextUtils.isEmpty(guids)) {
            String[] split = guids.split(Constants.SPLIT);
            for (String s : split) {
                if(!TextUtils.isEmpty(s)) {
                    mSelectUsers.put(s, s);
                }
            }
        }
        mView.update();
    }

    @Override
    public String getSelectGuids() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : mSelectUsers.entrySet()) {
            builder.append(entry.getKey()).append(Constants.SPLIT);
        }
        return builder.toString();
    }

    @Override
    public String getGroupGuid() {
        return mGroupGuid;
    }

    @Override
    public Map<String, String> getSelectUsers() {
        return mSelectUsers;
    }

    private String requestCreate() {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1012Url(mSelectUsers));
            if (isError(string)) {
                return null;
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String requestInvite() {
        try {
            String string = RetrofitHelper.
                    get(RequestUrl.getType1012Url(mSelectUsers, mGroupGuid));
            if (isError(string)) {
                return null;
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        mSelectUsers.clear();
        mSelectUsers = null;
    }
}
