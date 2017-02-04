package com.hong.bo.shi.model.bean;

import android.support.annotation.DrawableRes;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessItemBean {

    private String mActionUrl;
    private @DrawableRes int drawableRes;
    private String text;
    private int unReadCount;

    public BusinessItemBean(String mActionUrl, int drawableRes, String text, int unReadCount) {
        this.mActionUrl = mActionUrl;
        this.drawableRes = drawableRes;
        this.text = text;
        this.unReadCount = unReadCount;
    }

    public BusinessItemBean() {
    }

    public String getmActionUrl() {
        return mActionUrl;
    }

    public void setmActionUrl(String mActionUrl) {
        this.mActionUrl = mActionUrl;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }
}
