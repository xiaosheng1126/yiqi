package com.hong.bo.shi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.model.bean.HomeOperationBean;
import com.hong.bo.shi.model.bean.WelecomBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/8/4.
 */
public class PreferencesUtils {

    private static final String CONFIG = "config";

    public static void setAccount(String account){
        setString("account", account);
    }

    public static String getAccount(){
       return getString("account");
    }

    public static void setPass(String password){
        setString("password", password);
    }

    public static String getPass(){
        return getString("password");
    }

    public static SharedPreferences getSharedPreferences(Context context, String name){
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static void setGuid(String guid){
       setString("guid", guid);
    }

    public static void setMainUrl(String url){
        setString("main_url", url);
    }

    public static String getMainUrl(){
        return getString("main_url");
    }

    public static void setLocalUrl(String url){
        setString("local_url", url);
    }

    public static String getLocalUrl(){
        return getString("local_url");
    }

    public static void setString(String key, String value){
        SharedPreferences preferences = getSharedPreferences(App.getInstance(), CONFIG);
        preferences.edit().putString(key, value == null ? "" : value).apply();
    }

    public static String getString(String key){
        return getSharedPreferences(App.getInstance(), CONFIG).getString(key, "");
    }

    public static void setHomeSearchUrl(String searchUrl){
        setString("home_search_url", searchUrl);
    }

    public static String getHomeSearchUrl(){
        return getString("home_search_url");
    }

    public static void setHomeOperation(String value){
        setString("home_operation_data", value);
    }

    public static List<HomeOperationBean> getHomeOperation(){
        String data = getString("home_operation_data");
        if(TextUtils.isEmpty(data) || Constants.NO_MSG.equals(data)){
            return null;
        }
        String[] split = data.split(Constants.STRIP_SPLIT);
        List<HomeOperationBean> list = new ArrayList<HomeOperationBean>();
        for (String bean : split){
            if(!Constants.NO_MSG.equals(bean)) {
                list.add(new HomeOperationBean(bean));
            }
        }
        return list;
    }

    public static String getGuid(){
        return getString("guid");
    }

    public static String[] getMainTopUrl(){
        String[] urls = new String[4];
        for (int i = 0; i < 4; i++) {
            urls[i] = getString(String.format("main_top_%d_url", i + 1));
        }
        return urls;
    }

    public static void setMainTopUrl(String[] urls){
        for (int i = 0; i < 4; i++) {
            setString(String.format("main_top_%d_url", i + 1), urls[i]);
        }
    }

    public static int[] getMainTopCount(){
        int[] counts = new int[4];
        for (int i = 0; i < 4; i++) {
            String string = getString(String.format("main_top_%d_count", i + 1));
            if(TextUtils.isEmpty(string)){
                counts[i] = 0;
            }else{
                counts[i] = Integer.valueOf(string);
            }
        }
        return counts;
    }

    public static void setMainTopUnCount(String[] count){
        for (int i = 0; i < 4; i++) {
            setString(String.format("main_top_%d_count", i + 1), count[i]);
        }
    }

    public static void setMainBottomUnCount(String[] count){
        for (int i = 0; i < 4; i++) {
            setString(String.format("main_bottom_%d_count", i + 1), count[i]);
        }
    }

    public static int[] getMainBottomCount(){
        int[] counts = new int[4];
        for (int i = 0; i < 4; i++) {
            String string = getString(String.format("main_bottom_%d_count", i + 1));
            if(TextUtils.isEmpty(string)){
                counts[i] = 0;
            }else{
                counts[i] = Integer.valueOf(string);
            }
        }
        return counts;
    }

    public static void clearMain(){
        for (int i = 0; i < 4; i++) {
            setString(String.format("main_bottom_%d_count", i + 1), "");
            setString(String.format("main_top_%d_count", i + 1), "");
            setString(String.format("main_top_%d_url", i + 1), "");
        }
    }

    public static void setWelecomeData(String data){
        setString("welecom_data", data);
    }

    public static WelecomBean getWelecomBean(){
        String data = getString("welecom_data");
        if(TextUtils.isEmpty(data))return null;
        return new WelecomBean(data);
    }

    public static void setToken(String token){
        setString("token_id", token);
    }

    public static String getToken(){
        return getString("token_id");
    }

    public static void setVideoTime(String time){
        setString(String.format("%s_video_time", PreferencesUtils.getGuid()), time);
    }

    public static String getVideoTime(){
        String video_time = getString(String.format("%s_video_time", PreferencesUtils.getGuid()));
        if(TextUtils.isEmpty(video_time)){
            return "10";
        }
        return video_time;
    }

    public static void setAudioTime(String time){
        setString(String.format("%s_audio_time", PreferencesUtils.getGuid()), time);
    }

    public static String getAudioTime(){
        String audio_time = getString(String.format("%s_audio_time", PreferencesUtils.getGuid()));
        if(TextUtils.isEmpty(audio_time)){
            return "40";
        }
        return audio_time;
    }

}
