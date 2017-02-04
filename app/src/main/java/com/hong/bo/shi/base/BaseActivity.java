package com.hong.bo.shi.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.dialog.LoadingDialog;
import com.hong.bo.shi.utils.NotifyUtil;
import com.hong.bo.shi.widget.CommonTitle;

/**
 * Description: BaseActivity
 * Creator: yxc
 * date: 2016/9/7 11:45
 */
public abstract class BaseActivity extends AppCompatActivity implements CommonTitle.OnLeftClickListener{

    private LoadingDialog mLoadingDialog;
    private InputMethodManager mInputManager;//软键盘管理类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        App.getInstance().registerActivity(this);
    }

    protected abstract @LayoutRes int getLayoutId();

    protected <T extends View> T findViewByIds(int res){
        return (T) findViewById(res);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //清除所有的notify
        new NotifyUtil(this, "").clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().unregisterActivity(this);
    }

    public void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    public void dismiss() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            onLeftClick(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLeftClick(View view) {
        hideSoftInput();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismiss();
    }

    /**
     * 隐藏软件盘
     */
    protected void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
