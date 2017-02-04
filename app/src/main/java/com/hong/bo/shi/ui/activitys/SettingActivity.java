package com.hong.bo.shi.ui.activitys;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.dialog.CenterDialog;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvVideoTime;
    private TextView mTvAudioTime;
    private TextView mTvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    private void initView() {
        CommonTitle commonTitle = (CommonTitle) findViewById(R.id.commonTitle);
        commonTitle.setTitle("设置");
        commonTitle.setLeftView("我的");
        commonTitle.setOnLeftClickListener(this);
        mTvAudioTime = (TextView) findViewById(R.id.tvAudioTime);
        mTvVideoTime = (TextView) findViewById(R.id.tvVideoTime);
        mTvVersion = (TextView) findViewById(R.id.tvVersion);
        mTvAudioTime.setText(PreferencesUtils.getAudioTime() + "秒");
        mTvVideoTime.setText(PreferencesUtils.getVideoTime() + "秒");
        try {
            mTvVersion.setText("V " + this.getPackageManager().
                    getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mTvVersion.setText("V 1.0");
        }
        findViewById(R.id.tvExit).setOnClickListener(this);
        findViewById(R.id.rlVideoSet).setOnClickListener(this);
        findViewById(R.id.rlAudioSet).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvExit) {
            PreferencesUtils.setPass(null);
            App.getInstance().setUserInfo(null);
            UIHelper.exitLogin(this);
        }else if(v.getId() == R.id.rlVideoSet){
           new CenterDialog(this, R.menu.set_video_time, new PopupMenu.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   if(item.getItemId() == R.id.video_5){
                       PreferencesUtils.setVideoTime(String.valueOf(5));
                       mTvVideoTime.setText("5秒");
                   }else if(item.getItemId() == R.id.video_10){
                       PreferencesUtils.setVideoTime(String.valueOf(10));
                       mTvVideoTime.setText("10秒");
                   }else if(item.getItemId() == R.id.video_15){
                       PreferencesUtils.setVideoTime(String.valueOf(15));
                       mTvVideoTime.setText("15秒");
                   }
                   return true;
               }
           }).show();
        }else if(v.getId() == R.id.rlAudioSet){
            new CenterDialog(this, R.menu.set_audio_time, new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId() == R.id.audio_20){
                        PreferencesUtils.setAudioTime(String.valueOf(20));
                        mTvAudioTime.setText("20秒");
                    }else if(item.getItemId() == R.id.audio_40){
                        PreferencesUtils.setAudioTime(String.valueOf(40));
                        mTvAudioTime.setText("40秒");
                    }else if(item.getItemId() == R.id.audio_60){
                        PreferencesUtils.setAudioTime(String.valueOf(60));
                        mTvAudioTime.setText("60秒");
                    }
                    return true;
                }
            }).show();
        }
    }
}
