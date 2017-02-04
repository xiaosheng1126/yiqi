package com.hong.bo.shi.presenter;

import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.presenter.contract.ForwardContract;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardPresenter extends RxPresenter implements ForwardContract.Presenter {

    private ForwardContract.View mView;

    public ForwardPresenter(ForwardContract.View mView) {
        this.mView = mView;
    }

}
