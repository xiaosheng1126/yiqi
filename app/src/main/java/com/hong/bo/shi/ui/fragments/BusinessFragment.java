package com.hong.bo.shi.ui.fragments;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.presenter.BusinessPresenter;
import com.hong.bo.shi.presenter.contract.BusinessContract;
import com.hong.bo.shi.ui.view.BusinessView;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessFragment extends BaseFragment<BusinessContract.Presenter> {

    private BusinessView mBusinessView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_business;
    }

    @Override
    public void initView() {
        mBusinessView = findViewById(R.id.business_view);
        mPresenter = new BusinessPresenter(mBusinessView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mBusinessView.onResume();
    }

    public void update(){
        if(mBusinessView != null) {
            mBusinessView.update();
        }
    }

    public void handleScanResult(String result){
        if(mBusinessView != null){
            mBusinessView.handleScanResult(result);
        }
    }
}
