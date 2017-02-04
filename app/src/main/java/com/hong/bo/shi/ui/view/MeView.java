package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.contract.MeContract;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

/**
 * Created by andy on 2016/12/12.
 */

public class MeView extends RootView<MeContract.Presenter> implements MeContract.View {

    private CommonTitle mCommonTitle;
    private ImageDraweeView mImageDraweeView;
    private TextView mTvName;
    private ImageView mIvGender;

    public MeView(Context context) {
        super(context);
    }

    public MeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(MeContract.Presenter presenter) {
        mPresenter = presenter;
    }
    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.fragment_me_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setTitle("我的");
        mImageDraweeView = findViewByIds(R.id.ivPhoto);
        mTvName = findViewByIds(R.id.tvName);
        mIvGender = findViewByIds(R.id.ivGender);
        findViewById(R.id.rl_layout).setOnClickListener(this);
        findViewById(R.id.tvEditPass).setOnClickListener(this);
        findViewById(R.id.tvSetting).setOnClickListener(this);
        findViewById(R.id.tvFeedback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.rl_layout){
            UIHelper.showUserDetails(mContext, App.getInstance().getUserInfo().getGuid(), "我的");
        }else if(v.getId() == R.id.tvEditPass){
            UIHelper.showEditPass(mContext);
        }else if(v.getId() == R.id.tvSetting){
            UIHelper.showSetting(mContext);
        }else if(v.getId() == R.id.tvFeedback){
            
        }
    }

    @Override
    public void updateView() {
        UserInfo info = App.getInstance().getUserInfo();
        mImageDraweeView.setImageDraweeView(65, 65, info.getAvaturl());
        mTvName.setText(info.getName());
        mIvGender.setImageResource(info.getGender() == 0 ? R.mipmap.icon_24_gender_f : R.mipmap.icon_24_gender_m);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mCommonTitle = null;
        mIvGender = null;
        mTvName = null;
        mImageDraweeView = null;
    }
}
