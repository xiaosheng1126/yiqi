package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.presenter.contract.EditPassContract;
import com.hong.bo.shi.ui.activitys.EditPassActivity;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.widget.CommonTitle;

/**
 * Created by andy on 2016/12/19.
 */

public class EditPassView extends RootView<EditPassContract.Presenter> implements EditPassContract.View {

    private CommonTitle mCommonTitle;
    private EditText mEtOldPass;//旧密码
    private EditText mEtNewPass;//新密码
    private EditText mEtOkNewPass;//确认新密码
    private String mNewPass;

    public EditPassView(Context context) {
        super(context);
    }

    public EditPassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(EditPassContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showError(String msg) {
        dismissDialog();
       Toast.makeText(mContext, "密码修改失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_edit_pass_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setLeftView("我的");
        mCommonTitle.setTitle("修改密码");
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        mEtOldPass = findViewByIds(R.id.etOldPass);
        mEtNewPass = findViewByIds(R.id.etNewPass);
        mEtOkNewPass = findViewByIds(R.id.etOkNewPass);
        findViewById(R.id.tvEdit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvEdit){
            edit();
        }
    }

    private void edit(){
        String oldPass = mEtOldPass.getText().toString();
        mNewPass = mEtNewPass.getText().toString();
        String mOkNewPass = mEtOkNewPass.getText().toString();
        if(TextUtils.isEmpty(oldPass) || TextUtils.isEmpty(oldPass.trim())){
            Toast.makeText(mContext, "请输入原密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mNewPass) || TextUtils.isEmpty(mNewPass.trim())){
            Toast.makeText(mContext, "请输入新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(mOkNewPass) || TextUtils.isEmpty(mNewPass.trim())){
            Toast.makeText(mContext, "请输入确认新密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!oldPass.equals(PreferencesUtils.getPass())){
            Toast.makeText(mContext, "原密码输入不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!mNewPass.equals(mOkNewPass)){
            Toast.makeText(mContext, "新密码和确认密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoading();
        mPresenter.changePassword(mNewPass);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_SHORT).show();
        PreferencesUtils.setPass(mNewPass);
        ((EditPassActivity)mContext).finish();
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mEtNewPass = null;
        mEtOldPass = null;
        mEtOkNewPass = null;
        mCommonTitle = null;
        mNewPass = null;
    }
}
