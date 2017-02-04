package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.ChatContract;
import com.hong.bo.shi.utils.DateUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/14.
 */

public class ChatPresenter extends RxPresenter implements ChatContract.Presenter {

    private ChatContract.View mView;
    private Realm mRealm;
    private GroupInfo mGroupInfo;
    private RealmResults<GroupMessage> mResult;
    private RealmChangeListener<GroupInfo> mListener;
    private RealmChangeListener<RealmResults<GroupMessage>> mMessageListener;
    private int mGroupAttribute;
    private String mGroupGuid;
    public static final int DEFAULT_SIZE = 10;

    public ChatPresenter(ChatContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void query(String guid) {
        if (mGroupGuid != null) {
            unSubscribe();
        }
        mGroupGuid = guid;
        mRealm = RealmHelper.getRealm();
        mGroupInfo = RealmHelper.getGroupInfo(mRealm, guid);
        mListener = new RealmChangeListener<GroupInfo>() {
            @Override
            public void onChange(GroupInfo element) {
                if (element != null) {
                    mView.init(mGroupInfo);
                    mGroupAttribute = mGroupInfo.getGroupAttribute();
                }
            }
        };
        mGroupAttribute = mGroupInfo.getGroupAttribute();
        mGroupInfo.addChangeListener(mListener);
        mResult = RealmHelper.getGroupMessages(mRealm, guid);
        mRealm.beginTransaction();
        for (GroupMessage groupMessage : mResult) {
            if (groupMessage.getStatue() == 2) {
                groupMessage.setStatue(1);
            }
        }
        mRealm.commitTransaction();
        handleChange();
        mMessageListener = new RealmChangeListener<RealmResults<GroupMessage>>() {
            @Override
            public void onChange(RealmResults<GroupMessage> element) {
                handleChange();
            }
        };
        mResult.addChangeListener(mMessageListener);
        realTimeQuery();
        cleraGroupInfoUnReadCount();
    }

    private void handleChange() {
        GroupMessage first = mView.getFirst();
        int end = 0;
        if (first != null) {
            end = mResult.indexOf(first) + 1;
        }
        if (end == 0) {
            end = DEFAULT_SIZE;
        }
        List<GroupMessage> data = getData(0, end);
        mView.updateData(data, false);
    }

    public List<GroupMessage> getData(int start, int size) {
        int end = calcutionCount(start, size);
        List<GroupMessage> list = mRealm.copyFromRealm(mResult);
        List<GroupMessage> infos = list.subList(start, end);
        Collections.reverse(infos);
        return infos;
    }

    private int calcutionCount(int start, int size){
        int end = start + size;
        end = end > mResult.size() ? mResult.size() : end;
        return end;
    }

    @Override
    public void sendMessage(final GroupMessage message) {
        saveGroupMember(null, message);
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return requestSend(message);
            }
        }, new Action1<String>() {
            @Override
            public void call(String guid) {
                saveGroupMember(guid, message);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                message.setStatue(1);
                saveGroupMember(null, message);
                mView.showError(null);
            }
        }));
    }

    private void saveGroupMember(String newGuid, GroupMessage message) {
        if (!TextUtils.isEmpty(newGuid)) {
            RealmHelper.deleteMsg(mRealm, message.getGuid());
            message.setGuid(newGuid);
            DownloadManager.getRequestQueue().add(message);
        }
        RealmHelper.updateGroupMessage(mRealm, message);
    }

    @Override
    public void deleteMsg(final String guid) {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return requestDeleteMsg(guid);
            }
        }, new Action1<String>() {
            @Override
            public void call(String s) {
                if (guid != null) {
                    RealmHelper.deleteMsg(mRealm, guid);
                    mView.onSuccess();
                } else {
                    mView.onFailed();
                }
            }
        }));
    }

    @Override
    public void getMessage(final String guid) {
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                try {
                    String data = RetrofitHelper.get(RequestUrl.getType1024Url(guid));
                    if (isError(data)) {
                        return false;
                    }
                    String[] split = data.split(Constants.STRIP_SPLIT);
                    List<GroupMessage> list = new ArrayList<GroupMessage>();
                    for (String message : split) {
                        if(!Constants.NO_MSG.equals(message)) {
                            GroupMessage groupMessage = new GroupMessage(guid, message);
                            list.add(groupMessage);
                            if(groupMessage.getMsgType() == 3) {
                                DownloadManager.getRequestQueue().add(groupMessage);
                            }
                        }
                    }
                    if(list.size() > 0) {
                        RealmHelper.save(list);
                    }
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
            }
        }));
    }

    @Override
    public void realTimeQuery() {
        Subscription rxSubscription = Observable.interval(0, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        getMessage(mGroupGuid);
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void cleraGroupInfoUnReadCount() {
        mRealm.beginTransaction();
        mGroupInfo.setUnreadCount(0);
        mRealm.commitTransaction();
    }

    @Override
    public void loadMore(int start) {
        mView.updateData(getData(start, DEFAULT_SIZE), true);
    }

    private String requestDeleteMsg(String msgGuid) {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1016Url(msgGuid));
            if (isSuccess(string)) {
                return msgGuid;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String requestSend(GroupMessage message) {
        try {
            String string = RetrofitHelper.postMsg(
                    RequestUrl.getType1004Url(),
                    RequestHeader.getSendMsgHeader(message, mGroupAttribute), message);
            if (isError(string)) {
                message.setStatue(1);
                return null;
            }
            String[] split = string.split(Constants.SPLIT);
            if(!TextUtils.isEmpty(split[1]) && !Constants.NO_MSG.equals(split[1])){
                message.setMessage(split[1]);
            }
            if(!TextUtils.isEmpty(split[2]) && !Constants.NO_MSG.equals(split[2])){
                message.setTime(split[2]);
            }
            if(!TextUtils.isEmpty(split[3]) && !Constants.NO_MSG.equals(split[3])){
                message.setMsgTime(split[3]);
                message.setCreateTime(DateUtils.stringToLong(split[3]));
            }
            message.setStatue(0);
            return split[0];
        } catch (IOException e) {
            e.printStackTrace();
        }
        message.setStatue(1);
        return null;
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        if (mRealm != null && !mRealm.isClosed()) {
            if (mGroupInfo != null && mListener != null) {
                mGroupInfo.removeChangeListener(mListener);
            }
            if (mMessageListener != null) {
                mResult.removeChangeListener(mMessageListener);
            }
            mRealm.close();
        }
        mGroupInfo = null;
        mListener = null;
        mResult = null;
        mMessageListener = null;
        mRealm = null;
        mGroupGuid = null;
    }
}
