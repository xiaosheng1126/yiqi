package com.hong.bo.shi.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.model.bean.WelecomBean;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.WelcomeContract;
import com.hong.bo.shi.utils.ParseUtils;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.RxUtil;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/8.
 */

public class WelecomPresenter extends RxPresenter implements WelcomeContract.Presenter {

    private WelcomeContract.View mView;

    public WelecomPresenter(@NonNull WelcomeContract.View oneView) {
        mView = oneView;
        mView.setPresenter(this);
        getWelecomData();
    }

    @Override
    public void getWelecomData() {
        if (isNetworkAvailable(1500)){
            addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
                @Override
                public String call(String s) {
                    return requestWelecome();
                }
            }, new Action1<String>() {
                @Override
                public void call(String result) {
                    if (!TextUtils.isEmpty(result) && !isError(result)) {
                        WelecomBean bean = new WelecomBean(result);
                        PreferencesUtils.setWelecomeData(result);
                        DownloadManager.getRequestQueue().downloadPic(bean.getFiringImg());
                        DownloadManager.getRequestQueue().downloadPic(bean.getNoHttpImg());
                        check(bean);
                    } else {
                        mView.showError("服务器数据错误");
                    }
                }
            }));
        }
    }

    @Override
    protected void postDelayedRun() {
        mView.showError(null);
    }

    @Override
    public void login(final String account, final String pass, final long endTime) {
        if (isNetworkAvailable(0)) {
            addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
                @Override
                public Boolean call(String s) {
                    return ParseUtils.requestLogin(account, pass);
                }
            }, new Action1<Boolean>() {
                @Override
                public void call(Boolean result) {
                    if (result) {
                        if (endTime > System.currentTimeMillis()) {
                            startCountDown(endTime - System.currentTimeMillis());
                        } else {
                            mView.toMain();
                        }
                    } else {
                        mView.showError(null);
                    }
                }
            }));
        }else{
            mView.showError(null);
        }
    }

    private void check(WelecomBean bean) {
        String account = PreferencesUtils.getAccount();
        String pass = PreferencesUtils.getPass();
        String guid = PreferencesUtils.getGuid();
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(guid)) {
            login(account, pass, System.currentTimeMillis() + bean.getFiringTime() * 1000);
        } else {
            startCountDown(bean.getFiringTime() * 1000);
        }
    }


    private void startCountDown(long time) {
        mView.dismissDialog();
        Subscription rxSubscription = Observable.timer(time, TimeUnit.MILLISECONDS)
                .compose(RxUtil.<Long>rxSchedulerHelper())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (!TextUtils.isEmpty(PreferencesUtils.getAccount()) &&
                                !TextUtils.isEmpty(PreferencesUtils.getPass())) {
                            mView.toMain();
                        } else {
                            mView.toLogin();
                        }
                    }
                });
        addSubscribe(rxSubscription);
    }

    private String requestWelecome() {
        try {
            return RetrofitHelper.get(RequestUrl.getType901Url());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
