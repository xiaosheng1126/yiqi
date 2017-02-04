package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.EditGroupInfoPresenter;
import com.hong.bo.shi.presenter.contract.EditGroupInfoContract;
import com.hong.bo.shi.widget.CommonTitle;

public class EditGroupInfoActivity extends BaseMvpActivity<EditGroupInfoContract.Presenter, EditGroupInfoContract.View> implements CommonTitle.OnRightClickListener {

    private int mType;//0 修改群名称 1 修改公告 2 查看公告
    private String mGroupGuid;
    private String mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra(Constants.Key.TYPE, 2);
        mGroupGuid = getIntent().getStringExtra(Constants.Key.GUID);
        mContext = getIntent().getStringExtra(Constants.Key.NAME);
        mView.setType(mType, mContext, mGroupGuid);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_group_info;
    }

    @Override
    protected EditGroupInfoContract.View createView() {
        return findViewByIds(R.id.edit_group_info_view);
    }

    @Override
    protected EditGroupInfoContract.Presenter createPresenter() {
        return new EditGroupInfoPresenter(mView);
    }

    @Override
    public void onRightClick(View view) {
        if(mType == 0){
            mPresenter.editGroupName(mGroupGuid, mView.getGroupName());
        }else if(mType == 1){
            mPresenter.editGroupPublic(mGroupGuid, mView.getGroupPublic());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGroupGuid = null;
        mContext = null;
    }
}
