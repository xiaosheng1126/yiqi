package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.ForwardPresenter;
import com.hong.bo.shi.presenter.contract.ForwardContract;

public class ForwardActivity extends BaseMvpActivity<ForwardContract.Presenter, ForwardContract.View> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forward);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forward;
    }

    @Override
    protected ForwardContract.View createView() {
       return findViewByIds(R.id.forward_view);
    }

    @Override
    protected ForwardContract.Presenter createPresenter() {
        return new ForwardPresenter(mView);
    }
}
