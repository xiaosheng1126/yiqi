package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/8.
 */

public class WelcomeContract {

    public interface Presenter extends BasePresenter {

        void getWelecomData();

        void login(String account, String password, long endTime);
    }

    public interface View extends BaseView<Presenter>{

        void toLogin();

        void toMain();

    }
}
