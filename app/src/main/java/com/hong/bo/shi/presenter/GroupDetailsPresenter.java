package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMember;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.GroupDetailsContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/14.
 */

public class GroupDetailsPresenter extends RxPresenter implements GroupDetailsContract.Presenter {

    private GroupDetailsContract.View mView;
    private Realm mRealm;
    private GroupInfo mGroupInfo;
    private RealmChangeListener<GroupInfo> mGroupInfoListener;
    private String mGroupGuid;
    private GroupMember mGroupMembers;
    private RealmChangeListener<GroupMember> mGroupMemberListener;

    public GroupDetailsPresenter(GroupDetailsContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void query(String guid) {
        mGroupGuid = guid;
        mRealm = RealmHelper.getRealm();
        mGroupInfo = RealmHelper.getGroupInfo(mRealm, guid);
        mGroupInfoListener = new RealmChangeListener<GroupInfo>() {
            @Override
            public void onChange(GroupInfo element) {
                if (element != null) {
                    mView.init(element);
                }
            }
        };
        mGroupInfo.addChangeListener(mGroupInfoListener);
        setGroupMemberListener();
        getGroupMembers(guid);
        if (mGroupMembers == null) {
            mView.showLoading();
        }
    }

    private void setGroupMemberListener() {
        mGroupMembers = RealmHelper.getGroupMember(mRealm, mGroupGuid);
        if (mGroupMembers != null) {
            mGroupMemberListener = new RealmChangeListener<GroupMember>() {
                @Override
                public void onChange(GroupMember element) {
                    if (element != null) {
                        update();
                    }
                }
            };
            mGroupMembers.addChangeListener(mGroupMemberListener);
            update();
        }
    }

    @Override
    public void exit() {
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return request();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    mRealm.beginTransaction();
                    mGroupInfo.deleteFromRealm();
                    mRealm.commitTransaction();
                    mView.onSucess();
                } else {
                    mView.showError("退群失败");
                }
            }
        }));
    }

    @Override
    public void getGroupMembers(final String groupGuid) {
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                try {
                    String string = RetrofitHelper.get(RequestUrl.getType1026Url(mGroupGuid));
                    if (isError(string)) {
                        return null;
                    }
                    if (Constants.NO_MSG.equals(string)) {
                        return null;
                    }
                    return string;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new Action1<String>() {
            @Override
            public void call(String result) {
                if (!TextUtils.isEmpty(result)) {
                    if (mGroupMembers == null) {
                        GroupMember groupMembers = new GroupMember(groupGuid, result);
                        mRealm.beginTransaction();
                        mRealm.copyToRealmOrUpdate(groupMembers);
                        mRealm.commitTransaction();
                        setGroupMemberListener();
                    } else {
                        mRealm.beginTransaction();
                        mGroupMembers.setGroupMemberGuids(result);
                        mRealm.commitTransaction();
                    }
                } else {
                    mView.showError("群成员信息获取失败");
                }
                mView.dismissDialog();
            }
        }));
    }

    @Override
    public void getAllMessage() {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return requestGetAllMessage();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                mView.showError(result ? "消息同步成功" : "消息同步失败");
                mView.dismissDialog();
            }
        }));
    }

    private Boolean requestGetAllMessage(){
        try {
            String data = RetrofitHelper.get(RequestUrl.getType1025Url(mGroupGuid));
            if(Constants.NO_MSG.equals(data.trim())){
                return false;
            }
            String[] split = data.split(Constants.STRIP_SPLIT);
            List<GroupMessage> list = new ArrayList<GroupMessage>();
            for (String message : split) {
                GroupMessage groupMessage = new GroupMessage(mGroupGuid, message);
                list.add(groupMessage);
                if(groupMessage.getMsgType() == 3) {
                    DownloadManager.getRequestQueue().add(groupMessage);
                }
            }
            RealmHelper.updateMessage(list);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void getData_1030() {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return request_1030();
            }
        }, new Action1<String>() {
            @Override
            public void call(String result) {
                if(TextUtils.isEmpty(result) || isError(result)){
                    mView.showError("诊断数据请求失败");
                }else{
                    String[] split = result.split(Constants.CLASSIF_SPLIT);
                    String[] data = split[0].split(Constants.SPLIT);
                    WebBean bean = new WebBean(data[0], "群详情", "", 0);
                    bean.setUrl(data[1]);
                    bean.setOther(split[1]);
                    mView.toWebView(bean);
                }
                mView.dismissDialog();
            }
        }));
    }

    private String request_1030(){
        try {
            String data = RetrofitHelper.get(RequestUrl.getType1030Url(mGroupGuid));
            if(Constants.NO_MSG.equals(data.trim())){
                return data;
            }
           return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void update() {
        List<UserInfo> list = new ArrayList<>();
        String memberGuids = mRealm.copyFromRealm(mGroupMembers).getGroupMemberGuids();
        String[] split = memberGuids.split(Constants.SPLIT);
        for (String s : split) {
            UserInfo first = mRealm.where(UserInfo.class).equalTo(Constants.Key.GUID, s).findFirst();
            if (first != null) {
                list.add(mRealm.copyFromRealm(first));
            }
        }
        list.add(new UserInfo());
        mView.update(list);
    }

    private boolean request() {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1017Url(mGroupGuid));
            return isSuccess(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        if (mRealm != null && !mRealm.isClosed()) {
            if (mGroupMembers != null && mGroupMemberListener != null) {
                mGroupMembers.removeChangeListener(mGroupMemberListener);
            }
            if (mGroupInfo != null && mGroupInfoListener != null) {
                mGroupInfo.removeChangeListener(mGroupInfoListener);
            }
            mRealm.close();
        }
        mGroupMembers = null;
        mGroupMemberListener = null;
        mGroupInfoListener = null;
        mGroupInfo = null;
    }
}
