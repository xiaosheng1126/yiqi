package com.hong.bo.shi.app;

import android.text.TextUtils;

/**
 * Description: Constants
 * Creator: yxc
 * date: 2016/9/21 10:05
 */
public class Constants {

    public static final String CHARSET_GBK = "GBK";

    public static final class Key{
        public static final String GUID = "guid";
        public static final String GUIDS = "guids";
        public static final String GROUP_GUID = "groupGuid";
        public static final String CHAT_MSG = "ChatMsg";
        public static final String MESSAGE = "message";
        public static final String URL = "url";
        public static final String BACK_TITLE = "backTitle";
        public static final String TYPE = "type";
        public static final String NAME = "name";
        public static final String EXIT_LOGIN = "exitLogin";
        public static final String AGAIN_LOGIN = "again_login";
        public static final String PATH = "path";
        public static final String TIME = "time";
        public static final String LOT = "lot";
        public static final String LAT = "lat";
        public static final String TITLE = "title";
        public static final String ADDRESS = "address";
        public static final String INDEX = "index";
        public static final String SCAN_RESULT = "scan_result";
        public static final String CREATE_TIME = "createTime";
        public static final String IS_COMPANY = "isCompany";

    }

    public static final int REQUEST_CHAT_CAMERA_CODE = 100;
    public static final int REQUEST_CHAT_ALBUM_CODE = 101;
    public static final int REQUEST_CHAT_CROP_CODE = 107;
    public static final int REQUEST_VIDEO_CODE = 102;
    public static final int REQUEST_LOCATION_CODE = 103;
    public static final int REQUEST_EDIT_PHOTO_CAMERA_CODE = 104;
    public static final int REQUEST_EDIT_PHOTO_ALBUM_CODE = 105;
    public static final int REQUEST_EDIT_PHOTO_CROP = 106;
    public static final int REQUEST_REGISTER_CODE = 108;
    public static final int REQUEST_FORGET_PASS = 109;

    /**
     * 分隔符
     */
    public static final String SPLIT = "KHCLCHK";
    /**
     * 多条分隔符
     */
    public static final String STRIP_SPLIT = "KH666CLC666HK";
    /**
     * 多类分隔符
     */
    public static final String CLASSIF_SPLIT = "KHSEP3CLCSEP3HK";

    public static final int UNREAD_MAX_COUNT = 99;
    public static final String UNREAD_MAX_COUNT_STR = "99+";
    //数据类型
    public static final String ERROR = "error";
    public static final String ERROR_1 = "error_1";
    public static final String SUCCESS = "success";
    public static final String NO_MSG = "nomsg";
    public static final String DROPPED = "dropped";


    public static int dataToInt(String data, int defaultValue){
        if(TextUtils.isEmpty(data) || TextUtils.isEmpty(data.trim()) || "null".equals(data) || "null".equals(data.trim()) ||
                NO_MSG.equals(data)){
            return defaultValue;
        }else {
            try {
                return Integer.valueOf(data);
            }catch (NumberFormatException e){
                return defaultValue;
            }
        }
    }
}
