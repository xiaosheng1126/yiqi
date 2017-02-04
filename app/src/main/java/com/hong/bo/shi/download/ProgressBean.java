package com.hong.bo.shi.download;

/**
 * Created by andy on 2016/12/15.
 */

public class ProgressBean {

    private long length;
    private long current;
    private String guid;

    public ProgressBean(long length, long current, String guid) {
        this.length = length;
        this.current = current;
        this.guid = guid;
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

    @Override
    public String toString() {
        return "ProgressBean{" +
                "length=" + length +
                ", current=" + current +
                ", guid='" + guid + '\'' +
                '}';
    }
}
