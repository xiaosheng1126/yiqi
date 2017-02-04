package com.hong.bo.shi.presenter;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.app.SubscriptionUtils;
import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.presenter.contract.EditUserInfoContract;

import java.io.IOException;

import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/19.
 */

public class EditUserInfoPresenter extends RxPresenter implements EditUserInfoContract.Presenter {

    private EditUserInfoContract.View mView;
    private static final int TYPE_USER_NAME = 0;
    private static final int TYPE_USER_MOBILE = 1;
    private static final int TYPE_USER_GENDER = 2;

    public EditUserInfoPresenter(EditUserInfoContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    private boolean requestEdit(int type, String text) {
        try {
            String header = null;
            switch (type) {
                case TYPE_USER_NAME:
                    header = RequestHeader.getEditUserNameHeader(text);
                    break;
                case TYPE_USER_MOBILE:
                    header = RequestHeader.getEditMobileHeader(text);
                    break;
                case TYPE_USER_GENDER:
                    header = RequestHeader.getEditGenderHeader(text);
                    break;
            }
            String string = RetrofitHelper.edit(RequestUrl.getType1022Url(),
                    header, null);
            return isSuccess(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean requestEditGroup(String groupGuid, String groupName) {
        try {
            String string = RetrofitHelper.edit(RequestUrl.getType1022Url(),
                    RequestHeader.getEditGroupNameHeader(groupGuid, groupName), null);
            return isSuccess(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void editUserInfo(final int type, final String text) {
        mView.showLoading();
        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return requestEdit(type, text);
            }
        }, new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    UserInfo userInfo = App.getInstance().getUserInfo();
                    if (type == TYPE_USER_NAME) {
                        userInfo.setName(text);
                    }
                    if (type == TYPE_USER_GENDER) {
                        userInfo.setGender(Integer.valueOf(text));
                    }
                    if (type == TYPE_USER_MOBILE) {
                        userInfo.setMobile(text);
                    }
                    RealmHelper.updateUserInfo(userInfo);
                    mView.onSuccess();
                } else {
                    mView.onFailed();
                }
            }
        }));
    }
//
//    @Override
//    public void editGroupName(final String groupGuid, final String groupName) {
//        mView.showLoading();
//        addSubscribe(SubscriptionUtils.getB(new Func1<String, Boolean>() {
//            @Override
//            public Boolean call(String s) {
//                return requestEditGroup(groupGuid, groupName);
//            }
//        }, new Action1<Boolean>() {
//            @Override
//            public void call(Boolean aBoolean) {
//                if (aBoolean) {
//                    RealmHelper.editGroupInfo(groupGuid, groupName);
//                    mView.onSuccess();
//                } else {
//                    mView.onFailed();
//                }
//            }
//        }));
//    }
}
