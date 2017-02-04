package com.hong.bo.shi.presenter;

import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.EditPassContract;

import java.io.IOException;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/19.
 */

public class EditPassPresenter extends RxPresenter implements EditPassContract.Presenter {

    private EditPassContract.View mView;

    public EditPassPresenter(EditPassContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void changePassword(final String newPassword) {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
               return request(newPassword);
            }
        }, new Action1<String>() {
            @Override
            public void call(String pass) {
                if(pass != null){
                    mView.onSuccess();
                }else{
                    mView.showError(null);
                }
            }
        }));
    }

    private String request(String password){
        try {
            String string = RetrofitHelper.edit(RequestUrl.getType1022Url(), RequestHeader.getEditPassHeader(password), null);
            if(isSuccess(string)){
                return password;
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
