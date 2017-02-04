package com.hong.bo.shi.emoji;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.recorder.AudioRecorderButton;
import com.hong.bo.shi.utils.DensityUtils;
import com.hong.bo.shi.widget.UnScrollViewPager;

/**
 * author : zejian
 * time : 2016年1月5日 上午11:14:27
 * email : shinezejian@163.com
 * description :源码来自开源项目https://github.com/dss886/Android-EmotionInputDetector
 * 本人仅做细微修改以及代码解析
 */
public class EmotionKeyboard {

    public static final String SHARE_PREFERENCE_NAME = "EmotionKeyboard";
    public static final String SHARE_PREFERENCE_SOFT_INPUT_HEIGHT = "soft_input_height";

    private Activity mActivity;
    private InputMethodManager mInputManager;//软键盘管理类
    private SharedPreferences mSp;
    private View mEmotionLayout;//表情布局
    private EditText mEditText;//
    private View mContentView;//内容布局view,即除了表情布局或者软键盘布局以外的布局，用于固定bar的高度，防止跳闪
    private UnScrollViewPager mViewPager;
    private AudioRecorderButton mAudioRecorderButton;
    private ImageView mIvAudio;
    private ImageView mIvEmoji;
    private ImageView mIvMore;
    private TextView mTvSend;

    private EmotionKeyboard() {
    }
    /**
     * 外部静态调用
     * @param activity
     * @return
     */
    public static EmotionKeyboard with(Activity activity) {
        EmotionKeyboard emotionInputDetector = new EmotionKeyboard();
        emotionInputDetector.mActivity = activity;
        emotionInputDetector.mInputManager = (InputMethodManager)
                activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        emotionInputDetector.mSp = activity.getSharedPreferences(SHARE_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        return emotionInputDetector;
    }



    /**
     * 绑定内容view，此view用于固定bar的高度，防止跳闪
     * @param contentView
     * @return
     */
    public EmotionKeyboard bindToContent(View contentView) {
        mContentView = contentView;
        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    hideSoftInput();
                    if (mEmotionLayout.isShown()) {
                        hideEmotionLayout(false);
                    }
                }
                return false;
            }
        });
        return this;
    }

    public EmotionKeyboard bindToSendTextView(TextView textView){
        this.mTvSend = textView;
        return this;
    }

    /**
     * 绑定编辑框
     * @param editText
     * @return
     */
    public EmotionKeyboard bindToEditText(EditText editText) {
        mEditText = editText;
        mEditText.requestFocus();
        mEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP && mEmotionLayout.isShown()) {
                    mIvEmoji.setImageResource(R.drawable.chat_emoji_btn);
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏表情布局，显示软件盘
                    //软件盘显示后，释放内容高度
                    unlockContentHeightDelayed();
                } else {
                    emotioLayoutChange();
                }
                return false;
            }
        });
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mEditText.getText().toString();
                if(TextUtils.isEmpty(text)){
                    mIvMore.setVisibility(View.VISIBLE);
                    mTvSend.setVisibility(View.GONE);
                }else{
                    mIvMore.setVisibility(View.GONE);
                    mTvSend.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        return this;
    }

    public EmotionKeyboard bindToUnScrollViewPager(UnScrollViewPager viewPager){
        this.mViewPager = viewPager;
        return this;
    }

    /**
     * 绑定更多按钮
     * @param ivMore
     * @return
     */
    public EmotionKeyboard bindToMoreButton(ImageView ivMore) {
        this.mIvMore = ivMore;
        ivMore.setOnClickListener(listener);
        return this;
    }


    /**
     * 绑定更多按钮
     * @param ivEmoji
     * @return
     */
    public EmotionKeyboard bindToEmotionButton(ImageView ivEmoji) {
        this.mIvEmoji  = ivEmoji;
        ivEmoji.setOnClickListener(listener);
        return this;
    }

    public EmotionKeyboard bindToAudioRecorderButton(AudioRecorderButton button){
        this.mAudioRecorderButton = button;
        return this;
    }

    /**
     * 表情按钮和更多按钮可以使用这个listener
     */
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mAudioRecorderButton.isShown()){
                mEditText.setVisibility(View.VISIBLE);
                mAudioRecorderButton.setVisibility(View.GONE);
                mIvAudio.setImageResource(R.drawable.chat_voice_btn);
            }
            int index = v == mIvEmoji ? 0 : 1;
            if (mEmotionLayout.isShown()) {
                if(mViewPager.getCurrentItem() == index) {
                    lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                    hideEmotionLayout(true);//隐藏更多布局，显示软件盘
                    unlockContentHeightDelayed();//软件盘显示后，释放内容高度
                    mIvEmoji.setImageResource(R.drawable.chat_emoji_btn);
                }else{
                    if(v == mIvEmoji){
                        mIvEmoji.setImageResource(R.drawable.chat_keyboard_btn);
                    }else{
                        mIvEmoji.setImageResource(R.drawable.chat_emoji_btn);
                    }
                    mViewPager.setCurrentItem(index);
                }
            } else{
                mViewPager.setCurrentItem(index);
                if (isSoftInputShown()) {//同上
                    lockContentHeight();
                    showEmotionLayout();
                    unlockContentHeightDelayed();
                } else {
                    showEmotionLayout();//两者都没显示，直接显示表情布局
                }
                if(v == mIvEmoji){
                    mIvEmoji.setImageResource(R.drawable.chat_keyboard_btn);
                }else{
                    mIvEmoji.setImageResource(R.drawable.chat_emoji_btn);
                }
            }
        }
    };

    /**
     * 绑定语音按钮
     * @param ivAudio
     * @return
     */
    public EmotionKeyboard bindToAudioButton(ImageView ivAudio) {
        this.mIvAudio = ivAudio;
        ivAudio.setOnClickListener(audioListener);
        return this;
    }

    /**
     * 语音按钮可以使用这个listener
     */
    private View.OnClickListener audioListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mAudioRecorderButton.isShown()){
                mEditText.setVisibility(View.VISIBLE);
                mAudioRecorderButton.setVisibility(View.GONE);
                mIvAudio.setImageResource(R.drawable.chat_voice_btn);
            }else{
                mEditText.setVisibility(View.GONE);
                mAudioRecorderButton.setVisibility(View.VISIBLE);
                mIvAudio.setImageResource(R.drawable.chat_keyboard_btn);
            }
            mIvEmoji.setImageResource(R.drawable.chat_emoji_btn);
            if (mEmotionLayout.isShown() && mAudioRecorderButton.isShown()) {
                hideEmotionLayout(true);//隐藏更多布局
                unlockContentHeightDelayed();//释放内容高度
            }else if(!mEmotionLayout.isShown() && !mAudioRecorderButton.isShown()){
                lockContentHeight();//显示软件盘时，锁定内容高度，防止跳闪。
                hideEmotionLayout(true);//隐藏更多布局，显示软件盘
                unlockContentHeightDelayed();//软件盘显示后，释放内容高度
            }else{
                hideSoftInput();//隐藏软键盘
            }
        }
    };
    /**
     * 设置表情内容布局
     * @param emotionView
     * @return
     */
    public EmotionKeyboard setEmotionView(View emotionView) {
        mEmotionLayout = emotionView;
        return this;
    }

    public EmotionKeyboard build() {
        //设置软件盘的模式：SOFT_INPUT_ADJUST_RESIZE
        // 这个属性表示Activity的主窗口总是会被调整大小，从而保证软键盘显示空间。
        //从而方便我们计算软件盘的高度
        mActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN |
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //隐藏软件盘
        hideSoftInput();
        return this;
    }

    /**
     * 点击返回键时先隐藏表情布局
     *
     * @return
     */
    public boolean interceptBackPress() {
        if (mEmotionLayout.isShown()) {
            hideEmotionLayout(false);
            return true;
        }
        hideSoftInput();
        return false;
    }

    private void showEmotionLayout() {
        int softInputHeight = getSupportSoftInputHeight();
        if (softInputHeight == 0) {
            softInputHeight = mSp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT,
                    DensityUtils.dp2px(mActivity, 250));
        }
        hideSoftInput();
        mEmotionLayout.getLayoutParams().height = softInputHeight;
        mEmotionLayout.setVisibility(View.VISIBLE);
        emotioLayoutChange();
    }


    /**
     * 隐藏表情布局
     *
     * @param showSoftInput 是否显示软件盘
     */
    private void hideEmotionLayout(boolean showSoftInput) {
        if (mEmotionLayout.isShown()) {
            mEmotionLayout.setVisibility(View.GONE);
            if (showSoftInput) {
                showSoftInput();
            }
        }
    }

    /**
     * 锁定内容高度，防止跳闪
     */
    private void lockContentHeight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mContentView.getLayoutParams();
        params.height = mContentView.getHeight();
        params.weight = 0.0F;
    }

    /**
     * 释放被锁定的内容高度
     */
    private void unlockContentHeightDelayed() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) mContentView.getLayoutParams()).weight = 1.0F;
            }
        }, 200L);
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    public void showSoftInput() {
        mEditText.requestFocus();
        mEditText.post(new Runnable() {
            @Override
            public void run() {
                mInputManager.showSoftInput(mEditText, 0);
                emotioLayoutChange();
            }
        });
    }

    /**
     * 编辑框获取焦点，并显示软件盘
     */
    private void emotioLayoutChange() {
        mEditText.postDelayed(new Runnable() {
            @Override
            public void run() {
//				if(mListener != null){
//					mListener.show();
//				}
            }
        }, 100);
    }

    /**
     * 隐藏软件盘
     */
    private void hideSoftInput() {
        mInputManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * 是否显示软件盘
     * @return
     */
    private boolean isSoftInputShown() {
        return getSupportSoftInputHeight() != 0;
    }

    /**
     * 获取软件盘的高度
     * @return
     */
    private int getSupportSoftInputHeight() {
        Rect r = new Rect();
        /**
         * decorView是window中的最顶层view，可以从window中通过getDecorView获取到decorView。
         * 通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
         */
        mActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
        //获取屏幕的高度
        int screenHeight = mActivity.getWindow().getDecorView().getRootView().getHeight();
        //计算软件盘的高度
        int softInputHeight = screenHeight - r.bottom;

        /**
         * 某些Android版本下，没有显示软键盘时减出来的高度总是144，而不是零，
         * 这是因为高度是包括了虚拟按键栏的(例如华为系列)，所以在API Level高于20时，
         * 我们需要减去底部虚拟按键栏的高度（如果有的话）
         */
        if (Build.VERSION.SDK_INT >= 20) {
            // When SDK Level >= 20 (Android L), the softInputHeight will contain the height of softButtonsBar (if has)
            softInputHeight = softInputHeight - getSoftButtonsBarHeight();
        }

        if (softInputHeight < 0) {
        }
        //存一份到本地
        if (softInputHeight > 0) {
            mSp.edit().putInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, softInputHeight).apply();
        }
        return softInputHeight;
    }


    /**
     * 底部虚拟按键栏的高度
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 获取软键盘高度
     * @return
     */
    public int getKeyBoardHeight() {
        return mSp.getInt(SHARE_PREFERENCE_SOFT_INPUT_HEIGHT, 400);
    }

}
