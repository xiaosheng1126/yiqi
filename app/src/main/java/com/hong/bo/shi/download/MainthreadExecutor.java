package com.hong.bo.shi.download;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;

/**
 * Created by juude on 15-7-22.
 */
public class MainthreadExecutor implements Executor {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private static MainthreadExecutor sInstance;

    public static MainthreadExecutor getInstance() {
        if (sInstance == null) {
            sInstance = new MainthreadExecutor();
        }
        return sInstance;
    }

    private MainthreadExecutor() {
        super();
    }

    @Override
    public void execute(Runnable command) {
        mHandler.post(command);
    }
}
