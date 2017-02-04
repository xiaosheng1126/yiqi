package com.hong.bo.shi.model.bean;

import com.hong.bo.shi.app.Constants;

/**
 * Created by andy on 2017/1/6.
 */

public class HomeOperationBean {

    private String mPicUrl;
    private String mText;
    private String mUrl;

    public HomeOperationBean(String data) {
        String[] split = data.split(Constants.SPLIT);
        mPicUrl = split[0];
        mUrl = split[1];
        mText = split[2];
    }

    public String getPicUrl() {
        return mPicUrl;
    }

    public void setPicUrl(String mPicUrl) {
        this.mPicUrl = mPicUrl;
    }

    public String getText() {
        return mText;
    }

    public void setText(String mText) {
        this.mText = mText;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }
}
