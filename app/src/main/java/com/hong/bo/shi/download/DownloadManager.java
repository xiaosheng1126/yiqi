package com.hong.bo.shi.download;

/**
 * Created by andy on 2016/12/15.
 */

public class DownloadManager {

    public static RequestQueue getRequestQueue(){
       return Single.queue;
    }

    private static class Single{
        private static RequestQueue queue = new RequestQueue(3);
    }
}
