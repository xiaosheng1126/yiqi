package com.hong.bo.shi.presenter;

import android.support.annotation.NonNull;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.ForwardContract;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardPresenter extends RxPresenter implements ForwardContract.Presenter {

    private ForwardContract.View mView;
    private Map<String, String> mSelectGroup;
    private Map<String, String> mSelectPerson;
    private String msgGuid;

    public ForwardPresenter(ForwardContract.View mView) {
        this.mView = mView;
        mView.setPresenter(this);
        mSelectGroup = new HashMap<String, String>();
        mSelectPerson = new HashMap<String, String>();
        init();
    }

    private void init() {
        Realm realm = RealmHelper.getRealm();
        RealmResults<UserInfo> localContract = RealmHelper.getLocalContract(realm);
        RealmResults<UserInfo> clondContract = RealmHelper.getClondContract(realm);
        getLocalList(realm, localContract);
        getClondList(realm, clondContract);
        RealmResults<GroupInfo> groupInfos = realm.where(GroupInfo.class).notEqualTo("groupAttribute", 2).
                findAllSorted(Constants.Key.CREATE_TIME, Sort.DESCENDING);
        List<GroupInfo> list = realm.copyFromRealm(groupInfos);
        mView.updateGroup(list);
        if (realm != null && !realm.isClosed()) {
            realm.close();
        }
        realm = null;
    }

    @Override
    public boolean personIsSelect(String guid) {
        return mSelectPerson.containsKey(guid);
    }

    @Override
    public boolean groupIsSelect(String guid) {
        return mSelectGroup.containsKey(guid);
    }

    @Override
    public void operationPerson(String guid) {
        if(mSelectPerson.containsKey(guid)){
            mSelectPerson.remove(guid);
        }else{
            mSelectPerson.put(guid, guid);
        }
        mView.updatePerson();
    }

    @Override
    public void operationGroup(String guid) {
        if(mSelectGroup.containsKey(guid)){
            mSelectGroup.remove(guid);
        }else{
            mSelectGroup.put(guid, guid);
        }
        mView.updateGroup();
    }

    @Override
    public int getForwardCount() {
        return mSelectGroup.size() + mSelectPerson.size();
    }

    @Override
    public void forward() {
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return requestForward();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                if(result){
                    mView.onSuccess();
                }else{
                    mView.showError(null);
                }
            }
        }));
    }

    @Override
    public void setMsgGuid(String guid) {
        this.msgGuid = guid;
        init();
    }

    @NonNull
    private Boolean requestForward() {
        StringBuilder builder = new StringBuilder();
        if(mSelectGroup.isEmpty()){
            builder.append(Constants.NO_MSG);
        }else{
            for (Map.Entry<String, String> entry : mSelectGroup.entrySet()) {
                builder.append(entry.getKey()).append(Constants.SPLIT);
            }
            builder.delete(builder.length() - Constants.SPLIT.length(),
                    builder.length());
        }
        builder.append(Constants.STRIP_SPLIT);
        if(mSelectPerson.isEmpty()){
            builder.append(Constants.NO_MSG);
        }else{
            for (Map.Entry<String, String> entry : mSelectPerson.entrySet()) {
                builder.append(entry.getKey()).append(Constants.SPLIT);
            }
            builder.delete(builder.length() - Constants.SPLIT.length(),
                    builder.length());
        }
        try {
            String result = RetrofitHelper.get(RequestUrl.getType1032Url(msgGuid,
                    builder.toString()));
            return isSuccess(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getLocalList(Realm realm, List<UserInfo> list) {
        if (list.isEmpty()) return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        for (UserInfo userInfo : list) {
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

    private void getClondList(Realm realm, List<UserInfo> list) {
        if (list.isEmpty()) return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        for (UserInfo userInfo : list) {
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
}
