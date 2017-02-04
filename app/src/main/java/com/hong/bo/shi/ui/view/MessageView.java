package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.contract.MessageContract;
import com.hong.bo.shi.ui.adapter.MessageAdapter;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;

import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class MessageView extends RootView<MessageContract.Presenter> implements
        MessageContract.View,CommonTitle.OnRightClickListener{

    private MessageAdapter mAdapter;
    private CommonTitle mCommonTitle;

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.fragment_message_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setTitle("消息");
        mCommonTitle.setRightView(R.drawable.search_sel);
        RecyclerView recyclerView = findViewByIds(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new MessageAdapter();
        mAdapter.setActivity((BaseActivity) mContext);
        recyclerView.setAdapter(mAdapter);
        mCommonTitle.setOnRightClickListener(this);
    }

    @Override
    public void update(List<GroupInfo> list) {
        mAdapter.setData(list);
    }

    @Override
    public void onRightClick(View view) {
        UIHelper.searchMessage(mContext);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mCommonTitle = null;
        mAdapter = null;
    }
}
