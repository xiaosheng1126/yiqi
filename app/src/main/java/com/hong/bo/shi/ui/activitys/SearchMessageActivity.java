package com.hong.bo.shi.ui.activitys;

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
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.ui.adapter.MessageAdapter;
import com.hong.bo.shi.widget.CommonTitle;

import io.realm.Realm;
import io.realm.RealmResults;

public class SearchMessageActivity extends BaseActivity {

    private MessageAdapter mAdapter;
    private EditText mEtContent;
    private ImageView mIvClear;
    private String mLastText;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initEvent();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_message;
    }

    private void initView() {
        CommonTitle commonTitle = (CommonTitle) findViewById(R.id.commonTitle);
        commonTitle.setLeftView("消息");
        commonTitle.setTitle("搜索会话列表");
        commonTitle.setOnLeftClickListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MessageAdapter();
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
                    if(TextUtils.isEmpty(text) || TextUtils.isEmpty(text.trim()) ||
                            text.equals(mLastText)){
                        return;
                    }
                    query(text);
                    mLastText = text;
                }
            }
        };
    }

    private void query(String text){
        Realm realm = RealmHelper.getRealm();
        RealmResults<GroupInfo> list =
                realm.where(GroupInfo.class).contains("groupName", text).findAll();
        mAdapter.setData(realm.copyFromRealm(list));
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
}

