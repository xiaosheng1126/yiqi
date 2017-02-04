package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.text.TextUtils;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.UserDetailsPresenter;
import com.hong.bo.shi.presenter.contract.UserDetailsContract;

public class UserDetailsActivity extends BaseMvpActivity<UserDetailsContract.Presenter, UserDetailsContract.View> {

    private String mPath;
    private String mGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mGuid = getIntent().getStringExtra(Constants.Key.GUID);
        } else {
            mGuid = savedInstanceState.getString(Constants.Key.GUID);
            mPath = getIntent().getStringExtra(Constants.Key.PATH);
        }
        boolean isMe = App.getInstance().getUserInfo().getGuid().equals(mGuid);
        if (!isMe) {
            mPresenter.query(mGuid);
        }else{
            mView.init(App.getInstance().getUserInfo());
        }
        mView.setBackTitle(getIntent().getStringExtra(Constants.Key.BACK_TITLE));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user_details;
    }

    @Override
    protected UserDetailsContract.View createView() {
        return findViewByIds(R.id.user_details_view);
    }

    @Override
    protected UserDetailsContract.Presenter createPresenter() {
        return new UserDetailsPresenter(mView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.Key.GUID, mGuid);
        if (!TextUtils.isEmpty(mPath)) {
            outState.putString(Constants.Key.PATH, mPath);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mView.update();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPath = null;
        mGuid = null;
    }
}
