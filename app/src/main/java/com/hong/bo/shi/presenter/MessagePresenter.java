package com.hong.bo.shi.presenter;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.contract.MessageContract;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by andy on 2016/12/12.
 */

public class MessagePresenter extends RxPresenter implements MessageContract.Presenter {

    private MessageContract.View mView;
    private Realm mRealm;
    private RealmResults<GroupInfo> mResult;
    private RealmChangeListener<RealmResults<GroupInfo>> mListener;

    public MessagePresenter(MessageContract.View view) {
        mView = view;
        mView.setPresenter(this);
        init();
    }

    private void init(){
        mRealm = RealmHelper.getRealm();
        mResult = mRealm.where(GroupInfo.class).findAllSorted(Constants.Key.CREATE_TIME, Sort.DESCENDING);
        mListener = new RealmChangeListener<RealmResults<GroupInfo>>() {
            @Override
            public void onChange(RealmResults<GroupInfo> element) {
                mView.update(getGroupInfos());
            }
        };
        mResult.addChangeListener(mListener);
        mView.update(getGroupInfos());
    }

    private List<GroupInfo> getGroupInfos(){
        return mRealm.copyFromRealm(mResult);
    }

    @Override
    protected void unSubscribe() {
        super.unSubscribe();
        if(mRealm != null && !mRealm.isClosed()){
            if(mListener != null){
                mResult.removeChangeListener(mListener);
            }
            mRealm.close();
        }
        mResult = null;
        mListener = null;
        mRealm = null;
    }
}
