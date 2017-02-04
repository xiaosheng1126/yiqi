package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/19.
 */

public class EditUserInfoContract {

    public interface Presenter extends BasePresenter {
        void editUserInfo(int type, String name);
    }

    public interface View extends BaseView<Presenter>{
        void setType(int type);//0 修改用户名 1 修改手机号 2 修改性别
        String getContent();
        int getGender();
        void onSuccess();
        void onFailed();
    }
}
