package com.hong.bo.shi.app;

import android.text.TextUtils;

import com.hong.bo.shi.utils.PreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_1016;
import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_1020;
import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_1021;
import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_901;
import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_902;
import static com.hong.bo.shi.app.RequestUrl.Type.TYPE_904;

/**
 * Created by andy on 2016/12/22.
 * 所有的请求接口都封装这里
 */
public final class RequestUrl {

    public static class Type {//接口数据类型
        /**下面3个type基类地址使用CLOND_BASE_URL*/
        public static final String TYPE_901 = "901";//欢迎界面请求数据的type
        public static final String TYPE_902 = "902";//获取用户头像的type
        public static final String TYPE_904 = "904";//登录的type
        /**下面其他type基类地址使用LOCAL_BASE_URL 登录成功后返回*/
        public static final String TYPE_1020 = "1020";//获取群信息，通讯录的type
        public static final String TYPE_1021 = "1021";//实时获取新数据的type(实时接口开启定时器定时放送)
        public static final String TYPE_1016 = "1016";//删除消息的type
        public static final String TYPE_1004 = "1004";//发送消息的type
        public static final String TYPE_1022 = "1022";//修改密码,用户信息
        public static final String TYPE_1023 = "1023";//修改群名称和群公告
        public static final String TYPE_1017 = "1017";//退出群的type
        public static final String TYPE_1012 = "1012";//创建群，邀请群好友的type
        public static final String TYPE_1024 = "1024";//获取指定群未读消息type(实时接口开启定时器定时放送)
        public static final String TYPE_1025 = "1025";//获取指定群所有的聊天信息的type
        public static final String TYPE_1030 = "1030";//获取群诊断信息的type
        public static final String TYPE_1026 = "1026";//获取指定群的联系人信息type
        public static final String TYPE_1032 = "1032";//转发信息的type
    }

    /**云端基类url*/
    public static final String CLOND_BASE_URL = "http://139.196.204.234:5217/17Intell/mobileweb/postFile.jsp?type=";
    /**本地基类url由登录返回*/
    public static String LOCAL_BASE_URL = "";

    public static String getLocalBaseUrl(){
        if(TextUtils.isEmpty(LOCAL_BASE_URL)){
            LOCAL_BASE_URL = PreferencesUtils.getLocalUrl();
        }
        return LOCAL_BASE_URL;
    }

    public static void setLocalBaseUrl(String url) {
        LOCAL_BASE_URL = url;
    }

    /**
     * 获取类型type=901的url(获取欢迎界面数据)
     * @return
     */
    public static String getType901Url() {
        return String.format("%s%s", CLOND_BASE_URL, TYPE_901);
    }

    /**
     *  获取类型type=902的url(根据用户名获取头像)
     * @param account 用户名
     * @return
     */
    public static String getType902Url(String account) {
        return String.format("%s%s&17Intellu=%s", CLOND_BASE_URL, TYPE_902, account);
    }

    /**
     * 获取类型type=904的url(登录接口)
     * @param account  用户名
     * @param password 密码
     * @return
     */
    public static String getType904Url(String account, String password) {
        return String.format("%s%s&17Intellu=%s&17Intellp=%s", CLOND_BASE_URL, TYPE_904, account, password);
    }

    /**
     * 获取类型type=1020的url
     * 登录成功后进入主程序调用一次
     * @return
     */
    public static String getType1020Url() {
        //参数：用户guid
        return String.format("%s?type=%s&17Intellu=%s", getLocalBaseUrl(), TYPE_1020,
                getUserGuid());
    }

    /**
     * 获取类型type=1020的url
     *1020 调用成功后开始调用,这是一个实时接口,主要获取改变的信息
     * @return
     */
    public static String getType1021Url() {
        //参数：1:用户guid 2:登录返回的tokenId
        return String.format("%s?type=%s&17Intellu=%s&tokenId=%s", getLocalBaseUrl(), TYPE_1021,
                getUserGuid(), PreferencesUtils.getToken());
    }

    /**
     * 获取类型type=1016的url(删除群聊天信息)
     * @param guid 消息guid
     * @return
     */
    public static String getType1016Url(String guid) {
        //参数：1:用户guid 2:消息guid
        return String.format("%s?type=%s&17Intellu=%s&msgguid=", getLocalBaseUrl(), TYPE_1016, getUserGuid(), guid);
    }

    /**
     * 获取类型type=1004的url(发送群消息)
     * @return
     */
    public static String getType1004Url() {
        return String.format("%s?type=%s", getLocalBaseUrl(), Type.TYPE_1004);
    }

    /**
     * 获取类型type=1022的url(修改密码，用户信息)
     * @return
     */
    public static String getType1022Url(){
        return String.format("%s?type=%S", getLocalBaseUrl(), Type.TYPE_1022);
    }

    /**
     * 获取类型type=1023的url(修改群名称,群公告的接口url)
     * @return
     */
    public static String getType1023Url(){
        return String.format("%s?type=%S", getLocalBaseUrl(), Type.TYPE_1023);
    }

    /**
     * 获取类型type=1017的url(退出群的接口)
     * @param guid 群guid
     * @return
     */
    public static String getType1017Url(String guid){
        return String.format("%s?type=%s&17Intellu=%s&17Intellg=%s", getLocalBaseUrl(), Type.TYPE_1017, getUserGuid(), guid);
    }

    /**
     * 获取类型type=1012的url(新建多人群使用调用)
     * @return
     */
    public static String getType1012Url(Map<String, String> map){
        //&17Intellu=用户GUID&17Intellus=guids&17Intellg=群GUID
        StringBuilder builder = new StringBuilder(64);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        return String.format("%s?type=%s&17Intellu=%s&17Intellus=%s&17Intellg= ",
                getLocalBaseUrl(), Type.TYPE_1012, getUserGuid(), builder.toString());
    }

    /**
     * 获取类型type=1012的url(快速创建会话时调用)
     * @return
     */
    public static String getType1012Url(String guid){
        //&17Intellu=用户GUID&17Intellus=guids&17Intellg=群GUID
        return String.format("%s?type=%s&17Intellu=%s&17Intellus=%s&17Intellg= ",
                getLocalBaseUrl(), Type.TYPE_1012, getUserGuid(), guid);
    }

    /**
     * 获取类型type=1012的url(新增群成员时候调用)
     * @return
     */
    public static String getType1012Url(Map<String, String> map, String groupGuid){
        //&17Intellu=用户GUID&17Intellus=guids&17Intellg=群GUID
        StringBuilder builder = new StringBuilder(64);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.append(entry.getKey()).append(",");
        }
        builder.delete(builder.length() - 1, builder.length());
        return String.format("%s?type=%s&17Intellu=%s&17Intellus=%s&17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1012, getUserGuid(), builder.toString(), groupGuid);
    }

    /**
     * 获取类型type=1024的url(获取指定群未读消息的接口)
     * @param groupGuid 群guid
     * @return
     */
    public static String getType1024Url(String groupGuid){
        return String.format("%s?type=%s&17Intellu=%s&17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1024, getUserGuid(), groupGuid);
    }

    /**
     * 获取类型type=1024的url(获取指定群所有消息的接口)
     * @param groupGuid 群guid
     * @return
     */
    public static String getType1025Url(String groupGuid){
        return String.format("%s?type=%s&17Intellu=%s&17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1025, getUserGuid(), groupGuid);
    }

    /**
     * 获取类型type=1024的url(获取群诊断信息的接口)
     * @param groupGuid 群guid
     * @return
     */
    public static String getType1030Url(String groupGuid){
        return String.format("%s?type=%s&17Intellu=%s&17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1030, getUserGuid(), groupGuid);
    }
    /**
     * 获取类型type=1024的url(获取指定群成员的接口)
     * @param groupGuid 群guid
     * @return
     */
    public static String getType1026Url(String groupGuid){
        return String.format("%s?type=%s&17Intellu=%s&17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1026, getUserGuid(), groupGuid);
    }

    public static String getType1032Url(String msgGuid, String forwardGuids){
        return String.format("%s?type=%s&17Intellu=%s&17Intellm=%s17Intellg=%s",
                getLocalBaseUrl(), Type.TYPE_1032, getUserGuid(), msgGuid, forwardGuids);
    }

    /**
     * 根据首页搜索框输入的内容拼接首页搜索的网址
     * @param text 搜索内容
     * @return
     */
    public static String getHomeSearchUrl(String text){
        try {
            return String.format("%s%s", PreferencesUtils.getHomeSearchUrl(),
                    RequestHeader.encode(text));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getUserGuid() {
        if(App.getInstance().getUserInfo() == null){
            return "";
        }
        return App.getInstance().getUserInfo().getGuid();
    }

}
