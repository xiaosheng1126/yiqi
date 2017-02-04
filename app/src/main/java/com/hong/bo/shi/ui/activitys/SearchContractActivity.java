package com.hong.bo.shi.ui.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.ui.adapter.SearchContractAdapter;
import com.hong.bo.shi.widget.CommonTitle;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.hong.bo.shi.R.id.commonTitle;

/**
 * 搜索本地和云端成员
 */
public class SearchContractActivity extends BaseActivity implements CommonTitle.OnRightClickListener
,SearchContractAdapter.OnSelectUserListener{

    private EditText mEtContent;
    private ImageView mIvClear;
    private String mLastText;
    private Handler mHandler;
    private SearchContractAdapter mAdapter;
    private CommonTitle mCommonTitle;
    private int mType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getIntent().getIntExtra(Constants.Key.TYPE , 0);
        initView();
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_contract;
    }

    private void initView() {
        mCommonTitle = (CommonTitle) findViewById(commonTitle);
        mCommonTitle.setLeftView(getIntent().getStringExtra(Constants.Key.BACK_TITLE));
        mCommonTitle.setTitle("搜索通讯录好友");
        mCommonTitle.setOnLeftClickListener(this);
        if(mType == 1){
            mCommonTitle.setRightView("确定");
            mCommonTitle.setOnRightClickListener(this);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new SearchContractAdapter(mType);
        if(mType == 1) {
            mAdapter.setListener(this);
        }
        String groupGuid = getIntent().getStringExtra(Constants.Key.GUID);
        mAdapter.setSelectGuids(getIntent().getStringExtra(Constants.Key.GUIDS), groupGuid);
        mAdapter.setActivity(this);
        recyclerView.setAdapter(mAdapter);
        mEtContent = (EditText) findViewById(R.id.etContent);
        mIvClear = (ImageView) findViewById(R.id.ivClear);
    }

    private void initEvent() {
        mIvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEtContent.setText("");
                mIvClear.setVisibility(View.INVISIBLE);
                mAdapter.setData(null);
            }
        });
        mIvClear.setVisibility(View.INVISIBLE);
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.sendEmptyMessageDelayed(0, 500);
                String text = mEtContent.getText().toString();
                if(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())){
                    mIvClear.setVisibility(View.INVISIBLE);
                    mAdapter.setData(null);
                }else{
                    mIvClear.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0){
                    String text = mEtContent.getText().toString();
                    if(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim())
                            || text.equals(mLastText)){
                        return;
                    }
                    query(text);
                    mLastText = text;
                }
            }
        };
    }

    private void query(String text) {
        Realm realm = RealmHelper.getRealm();
        RealmResults<UserInfo> userInfos = realm.where(UserInfo.class)
                .contains(Constants.Key.NAME, text).findAll();
        mAdapter.setData(realm.copyFromRealm(userInfos));
        realm.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEtContent = null;
        mIvClear = null;
        mLastText = null;
        mAdapter = null;
        mHandler.removeMessages(0);
    }

    @Override
    public void onRightClick(View view) {
        Intent intent = getIntent();
        intent.putExtra(Constants.Key.GUIDS, mAdapter.getSelectGuids());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onChanged(int count) {
        mCommonTitle.setRightView(count == 0 ? "确定" : String.format("确定(%d)", count));
    }
}
