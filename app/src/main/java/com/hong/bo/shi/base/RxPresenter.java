package com.hong.bo.shi.base;

import android.os.Handler;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.utils.ParseUtils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * 基于Rx的Presenter封装,控制订阅的生命周期
 */
public class RxPresenter<T> implements BasePresenter<T> {

    protected T mView;
    protected CompositeSubscription mCompositeSubscription;

    protected void unSubscribe() {
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
            mCompositeSubscription = null;
        }
    }

    protected void addSubscribe(Subscription subscription) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        mCompositeSubscription.add(subscription);
    }

    @Override
    public void onDestory() {
        this.mView = null;
        unSubscribe();
    }

    protected boolean isError(String data){
       return ParseUtils.isError(data);
    }

    protected boolean isSuccess(String data){
        return Constants.SUCCESS.equals(data.trim());
    }

    protected boolean isNetworkAvailable(long delayTime){
        if(App.getInstance().isNetworkAvailable()){
            return true;
        }else{
            if(delayTime > 0){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run(){
                        App.getInstance().showNetworkError();
                        postDelayedRun();
                    }
                }, delayTime);
            }
            return false;
        }
    }

    protected void postDelayedRun(){
    }
}
