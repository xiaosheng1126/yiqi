package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.contract.ForwardContract;
import com.hong.bo.shi.ui.activitys.ForwardActivity;
import com.hong.bo.shi.ui.adapter.ForwardGroupAdapter;
import com.hong.bo.shi.ui.adapter.ForwardPersonAdapter;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.SegmentControl;

import java.util.List;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardView extends RootView<ForwardContract.Presenter> implements ForwardContract.View,SegmentControl.OnSegmentControlClickListener  {

    private SegmentControl mSegmentControl;
    private ForwardPersonAdapter mLocalAdapter;
    private ForwardPersonAdapter mClondAdapter;
    private ForwardGroupAdapter mGroupAdapter;
    private ExpandableListView mLocalList;
    private ExpandableListView mClondList;
    private RecyclerView mGroupRecyclerView;
    private TextView mTvFinish;
    private TextView mTvBack;
    private int mIndex;

    public ForwardView(Context context) {
        super(context);
    }

    public ForwardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(ForwardContract.Presenter presenter) {
        this.mPresenter = presenter;
        mLocalAdapter.setPresenter(presenter);
        mClondAdapter.setPresenter(presenter);
        mGroupAdapter.setPresenter(presenter);
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_forward_view, this);
    }

    @Override
    protected void initView() {
        mTvBack = findViewByIds(R.id.tvBack);
        mTvFinish = findViewByIds(R.id.tvFinish);
        mLocalList = findViewByIds(R.id.localExpandableListView);
        mLocalList.setGroupIndicator(null);
        mLocalAdapter = new ForwardPersonAdapter();
        mLocalList.setAdapter(mLocalAdapter);
        mClondList = findViewByIds(R.id.clondExpandableListView);
        mClondList.setGroupIndicator(null);
        mClondAdapter = new ForwardPersonAdapter();
        mClondList.setAdapter(mClondAdapter);
        mGroupRecyclerView = findViewByIds(R.id.recyclerView);
        mGroupRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mGroupAdapter = new ForwardGroupAdapter();
        mGroupRecyclerView.setAdapter(mGroupAdapter);
        setRightViewState(0);
        initEvent();
        updateView();
    }

    private void setRightViewState(int selectCount) {
        mTvFinish.setEnabled(selectCount > 0);
        if(selectCount == 0) {
            mTvFinish.setTextColor(Color.parseColor("#959595"));
            mTvFinish.setText(R.string.ac_select_contacts_ok);
        }else{
            mTvFinish.setTextColor(Color.parseColor("#ffffff"));
            mTvFinish.setText(mContext.getString(R.string.ac_select_contacts_ok_count, selectCount));
        }
        mTvBack.setText("会话");
    }

    @Override
    public void onClick(View v) {
        if(v == mTvBack){
            ((CommonTitle.OnLeftClickListener)mContext).onLeftClick(v);
        }else if(v == mTvFinish){
            showLoading();
            mPresenter.forward();
        }
    }

    private void initEvent() {
        mSegmentControl = (SegmentControl) findViewById(R.id.segment_control);
        mSegmentControl.setOnSegmentControlClickListener(this);
        mTvFinish.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
    }

    @Override
    public void onSegmentControlClick(int index) {
        if(mIndex != index){
            mIndex = index;
            updateView();
        }
    }

    private void updateView(){
        if(mIndex == 0){
            mGroupRecyclerView.setVisibility(VISIBLE);
            mLocalList.setVisibility(GONE);
            mClondList.setVisibility(GONE);
        }else if(mIndex == 1){
            mGroupRecyclerView.setVisibility(GONE);
            mLocalList.setVisibility(VISIBLE);
            mClondList.setVisibility(GONE);
        }else{
            mGroupRecyclerView.setVisibility(GONE);
            mLocalList.setVisibility(GONE);
            mClondList.setVisibility(VISIBLE);
        }
    }

    @Override
    public void updateLocal(List<DepartmentBean> userInfos) {
        mLocalAdapter.setData(userInfos);
    }

    @Override
    public void updateClond(List<DepartmentBean> userInfos) {
        mClondAdapter.setData(userInfos);
    }

    @Override
    public void updateGroup(List<GroupInfo> groupInfos) {
        mGroupAdapter.setData(groupInfos);
    }

    @Override
    public void updateGroup() {
        mGroupAdapter.notifyDataSetChanged();
        setRightViewState(mPresenter.getForwardCount());
    }

    @Override
    public void updatePerson() {
        mClondAdapter.notifyDataSetChanged();
        mLocalAdapter.notifyDataSetChanged();
        setRightViewState(mPresenter.getForwardCount());
    }

    @Override
    public void onSuccess() {
        dismissDialog();
        Toast.makeText(mContext, "消息转发成功", Toast.LENGTH_SHORT).show();
        ((ForwardActivity)mContext).finish();
    }

    @Override
    public void showError(String msg) {
        dismissDialog();
        Toast.makeText(mContext, "消息转发失败", Toast.LENGTH_SHORT).show();
    }
}
