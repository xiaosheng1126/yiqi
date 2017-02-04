package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.WebBean;

/**
 * Created by andy on 2016/12/12.
 */

public class WebViewContract {

    public interface Presenter extends BasePresenter {
    }
    public interface View extends BaseView<Presenter>{
        void initData(WebBean bean);
        void updateTitle(WebBean bean);
        boolean isBack();
    }
}
