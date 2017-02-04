package com.hong.bo.shi.model.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class DepartmentBean {

    private String department;
    private List<UserInfo> list;

    public DepartmentBean(String department) {
        this.department = department;
        this.list = new ArrayList<>();
    }

    public void add(UserInfo info){
        this.list.add(info);
    }

    public String getDepartment() {
        return department == null ? "" : department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<UserInfo> getList() {
        return list;
    }

    public void setList(List<UserInfo> list) {
        this.list = list;
    }

    @Override
    public boolean equals(Object obj) {
        return getDepartment().equals(((DepartmentBean)obj).getDepartment());
    }
}
