package com.hong.bo.shi.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Description: BaseFragment
 * Creator: yxc
 * date: 2016/9/7 11:40
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    protected T mPresenter;
    private View mRootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView == null) {
            View layout = inflater.inflate(getLayoutId(), null);
            return layout;
        }else{
            return mRootView;
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mRootView == null) {
            mRootView = getView();
            initView();
        }
    }

    public <T extends View> T findViewById(int res){
        return (T) getView().findViewById(res);
    }

    public abstract void initView();

    public abstract int getLayoutId();



}
