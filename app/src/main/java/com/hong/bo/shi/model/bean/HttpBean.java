package com.hong.bo.shi.model.bean;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by andy on 2016/12/9.
 */
public class HttpBean extends RealmObject {

    @PrimaryKey
    private String type;

    private String value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public HttpBean(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public HttpBean() {
    }

    @Override
    public String toString() {
        return "HttpBean{" +
                "type='" + type + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
