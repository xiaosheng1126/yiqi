package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.presenter.contract.GroupDetailsContract;
import com.hong.bo.shi.ui.adapter.GroupDetailsAdapter;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;

import java.util.List;

/**
 * Created by andy on 2016/12/14.
 */

public class GroupDetailsView extends RootView<GroupDetailsContract.Presenter> implements GroupDetailsContract.View {

    private CommonTitle mCommonTitle;
    private GroupDetailsAdapter mAdapter;

    public GroupDetailsView(Context context) {
        super(context);
    }

    public GroupDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(GroupDetailsContract.Presenter presenter) {
        mPresenter = presenter;
        mAdapter.setPresenter(mPresenter);
    }

    @Override
    public void showError(String msg) {
        dismissDialog();
        if(!TextUtils.isEmpty(msg)) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_group_details_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        RecyclerView recyclerView = findViewByIds(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 4);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position == (mAdapter.getItemCount() - 1)){
                    return 4;
                }
                return 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new GroupDetailsAdapter();
        mAdapter.setActivity((BaseActivity) mContext);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void init(GroupInfo info) {
        mCommonTitle.setLeftView("会话");
        mCommonTitle.setTitle(info.getGroupName());
        mAdapter.setGroupInfo(info);
    }

    @Override
    public void update(List<UserInfo> list) {
        mAdapter.setData(list);
    }

    @Override
    public void onSucess() {
        dismissDialog();
        Toast.makeText(mContext, "退群成功", Toast.LENGTH_SHORT).show();
        UIHelper.showMain(mContext);
    }

    @Override
    public void toWebView(WebBean bean) {
        UIHelper.showWebView(mContext, bean);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mAdapter = null;
    }
}
