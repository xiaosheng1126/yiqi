package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/8.
 */

public class LoginContract {

    public interface Presenter extends BasePresenter{

        void getAvaturl(String account);

        void login(String account, String password);

    }

    public interface View extends BaseView<Presenter>{
        void showAvaturl(String url);

        void showMain();

        void registerSuccess(String account, String password);

        void forgetSuccess(String account, String password);
    }
}
