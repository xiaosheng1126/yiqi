package com.hong.bo.shi.recorder;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.utils.PreferencesUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MovieRecorderView extends LinearLayout implements MediaRecorder.OnErrorListener {
   
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private MediaRecorder mMediaRecorder;
    private Camera mCamera;  
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口
   
    private int mWidth;// 视频分辨率宽度  
    private int mHeight;// 视频分辨率高度  
    private boolean isOpenCamera;// 是否一开始就打开摄像头  
    private int mRecordMaxTime;// 一次拍摄最长时间  
    private volatile int mTimeCount;// 时间计数
    private File mVecordFile = null;//文件
    private boolean isPause;
    private boolean isStop = true;
    private TextView mTvTime;

    public void setOnRecordFinishListener(OnRecordFinishListener mOnRecordFinishListener) {
        this.mOnRecordFinishListener = mOnRecordFinishListener;
    }

    public MovieRecorderView(Context context) {
        this(context, null);  
    }  
   
    public MovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);  
    }  
   
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieRecorderView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        mWidth = 320;
        mHeight = 240;
        isOpenCamera = true;
        mRecordMaxTime = Integer.valueOf(PreferencesUtils.getVideoTime());
        LayoutInflater.from(context).inflate(R.layout.moive_recorder_view, this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mTvTime = (TextView) findViewById(R.id.tvTime);
        mTvTime.setText(String.valueOf(mRecordMaxTime));
        mTvTime.setText(String.format("最大录制时间%d秒", mRecordMaxTime));
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());  
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);  
    }  
   
    /** 
     *  
     * @author liuyinjun 
     *  
     * @date 2015-2-5 
     */  
    private class CustomCallBack implements SurfaceHolder.Callback {
        @Override  
        public void surfaceCreated(SurfaceHolder holder) {  
            if (!isOpenCamera)  
                return;  
            try {  
                initCamera();  
            } catch (IOException e) {
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
        }  
   
        @Override  
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }  
   
        @Override  
        public void surfaceDestroyed(SurfaceHolder holder) {  
            if (!isOpenCamera)  
                return;  
            freeCameraResource();  
        }  
   
    }  
   
    /** 
     * 初始化摄像头 
     *  
     * @author lip 
     * @date 2015-3-16 
     * @throws IOException 
     */  
    private void initCamera() throws IOException {  
        if (mCamera != null) {  
            freeCameraResource();  
        }  
        try {  
            mCamera = Camera.open();
        } catch (Exception e) {  
            e.printStackTrace();  
            freeCameraResource();  
        }  
        if (mCamera == null)  
            return;
        setCameraParams();  
        mCamera.setDisplayOrientation(90);  
        mCamera.setPreviewDisplay(mSurfaceHolder);  
        mCamera.startPreview();  
        mCamera.unlock();  
    }  
   
    /** 
     * 设置摄像头为竖屏 
     * @author lip
     * @date 2015-3-16 
     */  
    private void setCameraParams() {  
        if (mCamera != null) {  
            Camera.Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");  
            mCamera.setParameters(params);  
        }  
    }  
   
    /** 
     * 释放摄像头资源
     * @author liuyinjun 
     * @date 2015-2-5 
     */  
    private void freeCameraResource() {  
        if (mCamera != null) {  
            mCamera.setPreviewCallback(null);  
            mCamera.stopPreview();  
            mCamera.lock();  
            mCamera.release();  
            mCamera = null;  
        }  
    }  
   
    private void createRecordDir() {  
        // 创建文件
        mVecordFile = new File(App.getInstance().getRecoderVideo(), generateFileName());//mp4格式
    }

    /**
     * 随机生成文件名
     * @return
     */
    private String generateFileName() {
        return UUID.randomUUID().toString() + ".mp4";
    }
   
    /** 
     * 初始化 
     *  
     * @author lip 
     * @date 2015-3-16 
     * @throws IOException 
     */  
    private void initRecord() throws IOException {  
        mMediaRecorder = new MediaRecorder();  
        mMediaRecorder.reset();  
        if (mCamera != null)  
            mMediaRecorder.setCamera(mCamera);  
        mMediaRecorder.setOnErrorListener(this);  
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());  
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 音频格式
        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率：  
        mMediaRecorder.setVideoEncodingBitRate(1 * 1024 * 512);//设置帧频率，然后就清晰了
        mMediaRecorder.setOrientationHint(90);//输出旋转90度，保持竖屏录制
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);//视频录制格式
        mMediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
        mMediaRecorder.prepare();  
        try {  
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();  
        } catch (RuntimeException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  

    public void record() {
        createRecordDir();
        try {  
            if (!isOpenCamera)//如果未打开摄像头,则打开
                initCamera();  
            initRecord();  
            mTimeCount = 0;//时间计数器重新赋值
            isStop = false;
            isPause = false;
            new Thread(timerRunnable).start();
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                if(mRecordMaxTime < mTimeCount){
                    mOnRecordFinishListener.onRecordFinish();
                    stop();
                    mTvTime.setText("视屏录制完成");
                }else {
                    mTvTime.setText(String.format("还可录制%d秒", mRecordMaxTime - mTimeCount));
                }
            }

        }
    };

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            while (!isStop){
                try {
                    Thread.sleep(1000);
                    if(!isPause){
                        mTimeCount += 1;
                        mHandler.sendEmptyMessage(0);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
   
    /** 
     * 停止拍摄
     * @author liuyinjun 
     * @date 2015-2-5 
     */  
    public void stop() {
        isStop = true;
        pause();
        releaseRecord();  
        freeCameraResource();  
    }  
   
    /** 
     * 停止录制
     * @author liuyinjun 
     * @date 2015-2-5 
     */  
    public void pause() {
        if (mMediaRecorder != null && !isPause) {
            // 设置后不会崩  
            mMediaRecorder.setOnErrorListener(null);  
            mMediaRecorder.setPreviewDisplay(null);  
            try {
                isPause = true;
                mMediaRecorder.stop();  
            } catch (IllegalStateException e) {  
                e.printStackTrace();  
            } catch (RuntimeException e) {  
                e.printStackTrace();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
    }

    /**
     * 停止录制
     * @author liuyinjun
     * @date 2015-2-5
     */
    public void resume() {
        if (mMediaRecorder != null) {
            try {
                isPause = false;
                mMediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    /** 
     * 释放资源 
     * @author liuyinjun
     * @date 2015-2-5 
     */  
    private void releaseRecord() {  
        if (mMediaRecorder != null) {  
            mMediaRecorder.setOnErrorListener(null);  
            try {  
                mMediaRecorder.release();  
            } catch (IllegalStateException e) {  
                e.printStackTrace();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        mMediaRecorder = null;  
    }  
   
    public long getTimeCount() {
        return mTimeCount;  
    }  
   
    /** 
     * @return the mVecordFile 
     */  
    public File getVecordFile() {
        return mVecordFile;  
    }  
   
    /** 
     * 录制完成回调接口 
     * 
     * @author lip 
     *  
     * @date 2015-3-16 
     */  
    public interface OnRecordFinishListener {  
        public void onRecordFinish();  
    }  
   
    @Override  
    public void onError(MediaRecorder mr, int what, int extra) {  
        try {  
            if (mr != null)  
                mr.reset();  
        } catch (IllegalStateException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
}
