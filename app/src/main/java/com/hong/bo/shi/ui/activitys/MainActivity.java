package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.download.BusProvider;
import com.hong.bo.shi.download.DownloadSuccessEntity;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.MainPresenter;
import com.hong.bo.shi.presenter.contract.MainContract;
import com.hong.bo.shi.utils.EmojiUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.squareup.otto.Subscribe;

public class MainActivity extends BaseMvpActivity<MainContract.Presenter,MainContract.View> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView.update();
        EmojiUtils.init(getApplicationContext());
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void downloadSuccess(DownloadSuccessEntity entity){
        if(entity != null){
            RealmHelper.downloadFileSuccess(entity.getGuid(), entity.getPath());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected MainContract.View createView() {
        return findViewByIds(R.id.main_view);
    }

    @Override
    protected MainContract.Presenter createPresenter() {
        return new MainPresenter(mView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        boolean againLogin = intent.getBooleanExtra(Constants.Key.AGAIN_LOGIN, false);
        if(againLogin){
            mPresenter.login();
        }else {
            boolean exit = intent.getBooleanExtra(Constants.Key.EXIT_LOGIN, false);
            if(exit){
                UIHelper.showLogin(this);
                finish();
            }else {
                int index = intent.getIntExtra(Constants.Key.INDEX, -1);
                String scanResult = intent.getStringExtra(Constants.Key.SCAN_RESULT);
                if (index != -1) {
                    mView.showIndex(index, scanResult);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
