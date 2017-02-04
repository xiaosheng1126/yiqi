package com.hong.bo.shi.model.http;

import android.text.TextUtils;

import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.GroupMessage;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Description: RetrofitHelper
 * Creator: yxc
 * date: 2016/9/21 10:03
 */
public class RetrofitHelper {

    private static OkHttpClient okHttpClient = null;
    private static OkHttpClient okHttpClient2 = null;

    private static void initOkHttp() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置超时
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);
//            //错误重连
            okHttpClient = builder.build();
        }
    }

    public static OkHttpClient getFrescoOkHttpClient(){
        if (okHttpClient2 == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(20, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS);
            builder.writeTimeout(20, TimeUnit.SECONDS);
            okHttpClient2 = builder.build();
        }
        return okHttpClient2;
    }
    public static OkHttpClient getOkHttpClient() {
        initOkHttp();
        return okHttpClient;
    }

    public static String get(String path) throws IOException {
        KLog.d(path);
        Request request = new Request.Builder().url(path).build();
        Response execute = getOkHttpClient().newCall(request).execute();
        if (execute.isSuccessful()) {
            return execute.body().string().trim();
        }
        return Constants.ERROR_1;
    }

    public static Response download(Request request) throws IOException {
        Response execute = getOkHttpClient().newCall(request).execute();
        return execute;
    }

    public static String postMsg(String url, String header, GroupMessage message) throws IOException {
        KLog.d(url);
        KLog.d(header);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.header(Constants.Key.CHAT_MSG, header);
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (message.getMsgType() == 0) {//发送文本消息
            bodyBuilder.addFormDataPart(Constants.Key.MESSAGE, URLEncoder.encode(message.getMessage(),
                    Constants.CHARSET_GBK));
        } else {
            File file = new File(message.getMessage());
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            bodyBuilder.addFormDataPart(Constants.Key.MESSAGE,  file.getName(), fileBody);
        }
        builder.post(bodyBuilder.build());
        Call call = RetrofitHelper.getOkHttpClient().newCall(builder.build());
        Response execute = call.execute();
        String string = execute.body().string().trim();
        return string;
    }

    /**
     *
     * @param url
     * @param header
     * @param path
     * @return
     * @throws IOException
     */
    public static String edit(String url, String header, String path) throws IOException {
        KLog.d(url);
        KLog.d(header);
        KLog.d(path);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.header(Constants.Key.CHAT_MSG, header);
        MultipartBody.Builder bodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if(TextUtils.isEmpty(path)){
            bodyBuilder.addFormDataPart(Constants.Key.MESSAGE, "");
        }else{
            File file = new File(path);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
            bodyBuilder.addFormDataPart(Constants.Key.MESSAGE, file.getName(), fileBody);
        }
        builder.post(bodyBuilder.build());
        Call call = RetrofitHelper.getOkHttpClient().newCall(builder.build());
        Response execute = call.execute();
        String string = execute.body().string().trim();
        return string;
    }

}
