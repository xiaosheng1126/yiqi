package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.dialog.PutaoDialog;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.contract.UserDetailsContract;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

/**
 * Created by andy on 2016/12/13.
 */

public class UserDetailsView extends RootView<UserDetailsContract.Presenter> implements UserDetailsContract.View {

    private CommonTitle mCommonTitle;
    private UserInfo mUserInfo;
    private boolean isMe;
    private TextView mTvName;
    private TextView mTvSex;
    private TextView mTvMobile;
    private ImageDraweeView mImageDraweeView;

    public UserDetailsView(Context context) {
        super(context);
    }

    public UserDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(UserDetailsContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showError(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        dismissDialog();
    }

    protected void getLayout() {
        inflate(mContext, R.layout.activity_user_details_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setTitle("详细资料");
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        findViewById(R.id.tvCreateChat).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvCreateChat) {
            mPresenter.createChat();
        } else if (v.getId() == R.id.rlPhoto) {
            if (isMe) {
                UIHelper.editUserPhoto(mContext);
            } else {
                UIHelper.showBigPic(mContext, mUserInfo.getAvaturl());
            }
        } else if (v.getId() == R.id.rlName) {
            UIHelper.showEditUserInfo(mContext, 0);
        } else if (v.getId() == R.id.rlSex) {
            UIHelper.showEditUserInfo(mContext, 2);
        } else if (v.getId() == R.id.rlMobile) {
            if (isMe) {
                UIHelper.showEditUserInfo(mContext, 1);
            } else {
                PutaoDialog.clickMobile(mContext, new PutaoDialog.OnDialogItemClickListener() {
                    @Override
                    public boolean onItemClick(PutaoDialog.Item item) {
                        if (item.getId() == R.id.call_mobile) {
                            UIHelper.callMobile(mContext, mUserInfo.getMobile());
                        } else if (item.getId() == R.id.send_message) {
                            UIHelper.sendMessage(mContext, mUserInfo.getMobile());
                        }
                        return true;
                    }
                });
            }
        } else if (v.getId() == R.id.rlQRCode) {
            UIHelper.showBigPic(mContext, mUserInfo.getqRCodeUrl());
        }
    }

    @Override
    public void init(UserInfo userInfo) {
        this.mUserInfo = userInfo;
        isMe = App.getInstance().getUserInfo().getGuid().equals(userInfo.getGuid());
        if (isMe) {
            findViewById(R.id.bottomLayout).setVisibility(GONE);
            findViewById(R.id.rlName).setOnClickListener(this);
            findViewById(R.id.rlSex).setOnClickListener(this);
        } else {
            findViewById(R.id.iv2).setVisibility(GONE);
            findViewById(R.id.iv5).setVisibility(GONE);
            findViewById(R.id.bottomLayout).setVisibility(VISIBLE);
        }
        findViewById(R.id.iv3).setVisibility(GONE);
        findViewById(R.id.iv8).setVisibility(GONE);
        findViewById(R.id.rlMobile).setOnClickListener(this);
        findViewById(R.id.rlPhoto).setOnClickListener(this);
        findViewById(R.id.rlQRCode).setOnClickListener(this);
        mImageDraweeView = findViewByIds(R.id.ivPhoto);
        mImageDraweeView.setImageDraweeView(80, 80, mUserInfo.getAvaturl());
        TextView tvAccount = findViewByIds(R.id.tvAccount);
        tvAccount.setText(mUserInfo.getUserId());
        TextView tvCompany = findViewByIds(R.id.tvCompany);
        tvCompany.setText(mUserInfo.getOrgname());
        mTvName = findViewByIds(R.id.tvName);
        mTvSex = findViewByIds(R.id.tvSex);
        mTvMobile = findViewByIds(R.id.tvMobile);
        mTvName.setText(mUserInfo.getName());
        mTvMobile.setText(mUserInfo.getMobile());
        if (mUserInfo.getGender() == 0) {
            mTvSex.setText("女");
        } else {
            mTvSex.setText("男");
        }
        if (mUserInfo.getGrade() == 0) {
            findViewById(R.id.rlRating).setVisibility(GONE);
        } else {
            findViewById(R.id.rlRating).setVisibility(VISIBLE);
            int rating = mUserInfo.getGrade();
            findViewById(R.id.iv_start1).setVisibility(GONE);
            findViewById(R.id.iv_start2).setVisibility(GONE);
            findViewById(R.id.iv_start3).setVisibility(GONE);
            findViewById(R.id.iv_start4).setVisibility(GONE);
            findViewById(R.id.iv_start5).setVisibility(GONE);
            switch (rating) {
                case 5:
                    findViewById(R.id.iv_start5).setVisibility(VISIBLE);
                case 4:
                    findViewById(R.id.iv_start4).setVisibility(VISIBLE);
                case 3:
                    findViewById(R.id.iv_start3).setVisibility(VISIBLE);
                case 2:
                    findViewById(R.id.iv_start2).setVisibility(VISIBLE);
                case 1:
                    findViewById(R.id.iv_start1).setVisibility(VISIBLE);
            }
        }
        findViewById(R.id.ivPersionIcon).setVisibility(userInfo.getIsCompany() ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void update() {
        if (isMe) {
            mUserInfo = App.getInstance().getUserInfo();
            mTvName.setText(mUserInfo.getName());
            mTvMobile.setText(mUserInfo.getMobile());
            mImageDraweeView.setImageDraweeView(80, 80, mUserInfo.getAvaturl());
            if (mUserInfo.getGender() == 0) {
                mTvSex.setText("女");
            } else {
                mTvSex.setText("男");
            }
        }
    }

    @Override
    public void onSuccess(GroupInfo info) {
        UIHelper.showChat(mContext, "详细资料", info);
    }

    @Override
    public void setBackTitle(String title) {
        mCommonTitle.setLeftView(title);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mCommonTitle = null;
    }
}
