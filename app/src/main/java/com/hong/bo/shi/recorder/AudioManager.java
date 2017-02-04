package com.hong.bo.shi.recorder;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by andy on 2016/11/30.
 */

public class AudioManager {

    private static AudioManager mInsatnce;
    private MediaRecorder mMediaRecorder;
    private String mDir;
    private String mCurFilePath;
    private AudioStateListener mListener;
    private boolean isPrepare;

    private AudioManager() {
    }

    public static AudioManager getInsatnce(String dir) {
        if (mInsatnce == null) {
            synchronized (AudioManager.class) {
                if (mInsatnce == null) {
                    mInsatnce = new AudioManager();
                }
            }
        }
        mInsatnce.mDir = dir;
        return mInsatnce;
    }

    /**
     * 设置录音准备完成监听器
     * @param listener
     */
    public void setAudioStateListener(AudioStateListener listener) {
        this.mListener = listener;
    }

    /**
     *  录音前的准备工作
     */
    public void prepareAudio() {
        try {
            isPrepare = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurFilePath = file.getAbsolutePath();
            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioSamplingRate(8000);
            //设置音频编码amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            //准备结束
            isPrepare = true;
            if (mListener != null) {
                mListener.wellPrepared();
            }
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            if (mListener != null) {
                mListener.forbidPower();
            }
        }
    }

    /**
     * 随机生成文件名
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".amr";
    }

    /**
     * 获取声音级别
     * @param maxLevel
     * @return
     */
    public int getVoiceLevel(int maxLevel) {
        if (isPrepare) {
            try {
                //mMediaRecorder.getMaxAmplitude() 范围 1- 32767
                return maxLevel * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return 1;
    }

    /**
     * 是否资源
     */
    public void release() {
        if(mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    /**
     * 取消
     */
    public void cancel(){
        release();
        if(mCurFilePath != null){
            File file = new File(mCurFilePath);
            file.delete();
            mCurFilePath = null;
        }
    }

    /**
     * 获取当前文件路径
     * @return
     */
    public String getCurrentFilePath(){
        return mCurFilePath;
    }

    /**
     * 录音准备完成后的回调
     */
    public interface AudioStateListener {
        void wellPrepared();
        /**权限被禁止*/
        void forbidPower();
    }


}
