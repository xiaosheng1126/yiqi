package com.hong.bo.shi.presenter;

import android.text.TextUtils;

import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.EditGroupInfoContract;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2017/1/22.
 */

public class EditGroupInfoPresenter extends RxPresenter implements EditGroupInfoContract.Presenter {

    EditGroupInfoContract.View mView;

    public EditGroupInfoPresenter(EditGroupInfoContract.View mView) {
        this.mView = mView;
    }

    @Override
    public void editGroupName(String guid, String groupName) {
        try {
            String header = RequestHeader.getEditGroupNameHeader(guid, groupName);
            editGroup(header);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editGroupPublic(String guid, String publics) {
        try {
            String header = RequestHeader.getEditGroupPublicHeader(guid, publics);
            editGroup(header);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void editGroup(final String head){
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getS(new Func1<String, String>() {
            @Override
            public String call(String s) {
                try {
                    return RetrofitHelper.edit(RequestUrl.getType1023Url(), head, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }, new Action1<String>() {
            @Override
            public void call(String s) {
                if(isError(s) || TextUtils.isEmpty(s)){
                    mView.showError("修改失败");
                }else{
                    mView.onSuccess();
                }
            }
        }));
    }
}
