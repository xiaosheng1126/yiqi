package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2016/12/12.
 */

public class MeContract {

    public interface Presenter extends BasePresenter {
        void update();
    }

    public interface View extends BaseView<Presenter> {
        void updateView();
    }
}
