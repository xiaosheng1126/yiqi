package com.hong.bo.shi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.hong.bo.shi.R;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("NewApi")
public class NotifyUtil {

    private String mGuids;
    private NotificationManager nm;
    private Notification notification;
    private NotificationCompat.Builder cBuilder;
    private Context mContext;

    private static class NotifyEntity{
        private int notifyId;
        private int count;

        public int getNotifyId() {
            return notifyId;
        }

        public void setNotifyId(int notifyId) {
            this.notifyId = notifyId;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
    public static class NotifyCount{
        private static int mNotifyId = 10;
        public static NotifyCount getInstance(){
            return Single.single;
        }
        private Map<String, NotifyEntity> mGuids;

        private NotifyCount() {
            mGuids = new HashMap<>();
        }

        private static class Single{
            private static final NotifyCount single = new NotifyCount();
        }

        public void add(String guid){
            NotifyEntity entity = mGuids.get(guid);
            if(entity != null){
                entity.setCount(entity.getCount() + 1);
                mGuids.put(guid, entity);
            }else{
                entity = new NotifyEntity();
                entity.setNotifyId(mNotifyId);
                entity.setCount(1);
                mNotifyId++;
                mGuids.put(guid, entity);
            }
        }

        public int getCount(String guid){
            NotifyEntity entity = mGuids.get(guid);
            if(entity == null){
                return 0;
            }else{
                return entity.getCount();
            }
        }

        public int getNotifyId(String guid){
            NotifyEntity entity = mGuids.get(guid);
            if(entity == null){
                return 100;
            }else{
                return entity.getNotifyId();
            }
        }

        public void clear(){
            if(mGuids.size() > 0){
                mGuids.clear();
            }
        }
    }

    public NotifyUtil(Context context, String guid) {
        this.mGuids = guid;
        mContext = context;
        // 获取系统服务来初始化对象
        nm = (NotificationManager) mContext
                .getSystemService(Activity.NOTIFICATION_SERVICE);
        cBuilder = new NotificationCompat.Builder(mContext);
    }

    /**
     * 设置在顶部通知栏中的各种信息
     *
     * @param pendingIntent
     */
    private void setCompatBuilder(PendingIntent pendingIntent, boolean sound, boolean vibrate,
                                  boolean lights) {
        cBuilder.setContentIntent(pendingIntent);// 该通知要启动的Intent
        cBuilder.setSmallIcon(R.mipmap.ic_launcher);// 设置顶部状态栏的小图标
        cBuilder.setTicker("一起");// 在顶部状态栏中的提示信息
        cBuilder.setContentTitle("一起");//设置通知中心的标题
        cBuilder.setContentText("您有新消息请注意查收");//设置通知中心中的内容
        cBuilder.setWhen(System.currentTimeMillis());
		/*
         * 将AutoCancel设为true后，当你点击通知栏的notification后，它会自动被取消消失,
		 * 不设置的话点击消息后也不清除，但可以滑动删除
		 */
        cBuilder.setAutoCancel(true);
        // 将Ongoing设为true 那么notification将不能滑动删除
        // notifyBuilder.setOngoing(true);
        /*
         * 从Android4.1开始，可以通过以下方法，设置notification的优先级，
		 * 优先级越高的，通知排的越靠前，优先级低的，不会在手机最顶部的状态栏显示图标
		 */
        cBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        /*
         * Notification.DEFAULT_ALL：铃声、闪光、震动均系统默认。
		 * Notification.DEFAULT_SOUND：系统默认铃声。
		 * Notification.DEFAULT_VIBRATE：系统默认震动。
		 * Notification.DEFAULT_LIGHTS：系统默认闪光。
		 * notifyBuilder.setDefaults(Notification.DEFAULT_ALL);
		 */
        int defaults = 0;

        if (sound) {
            defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrate) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (lights) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }

        cBuilder.setDefaults(defaults);
    }

    /**
     * 普通的通知
     * <p/>
     * 1. 侧滑即消失，下拉通知菜单则在通知菜单显示
     *
     * @param pendingIntent
     */
    public void notify_normal_singline(PendingIntent pendingIntent, boolean sound, boolean vibrate, boolean lights) {
        setCompatBuilder(pendingIntent,sound, vibrate, lights);
        sent();
    }



    /**
     * 发送通知
     */
    private void sent() {
        notification = cBuilder.build();
        // 发送该通知
        nm.notify(NotifyCount.getInstance().getNotifyId(mGuids), notification);
    }

    /**
     * 根据id清除通知
     */
    public void clear() {
        // 取消通知
        nm.cancelAll();
        NotifyUtil.NotifyCount.getInstance().clear();
    }
}
