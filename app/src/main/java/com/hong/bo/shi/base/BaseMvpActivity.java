package com.hong.bo.shi.base;

import android.os.Bundle;

/**
 * Created by andy on 2017/1/21.
 */
public abstract class BaseMvpActivity<T extends BasePresenter,P extends BaseView> extends BaseActivity{

    protected T mPresenter;
    protected P mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = createView();
        mPresenter = createPresenter();
    }

    protected abstract P createView();
    protected abstract T createPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mView != null){
            mView.onDestory();
        }
    }
}
