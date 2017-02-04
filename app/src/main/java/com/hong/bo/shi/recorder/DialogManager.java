package com.hong.bo.shi.recorder;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;

/**
 * Created by andy on 2016/11/30.
 */
public class DialogManager {

    private Dialog mDialog;
    private ImageView mIvRecorder;
    private ImageView mIvVoice;
    private TextView mTvLabel;
    private Context mContext;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 显示录音对话框
     */
    public void showRecorderDialog() {
        mDialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        View layout = LayoutInflater.from(mContext).inflate(R.layout.dialog_recorder, null);
        mDialog.setContentView(layout);
        mIvRecorder = (ImageView) layout.findViewById(R.id.ivRecorder);
        mIvVoice = (ImageView) layout.findViewById(R.id.ivVoice);
        mTvLabel = (TextView) layout.findViewById(R.id.tvText);
        mDialog.show();
    }

    /**
     * 正在录音的提示
     */
    public void recording(){
        if(mDialog != null && mDialog.isShowing()){
            mIvVoice.setVisibility(View.VISIBLE);
            mIvRecorder.setImageResource(R.mipmap.recorder);
            mTvLabel.setText("手指上划 取消发送");
        }
    }

    /**
     * 取消录音的提示
     */
    public void wantToCancel(){
        if(mDialog != null && mDialog.isShowing()){
            mIvVoice.setVisibility(View.GONE);
            mIvRecorder.setImageResource(R.mipmap.cancel);
            mTvLabel.setText("松开手指 取消发送");
        }
    }

    /**
     * 录音时间太短的提示
     */
    public void tooShort(){
        if(mDialog != null && mDialog.isShowing()){
            mIvVoice.setVisibility(View.GONE);
            mIvRecorder.setImageResource(R.mipmap.voice_to_short);
            mTvLabel.setText("录音时间太短");
        }
    }

    /**
     * 关闭对话框
     */
    public void dimissDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 更具传入声音的级别显示对应的图片
     * @param level
     */
    public void updateVoiceLevel(int level){
        if(mDialog != null && mDialog.isShowing()){
            if(level < 1 || level > 7){
                level = 1;
            }
            int mipmap = mContext.getResources().getIdentifier("v" + level, "mipmap",
                    mContext.getPackageName());
            mIvVoice.setImageResource(mipmap);
        }
    }

    /**
     * 更具传入声音的级别显示对应的图片同时倒计时提醒
     * @param level
     */
    public void updateVoiceLevelAndTooLongHint(int level, float time){
        if(mDialog != null && mDialog.isShowing()){
            if(level < 1 || level > 7){
                level = 1;
            }
            int mipmap = mContext.getResources().getIdentifier("v" + level, "mipmap",
                    mContext.getPackageName());
            mIvVoice.setImageResource(mipmap);
            mTvLabel.setText("还可以录音" + Math.round(time) + "秒");
        }
    }
}
