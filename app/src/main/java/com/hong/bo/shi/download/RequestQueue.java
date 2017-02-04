package com.hong.bo.shi.download;

import android.text.TextUtils;

import com.hong.bo.shi.model.bean.GroupMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by andy on 2016/12/15.
 */

public class RequestQueue {

    /** Used for generating monotonically-increasing sequence numbers for requests. */
    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    /**
     * The set of all requests currently being processed by this RequestQueue. A Request
     * will be in this set if it is waiting in any queue or currently being processed by
     * any dispatcher.
     */
    private final Set<Request> mCurrentRequests = new HashSet<Request>();

    /** The queue of requests that are actually going out to the network. */
    private final PriorityBlockingQueue<Request> mNetworkQueue =
            new PriorityBlockingQueue<Request>();

    private final PriorityBlockingQueue<String> mParseQueue =
            new PriorityBlockingQueue<String>();

    /** Number of network request dispatcher threads to start. */
    private static final int DEFAULT_NETWORK_THREAD_POOL_SIZE = 4;

    /** The network dispatchers. */
    private NetworkDispatcher[] mDispatchers;
    private DownLoadDispatcher mParseDispatcher;

    public RequestQueue() {
        mDispatchers = new NetworkDispatcher[DEFAULT_NETWORK_THREAD_POOL_SIZE];
    }

    public RequestQueue(int threadCount) {
        if(threadCount <= 0){
            threadCount = DEFAULT_NETWORK_THREAD_POOL_SIZE;
        }
        mDispatchers = new NetworkDispatcher[threadCount];
        start();
    }

    /**
     * Starts the dispatchers in this queue.
     */
    public void start() {
        stop();  // Make sure any currently running dispatchers are stopped.
        // Create network dispatchers (and corresponding threads) up to the pool size.
        for (int i = 0; i < mDispatchers.length; i++) {
            NetworkDispatcher networkDispatcher = new NetworkDispatcher(mNetworkQueue);
            mDispatchers[i] = networkDispatcher;
            networkDispatcher.start();
        }
        mParseDispatcher = new DownLoadDispatcher(mParseQueue);
        mParseDispatcher.start();
    }

    /**
     * Stops the cache and network dispatchers.
     */
    public void stop() {
        for (int i = 0; i < mDispatchers.length; i++) {
            if (mDispatchers[i] != null) {
                mDispatchers[i].quit();
            }
        }
        if(mParseDispatcher != null){
            mParseDispatcher.quit();
        }
    }

    /**
     *只下载语音，视频
     * @param message
     */
    public void add(GroupMessage message){
        if(message == null || (message.getMsgType() != 2 && message.getMsgType() != 3))return;
        String success = Request.isDownloadSuccess(message);
        if(success == null)return;
        Request request = new Request(message);
        if(isContains(request))return;
        request.setCachePath(success);
        request.setRequestQueue(this);
        request.setSequence(getSequenceNumber());
        mCurrentRequests.add(request);
        mNetworkQueue.add(request);
    }

    private boolean isContains(Request newRequest){
        for (Request request : mCurrentRequests) {
            if(request.getGuid().equals(newRequest.getGuid())){
                return true;
            }
        }
        return false;
    }


    /**
     * Gets a sequence number.
     */
    public int getSequenceNumber() {
        return mSequenceGenerator.incrementAndGet();
    }

    void finish(Request request) {
        // Remove from the set of requests currently being processed.
        synchronized (mCurrentRequests) {
            mCurrentRequests.remove(request);
        }
    }

    public void downloadPic(String parse){
        if(TextUtils.isEmpty(parse))return;
        mParseQueue.add(parse);
    }
}
