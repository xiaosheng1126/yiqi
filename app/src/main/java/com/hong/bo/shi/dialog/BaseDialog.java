package com.hong.bo.shi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.hong.bo.shi.R;

/**
 * Created by andy on 2016/12/1.
 */

public class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(Context context) {
        super(context, R.style.DialogStyle);
        mContext = context;
    }

    protected <T extends View> T findViewById(View layout, @IdRes int id){
        return (T) layout.findViewById(id);
    }

    protected View inflate(@LayoutRes int layoutId){
        return LayoutInflater.from(mContext).inflate(layoutId, null);
    }

    protected void setDialogLocation(int width, int gravity){
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = width;
        mWindow.setGravity(gravity);
        mWindow.setAttributes(lp);
    }

}
