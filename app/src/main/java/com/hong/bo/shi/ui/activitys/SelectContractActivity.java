package com.hong.bo.shi.ui.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseMvpActivity;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.presenter.SelectContractPresenter;
import com.hong.bo.shi.presenter.contract.SelectContract;
import com.hong.bo.shi.utils.UIHelper;

public class SelectContractActivity extends BaseMvpActivity<SelectContract.Presenter,SelectContract.View> {

    private String mGroupGuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupGuid = getIntent().getStringExtra(Constants.Key.GUID);
        mPresenter.setExitUsres(mGroupGuid);
        mView.setBackView(getIntent().getStringExtra(Constants.Key.BACK_TITLE));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_contract;
    }

    @Override
    protected SelectContract.View createView() {
        return findViewByIds(R.id.select_contract_view);
    }

    @Override
    protected SelectContract.Presenter createPresenter() {
        return new SelectContractPresenter(mView);
    }

    @Override
    public void onLeftClick(View view) {
        new AlertDialog.Builder(this).setMessage("确定放弃本次操作?")
                .setPositiveButton(R.string.ac_select_contacts_ok,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setNegativeButton(R.string.ac_select_contacts_cancel,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGroupGuid = null;
    }

    public void toChatActivity(GroupInfo info){
        UIHelper.showChat(this, "消息", info);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && RESULT_OK == resultCode && data != null){
            String guids = data.getStringExtra(Constants.Key.GUIDS);
            mPresenter.setSelect(guids);
        }
    }
}
