package com.hong.bo.shi.app;

import com.hong.bo.shi.utils.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by andy on 2016/12/22.
 */

public class SubscriptionUtils {

    public static Subscription getB(Func1<String, Boolean> map,
                                    Action1<Boolean> action1) {
        Subscription subscribe = Observable.just("111")
                .map(map).compose(RxUtil.<Boolean>rxSchedulerHelper())
                .subscribe(action1);
        return subscribe;
    }

    public static Subscription getS(Func1<String, String> map,
                                    Action1<String> action1) {
        Subscription subscribe = Observable.just("111")
                .map(map).compose(RxUtil.<String>rxSchedulerHelper())
                .subscribe(action1);
        return subscribe;
    }


    public static Subscription getS(Func1<String, String> map,
                                    Action1<String> action1, Action1<Throwable> action2) {
        Subscription subscribe = Observable.just("111")
                .map(map).compose(RxUtil.<String>rxSchedulerHelper())
                .subscribe(action1, action2);
        return subscribe;
    }

}
