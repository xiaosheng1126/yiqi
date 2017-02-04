package com.hong.bo.shi.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.LoginContract;
import com.hong.bo.shi.utils.ParseUtils;

import java.io.IOException;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/9.
 */

public class LoginPresenter extends RxPresenter implements LoginContract.Presenter {

    private LoginContract.View mView;

    public LoginPresenter(@NonNull LoginContract.View oneView) {
        mView = oneView;
        mView.setPresenter(this);
    }

    @Override
    public void getAvaturl(final String account) {
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                return requestGetAvaturl(account);
            }
        }, new Action1<String>() {
            @Override
            public void call(String s) {
                if(!TextUtils.isEmpty(s) && !isError(s)){
                    mView.showAvaturl(s);
                }
                KLog.d(s);
            }
        }));
    }

    @Override
    public void login(final String account, final String password) {
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return ParseUtils.requestLogin(account, password);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean result) {
                if(result) {
                    mView.showMain();
                }else{
                    mView.showError("用户名密码不匹配");
                }
            }
        }));
    }


    /**
     * 获取用户头像请求
     * @param account
     * @return
     */
    private String requestGetAvaturl(String account){
        try {
            String string = RetrofitHelper.get(RequestUrl.getType902Url(account));
            if(isError(string)){
                return null;
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
