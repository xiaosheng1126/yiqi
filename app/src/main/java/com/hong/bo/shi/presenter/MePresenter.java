package com.hong.bo.shi.presenter;

import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.presenter.contract.MeContract;

/**
 * Created by andy on 2016/12/12.
 */

public class MePresenter extends RxPresenter implements MeContract.Presenter {

    private MeContract.View mView;

    public MePresenter(MeContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void update() {
        mView.updateView();
    }
}
