package com.hong.bo.shi.ui.fragments;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.presenter.ContractPresenter;
import com.hong.bo.shi.presenter.contract.ContactContract;
import com.hong.bo.shi.ui.view.ContractView;

/**
 * Created by andy on 2016/12/12.
 */

public class ContractFragment extends BaseFragment<ContactContract.Presenter> {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_contract;
    }

    @Override
    public void initView() {
        ContractView contractView = findViewById(R.id.contract_view);
        mPresenter = new ContractPresenter(contractView);
    }
}
