package com.hong.bo.shi.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.app.RequestHeader;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.ui.activitys.ChatActivity;
import com.hong.bo.shi.ui.activitys.EditGroupInfoActivity;
import com.hong.bo.shi.ui.activitys.EditPassActivity;
import com.hong.bo.shi.ui.activitys.EditUserInfoActivity;
import com.hong.bo.shi.ui.activitys.EditUserPhotoActivity;
import com.hong.bo.shi.ui.activitys.KickedActivity;
import com.hong.bo.shi.ui.activitys.LocationActivity;
import com.hong.bo.shi.ui.activitys.LoginActivity;
import com.hong.bo.shi.ui.activitys.MainActivity;
import com.hong.bo.shi.ui.activitys.PictrueBrowseActivity;
import com.hong.bo.shi.ui.activitys.PicturePreviewActivity;
import com.hong.bo.shi.ui.activitys.PlayMovieActivity;
import com.hong.bo.shi.ui.activitys.RecorderVideoActivity;
import com.hong.bo.shi.ui.activitys.SearchContractActivity;
import com.hong.bo.shi.ui.activitys.SearchMessageActivity;
import com.hong.bo.shi.ui.activitys.SelectContractActivity;
import com.hong.bo.shi.ui.activitys.SettingActivity;
import com.hong.bo.shi.ui.activitys.ShowLocationActivity;
import com.hong.bo.shi.ui.activitys.UserDetailsActivity;
import com.hong.bo.shi.ui.activitys.WebViewActivity;

import java.io.File;
import java.io.IOException;

/**
 * Created by andy on 2016/12/9.
 */

public class UIHelper {

    public static void showLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void showMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void showScanResult(Context context, String result){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Key.INDEX, 0);
        intent.putExtra(Constants.Key.SCAN_RESULT, result);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void showMainPosition(Context context, int index){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Key.INDEX, index);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void showWebView(Context context, WebBean webBean, int requestCode) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(WebBean.class.getSimpleName(), webBean);
        if(requestCode != 0){
            ((BaseActivity)context).startActivityForResult(intent, requestCode);
        }else {
            context.startActivity(intent);
        }
    }

    public static void showWebView(Context context, WebBean webBean) {
       showWebView(context, webBean, 0);
    }

    public static void showSelectContract(Context context, String groupGuid,String backName) {
        Intent intent = new Intent(context, SelectContractActivity.class);
        if (!TextUtils.isEmpty(groupGuid)) {
            intent.putExtra(Constants.Key.GUID, groupGuid);
        }
        intent.putExtra(Constants.Key.BACK_TITLE, backName);
        context.startActivity(intent);
    }

    public static void showSelectContract(Context context, String backName) {
        showSelectContract(context, null, backName);
    }

    public static String openCamera(Activity context, int requestCode) {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File image = ImageUtils.createImageFile();
            String photoPath = image.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
            context.startActivityForResult(takePictureIntent, requestCode);
            return photoPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void openAlbum(Activity context, int requestCode) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为REQUEST_CODE_ALNUM
        context.startActivityForResult(intent, requestCode);
    }


    public static void openVideo(BaseActivity context, int requestCode) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(context, RecorderVideoActivity.class);
        // 开启一个带有返回值的Activity，请求码为REQUEST_CODE_VIdeo
        context.startActivityForResult(intent, requestCode);
    }

    public static void openLocation(BaseActivity context, int requestCode){
        Intent intent = new Intent(context, LocationActivity.class);
        // 开启一个带有返回值的Activity，请求码为REQUEST_CODE_LOCATION
        context.startActivityForResult(intent, requestCode);
    }

    public static void showUserDetails(Context context, String userGuid, String backName) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        if (userGuid != null) {
            intent.putExtra(Constants.Key.GUID, userGuid);
        }
        intent.putExtra(Constants.Key.BACK_TITLE, backName);
        context.startActivity(intent);
    }

    public static void showEditUserInfo(Context context, int type) {
        Intent intent = new Intent(context, EditUserInfoActivity.class);
        intent.putExtra(Constants.Key.TYPE, type);
        context.startActivity(intent);
    }

    public static void editUserPhoto(Context context){
        Intent intent = new Intent(context, EditUserPhotoActivity.class);
        context.startActivity(intent);
    }

    public static void showEditPass(Context context) {
        Intent intent = new Intent(context, EditPassActivity.class);
        context.startActivity(intent);
    }

    public static void showSetting(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public static void showEditGroupName(Context context, String groupGuid, String groupName) {
        Intent intent = new Intent(context, EditGroupInfoActivity.class);
        intent.putExtra(Constants.Key.GUID, groupGuid);
        intent.putExtra(Constants.Key.TYPE, 0);
        intent.putExtra(Constants.Key.NAME, groupName);
        context.startActivity(intent);
    }

    public static void showEditGroupPublic(Context context, String groupGuid, String groupPublic) {
        Intent intent = new Intent(context, EditGroupInfoActivity.class);
        intent.putExtra(Constants.Key.GUID, groupGuid);
        intent.putExtra(Constants.Key.TYPE, 1);
        intent.putExtra(Constants.Key.NAME, groupPublic);
        context.startActivity(intent);
    }

    public static void lookGroupPublic(Context context, String groupGuid, String groupPublic) {
        Intent intent = new Intent(context, EditGroupInfoActivity.class);
        intent.putExtra(Constants.Key.GUID, groupGuid);
        intent.putExtra(Constants.Key.TYPE, 2);
        intent.putExtra(Constants.Key.NAME, groupPublic);
        context.startActivity(intent);
    }

    public static void exitLogin(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Key.EXIT_LOGIN, true);
        context.startActivity(intent);
    }

    public static void againLogin(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.Key.AGAIN_LOGIN, true);
        context.startActivity(intent);
    }

    public static void searchMessage(Context context){
        Intent intent = new Intent(context, SearchMessageActivity.class);
        context.startActivity(intent);
    }

    public static void searchContract(Context context){
        Intent intent = new Intent(context, SearchContractActivity.class);
        intent.putExtra(Constants.Key.TYPE, 0);
        intent.putExtra(Constants.Key.BACK_TITLE, "通讯录");
        context.startActivity(intent);
    }

    public static void searchContractToResult(Context context, String groupGuid, String guids, int requestCode){
        Intent intent = new Intent(context, SearchContractActivity.class);
        intent.putExtra(Constants.Key.TYPE, 1);
        intent.putExtra(Constants.Key.GUIDS, guids);
        intent.putExtra(Constants.Key.BACK_TITLE, "选择联系人");
        intent.putExtra(Constants.Key.GUID, groupGuid);
        ((SelectContractActivity)context).startActivityForResult(intent, requestCode);
    }

    public static void showBigPic(Context context, String url){
        Intent intent = new Intent(context, PicturePreviewActivity.class);
        intent.putExtra(Constants.Key.URL, url);
        context.startActivity(intent);
    }

    public static void peviewPic(Context context, GroupMessage message){
        Intent intent = new Intent(context, PictrueBrowseActivity.class);
        intent.putExtra(GroupMessage.class.getSimpleName(), message);
        context.startActivity(intent);
    }

    public static void showLocation(Context context, GroupMessage message){
        Intent intent = new Intent(context, ShowLocationActivity.class);
        final String[] split = message.getTime().split(RequestHeader.SPLIT);
        final String decode = RequestHeader.decode(split[2]);
        intent.putExtra(Constants.Key.LOT, split[0]);
        intent.putExtra(Constants.Key.LAT, split[1]);
        intent.putExtra(Constants.Key.TITLE, decode);
        context.startActivity(intent);
    }

    public static void showChat(Context context, String backName, GroupInfo info){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(GroupInfo.class.getSimpleName(), info);
        intent.putExtra(Constants.Key.BACK_TITLE, backName);
        context.startActivity(intent);
    }

    public static void callMobile(Context context, String mobile){
        Uri uri = Uri.parse("tel:"+ mobile);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        context.startActivity(intent);
    }

    public static void sendMessage(Context context, String mobile){
        Uri uri = Uri.parse("smsto:"+ mobile);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void showKicked(Context context){
        Intent intent = new Intent(context, KickedActivity.class);
        context.startActivity(intent);
    }

    public static void playVideo(Context context, GroupMessage message){
        Intent intent = new Intent(context, PlayMovieActivity.class);
        intent.putExtra(GroupMessage.class.getSimpleName(), message);
        intent.putExtra(Constants.Key.TYPE, 1);
        context.startActivity(intent);
    }

    public static void previewVideo(Context context, String path){
        Intent intent = new Intent(context, PlayMovieActivity.class);
        intent.putExtra(Constants.Key.PATH, path);
        intent.putExtra(Constants.Key.TYPE, 0);
        ((BaseActivity)context).startActivityForResult(intent, Constants.REQUEST_VIDEO_CODE);
    }
}
