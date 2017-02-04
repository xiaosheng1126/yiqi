package com.hong.bo.shi.base;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Description:
 * Creator: yxc
 * date: $date $time
 */
public abstract class RootView<T extends BasePresenter> extends LinearLayout implements View.OnClickListener{

    protected Context mContext;
    protected T mPresenter;

    public RootView(Context context) {
        super(context);
        init();
    }

    public RootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected abstract void getLayout();

    protected abstract void initView();

    protected <T extends View> T findViewByIds(@IdRes int viewId){
        return (T) findViewById(viewId);
    }
    protected void init() {
        mContext = getContext();
        getLayout();
        initView();
    }

    public void showError(String msg) {

    }

    public void showLoading() {
        if(mContext != null && mContext instanceof BaseActivity){
            ((BaseActivity)mContext).showLoading();
        }
    }

    public void dismissDialog() {
        if(mContext != null && mContext instanceof BaseActivity){
            ((BaseActivity)mContext).dismiss();
        }
    }

    public void onDestory() {
        if(mPresenter != null){
            mPresenter.onDestory();
        }
        mContext = null;
    }

    @Override
    public void onClick(View v) {

    }
}
