package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.GroupDetailsPresenter;
import com.hong.bo.shi.presenter.contract.GroupDetailsContract;

public class GroupDetailsActivity extends BaseMvpActivity<GroupDetailsContract.Presenter,GroupDetailsContract.View> {

    private GroupInfo mGroupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mGroupInfo = getIntent().getParcelableExtra(GroupInfo.class.getSimpleName());
        }else {
            mGroupInfo = savedInstanceState.getParcelable(GroupInfo.class.getSimpleName());
        }
        mPresenter.query(mGroupInfo.getGuid());
        mView.init(mGroupInfo);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_details;
    }

    @Override
    protected GroupDetailsContract.View createView() {
        return findViewByIds(R.id.group_details_view);
    }

    @Override
    protected GroupDetailsContract.Presenter createPresenter() {
        return new GroupDetailsPresenter(mView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GroupInfo.class.getSimpleName(), mGroupInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGroupInfo = null;
    }
}
