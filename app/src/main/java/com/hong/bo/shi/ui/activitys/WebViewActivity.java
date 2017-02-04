package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.presenter.WebViewPresenter;
import com.hong.bo.shi.presenter.contract.WebViewContract;

public class WebViewActivity extends BaseMvpActivity<WebViewContract.Presenter, WebViewContract.View> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebBean bean = getIntent().getParcelableExtra(WebBean.class.getSimpleName());
        mView.initData(bean);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected WebViewContract.View createView() {
        return findViewByIds(R.id.webview_ui);
    }

    @Override
    protected WebViewContract.Presenter createPresenter() {
        return new WebViewPresenter(mView);
    }

    @Override
    public void onLeftClick(View view) {
        if (mView.isBack()) {
            super.onLeftClick(view);
        }
    }
}
