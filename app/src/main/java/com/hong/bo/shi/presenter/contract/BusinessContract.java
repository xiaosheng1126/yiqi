package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessContract {

    public interface Presenter extends BasePresenter {
    }

    public interface View extends BaseView<Presenter> {
        void update();
        void handleScanResult(String result);
        void onResume();
    }
}
