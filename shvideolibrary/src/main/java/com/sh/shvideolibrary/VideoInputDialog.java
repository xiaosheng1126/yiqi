package com.sh.shvideolibrary;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhush on 2016/11/11
 * E-mail zhush@jerei.com
 * PS  小视频输入控件
 */

public class VideoInputDialog extends DialogFragment {

    private static final String TAG = "VideoInputDialog";
    private Camera mCamera;
    private CameraPreview mPreview;
    private ProgressBar mProgressRight,mProgressLeft;
    private MediaRecorder mMediaRecorder;
    private Timer mTimer;
    private static final int DEFAULT_TIME = 1000;
    private int MAX_TIME = DEFAULT_TIME;
    private int mTimeCount;
    private long time;
    private boolean isRecording = false;
    private VideoCall videoCall;
    private static boolean flash = false;
    private static boolean cameraFront = false;

    public static int Q480 = CamcorderProfile.QUALITY_480P;
    public static int Q720 = CamcorderProfile.QUALITY_720P;
    public static int Q1080 = CamcorderProfile.QUALITY_1080P;
    public static int Q21600 = CamcorderProfile.QUALITY_2160P;
    private int quality = CamcorderProfile.QUALITY_480P;
    private String mPath;
    private ImageView button_ChangeCamera;
    private ImageView buttonFlash;
    Context mContext;

    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Runnable updateProgress = new Runnable() {
        @Override
        public void run() {
            mProgressRight.setProgress(mTimeCount);
            mProgressLeft.setProgress(mTimeCount);
        }
    };
    private Runnable sendVideo = new Runnable() {
        @Override
        public void run() {
            recordStop();
        }
    };

    public void setVideoCall(VideoCall videoCall) {
        this.videoCall = videoCall;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public void setMaxTime(int maxTime){
        this.MAX_TIME = maxTime;
    }

    public static VideoInputDialog newInstance(VideoCall videoCall,int quality, int maxTime, String path, Context context) {
        VideoInputDialog dialog = new VideoInputDialog();
        dialog.setVideoCall(videoCall);
        dialog.setQuality(quality);
        dialog.setmContext(context);
        dialog.setMaxTime(maxTime);
        dialog.setPath(path);
        dialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.maskDialog);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_video_input, container, false);
        mPreview = new CameraPreview(getActivity(), mCamera);
        button_ChangeCamera = (ImageView) v.findViewById(R.id.button_ChangeCamera);
        buttonFlash = (ImageView) v.findViewById(R.id.buttonFlash);
        button_ChangeCamera.setOnClickListener(switchCameraListener);
        buttonFlash.setOnClickListener(flashListener);
        FrameLayout preview = (FrameLayout) v.findViewById(R.id.camera_preview);
        mProgressRight = (ProgressBar) v.findViewById(R.id.progress_right);
        mProgressLeft = (ProgressBar) v.findViewById(R.id.progress_left);
        mProgressRight.setMax(MAX_TIME);
        mProgressLeft.setMax(MAX_TIME);
        mProgressLeft.setRotation(180);
        ImageButton record = (ImageButton) v.findViewById(R.id.btn_record);
        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //按下 开始录像
                        if (!isRecording) {
                            if (prepareVideoRecorder()) {
                                time = Calendar.getInstance().getTimeInMillis(); //倒计时
                                mMediaRecorder.start();
                                isRecording = true;
                                mTimer = new Timer();
                                mTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        mTimeCount++;
                                        mainHandler.post(updateProgress);
                                        if (mTimeCount == MAX_TIME) {
                                            mainHandler.post(sendVideo);
                                        }
                                    }
                                    }, 0, 10);
                            } else {
                                releaseMediaRecorder();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        //抬起 停止录像
                        recordStop();
                        break;
                }
                return true;
            }
        });
        preview.addView(mPreview);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {
            releaseCamera();
            final boolean frontal = cameraFront;
            int cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                //前置摄像头不存在
                switchCameraListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, R.string.dont_have_front_camera, Toast.LENGTH_SHORT).show();
                    }
                };
                //尝试寻找后置摄像头
                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.mipmap.ic_flash_on_white);
                }
            } else if (!frontal) {
                cameraId = findBackFacingCamera();
                if (flash) {
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    buttonFlash.setImageResource(R.mipmap.ic_flash_on_white);
                }
            }
            mCamera = Camera.open(cameraId);
            mPreview.refreshCamera(mCamera);
        }
    }

    //切换前置后置摄像头
    View.OnClickListener switchCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isRecording) {
                int camerasNumber = Camera.getNumberOfCameras();
                if (camerasNumber > 1) {
                    releaseCamera();
                    chooseCamera();
                } else {
                    //只有一个摄像头不允许切换
                    Toast.makeText(mContext, R.string.only_have_one_camera
                            , Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //闪光灯
    View.OnClickListener flashListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isRecording && !cameraFront) {
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_off_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    flash = true;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_on_white);
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        }
    };

    //选择摄像头
    public void chooseCamera() {
        if (cameraFront) {
            //当前是前置摄像头
            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }
        } else {
            //当前为后置摄像头
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview
                mCamera = Camera.open(cameraId);
                if (flash) {
                    flash = false;
                    buttonFlash.setImageResource(R.mipmap.ic_flash_off_white);
                    mPreview.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

            }
        }
    }

    /**
     * 找前置摄像头,没有则返回-1
     * @return cameraId
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    /**
     * 找后置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    //闪光灯
    public void setFlashMode(String mode) {
        try {
            if (mContext.getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)
                    && mCamera != null
                    && !cameraFront) {

                mPreview.setFlashMode(mode);
                mPreview.refreshCamera(mCamera);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mContext, R.string.changing_flashLight_mode,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recordStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    /**
     * 停止录制
     */
    private void recordStop(){
        if (isRecording) {
            isRecording = false;
            if (isLongEnough()){
                mMediaRecorder.stop();
            }
            releaseMediaRecorder();
            mCamera.lock();
            if (mTimer != null) mTimer.cancel();
            mTimeCount = 0;
            mainHandler.post(updateProgress);

        }
    }


    /**
     *
     * @param ft
     * @param videoCall  录制视频回调
     * @param quality 分辨率
     * @param context
     */
    public static void show(FragmentManager ft, VideoCall videoCall, int quality, String path, Context context){
        show(ft, videoCall, quality, DEFAULT_TIME, path, context);
    }

    /**
     *
     * @param ft
     * @param videoCall  录制视频回调
     * @param quality 分辨率
     * @param maxTime 视频录制最大时间
     * @param context
     */
    public static void show(FragmentManager ft, VideoCall videoCall, int quality, int maxTime,
                            String path, Context context){
        DialogFragment newFragment = VideoInputDialog.newInstance(videoCall, quality, maxTime, path, context);
        newFragment.show(ft, "VideoInputDialog");
    }



    /** A safe way to get an instance of the Camera object. */
    private static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }



    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
            if (isLongEnough()){
                videoCall.videoPathCall(mPath);
            }else{
                Toast.makeText(getContext(), getString(R.string.chat_video_too_short), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
    //初始化 mMediaRecorder 用于录像
    private boolean prepareVideoRecorder(){

        if (mCamera==null) return false;
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        //声音
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //视频
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置分辨率为480P
        mMediaRecorder.setProfile(CamcorderProfile.get(quality));
        //路径
        mMediaRecorder.setOutputFile(mPath);
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());


        try {
            //旋转90度 保持竖屏
            mMediaRecorder.setOrientationHint(90);
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    /**
     * 判断录制时间
     * @return
     */
    private boolean isLongEnough(){
        return Calendar.getInstance().getTimeInMillis() - time > 3000;
    }

    //检查设备是否有摄像头
    private boolean hasCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Created by zhush on 2016/11/11
     * E-mail zhush@jerei.com
     * PS  录制视频回调
     */

    public static interface VideoCall{
        public void videoPathCall(String path);
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }


    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
