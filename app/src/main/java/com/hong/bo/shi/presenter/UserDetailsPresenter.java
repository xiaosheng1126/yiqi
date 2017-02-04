package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.UserDetailsContract;

import java.io.IOException;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/13.
 */

public class UserDetailsPresenter extends RxPresenter implements UserDetailsContract.Presenter {

    private UserDetailsContract.View mView;
    private String mGuid;

    public UserDetailsPresenter(UserDetailsContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void query(String guid) {
        mGuid = guid;
        UserInfo userInfo = RealmHelper.getUserInfo(guid);
        mView.init(userInfo);
    }

    @Override
    public void createChat() {
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
                    mView.showError("创建会话失败");
                }
            }
        }));
    }

    private String requestCreate() {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1012Url(mGuid));
            if (isError(string)) {
                return null;
            }
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
