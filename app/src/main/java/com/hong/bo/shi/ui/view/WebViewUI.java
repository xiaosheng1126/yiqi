package com.hong.bo.shi.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.Constants;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.BottomBean;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.presenter.contract.WebViewContract;
import com.hong.bo.shi.ui.activitys.WebViewActivity;
import com.hong.bo.shi.ui.adapter.MainAdapter;
import com.hong.bo.shi.ui.adapter.WebViewAdapter;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.utils.WebViewUtils;
import com.hong.bo.shi.widget.CommonTitle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class WebViewUI extends RootView<WebViewContract.Presenter> implements WebViewContract.View, MainAdapter.OnCheckSelectListener,WebViewUtils.WebClientListener {

    private WebView mWebView;
    private CommonTitle mCommonTitle;
    private WebBean mWebBean;
    private LinearLayout mBootomLayout;
    private WebViewAdapter mAdapter;
    private View mFailView;
    private String mUrl;

    public WebViewUI(Context context) {
        super(context);
    }

    public WebViewUI(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(WebViewContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_web_view, this);
    }

    @Override
    protected void initView() {
        mFailView = findViewByIds(R.id.fail_view);
        mWebView = findViewByIds(R.id.webView);
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        WebViewUtils.setWebView(mWebView, this);
        mBootomLayout = findViewByIds(R.id.bottomLayout);
        RecyclerView recyclerView = findViewByIds(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new WebViewAdapter();
        mAdapter.setOnCheckSelectListener(this);
        recyclerView.setAdapter(mAdapter);
        mBootomLayout.setVisibility(View.GONE);
        mFailView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WebViewUtils.loadUrl(mWebView, mUrl);
        showLoading();
        mWebView.setVisibility(VISIBLE);
        mFailView.setVisibility(GONE);
    }

    @Override
    public void initData(WebBean bean) {
        updateTitle(bean);
        WebViewUtils.loadUrl(mWebView, bean.getUrl());
    }

    @Override
    public void updateTitle(WebBean bean) {
        mCommonTitle.setLeftView(bean.getBackTitle());
        mCommonTitle.setTitle(bean.getTitle());
        updateBottomTitle(bean.getOther());
    }

    public void updateBottomTitle(String data){
        if(!TextUtils.isEmpty(data) && !Constants.ERROR.equals(data)){
            mBootomLayout.setVisibility(VISIBLE);
            List<BottomBean> list = new ArrayList<>();
            String[] split = data.split(Constants.STRIP_SPLIT);
            for (String bean : split) {
                list.add(new BottomBean(bean));
            }
            mAdapter.setData(list);
        }else{
            mBootomLayout.setVisibility(GONE);
        }
    }

    @Override
    public boolean isBack() {
        if(mWebBean == null){
            return true;
        }
        if(mWebBean.isShowBack()){
            return true;
        }
        if(!App.getInstance().isNetworkAvailable()){
            return true;
        }
        String format = String.format("javascript:gotoRelativePage('%s')", mWebBean.getPagerUrl());
        WebViewUtils.loadUrl(mWebView, format);
        return false;
    }

    @Override
    public void onCheckSelect(int position) {
        BottomBean item = mAdapter.getItem(position);
        if(item.getType().equals("0")){
            WebViewUtils.loadUrl(mWebView, item.getExtra());
        }else if(item.getType().equals("1")){//跳转到会话界面
            GroupInfo groupInfo = RealmHelper.getGroupInfo(item.getExtra());
            if(groupInfo == null){
                Toast.makeText(mContext, String.format("找不到guid为%s的群",
                        item.getExtra()), Toast.LENGTH_SHORT).show();
            }else{
                UIHelper.showChat(mContext, mWebBean.getTitle(), groupInfo);
                mAdapter.setSelectPosition(0);
                BottomBean bean = mAdapter.getItem(0);
                WebViewUtils.loadUrl(mWebView, item.getExtra());
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(mWebBean != null){
            updateTitle(mWebBean);
        }
        WebViewUtils.loadUrl(view, url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        showLoading();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        dismissDialog();
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        dismissDialog();
        App.getInstance().showNetworkError();
        mUrl = failingUrl;
        KLog.d(failingUrl);
        mWebView.setVisibility(INVISIBLE);
        mFailView.setVisibility(VISIBLE);
    }

    @Override
    public void setHeadTitle(final WebBean webBean) {
        this.mWebBean = webBean;
    }

    @Override
    public void setBottomTitle(final String data) {
        ((WebViewActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateBottomTitle(data);
            }
        });
    }

    @Override
    public void backLogin(final String data) {
        ((WebViewActivity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra(Constants.Key.PATH, data);
                ((WebViewActivity)mContext).setResult(Activity.RESULT_OK, intent);
                ((WebViewActivity)mContext).finish();
            }
        });
    }
}
