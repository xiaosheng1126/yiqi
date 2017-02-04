package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.WelecomBean;
import com.hong.bo.shi.presenter.contract.WelcomeContract;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;

import java.io.File;

/**
 * Created by andy on 2016/12/8.
 */

public class WelecomeView extends RootView<WelcomeContract.Presenter> implements WelcomeContract.View{

    private ImageView mImageView;

    public WelecomeView(Context context) {
        super(context);
    }

    public WelecomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(WelcomeContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_welecome_view, this);
    }

    @Override
    protected void initView() {
        mImageView = findViewByIds(R.id.ivBg);
        WelecomBean bean = PreferencesUtils.getWelecomBean();
        if(bean != null && !TextUtils.isEmpty(bean.getFiringImg())){
            String img = bean.getFiringImg();
            String fileName = img.substring(img.lastIndexOf("/") + 1, img.length());
            File file = new File(App.getInstance().getPictureCache(), fileName);
            if(file != null && file.exists() && file.length() > 0) {
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mImageView.setImageBitmap(bitmap);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void toLogin() {
        UIHelper.showLogin(mContext);
    }

    @Override
    public void toMain() {
        UIHelper.showMain(mContext);
    }

    @Override
    public void showError(String msg){
        dismissDialog();
        toLogin();
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mImageView = null;
    }
}
