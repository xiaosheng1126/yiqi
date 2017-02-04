package com.hong.bo.shi.model.bean;

import com.hong.bo.shi.app.Constants;

/**
 * Created by andy on 2016/12/12.
 */

public class WelecomBean {

    private String firingImg;//启动画面图片url
    private int firingTime;//启动画面图片显式时长秒）
    private String title;//首行文字
    private String noHttpImg;//网络不通畅图片url
    private String registerUrl;//注册网页的网址
    private String forgetPassUrl;//忘记密码的网址

    public String getRegisterUrl() {
        return registerUrl;
    }

    public void setRegisterUrl(String registerUrl) {
        this.registerUrl = registerUrl;
    }

    public String getForgetPassUrl() {
        return forgetPassUrl;
    }

    public void setForgetPassUrl(String forgetPassUrl) {
        this.forgetPassUrl = forgetPassUrl;
    }

    public String getFiringImg() {
        return firingImg;
    }

    public void setFiringImg(String firingImg) {
        this.firingImg = firingImg;
    }

    public int getFiringTime() {
        return firingTime;
    }

    public void setFiringTime(int firingTime) {
        this.firingTime = firingTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoHttpImg() {
        return noHttpImg;
    }

    public void setNoHttpImg(String noHttpImg) {
        this.noHttpImg = noHttpImg;
    }

    public WelecomBean(String data) {
        String[] split = data.split(Constants.SPLIT);
        this.firingImg = split[0];
        this.firingTime = Constants.dataToInt(split[1], 0);
        this.title = split[2];
        this.noHttpImg = split[3];
        this.registerUrl = split[4];
        this.forgetPassUrl = split[5];
    }

    public WelecomBean() {
    }

    @Override
    public String toString() {
        return "WelecomBean{" +
                "firingImg='" + firingImg + '\'' +
                ", firingTime=" + firingTime +
                ", title='" + title + '\'' +
                ", noHttpImg='" + noHttpImg + '\'' +
                ", registerUrl='" + registerUrl + '\'' +
                ", forgetPassUrl='" + forgetPassUrl + '\'' +
                '}';
    }
}
