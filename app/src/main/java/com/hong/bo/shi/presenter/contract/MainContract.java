package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/11.
 */

public class MainContract {

    public interface Presenter extends BasePresenter{

        /**实时查询数据*/
        void realTimeQuery();

        /**查询数据*/
        void queryData();

        /**获取1020接口的数据*/
        void getData_1020();

        void login();
    }

    public interface View extends BaseView<Presenter>{
        void update();
        void showIndex(int index, String result);
        void kicked();
        void loginSuccess();
        void loginFailed();
    }
}
