package com.hong.bo.shi.ui.fragments;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.presenter.MePresenter;
import com.hong.bo.shi.presenter.contract.MeContract;
import com.hong.bo.shi.ui.view.MeView;

/**
 * Created by andy on 2016/12/12.
 */

public class MeFragment extends BaseFragment<MeContract.Presenter> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public void initView() {
        MeView meView = findViewById(R.id.me_view);
        mPresenter = new MePresenter(meView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.update();
    }
}
