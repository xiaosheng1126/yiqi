package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.widget.CommonTitle;

public class FeebackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonTitle commonTitle = findViewByIds(R.id.commonTitle);
        commonTitle.setLeftView("我的");
        commonTitle.setTitle("问题反馈");
        commonTitle.setOnLeftClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_feeback;
    }
}
