package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.contract.EditUserInfoContract;
import com.hong.bo.shi.ui.activitys.EditUserInfoActivity;
import com.hong.bo.shi.widget.CommonTitle;

/**
 * Created by andy on 2016/12/19.
 */

public class EditUserInfoView extends RootView<EditUserInfoContract.Presenter>
implements EditUserInfoContract.View, View.OnClickListener,TextWatcher{

    private CommonTitle mCommonTitle;
    private RelativeLayout mEditLayout;
    private LinearLayout mSelectLayout;
    private TextView mTvSelectMan;
    private TextView mTvSelectWomen;
    private EditText mEtContent;
    private ImageView mIvClear;
    private int mGender;

    public EditUserInfoView(Context context) {
        super(context);
    }

    public EditUserInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(EditUserInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_edit_user_info_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mEditLayout = findViewByIds(R.id.rl_edit_layout);
        mSelectLayout = findViewByIds(R.id.ll_select_layout);
        mTvSelectMan = findViewByIds(R.id.tvSelectMan);
        mTvSelectWomen = findViewByIds(R.id.tvSelectWomen);
        mEtContent = findViewByIds(R.id.etContent);
        mIvClear = findViewByIds(R.id.ivClear);
        mCommonTitle.setLeftView("详细资料");
        mCommonTitle.setRightView("确定");
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        mCommonTitle.setOnRightClickListener((CommonTitle.OnRightClickListener) mContext);
        initEvent();
    }

    private void initEvent() {
        mTvSelectMan.setOnClickListener(this);
        mTvSelectWomen.setOnClickListener(this);
        mIvClear.setOnClickListener(this);
        mEtContent.addTextChangedListener(this);
    }

    @Override
    public void setType(int type) {
        UserInfo userInfo = App.getInstance().getUserInfo();
        if(type == 2){
            mCommonTitle.setTitle("选择性别");
            mSelectLayout.setVisibility(VISIBLE);
            mEditLayout.setVisibility(GONE);
            mGender = userInfo.getGender();
            setGender();
        }else{
            mSelectLayout.setVisibility(GONE);
            mEditLayout.setVisibility(VISIBLE);
            if(type == 0){
                mEtContent.setText(userInfo.getName());
                mCommonTitle.setTitle("修改用户名");
            }else{
                mEtContent.setText(userInfo.getMobile());
                mCommonTitle.setTitle("修改手机号");
            }
            mCommonTitle.setEnabled(false);
            mCommonTitle.setRightViewColor(Color.parseColor("#959595"));
            mIvClear.setVisibility(VISIBLE);
        }
    }

    @Override
    public String getContent() {
        return mEtContent.getText().toString();
    }

    @Override
    public int getGender() {
        return mGender;
    }

    @Override
    public void onSuccess() {
        dismissDialog();
        ((EditUserInfoActivity)mContext).finish();
    }

    @Override
    public void onFailed() {
        dismissDialog();
        Toast.makeText(mContext, "用户信息修改失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        if(mTvSelectMan == v){
            mGender = 1;
            setRightViewEnable(true);
            setGender();
        }else if(mTvSelectWomen == v){
            mGender = 0;
            setRightViewEnable(true);
            setGender();
        }else if(mIvClear == v){
            mEtContent.setText("");
            mIvClear.setVisibility(INVISIBLE);
            setRightViewEnable(false);
        }
    }

    private void setGender(){
        Drawable drawable = getResources().getDrawable(R.mipmap.radio_20_sel);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        if(mGender == 0){
            mTvSelectWomen.setCompoundDrawables(null, null, drawable, null);
            mTvSelectMan.setCompoundDrawables(null, null, null, null);
        }else{
            mTvSelectMan.setCompoundDrawables(null, null, drawable, null);
            mTvSelectWomen.setCompoundDrawables(null, null, null, null);
        }
    }

    private void setRightViewEnable(boolean enable){
        mCommonTitle.setEnabled(enable);
        if(enable){
            mCommonTitle.setRightViewColor(Color.parseColor("#ffffff"));
        }else {
            mCommonTitle.setRightViewColor(Color.parseColor("#959595"));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String content = mEtContent.getText().toString();
        if(TextUtils.isEmpty(content)){
            mIvClear.setVisibility(INVISIBLE);
            setRightViewEnable(false);
        }else{
            mIvClear.setVisibility(VISIBLE);
            setRightViewEnable(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onDestory() {
        super.onDestory();
        mCommonTitle = null;
        mEditLayout = null;
        mSelectLayout = null;
        mTvSelectMan = null;
        mTvSelectWomen = null;
        mEtContent = null;
        mIvClear = null;
    }
}
