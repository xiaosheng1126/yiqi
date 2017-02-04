package com.hong.bo.shi.recorder;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by andy on 2016/11/30.
 */

public class MediaManager {

    private static MediaPlayer mMediaPlay;
    private static boolean isPause;

    public static void playSound(String path, MediaPlayer.OnCompletionListener listener,
                                 MediaPlayer.OnErrorListener errorListener){
        if(mMediaPlay == null){
            mMediaPlay = new MediaPlayer();
            mMediaPlay.setOnErrorListener(errorListener);
        }else{
            mMediaPlay.reset();
        }
        try {
            mMediaPlay.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlay.setOnCompletionListener(listener);
            mMediaPlay.setDataSource(path);
            mMediaPlay.prepare();
            mMediaPlay.start();
        } catch (IOException e) {
            e.printStackTrace();
            if(errorListener != null){
                errorListener.onError(mMediaPlay, 0, 0);
            }
        }
    }

    public static void pause(){
        if(mMediaPlay != null && mMediaPlay.isPlaying()){
            mMediaPlay.pause();
            isPause = true;
        }
    }

    public static void resume(){
        if(mMediaPlay != null && isPause){
            mMediaPlay.start();
            isPause = false;
        }
    }

    public static void release(){
        if(mMediaPlay != null){
            mMediaPlay.release();
            mMediaPlay = null;
        }
    }

    public static void reset(){
        if(mMediaPlay != null){
            mMediaPlay.reset();
            mMediaPlay = null;
        }
    }
}
