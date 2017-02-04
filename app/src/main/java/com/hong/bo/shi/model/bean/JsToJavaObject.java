package com.hong.bo.shi.model.bean;

import android.webkit.JavascriptInterface;

/**
 * Created by andy on 2017/1/15.
 * js通信接口
 */
public class JsToJavaObject {

    private CallBackSetHeadTitle headTitle;
    private CallBackSetBottomTitle bottomTitle;
    private BackLoginListener mBackLoginListener;

    public void setBackLoginListener(BackLoginListener listener){
        this.mBackLoginListener = listener;
    }

    public void setCallBackSetHeadTitle(CallBackSetHeadTitle headTitle){
        this.headTitle = headTitle;
    }

    public void setCallBackSetBottomTitle(CallBackSetBottomTitle bottomTitile){
        this.bottomTitle = bottomTitile;
    }


    @JavascriptInterface
    public void setHeadTitle(String title, String showBack, String backName, String backPager) {
        WebBean bean = new WebBean(title, backName, backPager, Integer.valueOf(showBack));
        if (this.headTitle != null) {
            this.headTitle.onHeadTitle(bean);
        }
    }

    @JavascriptInterface
    public void setBottomTitle(String data) {
        if(bottomTitle != null){
            bottomTitle.onBottomTitle(data);
        }
    }

    //注册忘记密码成功回掉该方法
    @JavascriptInterface
    public void backLogin(String data){
        if(this.mBackLoginListener != null){
            this.mBackLoginListener.onBackLogin(data);
        }
    }
    public static interface CallBackSetHeadTitle {
        void onHeadTitle(WebBean bean);
    }

    public static interface CallBackSetBottomTitle{
        void onBottomTitle(String bean);
    }

    public static interface BackLoginListener{
        void onBackLogin(String data);
    }
}
