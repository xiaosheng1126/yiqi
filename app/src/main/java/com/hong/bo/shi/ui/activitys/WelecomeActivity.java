package com.hong.bo.shi.ui.activitys;

import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.WelecomPresenter;
import com.hong.bo.shi.presenter.contract.WelcomeContract;

public class WelecomeActivity extends BaseMvpActivity<WelcomeContract.Presenter,WelcomeContract.View> {

    @Override
    protected WelcomeContract.View createView() {
        return findViewByIds(R.id.welcome_view);
    }

    @Override
    protected WelcomeContract.Presenter createPresenter() {
        return new WelecomPresenter(mView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_welecome;
    }

    @Override
    public void onLeftClick(View view) {
        App.getInstance().exitApp();
    }
}
