package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hong.bo.shi.R;
import com.hong.bo.shi.app.App;
import com.hong.bo.shi.app.RequestUrl;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.klog.KLog;
import com.hong.bo.shi.model.bean.BusinessItemBean;
import com.hong.bo.shi.model.bean.HomeOperationBean;
import com.hong.bo.shi.model.bean.WebBean;
import com.hong.bo.shi.presenter.contract.BusinessContract;
import com.hong.bo.shi.ui.adapter.BusinessAdapter;
import com.hong.bo.shi.ui.adapter.MenuAdapter;
import com.hong.bo.shi.utils.InputMethodUtils;
import com.hong.bo.shi.utils.PreferencesUtils;
import com.hong.bo.shi.utils.UIHelper;
import com.hong.bo.shi.utils.WebViewUtils;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2016/12/12.
 */

public class BusinessView extends RootView<BusinessContract.Presenter> implements BusinessContract.View
,WebViewUtils.WebClientListener{

    private BusinessAdapter mAdapter;
    private WebView mWebView;
    private PopupWindow mPopupWindow;
    private EditText mEtContent;
    private WebBean mWebBean;
    private String mPhotoUrl;
    private ImageDraweeView mImageDraweeView;
    private TextView mFailView;

    public BusinessView(Context context) {
        super(context);
    }

    public BusinessView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(BusinessContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    protected void getLayout() {
       inflate(mContext, R.layout.fragment_business_view, this);
    }

    @Override
    protected void initView() {
        mFailView = findViewByIds(R.id.fail_view);
        mEtContent = findViewByIds(R.id.etContent);
        mPhotoUrl = App.getInstance().getUserInfo().getAvaturl();
        mImageDraweeView= findViewByIds(R.id.ivPhoto);
        mImageDraweeView.setImageDraweeView(30, 30, mPhotoUrl);
        RecyclerView recyclerView = findViewByIds(R.id.topRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
        mAdapter = new BusinessAdapter();
        mAdapter.setActivity((BaseActivity) mContext);
        recyclerView.setAdapter(mAdapter);
        update();
        mWebView = findViewByIds(R.id.webView);
        WebViewUtils.setWebView(mWebView, this);
        mWebView.loadUrl(PreferencesUtils.getMainUrl());
        initEvent();
        initPopupWindow();
    }

    private void initEvent() {
        findViewById(R.id.ivOperation).setOnClickListener(this);
        findViewById(R.id.ivSearch).setOnClickListener(this);
        mFailView.setOnClickListener(this);
        mImageDraweeView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivOperation:
                InputMethodUtils.hideSoftInput(mContext, mEtContent);
                if (mPopupWindow != null && !mPopupWindow.isShowing()) {
                    mPopupWindow.showAsDropDown(v);
                }
                break;
            case R.id.ivSearch:
                String text = mEtContent.getText().toString();
                InputMethodUtils.hideSoftInput(mContext, mEtContent);
                if(!TextUtils.isEmpty(text)) {
                    WebBean webBean = new WebBean("搜索", "业务", "", 0);
                    webBean.setUrl(RequestUrl.getHomeSearchUrl(text));
                    UIHelper.showWebView(mContext, webBean);
                }else{
                    Toast.makeText(mContext, "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
                mEtContent.setText("");
                break;
            case R.id.ivPhoto:
                InputMethodUtils.hideSoftInput(mContext, mEtContent);
                UIHelper.showUserDetails(mContext, App.getInstance().getUserInfo().getGuid(), "业务");
                break;
            case R.id.fail_view:
                mWebView.loadUrl(PreferencesUtils.getMainUrl());
                showLoading();
                mFailView.setVisibility(GONE);
                break;
        }
    }

    @Override
    public void update() {
        int[] counts = PreferencesUtils.getMainTopCount();
        String[] urls = PreferencesUtils.getMainTopUrl();
        if(mAdapter.isChanged(counts, urls)) {
            List<BusinessItemBean> list = new ArrayList<BusinessItemBean>();
            BusinessItemBean itemBean = new BusinessItemBean(urls[0], R.mipmap.business_top_1,
                    "协秘书", counts[0]);
            list.add(itemBean);
            itemBean = new BusinessItemBean(urls[1], R.mipmap.business_top_2,
                    "搜一搜", counts[1]);
            list.add(itemBean);
            itemBean = new BusinessItemBean(urls[2], R.mipmap.business_top_3,
                    "扫一扫", counts[2]);
            list.add(itemBean);
            itemBean = new BusinessItemBean(urls[3], R.mipmap.business_top_4,
                    "宏博士", counts[3]);
            list.add(itemBean);
            mAdapter.setData(list);
        }
    }

    @Override
    public void handleScanResult(String result) {
        String tag = "guid=";
        if(result.contains(tag)) {
            String substring = result.substring(
                    result.indexOf(tag) + tag.length(), result.length());
            KLog.i(substring);
            String format = String.format("javascript:jumpToEquipInfo('%s')", substring);
            mWebView.loadUrl(format);
        }else{
            String format = String.format("javascript:jumpToEquipInfo('%s')", result);
            mWebView.loadUrl(format);
        }
    }

    @Override
    public void onResume() {
        String avaturl = App.getInstance().getUserInfo().getAvaturl();
        if(!mPhotoUrl.equals(avaturl)){
            mPhotoUrl = avaturl;
            mImageDraweeView.setImageDraweeView(35, 35, mPhotoUrl);
        }
    }

    private void initPopupWindow() {
        View menuLayout = LayoutInflater.from(mContext).inflate(R.layout.fragment_business_menu, null);
        mPopupWindow = new PopupWindow(menuLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ListView listView = (ListView) menuLayout.findViewById(R.id.listView);
        final MenuAdapter adapter = new MenuAdapter(PreferencesUtils.getHomeOperation());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                HomeOperationBean bean = adapter.getItem(position);
                WebBean webBean = new WebBean(bean.getText(), "业务", "", 0);
                webBean.setUrl(bean.getUrl());
                UIHelper.showWebView(mContext, webBean);
            }
        });
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(mWebBean != null){
            mWebBean.setUrl(url);
            UIHelper.showWebView(mContext, mWebBean);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mWebBean = null;
                }
            }, 200);
        }
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
        mFailView.setVisibility(VISIBLE);
    }

    @Override
    public void setHeadTitle(WebBean webBean) {
        this.mWebBean = webBean;
    }

    @Override
    public void setBottomTitle(String data) {
    }

    @Override
    public void backLogin(String data) {
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mAdapter = null;
        mEtContent = null;
        mPopupWindow = null;
        mWebBean = null;
        mImageDraweeView = null;
        mPhotoUrl = null;
        mWebView = null;
    }
}
