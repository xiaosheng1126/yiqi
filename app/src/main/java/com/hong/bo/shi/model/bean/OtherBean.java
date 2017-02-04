package com.hong.bo.shi.model.bean;

import android.support.annotation.DrawableRes;

/**
 * Created by andy on 2016/12/13.
 */

public class OtherBean {

    private String name;
    private String action;
    private @DrawableRes int drawableRes;

    public OtherBean(String name, String action, @DrawableRes int res) {
        this.name = name;
        this.action = action;
        this.drawableRes = res;
    }

    public OtherBean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }
}
