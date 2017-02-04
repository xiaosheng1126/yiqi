package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.MainItembean;
import com.hong.bo.shi.presenter.contract.MainContract;
import com.hong.bo.shi.ui.activitys.MainActivity;
import com.hong.bo.shi.ui.adapter.MainAdapter;
import com.hong.bo.shi.ui.fragments.BusinessFragment;
import com.hong.bo.shi.ui.fragments.ContractFragment;
import com.hong.bo.shi.ui.fragments.MeFragment;
import com.hong.bo.shi.ui.fragments.MessageFragment;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class MainView extends RootView<MainContract.Presenter> implements MainContract.View, MainAdapter.OnCheckSelectListener {

    private MainActivity mActivity;
    private MainAdapter mAdapter;
    private SparseArray<Fragment> mFragments;
    private int mCurrentIndex = -1;

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_main_view, this);
    }

    @Override
    protected void initView() {
        mActivity = (MainActivity) mContext;
        RecyclerView recyclerView = findViewByIds(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new MainAdapter();
        mAdapter.setOnCheckSelectListener(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onCheckSelect(int position) {
        showFragment(position);
    }

    @Override
    public void update() {
        int[] count = PreferencesUtils.getMainBottomCount();
        if (mAdapter.isChanged(count)) {
            List<MainItembean> list = new ArrayList<MainItembean>();
            MainItembean itembean = new MainItembean(R.mipmap.business_sel,
                    R.mipmap.business_nor, "业务", count[0]);
            list.add(itembean);
            itembean = new MainItembean(R.mipmap.message_sel, R.mipmap.message_nor, "消息", count[1]);
            list.add(itembean);
            itembean = new MainItembean(R.mipmap.contract_sel, R.mipmap.contract_nor, "通讯录", count[2]);
            list.add(itembean);
            itembean = new MainItembean(R.mipmap.me_sel, R.mipmap.me_nor, "我的", count[3]);
            list.add(itembean);
            mAdapter.setData(list);
        }
        if(mFragments != null && mFragments.get(0) != null) {
            BusinessFragment fragment = (BusinessFragment) mFragments.get(0);
            fragment.update();
        }
        if(mFragments == null){
            mFragments = new SparseArray<>();
            showFragment(0);
        }
    }

    @Override
    public void showIndex(int index, String result) {
        index = index >= 4 ? 0 : index;
        mAdapter.setPosition(index);
        showFragment(index);
        if (index == 0 && !TextUtils.isEmpty(result)) {
            BusinessFragment fragment = (BusinessFragment) mFragments.get(0);
            if (fragment != null) {
                fragment.handleScanResult(result);
            }
        }
    }

    @Override
    public void kicked() {
        UIHelper.showKicked(mContext);
    }

    @Override
    public void loginSuccess() {
        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginFailed() {
        UIHelper.exitLogin(mContext);
        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
    }

    private void showFragment(int position){
        if(mCurrentIndex != position){
            Fragment fragment = null;
            if(mFragments.get(position) == null){
                if(position == 0){
                    fragment = new BusinessFragment();
                }else if(position == 1){
                    fragment = new MessageFragment();
                }else if(position == 2){
                    fragment = new ContractFragment();
                }else{
                    fragment = new MeFragment();
                }
            }else{
                fragment = mFragments.get(position);
            }
            showFragment(position, fragment);
        }
    }

    private void showFragment(int id, Fragment fragment) {
        if(fragment == null)return;
        if(mCurrentIndex == id)return;
        mCurrentIndex = id;
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        Fragment fragment1 = mActivity.getSupportFragmentManager()
                .findFragmentById(R.id.frameLayout);
        if (fragment1 == null) {
            ft.add(R.id.frameLayout, fragment);
        } else {
            ft.detach(fragment1);
            if (mFragments.get(id) == null) {
                ft.add(R.id.frameLayout, fragment);
            } else {
                ft.attach(fragment);
            }
        }
        if (mFragments.get(id) == null) {
            mFragments.put(id, fragment);
        }
        ft.commit();
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mFragments.clear();
        mAdapter = null;
        mActivity = null;
    }
}
