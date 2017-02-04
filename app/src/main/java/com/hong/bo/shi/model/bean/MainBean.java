package com.hong.bo.shi.model.bean;

import com.hong.bo.shi.app.Constants;

import io.realm.annotations.Ignore;

/**
 * Created by andy on 2016/12/12.
 */

public class MainBean {

    private String businessUrl;//业务模块webview使用的网址
    private String localBaseUrl;//后面接口使用的基类地址

    private String topTab1Url;//协秘书网址
    private int topTab1Count;//协秘书未读数量
    private String topTab2Url;//搜一搜网址
    private int topTab2Count;//搜一搜未读数量
    private String topTab3Url;//扫一扫网址
    private int topTab3Count;//扫一扫未读数量
    private String topTab4Url;//宏博士网址
    private int topTab4Count;//宏博士未读数量

    private String bottomTab1Url;//业务网址
    private int bottomTab1Count;//业务未读数量
    private String bottomTab2Url;//消息网址
    private int bottomTab2Count;//消息未读数量
    private String bottomTab3Url;//通讯录网址
    private int bottomTab3Count;//通讯录未读数量
    private String bottomTab4Url;//我的网址
    private int bottomTab4Count;//我的未读数量
    @Ignore
    private boolean isSuccess;

    public MainBean() {
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public MainBean(String data) {
        String[] split = data.split(Constants.SPLIT);
        if(split.length == 18){
            isSuccess = true;
            this.businessUrl = split[0];
            this.localBaseUrl = split[1];
            this.topTab1Url = split[2];
            this.topTab1Count = Constants.dataToInt(split[3], 0);
            this.topTab2Url = split[4];
            this.topTab2Count = Constants.dataToInt(split[5], 0);
            this.topTab3Url = split[6];
            this.topTab3Count = Constants.dataToInt(split[7], 0);
            this.topTab4Url = split[8];
            this.topTab4Count = Constants.dataToInt(split[9], 0);
            this.bottomTab1Url = split[10];
            this.bottomTab1Count = Constants.dataToInt(split[11], 0);
            this.bottomTab2Url = split[12];
            this.bottomTab2Count = Constants.dataToInt(split[13], 0);
            this.bottomTab3Url = split[14];
            this.bottomTab3Count = Constants.dataToInt(split[15], 0);
            this.bottomTab4Url = split[16];
            this.bottomTab4Count = Constants.dataToInt(split[17], 0);
        }else if(split.length == 16){
            isSuccess = true;
            this.topTab1Url = split[0];
            this.topTab1Count = Constants.dataToInt(split[1], 0);
            this.topTab2Url = split[2];
            this.topTab2Count = Constants.dataToInt(split[3], 0);
            this.topTab3Url = split[4];
            this.topTab3Count = Constants.dataToInt(split[5], 0);
            this.topTab4Url = split[6];
            this.topTab4Count = Constants.dataToInt(split[7], 0);
            this.bottomTab1Url = split[8];
            this.bottomTab1Count = Constants.dataToInt(split[9], 0);
            this.bottomTab2Url = split[10];
            this.bottomTab2Count = Constants.dataToInt(split[11], 0);
            this.bottomTab3Url = split[12];
            this.bottomTab3Count = Constants.dataToInt(split[13], 0);
            this.bottomTab4Url = split[14];
            this.bottomTab4Count = Constants.dataToInt(split[15], 0);
        }
    }

    public void update(MainBean bean){
        this.topTab1Url = bean.getTopTab1Url();
        this.topTab1Count = bean.getTopTab1Count();

        this.topTab2Url = bean.getTopTab2Url();
        this.topTab2Count = bean.getTopTab2Count();

        this.topTab3Url = bean.getTopTab3Url();
        this.topTab3Count = bean.getTopTab3Count();

        this.topTab4Url = bean.getTopTab4Url();
        this.topTab4Count = bean.getTopTab4Count();

        this.bottomTab1Url = bean.getBottomTab1Url();
        this.bottomTab1Count = bean.getBottomTab1Count();

        this.bottomTab2Url = bean.getBottomTab2Url();
        this.bottomTab2Count = bean.getBottomTab2Count();

        this.bottomTab3Url = bean.getBottomTab3Url();
        this.bottomTab3Count = bean.getBottomTab3Count();

        this.bottomTab4Url = bean.getBottomTab4Url();
        this.bottomTab4Count = bean.getBottomTab4Count();
    }
    public String getBusinessUrl() {
        return businessUrl;
    }

    public void setBusinessUrl(String businessUrl) {
        this.businessUrl = businessUrl;
    }

    public String getLocalBaseUrl() {
        return localBaseUrl;
    }

    public void setLocalBaseUrl(String localBaseUrl) {
        this.localBaseUrl = localBaseUrl;
    }

    public String getTopTab1Url() {
        return topTab1Url;
    }

    public void setTopTab1Url(String topTab1Url) {
        this.topTab1Url = topTab1Url;
    }

    public int getTopTab1Count() {
        return topTab1Count;
    }

    public void setTopTab1Count(int topTab1Count) {
        this.topTab1Count = topTab1Count;
    }

    public String getTopTab2Url() {
        return topTab2Url;
    }

    public void setTopTab2Url(String topTab2Url) {
        this.topTab2Url = topTab2Url;
    }

    public int getTopTab2Count() {
        return topTab2Count;
    }

    public void setTopTab2Count(int topTab2Count) {
        this.topTab2Count = topTab2Count;
    }

    public String getTopTab3Url() {
        return topTab3Url;
    }

    public void setTopTab3Url(String topTab3Url) {
        this.topTab3Url = topTab3Url;
    }

    public int getTopTab3Count() {
        return topTab3Count;
    }

    public void setTopTab3Count(int topTab3Count) {
        this.topTab3Count = topTab3Count;
    }

    public String getTopTab4Url() {
        return topTab4Url;
    }

    public void setTopTab4Url(String topTab4Url) {
        this.topTab4Url = topTab4Url;
    }

    public int getTopTab4Count() {
        return topTab4Count;
    }

    public void setTopTab4Count(int topTab4Count) {
        this.topTab4Count = topTab4Count;
    }

    public String getBottomTab1Url() {
        return bottomTab1Url;
    }

    public void setBottomTab1Url(String bottomTab1Url) {
        this.bottomTab1Url = bottomTab1Url;
    }

    public int getBottomTab1Count() {
        return bottomTab1Count;
    }

    public void setBottomTab1Count(int bottomTab1Count) {
        this.bottomTab1Count = bottomTab1Count;
    }

    public String getBottomTab2Url() {
        return bottomTab2Url;
    }

    public void setBottomTab2Url(String bottomTab2Url) {
        this.bottomTab2Url = bottomTab2Url;
    }

    public int getBottomTab2Count() {
        return bottomTab2Count;
    }

    public void setBottomTab2Count(int bottomTab2Count) {
        this.bottomTab2Count = bottomTab2Count;
    }

    public String getBottomTab3Url() {
        return bottomTab3Url;
    }

    public void setBottomTab3Url(String bottomTab3Url) {
        this.bottomTab3Url = bottomTab3Url;
    }

    public int getBottomTab3Count() {
        return bottomTab3Count;
    }

    public void setBottomTab3Count(int bottomTab3Count) {
        this.bottomTab3Count = bottomTab3Count;
    }

    public String getBottomTab4Url() {
        return bottomTab4Url;
    }

    public void setBottomTab4Url(String bottomTab4Url) {
        this.bottomTab4Url = bottomTab4Url;
    }

    public int getBottomTab4Count() {
        return bottomTab4Count;
    }

    public void setBottomTab4Count(int bottomTab4Count) {
        this.bottomTab4Count = bottomTab4Count;
    }

    @Override
    public String toString() {
        return "MainBean{" +
                "businessUrl='" + businessUrl + '\'' +
                ", localBaseUrl='" + localBaseUrl + '\'' +
                ", topTab1Url='" + topTab1Url + '\'' +
                ", topTab1Count=" + topTab1Count +
                ", topTab2Url='" + topTab2Url + '\'' +
                ", topTab2Count=" + topTab2Count +
                ", topTab3Url='" + topTab3Url + '\'' +
                ", topTab3Count=" + topTab3Count +
                ", topTab4Url='" + topTab4Url + '\'' +
                ", topTab4Count=" + topTab4Count +
                ", bottomTab1Url='" + bottomTab1Url + '\'' +
                ", bottomTab1Count=" + bottomTab1Count +
                ", bottomTab2Url='" + bottomTab2Url + '\'' +
                ", bottomTab2Count=" + bottomTab2Count +
                ", bottomTab3Url='" + bottomTab3Url + '\'' +
                ", bottomTab3Count=" + bottomTab3Count +
                ", bottomTab4Url='" + bottomTab4Url + '\'' +
                ", bottomTab4Count=" + bottomTab4Count +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
