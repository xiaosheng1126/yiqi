package com.hong.bo.shi.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.utils.DensityUtils;

import static com.hong.bo.shi.widget.CommonTitle.Type.CENTER;
import static com.hong.bo.shi.widget.CommonTitle.Type.LEFT;
import static com.hong.bo.shi.widget.CommonTitle.Type.RIGHT;

/**
 * Created by andy on 2016/12/13.
 */

public class CommonTitle extends FrameLayout implements View.OnClickListener{

    private TextView mTvLeft;
    private TextView mTvTitle;
    private TextView mTvRight;
    private ImageView mIvRight;
    private FrameLayout mLeftLayout;
    private FrameLayout mCenterLayout;
    private FrameLayout mRightLayout;
    private OnLeftClickListener mLeftClickListener;
    private OnCenterClickListener mCenterClickListener;
    private OnRightClickListener mRightClickListener;
    private long mLastTime;
    private static final long DISTANCE = 500;

    public CommonTitle(Context context) {
        this(context, null);
    }

    public CommonTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.common_title_layout, this);
        initView();
    }

    private void initView() {
        mTvLeft = (TextView) findViewById(R.id.tvLeft);
        mTvTitle = (TextView) findViewById(R.id.tvTitle);
        mTvRight = (TextView) findViewById(R.id.tvRight);
        mIvRight = (ImageView) findViewById(R.id.ivRight);
        mLeftLayout = (FrameLayout) findViewById(R.id.leftLayout);
        mCenterLayout = (FrameLayout) findViewById(R.id.centerLayout);
        mRightLayout = (FrameLayout) findViewById(R.id.rightLayout);
        mLeftLayout.setVisibility(INVISIBLE);
        mCenterLayout.setVisibility(INVISIBLE);
        mRightLayout.setVisibility(INVISIBLE);
        mTvLeft.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
    }


    public void setLeftView(@NonNull String text){
        setLeftView();
        mTvLeft.setText(text);
    }

    public void setLeftView(@StringRes int textId){
        setLeftView();
        mTvLeft.setText(textId);
    }

    private void setLeftView(){
        mLeftLayout.setVisibility(VISIBLE);
        Drawable drawable = getResources().getDrawable(R.mipmap.back_icon);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTvLeft.setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), 2));
        mTvLeft.setCompoundDrawables(drawable, null, null, null);
    }

    public void setTitle(@NonNull String text){
        mCenterLayout.setVisibility(VISIBLE);
        mTvTitle.setText(text);
    }

    public void setTitle(@StringRes int textId){
        mCenterLayout.setVisibility(VISIBLE);
        mTvTitle.setText(textId);
    }

    public void setRightView(@DrawableRes int drawableId){
        mRightLayout.setVisibility(VISIBLE);
        mIvRight.setVisibility(VISIBLE);
        mTvRight.setVisibility(GONE);
        mIvRight.setImageResource(drawableId);
    }

    public void setRightView(String text){
        mRightLayout.setVisibility(VISIBLE);
        mTvRight.setVisibility(VISIBLE);
        mIvRight.setVisibility(GONE);
        mTvRight.setText(text);
    }

    public TextView getRightTextView(){
        return mTvRight;
    }

    public ImageView getRightImageView(){
        return mIvRight;
    }

    public void setRightViewEnable(boolean enable){
        mTvRight.setEnabled(enable);
        mIvRight.setEnabled(enable);
    }

    public void setRightViewColor(int color){
        mTvRight.setTextColor(color);
    }


    public void showOrHide(@Types int type, boolean isShow){
        if(type == LEFT){
            mLeftLayout.setVisibility(isShow ? VISIBLE : INVISIBLE);
        }
        if(type == CENTER){
            mCenterLayout.setVisibility(isShow ? VISIBLE : INVISIBLE);
        }
        if(type == RIGHT){
            mRightLayout.setVisibility(isShow ? VISIBLE : INVISIBLE);
        }
    }

    public void setCenterLayout(View centerLayout){
        mCenterLayout.setVisibility(VISIBLE);
        mCenterLayout.removeAllViews();
        mCenterLayout.addView(centerLayout);
    }

    @Override
    public void onClick(View v) {
        //避免快速点击
        if(System.currentTimeMillis() - mLastTime  < DISTANCE){
            mLastTime = System.currentTimeMillis();
            return;
        }
        mLastTime = System.currentTimeMillis();
        switch (v.getId()){
            case R.id.tvLeft:
                if(mLeftClickListener != null)mLeftClickListener.onLeftClick(v);
                break;
            case R.id.tvTitle:
                if(mCenterClickListener != null)mCenterClickListener.onCenterClick(v);
                break;
            case R.id.tvRight:
            case R.id.ivRight:
                if(mRightClickListener != null)mRightClickListener.onRightClick(v);
                break;
        }
    }

    @IntDef({LEFT, CENTER, RIGHT})
    public @interface Types{
    }

    public static final class Type{
        public static final int LEFT = 0;
        public static final int CENTER = 1;
        public static final int RIGHT = 2;
    }

    public interface OnLeftClickListener{
        void onLeftClick(View view);
    }

    public interface OnRightClickListener{
        void onRightClick(View view);
    }

    public interface OnCenterClickListener{
        void onCenterClick(View view);
    }

    public void setOnLeftClickListener(OnLeftClickListener listener){
        this.mLeftClickListener = listener;
    }
    public void setOnRightClickListener(OnRightClickListener listener){
        this.mRightClickListener = listener;
    }
    public void setOnCenterClickListener(OnCenterClickListener listener){
        this.mCenterClickListener = listener;
    }
}
