package com.hong.bo.shi.app;

import com.hong.bo.shi.model.bean.GroupMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by andy on 2016/12/22.
 */

public final class RequestHeader {

    private static final String SPACE = " ";
    public static final String SPLIT = "=KHHCLHK=";
    /**
     * 编辑用户信息, 密码的header
     * @param newPasword 新密码(不修改时传"")
     * @param name 用户名(不修改时传"")
     * @param gender 用户性别 0 女 1 男(不修改时传-1)
     * @param mobile 用户手机号码(不修改时传"")
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getEditUserHeader(String newPasword, String name, String gender,
                                            String mobile) throws UnsupportedEncodingException {
        String guid = App.getInstance().getUserInfo().getGuid();
        StringBuilder builder = new StringBuilder(64);
        builder.append(RequestUrl.Type.TYPE_1022).append(SPLIT)
                .append(guid).append(SPLIT)//用户id
                .append(newPasword).append(SPLIT)//新密码
                .append(SPACE.equals(name) ? SPACE : encode(name)).append(SPLIT)//用户名称
                .append(gender).append(SPLIT)//用户性别
                .append(mobile).append(SPLIT);//手机号码
        return builder.toString();
    }

    /**
     * 修改群header
     * @param groupGuid
     * @param groupName
     * @param groupPublic
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getEditGroupHeader(String groupGuid, String groupName, String groupPublic) throws UnsupportedEncodingException {
        String guid = App.getInstance().getUserInfo().getGuid();
        StringBuilder builder = new StringBuilder(64);
        builder.append(RequestUrl.Type.TYPE_1023).append(SPLIT)
                .append(guid).append(SPLIT)//用户id
                .append(groupGuid).append(SPLIT)//群guid
                .append(SPACE.equals(groupName) ? SPACE : encode(groupName)).append(SPLIT)//群名称
                .append(SPACE.equals(groupPublic) ? SPACE : encode(groupPublic));//群公告
        return builder.toString();
    }

    public static String encode(String old) throws UnsupportedEncodingException {
        return URLEncoder.encode(old, Constants.CHARSET_GBK);
    }

    public static String decode(String old){
        try {
            return URLDecoder.decode(old, Constants.CHARSET_GBK);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 修改密码的header
     * @param pass
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditPassHeader(String pass) throws UnsupportedEncodingException {
        return getEditUserHeader(pass, SPACE, SPACE, SPACE);
    }

    /**
     * 修改用户名称的header
     * @param userName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditUserNameHeader(String userName) throws UnsupportedEncodingException {
        return getEditUserHeader(SPACE, userName, SPACE, SPACE);
    }

    /**
     * 修改用户性别的header
     * @param gender
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditGenderHeader(String gender) throws UnsupportedEncodingException {
        return getEditUserHeader(SPACE, SPACE, gender, SPACE);
    }

    /**
     * 修改用户手机号码的header
     * @param mobile
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditMobileHeader(String mobile) throws UnsupportedEncodingException {
        return getEditUserHeader(SPACE, SPACE, SPACE, mobile);
    }

    /**
     * 修改群名称的header
     * @param groupGuid
     * @param groupName
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditGroupNameHeader(String groupGuid, String groupName) throws UnsupportedEncodingException {
        return getEditGroupHeader(groupGuid, groupName, SPACE);
    }

    /**
     * 修改群公告的header
     * @param groupGuid
     * @param groupPublic
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditGroupPublicHeader(String groupGuid, String groupPublic) throws UnsupportedEncodingException {
        return getEditGroupHeader(groupGuid, SPACE, groupPublic);
    }

    /**
     * 修改用户头像的header
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getEditUserPhotoHeader() throws UnsupportedEncodingException {
        return getEditUserHeader(SPACE, SPACE, SPACE, SPACE);
    }

    /**
     * 发送消息的header
     * @param message
     * @param mGroupAttribute
     * @return
     */
    public static String getSendMsgHeader(GroupMessage message, int mGroupAttribute) {
        //1:type
        //2:用户GUID
        //3:群GUID
        //4:群属性
        // 5:消息体类型
        //6:消息属性
        //7:消息时长
        StringBuilder builder = new StringBuilder();
        builder.append(RequestUrl.Type.TYPE_1004).append(SPLIT)
                .append(message.getFromUserGuid()).append(SPLIT)
                .append(message.getGroupGuid()).append(SPLIT)
                .append(mGroupAttribute).append(SPLIT)
                .append(message.getMsgType()).append(SPLIT)
                .append(message.getMsgAttribute()).append(SPLIT)
                .append(message.getTime());
        return builder.toString();
    }
}
