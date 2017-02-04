package com.hong.bo.shi.ui.fragments;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.presenter.MessagePresenter;
import com.hong.bo.shi.presenter.contract.MessageContract;
import com.hong.bo.shi.ui.view.MessageView;

/**
 * Created by andy on 2016/12/12.
 */

public class MessageFragment extends BaseFragment<MessageContract.Presenter> {

    private MessageView mMessageView;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    @Override
    public void initView() {
        mMessageView = findViewById(R.id.message_view);
        mPresenter = new MessagePresenter(mMessageView);
    }
}
