package com.hong.bo.shi.ui.fragments;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseFragment;
import com.hong.bo.shi.emoji.EmotiomComplateFragment;
import com.hong.bo.shi.emoji.EmotionKeyboard;
import com.hong.bo.shi.emoji.GlobalOnItemClickManagerUtils;
import com.hong.bo.shi.emoji.MoreFragment;
import com.hong.bo.shi.emoji.NoHorizontalScrollerVPAdapter;
import com.hong.bo.shi.presenter.contract.ChatContract;
import com.hong.bo.shi.recorder.AudioRecorderButton;
import com.hong.bo.shi.widget.UnScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/13.
 */

public class ChatBottomFragment extends BaseFragment<ChatContract.Presenter> {

    //表情面板
    private EmotionKeyboard mEmotionKeyboard;
    private EditText mEtContent;
    private ImageView mIvEmjio;
    private ImageView mIvAudio;
    private ImageView mIvMore;
    private TextView mTvSend;
    //需要绑定的内容view
    private View mContentView;
    //不可横向滚动的ViewPager
    private UnScrollViewPager mViewPager;
    private AudioRecorderButton mAudioRecorderButton;
    private OnBottomOperationListener mListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chat_bottom;
    }

    @Override
    public void initView() {
        mViewPager = findViewById(R.id.vp_emotionview_layout);
        mAudioRecorderButton = findViewById(R.id.audioRecorderButn);
        mEtContent = findViewById(R.id.chat_et_input);
        mIvEmjio = findViewById(R.id.chat_iv_emoji);
        mIvAudio = findViewById(R.id.chat_iv_audio);
        mIvMore = findViewById(R.id.chat_iv_more);
        mTvSend = findViewById(R.id.chat_tv_send);
        mEtContent.setVisibility(View.VISIBLE);
        mIvEmjio.setVisibility(View.VISIBLE);
        mIvAudio.setVisibility(View.VISIBLE);
        mTvSend.setVisibility(View.GONE);
        mAudioRecorderButton.setVisibility(View.GONE);
        mIvMore.setVisibility(View.VISIBLE);
        initEvent();
    }

    private void initEvent() {
        if (getActivity() instanceof OnBottomOperationListener) {
            mListener = (OnBottomOperationListener) getActivity();
        }
        //创建全局监听
        GlobalOnItemClickManagerUtils globalOnItemClickManager =
                GlobalOnItemClickManagerUtils.getInstance(getActivity());
        //绑定当前Bar的编辑框
        globalOnItemClickManager.attachToEditText(mEtContent);
        globalOnItemClickManager.setListener(mListener).
                attachToChatBottomFragment(this);
        mAudioRecorderButton.setOnAudioFinishRecorderListener(new AudioRecorderButton.OnAudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String path) {
                if(mListener != null){
                    mListener.sendAudio(path, Math.round(seconds));
                }
            }
            @Override
            public void onDown() {
                if(mListener != null){
                    mListener.startRecoredAudio();
                }
            }
        });
        mTvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    mListener.sendText(mEtContent.getText().toString());
                    mEtContent.setText("");
                }
            }
        });
        mEmotionKeyboard = EmotionKeyboard.with(getActivity())
                .setEmotionView(findViewById(R.id.ll_emotion_layout))//绑定表情面板
                .bindToContent(mContentView)//绑定内容view
                .bindToAudioRecorderButton(mAudioRecorderButton)//AudioRecorderButton
                .bindToEditText(mEtContent)//判断绑定那种EditView
                .bindToSendTextView(mTvSend)//绑定发送按钮
                .bindToEmotionButton(mIvEmjio)//绑定表情按钮
                .bindToMoreButton(mIvMore)//绑定更多按钮
                .bindToAudioButton(mIvAudio)//绑定录音按钮
                .bindToUnScrollViewPager(mViewPager)//绑定viewPager按钮
                .build();
        List<Fragment> list = new ArrayList<>();
        list.add(new EmotiomComplateFragment());
        list.add(new MoreFragment());
        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(
                getActivity().getSupportFragmentManager(), list);
        mViewPager.setAdapter(adapter);
    }

    /**
     * 绑定内容view
     * @param contentView
     * @return
     */
    public void bindToContentView(View contentView) {
        this.mContentView = contentView;
    }

    /**
     * 是否拦截返回键操作，如果此时表情布局未隐藏，先隐藏表情布局
     * @return true则隐藏表情布局，拦截返回键操作
     * false 则不拦截返回键操作
     */
    public boolean isInterceptBackPress() {
        return mEmotionKeyboard.interceptBackPress();
    }

    public interface OnBottomOperationListener{
        void sendText(String text);
        void sendAudio(String filePath, long time);
        void openCamera();// 打开相机
        void openAlbum();//打开相册
        void openVideo();//打开摄像机
        void openLocation();//定位
        void startRecoredAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mEmotionKeyboard = null;
        mIvAudio = null;
        mIvEmjio = null;
        mIvMore = null;
        mEtContent = null;
        mAudioRecorderButton = null;
        mTvSend = null;
        mViewPager = null;
        mListener = null;
    }
}
