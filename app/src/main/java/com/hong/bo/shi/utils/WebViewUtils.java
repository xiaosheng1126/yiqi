package com.hong.bo.shi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.model.bean.JsToJavaObject;
import com.hong.bo.shi.model.bean.WebBean;

/**
 * Created by andy on 2017/1/15.
 */

public class WebViewUtils {

    public static void loadUrl(WebView webView, String url){
        WebSettings webSettings = webView.getSettings();
        if(App.getInstance().isNetworkAvailable()){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//缓存模式
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
        }
        webView.loadUrl(url);
    }
    public static void setWebView(WebView webView, final WebClientListener listener){
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);//开启DOM形式存储
        webSettings.setDatabaseEnabled(true);   //开启数据库形式存储
        String appCacheDir = App.getInstance().getApplicationContext().
                getDir("cache", Context.MODE_PRIVATE).getPath();   //缓存数据的存储地址
        webSettings.setAppCachePath(appCacheDir);
        webSettings.setAppCacheEnabled(true);  //开启缓存功能
        if(App.getInstance().isNetworkAvailable()){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//缓存模式
        }else {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//缓存模式
        }
        webSettings.setAllowFileAccess(true);
        //设置与js 交互的对象
        JsToJavaObject jsToJavaObject = new JsToJavaObject();
        jsToJavaObject.setCallBackSetBottomTitle(new JsToJavaObject.CallBackSetBottomTitle() {
            @Override
            public void onBottomTitle(String bean) {
                if(listener != null){
                    listener.setBottomTitle(bean);
                }
            }
        });
        jsToJavaObject.setCallBackSetHeadTitle(new JsToJavaObject.CallBackSetHeadTitle() {
            @Override
            public void onHeadTitle(WebBean bean) {
                if(listener != null){
                    listener.setHeadTitle(bean);
                }
            }
        });
        jsToJavaObject.setBackLoginListener(new JsToJavaObject.BackLoginListener() {
            @Override
            public void onBackLogin(String data) {
                if(listener != null){
                    listener.backLogin(data);
                }
            }
        });
        webView.addJavascriptInterface(jsToJavaObject, "nativeMethod");
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(listener != null){
                    return  listener.shouldOverrideUrlLoading(view, url);
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if(listener != null){
                    listener.onPageStarted(view, url, favicon);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(listener != null){
                    listener.onPageFinished(view, url);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(listener != null){
                    listener.onReceivedError(view, errorCode, description, failingUrl);
                }
            }
        });
    }

    public static interface WebClientListener{

        public boolean shouldOverrideUrlLoading(WebView view, String url);

        public void onPageStarted(WebView view, String url, Bitmap favicon);

        public void onPageFinished(WebView view, String url);

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

        public void setHeadTitle(WebBean webBean);

        public void setBottomTitle(String data);

        public void backLogin(String data);
    }
}
