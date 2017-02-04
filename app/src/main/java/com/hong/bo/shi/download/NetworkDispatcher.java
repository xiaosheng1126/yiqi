
package com.hong.bo.shi.download;

import android.os.Process;

import com.hong.bo.shi.model.http.RetrofitHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;

import okhttp3.Response;

/**
 */
public class NetworkDispatcher extends Thread {

    /**
     * The queue of requests to service.
     */
    private final BlockingQueue<Request> mQueue;
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
    public NetworkDispatcher(BlockingQueue<Request> queue) {
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
            Request request = null;
            File cacheFile = null;
            try {
                request = mQueue.take();
                Response response = RetrofitHelper.download(new okhttp3.Request.Builder().
                                url(request.getDownloadUrl()).build());
                InputStream stream = response.body().byteStream();
                cacheFile = request.getCacheFile();
                byte[] buf = new byte[2048];
                int len = 0;
                long lenght = response.body().contentLength();
                //文件存在且大小一致
                if(cacheFile.exists() && cacheFile.length() >= lenght){
                    if(!request.isAudio()) {
                        ProgressEntity entity = new ProgressEntity(lenght, lenght, request.getGuid(), false);
                        entity.setPath(cacheFile.getAbsolutePath());
                        post(entity);
                    }
                    //文件下载成功
                    post(new DownloadSuccessEntity(cacheFile.getAbsolutePath(), request.getGuid()));
                    return;
                }
                if(!request.isAudio()) {
                    post(new ProgressEntity(lenght, 0, request.getGuid(), false));
                }
                long count = 0;
                FileOutputStream fos = null;
                fos = new FileOutputStream(cacheFile);
                while ((len = stream.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    count += len;
                    if(!request.isAudio()) {
                        post(new ProgressEntity(lenght, count, request.getGuid(), false));
                    }
                }
                fos.flush();
                stream.close();
                fos.close();
                request.finish();
                if(!request.isAudio()) {
                    ProgressEntity entity = new ProgressEntity(lenght, count, request.getGuid(), false);
                    entity.setPath(cacheFile.getAbsolutePath());
                    post(entity);
                }
                //文件下载成功
                post(new DownloadSuccessEntity(cacheFile.getAbsolutePath(), request.getGuid()));
            } catch (InterruptedException e) {
                // We may have been interrupted because it was time to quit.
                if (request != null)
                    request.finish();
                if(cacheFile != null && cacheFile.exists()){
                    cacheFile.delete();
                }
                post(new ProgressEntity(0, 0, request.getGuid(), true));
                if (mQuit) {
                    return;
                }
                continue;
            } catch (IOException e) {
                e.printStackTrace();
                if (request != null)
                    request.finish();
                if(cacheFile != null && cacheFile.exists()){
                    cacheFile.delete();
                }
                post(new ProgressEntity(0, 0, request.getGuid(), true));
            }
        }
    }

    private void post(final Object entity) {
        MainthreadExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                BusProvider.getInstance().post(entity);
            }
        });
    }
}
