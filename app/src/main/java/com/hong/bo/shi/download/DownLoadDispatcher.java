package com.hong.bo.shi.download;

import android.os.Process;

import com.hong.bo.shi.app.App;
import com.hong.bo.shi.model.http.RetrofitHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import okhttp3.Response;

/**
 * Created by andy on 2016/12/21.
 * 下载图片
 */
public class DownLoadDispatcher extends Thread {

    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<String> mQueue;
    /**
     * The cache to write to.
     */
    private volatile boolean mQuit = false;

    /**
     * Creates a new network dispatcher thread.  You must call {@link #start()}
     * in order to begin processing.
     *
     * @param queue Queue of incoming requests for triage
     */
    public DownLoadDispatcher(BlockingQueue<String> queue) {
        mQueue = queue;
    }

    /**
     * Forces this dispatcher to quit immediately.  If any requests are still in
     * the queue, they are not guaranteed to be processed.
     */
    public void quit() {
        mQuit = true;
        interrupt();
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            try {
                String take = mQueue.take();
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buffer = new byte[1024];
                int lenght = 0;
                int indexOf = take.lastIndexOf("/");
                String substring = take.substring(indexOf + 1, take.length());
                File file = new File(App.getInstance().getPictureCache(), substring);
                final okhttp3.Request request = new okhttp3.Request.Builder().url(take).build();
                final Response response;
                try {
                    response = RetrofitHelper.getOkHttpClient().newCall(request).execute();
                    long length = response.body().contentLength();
                    if(file != null && file.exists() && file.length() == length){
                        continue;
                    }
                    if(file != null && file.exists() && file.length() > 0){
                        file.delete();
                        file = new File(App.getInstance().getPictureCache(), substring);
                    }
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((lenght = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, lenght);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    if(file != null && file.exists()){
                        file.delete();
                    }
                }finally {
                    try {
                        if(fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (mQuit) {
                    return;
                }
                continue;
            }
        }
    }

}
