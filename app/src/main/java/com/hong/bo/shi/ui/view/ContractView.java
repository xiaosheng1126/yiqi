package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.presenter.contract.ContactContract;
import com.hong.bo.shi.ui.adapter.ContractAdapter;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.SegmentControl;

import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class ContractView extends RootView<ContactContract.Presenter> implements ContactContract.View,SegmentControl.OnSegmentControlClickListener{

    private ContractAdapter mLocalAdapter;
    private ContractAdapter mClondAdapter;
    private ExpandableListView mLocalList;
    private ExpandableListView mClondList;
    private int mIndex;
    private SegmentControl mSegmentControl;

    public ContractView(Context context) {
        super(context);
    }

    public ContractView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(ContactContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.fragment_contract_view, this);
    }

    @Override
    protected void initView() {
        mLocalList = findViewByIds(R.id.localExpandableListView);
        mLocalList.setGroupIndicator(null);
        mLocalAdapter = new ContractAdapter();
        mLocalList.setAdapter(mLocalAdapter);
        mClondList = findViewByIds(R.id.clondExpandableListView);
        mClondAdapter = new ContractAdapter();
        mClondList.setGroupIndicator(null);
        mClondList.setAdapter(mClondAdapter);
        updateView();
        initEvent();
    }

    private void initEvent() {
        mSegmentControl = (SegmentControl) findViewById(R.id.segment_control);
        mSegmentControl.setOnSegmentControlClickListener(this);
        findViewById(R.id.ivCreate).setOnClickListener(this);
        findViewById(R.id.ivSearch).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ivCreate){
            UIHelper.showSelectContract(mContext, null, "通讯录");
        }else if(v.getId() == R.id.ivSearch){
            UIHelper.searchContract(mContext);
        }
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

    @Override
    public void updateLocal(List<DepartmentBean> list) {
        mLocalAdapter.setData(list);
    }

    @Override
    public void updateClond(List<DepartmentBean> list) {
        mClondAdapter.setData(list);
    }

    @Override
    public void onSegmentControlClick(int index) {
        if(mIndex != index){
            mIndex = index;
            updateView();
        }
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mLocalList = null;
        mClondList = null;
        mLocalAdapter = null;
        mClondAdapter = null;
        mSegmentControl = null;
    }
}
