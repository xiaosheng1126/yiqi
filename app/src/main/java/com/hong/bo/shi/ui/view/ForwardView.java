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
import com.hong.bo.shi.presenter.contract.ForwardContract;
import com.hong.bo.shi.ui.adapter.ContractAdapter;
import com.hong.bo.shi.ui.adapter.SelectUserAdapter;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.SegmentControl;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardView extends RootView<ForwardContract.Presenter> implements ForwardContract.View,SegmentControl.OnSegmentControlClickListener  {

    private SegmentControl mSegmentControl;
    private ContractAdapter mLocalAdapter;
    private ContractAdapter mClondAdapter;
    private ExpandableListView mLocalList;
    private ExpandableListView mClondList;
    private RecyclerView mSelectRecyclerView;
    private SelectUserAdapter mAdapter;
    private RecyclerView mRecyclerView;
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
//        mLocalAdapter.setPresenter(presenter);
//        mClondAdapter.setPresenter(presenter);
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
        mLocalAdapter = new ContractAdapter();
        mLocalList.setAdapter(mLocalAdapter);
        mClondList = findViewByIds(R.id.clondExpandableListView);
        mClondList.setGroupIndicator(null);
        mClondAdapter = new ContractAdapter();
        mClondList.setAdapter(mClondAdapter);
        mSelectRecyclerView = findViewByIds(R.id.recyclerView);
        mRecyclerView = findViewByIds(R.id.recyclerView2);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new SelectUserAdapter();
        mSelectRecyclerView.setLayoutManager
                (new LinearLayoutManager(mContext, HORIZONTAL, false));
        mSelectRecyclerView.setAdapter(mAdapter);
        mSelectRecyclerView.setVisibility(GONE);
        mTvFinish.setEnabled(false);
        mTvFinish.setTextColor(Color.parseColor("#959595"));
        mTvFinish.setText(R.string.ac_select_contacts_ok);
        mTvBack.setText("会话");
        initEvent();
    }

    @Override
    public void onClick(View v) {
        if(v == mTvBack){
            ((CommonTitle.OnLeftClickListener)mContext).onLeftClick(v);
        }else if(v == mTvFinish){

        }else if(v.getId() == R.id.ivSearch){

        }
    }

    private void initEvent() {
        mSegmentControl = (SegmentControl) findViewById(R.id.segment_control);
        mSegmentControl.setOnSegmentControlClickListener(this);
        mTvFinish.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        findViewById(R.id.ivSearch).setOnClickListener(this);
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
            mRecyclerView.setVisibility(VISIBLE);
            mLocalList.setVisibility(GONE);
            mClondList.setVisibility(GONE);
        }else if(mIndex == 1){
            mRecyclerView.setVisibility(GONE);
            mLocalList.setVisibility(VISIBLE);
            mClondList.setVisibility(GONE);
        }else{
            mRecyclerView.setVisibility(GONE);
            mLocalList.setVisibility(GONE);
            mClondList.setVisibility(VISIBLE);
        }
    }
}
