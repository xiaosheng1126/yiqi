package com.hong.bo.shi.download;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.model.bean.GroupMessage;

import java.io.File;
import java.io.IOException;

/**
 * Created by andy on 2016/12/15.
 */

public class Request implements Comparable<Request> {

    private String guid;
    private boolean isAudio;
    private String downloadUrl;
    private String mCachePath;
    /**
     * Sequence number of this request, used to enforce FIFO ordering.
     */
    private Integer mSequence;

    /**
     * The request queue this request is associated with.
     */
    private RequestQueue mRequestQueue;
    private Priority mPriority;

    public static String isDownloadSuccess(GroupMessage message) {
        String cacheDir = null;
        String url = null;
        if (message.getMsgType() == 2) {
            cacheDir = App.getInstance().getRecoderVideo();
            url = message.getTime();
        }
        if (message.getMsgType() == 3) {
            cacheDir = App.getInstance().getRecoderAudio();
            url = message.getMessage();
        }
        if (message.isDownloadSuccess()){
            return null;
        }
        int index = url.lastIndexOf("/");
        if (index == -1) return null;
        File file = new File(cacheDir, url.substring(index + 1, url.length()));
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }

    File getCacheFile() {
        File file = new File(mCachePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public Request(GroupMessage message) {
        this.guid = message.getGuid();
        if(message.getMsgType() == 3) {
            this.downloadUrl = message.getMessage();
            isAudio = true;
        }else {
            this.downloadUrl = message.getTime();
            isAudio = false;
        }
        this.mPriority = Priority.NORMAL;
    }

    public String getCachePath() {
        return mCachePath;
    }

    public void setCachePath(String mCachePath) {
        this.mCachePath = mCachePath;
    }

    public Priority getPriority() {
        return mPriority;
    }

    public void setPriority(Priority mPriority) {
        this.mPriority = mPriority;
    }

    public Integer getSequence() {
        return mSequence;
    }

    public void setSequence(Integer mSequence) {
        this.mSequence = mSequence;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void setRequestQueue(RequestQueue mRequestQueue) {
        this.mRequestQueue = mRequestQueue;
    }

    @Override
    public boolean equals(Object obj) {
        return guid.equals(((Request) obj).guid);
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public boolean isAudio() {
        return isAudio;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public int compareTo(Request other) {
        Priority left = this.getPriority();
        Priority right = other.getPriority();

        // High-priority requests are "lesser" so they are sorted to the front.
        // Equal priorities are sorted by sequence number to provide FIFO ordering.
        return left == right ?
                this.mSequence - other.mSequence :
                right.ordinal() - left.ordinal();
    }

    /**
     * Priority values.  Requests will be processed from higher priorities to
     * lower priorities, in FIFO order.
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        IMMEDIATE
    }


    void finish() {
        mRequestQueue.finish(this);
    }
}
