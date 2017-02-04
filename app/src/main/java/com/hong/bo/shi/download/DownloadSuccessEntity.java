package com.hong.bo.shi.download;

/**
 * Created by andy on 2017/1/21.
 */

public class DownloadSuccessEntity {

    private String mPath;
    private String mGuid;

    public DownloadSuccessEntity(String mPath, String mGuid) {
        this.mPath = mPath;
        this.mGuid = mGuid;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setGuid(String mGuid) {
        this.mGuid = mGuid;
    }
}
