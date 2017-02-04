package com.hong.bo.shi.presenter;

import com.hong.bo.shi.base.RxPresenter;
import com.hong.bo.shi.presenter.contract.WebViewContract;

/**
 * Created by andy on 2016/12/12.
 */

public class WebViewPresenter extends RxPresenter implements WebViewContract.Presenter {

    private WebViewContract.View mView;

    public WebViewPresenter(WebViewContract.View view) {
        mView = view;
        mView.setPresenter(this);
    }

}
