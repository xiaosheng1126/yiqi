package com.hong.bo.shi.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.hong.bo.shi.R;

/**
 * loading对话框
 * Created by sunnybear on 2015/3/9.
 */
public class LoadingDialog extends Dialog {

    private ImageView mImageView;
    private Context mContext;
    private RotateAnimation mRotateAnimation;//旋转动画

    public LoadingDialog(Context context) {
        super(context, R.style.DialogTheme);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCanceledOnTouchOutside(false);
        this.mContext = context;
        View layout = getLayoutInflater().inflate(R.layout.loading_dialog_layout, null);
        mImageView = (ImageView) layout.findViewById(R.id.iv_icon);
        initAnimation();
        this.setContentView(layout);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(mRotateAnimation != null){
                    mRotateAnimation.cancel();
                }
            }
        });
    }

    /**
     * 初始化旋转动画
     */
    private void initAnimation() {
        mRotateAnimation = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(1000);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    @Override
    public void show() {
        try {
            if(this.isShowing()) return;
            if (!((Activity) mContext).isFinishing()) {
                super.show();
                mImageView.startAnimation(mRotateAnimation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if(mRotateAnimation != null){
                mRotateAnimation.cancel();
            }
            super.dismiss();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
