package com.hong.bo.shi.presenter;

import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.presenter.contract.BusinessContract;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessPresenter extends RxPresenter implements BusinessContract.Presenter {

    private BusinessContract.View mView;

    public BusinessPresenter(BusinessContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }
}
