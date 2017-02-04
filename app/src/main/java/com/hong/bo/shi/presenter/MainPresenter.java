package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.presenter.contract.MainContract;
import com.hong.bo.shi.utils.ParseUtils;
import com.hong.bo.shi.utils.PreferencesUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/12.
 */

public class MainPresenter extends RxPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        mView = view;
        mView.setPresenter(this);
        getData_1020();
    }

    @Override
    public void realTimeQuery() {
        Subscription rxSubscription = Observable.interval(0, 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        queryData();
                    }
                });
        addSubscribe(rxSubscription);
    }

    @Override
    public void queryData() {
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return ParseUtils.requestType1021();
            }
        }, new Action1<String>() {
            @Override
            public void call(String result) {
                if (TextUtils.isEmpty(result)) {
                    mView.update();
                } else {
                    if (Constants.DROPPED.equals(result)) {
                        unSubscribe();
                        mView.kicked();
                    }
                }
            }
        }));
    }

    @Override
    public void getData_1020() {
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return ParseUtils.requestType1020();
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                if (result) {
                    mView.update();
                    realTimeQuery();
                }else{
                    getData_1020();
                }
            }
        }));
    }

    @Override
    public void login() {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                String account = PreferencesUtils.getAccount();
                String pass = PreferencesUtils.getPass();
                return ParseUtils.requestLogin(account, pass);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                if (result) {
//                    PreferencesUtils.clearMain();
                    getData_1020();
                    mView.loginSuccess();
                } else {
                    mView.loginFailed();
                }
                mView.dismissDialog();
            }
        }));
    }
}
