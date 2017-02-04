package com.hong.bo.shi.model.bean;

import com.hong.bo.shi.app.Constants;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andy on 2016/12/9.
 */

public class UserInfo extends RealmObject{

    private String avaturl;//人员头像
    private String name;//人员名称
    private int grade;//人员星级(0表示无星级 有星级范围1-5),
    private boolean isOnline;//人员是否在线（0表示不在线 1表示在线）该值只取1,
    @PrimaryKey
    private String guid;//人员GUID,
    private String department;//人员部门
    private String mobile;//人员电话号码
    private String userId;//人员USERID
    private int gender;//人员性别（0表示女性 1表示男性）
    private String orgguid;//人员公司orgguid
    private String orgid;//人员公司orgid
    private String orgname;//人员公司名称orgname
    private int isCompany;//是否本公司人员（1 是本公司人员 0 是云端专家 2 其他）
    private String qRCodeUrl;//二维码图片

    public UserInfo(String data) {
        String[] datas = data.split(Constants.SPLIT);
        this.avaturl = datas[0];
        this.name = datas[1];
        this.grade = Constants.dataToInt(datas[2], 0);
        this.isOnline = Constants.dataToInt(datas[3], 1) == 1;
        this.guid = datas[4];
        this.department = datas[5];
        this.mobile = datas[6];
        this.userId = datas[7];
        this.gender = Constants.dataToInt(datas[8], 0);
        this.orgguid = datas[9];
        this.orgid = datas[10];
        this.orgname = datas[11];
        this.isCompany = Constants.dataToInt(datas[12], 1);
        this.qRCodeUrl = datas[13];
    }

    public UserInfo() {
    }

    public String getqRCodeUrl() {
        return qRCodeUrl;
    }

    public void setqRCodeUrl(String qRCodeUrl) {
        this.qRCodeUrl = qRCodeUrl;
    }

    public String getAvaturl() {
        return avaturl;
    }

    public void setAvaturl(String avaturl) {
        this.avaturl = avaturl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getOrgguid() {
        return orgguid;
    }

    public void setOrgguid(String orgguid) {
        this.orgguid = orgguid;
    }

    public String getOrgid() {
        return orgid;
    }

    public void setOrgid(String orgid) {
        this.orgid = orgid;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public boolean getIsCompany() {
        return isCompany == 1;
    }

    public void setIsCompany(int isCompany) {
        this.isCompany = isCompany;
    }

    @Override
    public boolean equals(Object obj) {
        return guid.equals(((UserInfo)obj).guid);
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "avaturl='" + avaturl + '\'' +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                ", isOnline=" + isOnline +
                ", guid='" + guid + '\'' +
                ", department='" + department + '\'' +
                ", mobile='" + mobile + '\'' +
                ", userId='" + userId + '\'' +
                ", gender=" + gender +
                ", orgguid='" + orgguid + '\'' +
                ", orgid='" + orgid + '\'' +
                ", orgname='" + orgname + '\'' +
                ", isCompany=" + isCompany +
                ", qRCodeUrl=" + qRCodeUrl +
                '}';
    }
}
