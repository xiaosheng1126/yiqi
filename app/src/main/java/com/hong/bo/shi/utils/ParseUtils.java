package com.hong.bo.shi.utils;

import android.app.PendingIntent;
import android.content.Intent;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.model.http.RetrofitHelper;
import com.hong.bo.shi.ui.activitys.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/10.
 */

public class ParseUtils {

    private static long mBestNewTime;

    public static void parseConstact(String type, String localConstact, String clondConstact) {
        List<UserInfo> localUsres = null;
        List<UserInfo> clondUsers = null;
        KLog.d(type, "----------本地联系人-------------");
        if (!Constants.NO_MSG.equals(localConstact.trim())) {
            String[] split = localConstact.split(Constants.STRIP_SPLIT);
            localUsres = new ArrayList<>();
            for (String s : split) {
                UserInfo userInfo = new UserInfo(s);
                localUsres.add(userInfo);
                KLog.json(type, userInfo.toString());
            }
        } else {
            KLog.d(type, "----------本地联系人没有-------------");
        }
        KLog.d(type, "----------云端联系人-------------");
        if (!Constants.NO_MSG.equals(clondConstact.trim())) {
            String[] split = clondConstact.split(Constants.STRIP_SPLIT);
            clondUsers = new ArrayList<>();
            for (String s : split) {
                UserInfo userInfo = new UserInfo(s);
                clondUsers.add(userInfo);
                KLog.json(type, userInfo.toString());
            }
        } else {
            KLog.d(type, "----------云端联系人没有-------------");
        }
        if (localConstact != null || clondConstact != null) {
            RealmHelper.updateContract(localUsres, clondUsers);
        }
    }


    public static boolean requestLogin(String account, String password) {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType904Url(account, password));
            if (isError(string)) {
                KLog.d(RequestUrl.Type.TYPE_904, "登录失败 e = " + string);
                return false;
            }
            KLog.d(RequestUrl.Type.TYPE_904, "登录成功");
            //分类分隔符
            String[] datas = string.split(Constants.CLASSIF_SPLIT);
            UserInfo userInfo = new UserInfo(datas[0]);
            KLog.d(RequestUrl.Type.TYPE_904, "用户信息");
            KLog.json(RequestUrl.Type.TYPE_904, userInfo.toString());
            PreferencesUtils.setGuid(userInfo.getGuid());
            App.getInstance().setUserInfo(userInfo);
            String[] split = datas[1].split(Constants.SPLIT);
            KLog.d(RequestUrl.Type.TYPE_904, "main url = " + split[0]);
            KLog.d(RequestUrl.Type.TYPE_904, "local base url = " + split[1]);
            KLog.d(RequestUrl.Type.TYPE_904, "home search url = " + split[2]);
            PreferencesUtils.setMainUrl(split[0]);
            PreferencesUtils.setLocalUrl(split[1]);
            RequestUrl.setLocalBaseUrl(split[1]);
            PreferencesUtils.setHomeSearchUrl(split[2]);
            if (datas.length >= 3) {
                KLog.d(RequestUrl.Type.TYPE_904, "home operation  = " + datas[2]);
                PreferencesUtils.setHomeOperation(datas[2]);
            }
            if (datas.length >= 4) {
                KLog.d(RequestUrl.Type.TYPE_904, "token  = " + datas[3]);
                PreferencesUtils.setToken(datas[3]);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            KLog.json(RequestUrl.Type.TYPE_904, "error e = " + e.toString());
        }
        return false;
    }

    public static String requestType1021() {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1021Url());
            if (Constants.DROPPED.equals(string)) {
                KLog.d(RequestUrl.Type.TYPE_1021, "你已下线 " + string);
            }
            if (isError(string) || Constants.DROPPED.equals(string)) {
                KLog.d(RequestUrl.Type.TYPE_1021, "数据获取失败 e = " + string);
                return string;
            }
            KLog.d(RequestUrl.Type.TYPE_1021, "数据获取成功");
            String[] split = string.split(Constants.CLASSIF_SPLIT);
            if (!Constants.NO_MSG.equals(split[0])) {
                String[] main = split[0].split(Constants.SPLIT);
                if (main.length >= 8) {
                    KLog.d(RequestUrl.Type.TYPE_1021, "----------顶部四个itme 未读数 -------------");
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_top_count_1 = " + main[0]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_top_count_2 = " + main[1]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_top_count_3 = " + main[2]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_top_count_4 = " + main[3]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "---------- 底部四个itme 未读数 -------------");
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_bottom_count_1 = " + main[4]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_bottom_count_2 = " + main[5]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_bottom_count_3 = " + main[6]);
                    KLog.d(RequestUrl.Type.TYPE_1021, "main_bottom_count_4 = " + main[7]);
                    PreferencesUtils.setMainTopUnCount(new String[]{main[0], main[1], main[2], main[3]});
                    PreferencesUtils.setMainBottomUnCount(new String[]{main[4], main[5], main[6], main[7]});
                }
            }
            if (split.length >= 3) {
                ParseUtils.parseConstact(RequestUrl.Type.TYPE_1021, split[1], split[2]);
            }
            if (split.length > 3) {
                List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
                KLog.d(RequestUrl.Type.TYPE_1021, "-----群组信息-----------");
                KLog.d(RequestUrl.Type.TYPE_1021, "size = " + (split.length - 3));
                boolean isNotify = true;
                for (int i = 3; i < split.length; i++) {
                    if (!Constants.NO_MSG.equals(split[i])) {
                        GroupInfo groupInfo = new GroupInfo(split[i]);
                        groupInfos.add(groupInfo);
                        if (!App.getInstance().isForeground() && groupInfo.getCreateTime() > mBestNewTime) {
                            mBestNewTime = groupInfo.getCreateTime();
                            if (isNotify) {
                                isNotify = false;
                                notify(groupInfo);
                            }
                        }
                        KLog.json(RequestUrl.Type.TYPE_1021, groupInfo.toString());
                    } else {
                        KLog.d(RequestUrl.Type.TYPE_1021, split[i]);
                    }
                }
                RealmHelper.saveGroupsList(groupInfos, false);
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            KLog.json(RequestUrl.Type.TYPE_1021, "error e = " + e.toString());
        }
        return Constants.ERROR_1;
    }

    public static boolean requestType1020() {
        try {
            String string = RetrofitHelper.get(RequestUrl.getType1020Url());
            if (isError(string)) {
                KLog.d(RequestUrl.Type.TYPE_1020, "数据获取失败 e = " + string);
                return false;
            }
            KLog.d(RequestUrl.Type.TYPE_1020, "数据获取成功");
            String[] split = string.split(Constants.CLASSIF_SPLIT);
            if (!Constants.NO_MSG.equals(split[0])) {
                String[] main = split[0].split(Constants.SPLIT);
                if (main.length >= 16) {
                    KLog.d(RequestUrl.Type.TYPE_1020, "----------顶部四个itme URL -------------");
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_url_1 = " + main[0]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_url_2 = " + main[2]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_url_3 = " + main[4]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_url_4 = " + main[6]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "----------顶部四个itme 未读数 -------------");
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_count_1 = " + main[1]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_count_2 = " + main[3]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_count_3 = " + main[5]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_top_count_4 = " + main[7]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "---------- 底部四个itme 未读数 -------------");
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_bottom_count_1 = " + main[9]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_bottom_count_2 = " + main[11]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_bottom_count_3 = " + main[13]);
                    KLog.d(RequestUrl.Type.TYPE_1020, "main_bottom_count_4 = " + main[15]);
                    PreferencesUtils.setMainTopUrl(new String[]{main[0], main[2], main[4], main[6]});
                    PreferencesUtils.setMainTopUnCount(new String[]{main[1], main[3], main[5], main[7]});
                    PreferencesUtils.setMainBottomUnCount(new String[]{main[9], main[11], main[13], main[15]});
                }
            }
            if (split.length >= 3) {
                ParseUtils.parseConstact(RequestUrl.Type.TYPE_1020, split[1], split[2]);
            }
            if (split.length > 3) {
                KLog.d(RequestUrl.Type.TYPE_1020, "-----群组信息-----------");
                KLog.d(RequestUrl.Type.TYPE_1020, "size = " + split.length);
                List<GroupInfo> groupInfos = new ArrayList<GroupInfo>();
                for (int i = 3; i < split.length; i++) {
                    if (!Constants.NO_MSG.equals(split[i])) {
                        GroupInfo groupInfo = new GroupInfo(split[i]);
                        groupInfos.add(groupInfo);
                        KLog.json(RequestUrl.Type.TYPE_1020, groupInfo.toString());
                    } else {
                        KLog.d(RequestUrl.Type.TYPE_1020, split[i]);
                    }
                }
                if (!groupInfos.isEmpty()) {
                    RealmHelper.saveGroupsList(groupInfos, true);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            KLog.json(RequestUrl.Type.TYPE_1020, "error e = " + e.toString());
        }
        return false;
    }


    public static boolean isError(String data) {
        return Constants.ERROR.equals(data.trim()) || Constants.ERROR_1.equals(data);
    }


    private static void notify(GroupInfo info) {
        //实例化工具类，并且调用接口
        NotifyUtil notify = new NotifyUtil(App.getInstance(), "11111");
        //设置想要展示的数据内容
        Intent intent = new Intent(App.getInstance(), MainActivity.class);
        intent.putExtra(Constants.Key.INDEX, 1);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pIntent = PendingIntent.getActivity(App.getInstance(),
                (int) (info.getCreateTime() / 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notify.notify_normal_singline(pIntent, true, true, false);
    }
}
