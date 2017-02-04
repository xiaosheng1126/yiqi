package com.hong.bo.shi.model.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.hong.bo.shi.utils.ParseUtils;

/**
 * Created by andy on 2017/1/7.
 */

public class WebBean implements Parcelable {

    private String title;
    private String backTitle;
    private String pagerUrl;
    private String mUrl;
    private boolean showBack;
    private String other;

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        if(other != null){
            other = other.trim();
        }
        if(!ParseUtils.isError(other) && !"undefined".equals(other)) {
            this.other = other;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackTitle() {
        return backTitle;
    }

    public void setBackTitle(String backTitle) {
        this.backTitle = backTitle;
    }

    public String getPagerUrl() {
        return pagerUrl;
    }

    public void setPagerUrl(String pagerUrl) {
        this.pagerUrl = pagerUrl;
    }

    public boolean isShowBack() {
        return showBack;
    }

    public void setShowBack(int showBack) {
        this.showBack = showBack == 0;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.backTitle);
        dest.writeString(this.pagerUrl);
        dest.writeByte(this.showBack ? (byte) 1 : (byte) 0);
        dest.writeString(this.mUrl);
        dest.writeString(this.other);
    }

    public WebBean() {
    }

    public WebBean(String title, String backTitle, String pagerUrl, int showBack) {
        this.title = title;
        this.backTitle = backTitle;
        this.pagerUrl = pagerUrl;
        this.showBack = showBack == 0;
    }

    protected WebBean(Parcel in) {
        this.title = in.readString();
        this.backTitle = in.readString();
        this.pagerUrl = in.readString();
        this.showBack = in.readByte() != 0;
        this.mUrl = in.readString();
        this.other = in.readString();
    }

    public static final Parcelable.Creator<WebBean> CREATOR = new Parcelable.Creator<WebBean>() {
        @Override
        public WebBean createFromParcel(Parcel source) {
            return new WebBean(source);
        }

        @Override
        public WebBean[] newArray(int size) {
            return new WebBean[size];
        }
    };

    @Override
    public String toString() {
        return "WebBean{" +
                "title='" + title + '\'' +
                ", backTitle='" + backTitle + '\'' +
                ", pagerUrl='" + pagerUrl + '\'' +
                ", showBack=" + showBack +
                ", mUrl=" + mUrl +
                ", other=" + other +
                '}';
    }
}
