package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.contract.SelectContract;
import com.hong.bo.shi.ui.activitys.SelectContractActivity;
import com.hong.bo.shi.ui.adapter.ContractAdapter;
import com.hong.bo.shi.ui.adapter.SelectUserAdapter;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.SegmentControl;

import java.util.List;

/**
 * Created by andy on 2016/12/16.
 */

public class SelectContractView extends RootView<SelectContract.Presenter>
        implements SelectContract.View,SegmentControl.OnSegmentControlClickListener {

    private SegmentControl mSegmentControl;
    private ContractAdapter mLocalAdapter;
    private ContractAdapter mClondAdapter;
    private ExpandableListView mLocalList;
    private ExpandableListView mClondList;
    private RecyclerView mRecyclerView;
    private SelectUserAdapter mAdapter;
    private TextView mTvFinish;
    private TextView mTvBack;
    private int mIndex;

    public SelectContractView(Context context) {
        super(context);
    }

    public SelectContractView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(SelectContract.Presenter presenter) {
        mPresenter = presenter;
        mLocalAdapter.setPresenter(mPresenter);
        mClondAdapter.setPresenter(mPresenter);
        mAdapter.setPresenter(mPresenter);
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_select_contract_view, this);
    }

    @Override
    protected void initView() {
        mTvBack = findViewByIds(R.id.tvBack);
        mTvFinish = findViewByIds(R.id.tvFinish);
        mLocalList = findViewByIds(R.id.localExpandableListView);
        mLocalList.setGroupIndicator(null);
        mLocalAdapter = new ContractAdapter();
        mLocalList.setAdapter(mLocalAdapter);
        mClondList = findViewByIds(R.id.clondExpandableListView);
        mClondList.setGroupIndicator(null);
        mClondAdapter = new ContractAdapter();
        mClondList.setAdapter(mClondAdapter);
        mRecyclerView = findViewByIds(R.id.recyclerView);
        mAdapter = new SelectUserAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
        updateView();
        mRecyclerView.setVisibility(GONE);
        mTvFinish.setEnabled(false);
        mTvFinish.setTextColor(Color.parseColor("#959595"));
        mTvFinish.setText(R.string.ac_select_contacts_ok);
        initEvent();
    }

    private void updateView(){
        if(mIndex == 0){
            mLocalList.setVisibility(VISIBLE);
            mClondList.setVisibility(GONE);
        }else{
            mLocalList.setVisibility(GONE);
            mClondList.setVisibility(VISIBLE);
        }
    }

    private void initEvent() {
        mSegmentControl = (SegmentControl) findViewById(R.id.segment_control);
        mSegmentControl.setOnSegmentControlClickListener(this);
        mTvFinish.setOnClickListener(this);
        findViewById(R.id.ivSearch).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mTvFinish == v){
            mPresenter.operation();
            showLoading();
        }else if(v.getId() == R.id.ivSearch){
            UIHelper.searchContractToResult(mContext, mPresenter.getGroupGuid(), mPresenter.getSelectGuids(), 100);
        }
    }

    @Override
    public void update() {
        mLocalAdapter.notifyDataSetChanged();
        mClondAdapter.notifyDataSetChanged();
        mAdapter.update();
        if(mPresenter.selectCount() == 0){
            mRecyclerView.setVisibility(GONE);
            mTvFinish.setEnabled(false);
            mTvFinish.setTextColor(Color.parseColor("#959595"));
            mTvFinish.setText(R.string.ac_select_contacts_ok);
        }else{
            mRecyclerView.setVisibility(VISIBLE);
            mTvFinish.setEnabled(true);
            mTvFinish.setTextColor(Color.parseColor("#ffffff"));
            mTvFinish.setText(mContext.getString(R.string.ac_select_contacts_ok_count,
                    mPresenter.selectCount()));
        }
    }

    @Override
    public void updateLocal(List<DepartmentBean> list) {
        mLocalAdapter.setData(list);
    }

    @Override
    public void updateClond(List<DepartmentBean> list) {
        mClondAdapter.setData(list);
    }

    @Override
    public void onSuccess(GroupInfo info) {
        dismissDialog();
        ((SelectContractActivity)mContext).toChatActivity(info);
    }

    @Override
    public void onFailed() {
        dismissDialog();
        ((SelectContractActivity)mContext).finish();
    }

    @Override
    public void setBackView(String text) {
        mTvBack.setText(text);
        mTvBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CommonTitle.OnLeftClickListener)mContext).onLeftClick(v);
            }
        });
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mSegmentControl = null;
        mLocalList = null;
        mClondList = null;
        mClondAdapter = null;
        mLocalAdapter = null;
        mTvFinish = null;
        mTvBack = null;
    }

    @Override
    public void onSegmentControlClick(int index) {
        if(mIndex != index){
            mIndex = index;
            updateView();
        }
    }
}
