package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.recorder.MovieRecorderView;

import java.io.File;

public class RecorderVideoActivity extends BaseActivity implements View.OnClickListener, MovieRecorderView.OnRecordFinishListener {

    private MovieRecorderView mMovieRecorderView;
    private TextView mTvStart;
    private TextView mTvCancel;
    private TextView mTvFinish;
    private boolean isFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder_audio);
        mMovieRecorderView = (MovieRecorderView) findViewById(R.id.movie_recorder);
        mMovieRecorderView.setOnRecordFinishListener(this);
        mTvStart = (TextView) findViewById(R.id.tvStart);
        mTvCancel = (TextView) findViewById(R.id.tvCancel);
        mTvFinish = (TextView) findViewById(R.id.tvFinish);
        mTvStart.setOnClickListener(this);
        mTvCancel.setOnClickListener(this);
        mTvFinish.setOnClickListener(this);
        mTvFinish.setEnabled(false);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMovieRecorderView != null) {
            mMovieRecorderView.stop();
        }
        mMovieRecorderView = null;
        mTvCancel = null;
        mTvStart = null;
        mTvFinish = null;
    }

    @Override
    public void onClick(View v) {
        if (v == mTvStart) {
            v.setEnabled(false);
            mMovieRecorderView.record();
            mTvFinish.setEnabled(true);
            mTvCancel.setVisibility(View.VISIBLE);
            mTvFinish.setVisibility(View.VISIBLE);
            mTvStart.setVisibility(View.GONE);
        } else if (v == mTvCancel) {
            mMovieRecorderView.stop();
            //取消前删除视频文件
            if (mMovieRecorderView.getVecordFile() != null && mMovieRecorderView.getVecordFile().exists()) {
                mMovieRecorderView.getVecordFile().delete();
            }
            onLeftClick(null);
        } else if (v == mTvFinish) {
            if (!isFinish) {
                mMovieRecorderView.stop();
            }
            File vecordFile = mMovieRecorderView.getVecordFile();
            Intent intent = new Intent();
            intent.putExtra(Constants.Key.PATH, vecordFile.getAbsolutePath());
            intent.putExtra(Constants.Key.TIME, mMovieRecorderView.getTimeCount());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mMovieRecorderView != null){
            mMovieRecorderView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mTvStart.getVisibility() == View.GONE){
            if(mMovieRecorderView != null){
                mMovieRecorderView.resume();
            }
        }
    }

    @Override
    public void onRecordFinish() {
        Toast.makeText(this, "视频录制完成", Toast.LENGTH_SHORT).show();
        isFinish = true;
    }
}
