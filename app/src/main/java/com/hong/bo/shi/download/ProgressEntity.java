package com.hong.bo.shi.download;

/**
 * Created by andy on 16/9/28.
 */

public class ProgressEntity {

    private long length;
    private long current;
    private String guid;
    private boolean isFailed;
    private String path;

    public ProgressEntity(long length, long current, String guid, boolean isFailed) {
        this.length = length;
        this.current = current;
        this.guid = guid;
        this.isFailed = isFailed;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean failed) {
        isFailed = failed;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

}
