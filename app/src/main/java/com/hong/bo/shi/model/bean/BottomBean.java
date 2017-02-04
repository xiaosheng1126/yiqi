package com.hong.bo.shi.model.bean;

import com.hong.bo.shi.app.Constants;

/**
 * Created by andy on 2017/1/14.
 */

public class BottomBean {

    private String text;
    private String type;
    private String selectUrl;
    private String unSelectUrl;
    private String extra;

    public BottomBean(String result) {
        String[] split = result.split(Constants.SPLIT);
        this.text = split[0];
        this.type = split[1];
        this.selectUrl = split[2];
        this.unSelectUrl = split[3];
        this.extra = split[4];
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSelectUrl() {
        return selectUrl;
    }

    public void setSelectUrl(String selectUrl) {
        this.selectUrl = selectUrl;
    }

    public String getUnSelectUrl() {
        return unSelectUrl;
    }

    public void setUnSelectUrl(String unSelectUrl) {
        this.unSelectUrl = unSelectUrl;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "BottomBean{" +
                "text='" + text + '\'' +
                ", type='" + type + '\'' +
                ", selectUrl='" + selectUrl + '\'' +
                ", unSelectUrl='" + unSelectUrl + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
