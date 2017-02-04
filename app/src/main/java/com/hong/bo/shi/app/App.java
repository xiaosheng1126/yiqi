package com.hong.bo.shi.app;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.utils.FileUtils;
import com.hong.bo.shi.widget.fresco.ImagePipelineConfigFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

public class App extends MultiDexApplication {

    private static App instance;
    private Set<Activity> allActivities;
    private UserInfo userInfo;
    private Toast mNetworkToast;

    public static App getInstance() {
        return instance;
    }

    /**
     * 获取用户自己信息
     * @return
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        if(userInfo != null){
            initRealm(userInfo);
            RealmHelper.save(userInfo);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //Fresco初始化
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        registerActivityLifecycleCallbacks(new LifecycleCallbacks());
    }

    public void registerActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<Activity>();
        }
        allActivities.add(act);
    }

    public void unregisterActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    if (act != null && !act.isFinishing())
                        act.finish();
                }
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void showNetworkError(){
        if(mNetworkToast != null){
            mNetworkToast.cancel();
            mNetworkToast = null;
        }
        mNetworkToast = Toast.makeText(instance, "网络有问题，请检测网络连接", Toast.LENGTH_SHORT);
        mNetworkToast.show();
    }

    /**
     * 初始化Realm数据库
     * @param userInfo
     */
    private void initRealm(UserInfo userInfo) {
        String dbName = String.format("%s.realm", userInfo.getGuid());
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .name(dbName)
                .schemaVersion(1)
                .rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    /**
     * freso缓存图片的基路径
     * @return
     */
    public File getFresoCache() {
        return FileUtils.getExternalCacheDirectory(this, Environment.DIRECTORY_PICTURES);
    }

    /**
     * 获取录音的缓存目录
     * @return
     */
    public String getRecoderAudio() {
        return FileUtils.getExternalCacheDirectory(this, Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 获取视频的缓存目录
     * @return
     */
    public String getRecoderVideo() {
        return FileUtils.getExternalCacheDirectory(this, Environment.DIRECTORY_MOVIES).getAbsolutePath();
    }

    /**
     * 获取图片的缓存目录
     * @return
     */
    public String getPictureCache() {
        return FileUtils.getExternalCacheDirectory(this, Environment.DIRECTORY_ALARMS).getAbsolutePath();
    }

    /**
     * 检测网络连接状态
     * @return true 网络可用 false 网络不可用
     */
    public boolean isNetworkAvailable(){
        //获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    //当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }


    private int appCount;

    /**
     * 方法3：通过ActivityLifecycleCallbacks来批量统计Activity的生命周期，来做判断，
     * 此方法在API 14以上均有效，但是需要在Application中注册此回调接口
     * 必须：
     * 1. 自定义Application并且注册ActivityLifecycleCallbacks接口
     * 2. AndroidManifest.xml中更改默认的Application为自定义
     * 3. 当Application因为内存不足而被Kill掉时，这个方法仍然能正常使用
     * 。虽然全局变量的值会因此丢失，但是再次进入App时候会重新统计一次的
     * @return
     */
    public boolean isForeground() {
        return appCount > 0;
    }

    private class LifecycleCallbacks implements ActivityLifecycleCallbacks{
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }
        @Override
        public void onActivityStarted(Activity activity) {
            appCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }
        @Override
        public void onActivityPaused(Activity activity) {
        }
        @Override
        public void onActivityStopped(Activity activity) {
            appCount--;
        }
        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }
        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }


}
