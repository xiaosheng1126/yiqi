package com.hong.bo.shi.model.bean;

import android.support.annotation.DrawableRes;

/**
 * Created by andy on 2016/12/12.
 */

public class MainItembean {

    private @DrawableRes int selectDrawable;
    private @DrawableRes int drawable;
    private String text;
    private int unReadCount;

    public MainItembean(@DrawableRes int selectDrawable,
                        @DrawableRes int drawable, String text, int unReadCount) {
        this.selectDrawable = selectDrawable;
        this.drawable = drawable;
        this.text = text;
        this.unReadCount = unReadCount;
    }
    public MainItembean() {
    }

    public int getSelectDrawable() {
        return selectDrawable;
    }

    public void setSelectDrawable(int selectDrawable) {
        this.selectDrawable = selectDrawable;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
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
