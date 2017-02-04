package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/19.
 */

public class EditPassContract {

    public interface Presenter extends BasePresenter{
        void changePassword(String newPassword);
    }

    public interface View extends BaseView<Presenter>{
        void onSuccess();
    }
}
