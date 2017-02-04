package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.LoginPresenter;
import com.hong.bo.shi.presenter.contract.LoginContract;

public class LoginActivity extends BaseMvpActivity<LoginContract.Presenter,LoginContract.View> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected LoginContract.View createView() {
        return findViewByIds(R.id.login_view);
    }

    @Override
    protected LoginContract.Presenter createPresenter() {
        return new LoginPresenter(mView);
    }

    @Override
    public void onLeftClick(View view) {
        App.getInstance().exitApp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)return;
        if(requestCode == Constants.REQUEST_REGISTER_CODE){
            String extra = data.getStringExtra(Constants.Key.PATH);
            String[] split = extra.split(Constants.SPLIT);
            mView.registerSuccess(split[0], split[1]);
        }else if(requestCode == Constants.REQUEST_FORGET_PASS){
            String extra = data.getStringExtra(Constants.Key.PATH);
            String[] split = extra.split(Constants.SPLIT);
            mView.forgetSuccess(split[0], split[1]);
        }
    }
}
