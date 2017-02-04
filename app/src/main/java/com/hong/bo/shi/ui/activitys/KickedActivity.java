package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;

public class KickedActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        super.onCreate(savedInstanceState);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setFinishOnTouchOutside(false);
        findViewById(R.id.tvExit).setOnClickListener(this);
        findViewById(R.id.tvLogin).setOnClickListener(this);
        findViewById(R.id.tvAgainLogin).setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_kicked;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            return true;
        }
        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvAgainLogin){
            UIHelper.againLogin(this);
        }else if(v.getId() == R.id.tvExit){
            PreferencesUtils.setPass(null);
            App.getInstance().exitApp();
        }else if(v.getId() == R.id.tvLogin){
            PreferencesUtils.setAccount(null);
            PreferencesUtils.setPass(null);
            PreferencesUtils.setGuid(null);
            UIHelper.exitLogin(this);
            finish();
        }
    }
}
