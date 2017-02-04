package com.hong.bo.shi.ui.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.model.bean.WelecomBean;
import com.hong.bo.shi.presenter.contract.LoginContract;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

import static com.hong.bo.shi.emoji.EmotionKeyboard.SHARE_PREFERENCE_NAME;
import static com.hong.bo.shi.emoji.EmotionKeyboard.SHARE_PREFERENCE_SOFT_INPUT_HEIGHT;

/**
 * Created by andy on 2016/12/8.
 */

public class LoginView extends RootView<LoginContract.Presenter> implements LoginContract.View,TextWatcher{

    private EditText mEtAccount;
    private EditText mEtPass;
    private ImageDraweeView mIvAvaturl;
    private TextView mTvLogin;
    private CommonTitle mCommonTitle;
    //
    private String mLastText;
    private Handler mHandler;
    private String mAccount;
    private String mPassword;
    private SharedPreferences mSp;

    public LoginView(Context context) {
        super(context);
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_login_view, this);
    }

    @Override
    protected void initView() {
        mSp = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mEtAccount = findViewByIds(R.id.etAccount);
        mEtPass = findViewByIds(R.id.etPass);
        mIvAvaturl = findViewByIds(R.id.ivAvaturl);
        mTvLogin = findViewByIds(R.id.tvLogin);
        WelecomBean bean = PreferencesUtils.getWelecomBean();
        mCommonTitle.setTitle(bean.getTitle());
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    String text = mEtAccount.getText().toString();
                    if(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim()) || text.equals(mLastText))return;
                    mPresenter.getAvaturl(text);
                    mLastText = text;
                }
            }
        };
        mEtAccount.setText(PreferencesUtils.getAccount());
        mEtPass.setText(PreferencesUtils.getPass());
        if(!TextUtils.isEmpty(PreferencesUtils.getAccount())){
            mHandler.sendEmptyMessage(0);
        }
        mEtAccount.addTextChangedListener(this);
        mTvLogin.setOnClickListener(this);
        findViewById(R.id.tvForgetPass).setOnClickListener(this);
        findViewById(R.id.tvRegister).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mTvLogin == v){
            login();
        }else if(v.getId() == R.id.tvForgetPass){
            WebBean bean = new WebBean("忘记密码", "登录", "", 0);
            bean.setUrl(PreferencesUtils.getWelecomBean().getForgetPassUrl());
            UIHelper.showWebView(mContext, bean, Constants.REQUEST_REGISTER_CODE);
        }else if(v.getId() == R.id.tvRegister){
            WebBean bean = new WebBean("注册", "登录", "", 0);
            bean.setUrl(PreferencesUtils.getWelecomBean().getRegisterUrl());
            UIHelper.showWebView(mContext, bean, Constants.REQUEST_FORGET_PASS);
        }
    }

    private void login(){
        mAccount = mEtAccount.getText().toString();
        mPassword = mEtPass.getText().toString();
        if(TextUtils.isEmpty(mAccount) || TextUtils.isEmpty(mAccount.trim())){
            Toast.makeText(mContext, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mPassword) || TextUtils.isEmpty(mPassword.trim())){
            Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        mPresenter.login(mAccount, mPassword);
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showError(String msg) {
        dismissDialog();
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    public void showAvaturl(String url) {
        mIvAvaturl.setImageDraweeView(85, 85, url.trim());
    }

    @Override
    public void showMain() {
        dismissDialog();
        mHandler.removeMessages(0);
        PreferencesUtils.setAccount(mAccount);
        PreferencesUtils.setPass(mPassword);
        UIHelper.showMain(mContext);
    }

    @Override
    public void registerSuccess(String account, String password) {
        PreferencesUtils.setAccount(account);
        mEtAccount.setText(account);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void forgetSuccess(String account, String password) {
        PreferencesUtils.setAccount(account);
        mEtAccount.setText(account);
        mHandler.sendEmptyMessage(0);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mHandler.sendEmptyMessageDelayed(0, 2500);
        if (mSp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 0) == 0) {
             getSupportSoftInputHeight();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mHandler.removeMessages(0);
        mHandler = null;
        mTvLogin = null;
        mCommonTitle = null;
        mIvAvaturl = null;
    }


    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        Window window = ((Activity) mContext).getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = window.getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
        }
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }


    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }
}
