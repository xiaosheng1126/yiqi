package com.hong.bo.shi.recorder;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.utils.PreferencesUtils;

/**
 * Created by andy on 2016/11/30.
 */
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener{

    private static final int DISPLAY_Y_CANCEL = 50;
    //按钮状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    //Handler 使用的what
    private static final int MSG_AUDIO_PREPARED = 0X001;
    private static final int MSG_VOICE_CHANGE = 0X002;
    private static final int MSG_DIALOG_DIMISS = 0X003;
    private static final int MSG_TOO_LONG = 0X004;
    //录音最小时间
    private static final float MIN_TIME = 0.6f;
    //录音最长时间
    private static float MAX_TIME = 60.0f;

    private int mCurState = STATE_NORMAL;
    /**是否已经开始录音*/
    private volatile boolean isRecording = false;
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    private volatile float mTime;
    //是否出发LongClick
    private boolean isReady;
    //录音时间是否超时
    private boolean isTooLong;
    private boolean mIsForbidPower;//权限是否被禁止
    private OnAudioFinishRecorderListener mListener;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDialogManager = new DialogManager(context);
        String dir = App.getInstance().getRecoderAudio();
        mAudioManager = AudioManager.getInsatnce(dir);
        mAudioManager.setAudioStateListener(this);
        setGravity(Gravity.CENTER);
        MAX_TIME = Integer.valueOf(PreferencesUtils.getAudioTime());
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                isReady = true;
                mAudioManager.prepareAudio();
                return true;
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_AUDIO_PREPARED:
                    mDialogManager.showRecorderDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunable).start();
                    break;
                case MSG_VOICE_CHANGE:
                    if(MAX_TIME - mTime > 10) {
                        mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    }else{
                        mDialogManager.updateVoiceLevelAndTooLongHint(mAudioManager.getVoiceLevel(7), MAX_TIME - mTime);
                    }
                    break;
                case MSG_DIALOG_DIMISS:
                    mDialogManager.dimissDialog();
                    break;
                case MSG_TOO_LONG:
                    if(!isTooLong) {
                        isTooLong = true;
                        changeState(STATE_NORMAL);
                        mDialogManager.dimissDialog();
                        if (mListener != null) {
                            mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                        }
                        mAudioManager.release();
                        Toast.makeText(getContext(), "录音时长最多" + MAX_TIME + "秒",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    /**
     * 获取音量的Runable
     */
    private Runnable mGetVoiceLevelRunable = new Runnable(){
        @Override
        public void run() {
            while (isRecording){
                try {
                    Thread.sleep(100);
                    if(isRecording) {
                        mTime += 0.1f;
                        if (MAX_TIME <= mTime) {
                            isRecording = false;
                            mHandler.sendEmptyMessage(MSG_TOO_LONG);
                        } else {
                            mHandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void wellPrepared() {
        mIsForbidPower = false;
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public void forbidPower() {
        mIsForbidPower = true;
        Toast.makeText(getContext(), "录音权限被禁止，请手动打开权限", Toast.LENGTH_LONG).show();
        changeState(STATE_NORMAL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mListener != null) {
                    mListener.onDown();
                }
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isRecording) {
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!isReady){
                    reset();
                    return super.onTouchEvent(event);
                }
                if(mIsForbidPower){
                    reset();
                    return super.onTouchEvent(event);
                }
                if(isTooLong){//录音时间太长
                    reset();
                    return super.onTouchEvent(event);
                }
                if(!isRecording || mTime < MIN_TIME){
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                }else if(mCurState == STATE_WANT_TO_CANCEL){
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }else if(mCurState == STATE_RECORDING){//正常录制结束
                    mDialogManager.dimissDialog();
                    if(mListener != null){
                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                    mAudioManager.release();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 根据x,y判断是否取消
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if(x < 0 || x > getWidth()){
            return true;
        }
        if(y < - DISPLAY_Y_CANCEL || y > getHeight() + DISPLAY_Y_CANCEL){
            return true;
        }
        return false;
    }

    /**
     * 改变按钮状态
     * @param stateRecorder
     */
    private void changeState(int stateRecorder) {
        if(mCurState != stateRecorder){
            mCurState = stateRecorder;
            switch (stateRecorder){
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recordering_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recording);
                    if(isRecording){
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_want_to_cancel);
                    if(isRecording){
                        mDialogManager.wantToCancel();
                    }
                    break;
            }
        }
    }

    /**
     * 恢复状态和标志位
     */
    private void reset(){
        mTime = 0;
        isReady = false;
        isRecording = false;
        isTooLong = false;
        changeState(STATE_NORMAL);
    }

    /**
     * 录音完成后的回调
     */
    public interface OnAudioFinishRecorderListener{
        void onFinish(float seconds, String path);
        void onDown();
    }

    /**
     * 设置录音完成监听器
     * @param listener
     */
    public void setOnAudioFinishRecorderListener(OnAudioFinishRecorderListener listener){
        this.mListener = listener;
    }

}
