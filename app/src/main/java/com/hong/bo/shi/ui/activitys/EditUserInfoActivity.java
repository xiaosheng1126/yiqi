package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.EditUserInfoPresenter;
import com.hong.bo.shi.presenter.contract.EditUserInfoContract;
import com.hong.bo.shi.widget.CommonTitle;

public class EditUserInfoActivity extends BaseMvpActivity<EditUserInfoContract.Presenter, EditUserInfoContract.View> implements CommonTitle.OnRightClickListener {

    private int mType;// 0 修改用户名, 1 修改用户手机号码 2 修改用户性别

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mType = getIntent().getIntExtra(Constants.Key.TYPE, 0);
        } else {
            mType = savedInstanceState.getInt(Constants.Key.TYPE);
        }
        mView.setType(mType);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_user_info;
    }

    @Override
    protected EditUserInfoContract.View createView() {
        return findViewByIds(R.id.edit_user_info_view);
    }

    @Override
    protected EditUserInfoContract.Presenter createPresenter() {
        return new EditUserInfoPresenter(mView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Constants.Key.TYPE, mType);
    }

    @Override
    public void onRightClick(View view) {
        if (mType == 0) {
            mPresenter.editUserInfo(mType, mView.getContent());
        } else if (mType == 1) {
            mPresenter.editUserInfo(mType, mView.getContent());
        } else if (mType == 2) {
            mPresenter.editUserInfo(mType, String.valueOf(mView.getGender()));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
