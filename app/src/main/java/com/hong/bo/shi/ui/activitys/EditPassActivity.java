package com.hong.bo.shi.ui.activitys;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.presenter.EditPassPresenter;
import com.hong.bo.shi.presenter.contract.EditPassContract;

public class EditPassActivity extends BaseMvpActivity<EditPassContract.Presenter,EditPassContract.View> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_pass;
    }

    @Override
    protected EditPassContract.View createView() {
        return findViewByIds(R.id.edit_pass_view);
    }

    @Override
    protected EditPassContract.Presenter createPresenter() {
        return new EditPassPresenter(mView);
    }
}
