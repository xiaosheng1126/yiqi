package com.hong.bo.shi.ui.activitys;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.download.BusProvider;
import com.hong.bo.shi.download.DownloadManager;
import com.hong.bo.shi.download.ProgressEntity;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.utils.DensityUtils;
import com.squareup.otto.Subscribe;

import java.io.IOException;

import static com.hong.bo.shi.R.id.progressBar;


public class PlayMovieActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, MediaPlayer.OnErrorListener
        , MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private SurfaceView mSurfaceView;
    private GroupMessage mMessage;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder holder;
    private ProgressBar mProgressBar;
    private ImageView mIvPlay;
    private ImageView mIvBack;
    private ImageView mIvControl;
    private LinearLayout mLlBottomLayout;
    private TextView mTvProgress;
    private boolean isPrepare;
    private TextView mTvCurrentTime;
    private TextView mTvCountTime;
    private boolean isFinish;
    private boolean isPause;
    private boolean isStarted;//是否播放过
    private ProgressBar mCurrentPb;
    private int mType = 0;// 0 预览 1 播放
    private String mPath;
    private TextView mTvFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_movie);
        BusProvider.getInstance().register(this);
        App.getInstance().registerActivity(this);
        mType = getIntent().getIntExtra(Constants.Key.TYPE, 0);
        if (mType == 1) {
            mMessage = getIntent().getParcelableExtra(GroupMessage.class.getSimpleName());
        } else {
            mPath = getIntent().getStringExtra(Constants.Key.PATH);
        }
        initView();
        if (mType == 0) {
            mTvProgress.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mTvFinish = (TextView) findViewById(R.id.tvFinish);
        if (mType == 1) {
            mTvFinish.setVisibility(View.GONE);
        } else {
            mTvFinish.setVisibility(View.VISIBLE);
        }
        mTvProgress = (TextView) findViewById(R.id.tvProgress);
        mIvPlay = (ImageView) findViewById(R.id.ivPlay);
        mIvPlay.setOnClickListener(this);
        mIvPlay.setVisibility(View.GONE);
        mIvBack = (ImageView) findViewById(R.id.ivBack);
        mCurrentPb = (ProgressBar) findViewById(progressBar);
        mIvControl = (ImageView) findViewById(R.id.ivControl);
        mTvCountTime = (TextView) findViewById(R.id.tvCountTime);
        mTvCurrentTime = (TextView) findViewById(R.id.tvPlayTime);
        mLlBottomLayout = (LinearLayout) findViewById(R.id.llBottom);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        /**获取并设置SurfaceHolder对象 */
        holder = mSurfaceView.getHolder();/**根据 SurfaceView 组件, 获取 SurfaceHolder 对象 */
        holder.addCallback(this);/**为SurfaceHolder 设置回调函数, 即 SurfaceHolder.Callback 子类对象 */
        holder.setFixedSize(DensityUtils.getScreenWidth(this),
                DensityUtils.getScreenHeight(this) - DensityUtils.dp2px(this, 60));/**设置视频大小比例*/
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);/**设置视频类型 */
        mIvBack.setOnClickListener(this);
        mIvControl.setOnClickListener(this);
        mTvFinish.setOnClickListener(this);
        findViewById(R.id.rlControl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPrepare) {
                    if (mIvBack.getVisibility() == View.VISIBLE) {
                        mIvBack.setVisibility(View.GONE);
                        mLlBottomLayout.setVisibility(View.GONE);
                        mTvFinish.setVisibility(View.GONE);
                    } else {
                        mIvBack.setVisibility(View.VISIBLE);
                        mLlBottomLayout.setVisibility(View.VISIBLE);
                        if (mType == 0) {
                            mTvFinish.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaPlayer == null) {
            if (mType == 1) {
                if (mMessage.isDownloadSuccess()) {
                    createMediaPlay(mMessage.getFilePath());
                    mProgressBar.setVisibility(View.GONE);
                    mTvProgress.setVisibility(View.GONE);
                }else{
                    DownloadManager.getRequestQueue().add(mMessage);
                }
            } else {
                createMediaPlay(mPath);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void startPlay() {
        if (mediaPlayer != null && isPrepare) {
            mediaPlayer.start();
            isFinish = false;
            isStarted = true;
            isPause = false;
            setPauseStatue();
        }
    }

    private void setPauseStatue() {
        if (isPause) {
            mIvControl.setImageResource(R.mipmap.play);
            if (isFinish) {
                mIvPlay.setVisibility(View.VISIBLE);
            }
        } else {
            mIvControl.setImageResource(R.mipmap.pause);
            mIvPlay.setVisibility(View.GONE);
        }
    }

    private void pause() {
        if (mediaPlayer != null && isPrepare) {
            mediaPlayer.pause();
            isPause = true;
            setPauseStatue();
        }
    }

    private void error() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "文件已损坏或不存在", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mIvPlay) {
            if (!isPrepare) {
                Toast.makeText(this, "视频播放还没准备好，请等待", Toast.LENGTH_SHORT).show();
            } else {
                startPlay();
            }
        } else if (v == mIvControl) {
            if (!isPrepare) {
                Toast.makeText(this, "视频播放还没准备好，请等待", Toast.LENGTH_SHORT).show();
            } else {
                if (!isStarted || isPause) {
                    startPlay();
                } else if (!isPause) {
                    pause();
                }
            }
        } else if (v == mIvBack) {
            finish();
        } else if (v == mTvFinish) {
            if (mType == 0) {
                Intent intent = new Intent();
                intent.putExtra(Constants.Key.PATH, mPath);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        App.getInstance().unregisterActivity(this);
        mHanlder.removeMessages(0);
        mHanlder = null;
        mediaPlayer = null;
        mIvPlay = null;
        mTvProgress = null;
        mMessage = null;
    }

    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mediaPlayer == null) return;
            if (msg.what == 0) {
                int duration = mediaPlayer.getCurrentPosition() / 1000;
                mTvCurrentTime.setText(String.format("00:%s", duration < 10 ? "0" + duration : duration));
                mHanlder.sendEmptyMessageDelayed(0, 1000);
                mCurrentPb.setProgress(duration);
            }
        }
    };

    /**
     * 创建MediaPlay
     *
     * @param dataSource 播放的数据源
     */
    private void createMediaPlay(final String dataSource) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();/**创建MediaPlayer对象*/
            mediaPlayer.setAudioStreamType(2);/**设置播放音量*/
            mediaPlayer.setDisplay(holder);/**设置播放载体*/
            /**设置MediaPlayer错误监听器,如果出现错误就会回调该方法打印错误代码*/
            mediaPlayer.setOnErrorListener(this);
            /**设置缓冲进度更新监听器 */
            mediaPlayer.setOnBufferingUpdateListener(this);
            /**设置播放完毕监听器 */
            mediaPlayer.setOnCompletionListener(this);
            /**设置准备完毕监听器 */
            mediaPlayer.setOnPreparedListener(this);
        }
        new Thread() {
            public void run() {
                try {
                    KLog.d("设置数据源");
                    mediaPlayer.setDataSource(dataSource);
                    mediaPlayer.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    error();
                } catch (IOException e) {
                    e.printStackTrace();
                    error();
                }
            }
        }.start();
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Toast.makeText(this, "MediaPlayer出现错误" + what + " , extra : " + extra, Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        /**打印缓冲的百分比, 如果缓冲 */
        if (percent == 100) {
            if (mTvProgress.getVisibility() == View.VISIBLE) {
                mTvProgress.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }
        } else {
            if (mTvProgress.getVisibility() == View.GONE) {
                mProgressBar.setVisibility(View.VISIBLE);
                mTvProgress.setVisibility(View.VISIBLE);
            }
            mTvProgress.setText(percent + "%");
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        KLog.d("播放完毕了");
        isFinish = true;
        isPause = true;
        setPauseStatue();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        isPrepare = true;
        KLog.d("准备完毕");
        int duration = mediaPlayer.getDuration() / 1000;
        mCurrentPb.setMax(duration);
        mTvCountTime.setText(String.format("00:%s", duration < 10 ? "0" + duration : duration));
        mTvCurrentTime.setText("00:00");
        mHanlder.sendEmptyMessageDelayed(0, 1000);
        startPlay();
    }

    @Subscribe
    public void updateProgress(ProgressEntity entity) {
        if (entity == null || mMessage == null || !entity.getGuid().equals(mMessage.getGuid())) {
            return;
        }
        if (entity.isFailed()) {
            Toast.makeText(this, "文件下载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        long current = entity.getCurrent() * 100 / entity.getLength();
        mTvProgress.setText(current + "%");
        if (entity.getCurrent() >= entity.getLength()) {
            mTvProgress.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            if (entity.getPath() != null) {
                mMessage.setFilePath(entity.getPath());
                createMediaPlay(mMessage.getFilePath());
            }
        } else {
            mTvProgress.setVisibility(View.VISIBLE);
        }
    }
}
