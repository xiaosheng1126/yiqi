package com.hong.bo.shi.presenter;

import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.contract.ContactContract;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by andy on 2016/12/12.
 */

public class ContractPresenter extends RxPresenter implements ContactContract.Presenter {

    private ContactContract.View mView;
    private Realm mRealm;
    private RealmResults<UserInfo> mLocalContract;
    private RealmChangeListener<RealmResults<UserInfo>> mLocalContractlistener;
    private RealmResults<UserInfo> mClondContract;
    private RealmChangeListener<RealmResults<UserInfo>> mClondContractlistener;

    public ContractPresenter(ContactContract.View view) {
        mView = view;
        mView.setPresenter(this);
        init();
    }

    private void init(){
        mRealm = RealmHelper.getRealm();
        mLocalContract = RealmHelper.getLocalContract(mRealm);
        mLocalContractlistener = new RealmChangeListener<RealmResults<UserInfo>>() {
            @Override
            public void onChange(RealmResults<UserInfo> element) {
                getLocalList();
            }
        };
        mLocalContract.addChangeListener(mLocalContractlistener);
        getLocalList();
        mClondContract = RealmHelper.getClondContract(mRealm);
        mClondContractlistener = new RealmChangeListener<RealmResults<UserInfo>>() {
            @Override
            public void onChange(RealmResults<UserInfo> element) {
                getClondList();
            }
        };
        mClondContract.addChangeListener(mClondContractlistener);
        getClondList();
    }

    private void getLocalList(){
        if(mLocalContract.isEmpty())return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        List<UserInfo> list = mRealm.copyFromRealm(mLocalContract);
        for (UserInfo userInfo : list) {
            departmentBean = new DepartmentBean(userInfo.getDepartment());
            int indexOf = departmentList.indexOf(departmentBean);
            if(indexOf == -1){
                departmentBean.add(userInfo);
                departmentList.add(departmentBean);
            }else{
                departmentBean = departmentList.get(indexOf);
                departmentBean.add(userInfo);
            }
        }
        mView.updateLocal(departmentList);
    }

    private void getClondList(){
        if(mClondContract.isEmpty())return;
        List<DepartmentBean> departmentList = new ArrayList<DepartmentBean>();
        DepartmentBean departmentBean = null;
        List<UserInfo> list = mRealm.copyFromRealm(mClondContract);
        for (UserInfo userInfo : list) {
            departmentBean = new DepartmentBean(userInfo.getOrgname());
            int indexOf = departmentList.indexOf(departmentBean);
            if(indexOf == -1){
                departmentBean.add(userInfo);
                departmentList.add(departmentBean);
            }else{
                departmentBean = departmentList.get(indexOf);
                departmentBean.add(userInfo);
            }
        }
        mView.updateClond(departmentList);
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        if(mRealm != null && !mRealm.isClosed()){
            if(mLocalContract != null && mLocalContractlistener != null) {
                mLocalContract.removeChangeListener(mLocalContractlistener);
            }
            if(mClondContract != null && mClondContractlistener != null){
                mClondContract.removeChangeListener(mClondContractlistener);
            }
            mRealm.close();
        }
        mLocalContract = null;
        mLocalContractlistener = null;
        mClondContract = null;
        mClondContractlistener = null;
        mRealm = null;
    }
}
