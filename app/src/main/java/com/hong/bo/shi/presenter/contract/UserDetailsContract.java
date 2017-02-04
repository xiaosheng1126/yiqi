package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;

/**
 * Created by andy on 2016/12/13.
 */

public class UserDetailsContract {

    public interface Presenter extends BasePresenter{
        void query(String guid);
        void createChat();
    }

    public interface View extends BaseView<Presenter>{
        void init(UserInfo userInfo);
        void update();
        void onSuccess(GroupInfo info);
        void setBackTitle(String title);
    }
}
