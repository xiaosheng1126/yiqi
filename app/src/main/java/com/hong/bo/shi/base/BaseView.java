package com.hong.bo.shi.base;

public interface BaseView<T> {

    void setPresenter(T presenter);

    void showError(String msg);

    void showLoading();

    void dismissDialog();

    void onDestory();
}
