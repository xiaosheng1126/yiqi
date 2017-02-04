package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.contract.EditGroupInfoContract;
import com.hong.bo.shi.ui.activitys.EditGroupInfoActivity;
import com.hong.bo.shi.widget.CommonTitle;

import io.realm.Realm;

/**
 * Created by andy on 2017/1/22.
 */

public class EditGroupInfoView extends RootView<EditGroupInfoContract.Presenter> implements EditGroupInfoContract.View,TextWatcher {

    private CommonTitle mCommonTitle;
    private EditText mEtName;
    private EditText mEtPublic;
    private ImageView mIvClear;
    private String mGroupGuid;
    private int mType;

    public EditGroupInfoView(Context context) {
        super(context);
    }

    public EditGroupInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void showError(String msg) {
        if(mType == 0) {
            Toast.makeText(mContext,"群名称修改失败", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"群公告修改失败", Toast.LENGTH_SHORT).show();
        }
        dismissDialog();
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_edit_groupinfo_view, this);
    }

    @Override
    protected void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setOnLeftClickListener
                ((CommonTitle.OnLeftClickListener) mContext);
        mCommonTitle.setOnRightClickListener((CommonTitle.OnRightClickListener) mContext);
        mEtName = findViewByIds(R.id.etName);
        mEtPublic = findViewByIds(R.id.etPublic);
        mIvClear = findViewByIds(R.id.ivClear);
        mIvClear.setOnClickListener(this);
        mEtName.addTextChangedListener(this);
        mEtPublic.addTextChangedListener(this);
        mCommonTitle.setLeftView("群详情");
    }

    @Override
    public void setPresenter(EditGroupInfoContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void setType(int type, String text, String guid) {
        this.mType = type;
        this.mGroupGuid = guid;
        if(type == 0){
            findViewById(R.id.rl_edit_name_layout).setVisibility(VISIBLE);
            findViewById(R.id.rl_edit_public_layout).setVisibility(GONE);
            findViewById(R.id.fl_public_layout).setVisibility(GONE);
            mEtName.setText(text);
            mCommonTitle.setTitle("群名称");
            mCommonTitle.setRightView("确定");
        }else if(type == 1){
            findViewById(R.id.rl_edit_public_layout).setVisibility(VISIBLE);
            findViewById(R.id.rl_edit_name_layout).setVisibility(GONE);
            findViewById(R.id.fl_public_layout).setVisibility(GONE);
            mEtPublic.setText(text);
            mCommonTitle.setTitle("群公告");
            mCommonTitle.setRightView("确定");
        }else{
            findViewById(R.id.rl_edit_public_layout).setVisibility(GONE);
            findViewById(R.id.rl_edit_name_layout).setVisibility(GONE);
            findViewById(R.id.fl_public_layout).setVisibility(VISIBLE);
            TextView tvPublic = findViewByIds(R.id.tvPublic);
            tvPublic.setText(text);
            mCommonTitle.setTitle("群公告");
        }
        setRightViewEnable(false);
    }

    @Override
    public String getGroupName() {
       return mEtName.getText().toString();
    }

    @Override
    public String getGroupPublic() {
        return mEtPublic.getText().toString();
    }

    @Override
    public void onSuccess() {
        if(mType == 0) {
            Toast.makeText(mContext,"群名称修改成功", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"群公告修改成功", Toast.LENGTH_SHORT).show();
        }
        Realm realm = RealmHelper.getRealm();
        GroupInfo first = RealmHelper.getGroupInfo(realm, mGroupGuid);
        if(first != null){
            realm.beginTransaction();
            if(mType == 0){
                first.setGroupName(getGroupName());
            }else{
                first.setGroupNotice(getGroupPublic());
            }
            realm.commitTransaction();
        }
        realm.close();
        dismissDialog();
        ((EditGroupInfoActivity)mContext).finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String content = null;
        if(mType == 0) {
            content = mEtName.getText().toString();
        }else {
            content = mEtPublic.getText().toString();
        }
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
    public void onClick(View v) {
        if(v == mIvClear){
            mEtName.setText("");
            mIvClear.setVisibility(INVISIBLE);
            setRightViewEnable(false);
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
}
