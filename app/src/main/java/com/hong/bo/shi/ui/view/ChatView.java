package com.hong.bo.shi.ui.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.RootView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.presenter.ChatPresenter;
import com.hong.bo.shi.presenter.contract.ChatContract;
import com.hong.bo.shi.ui.adapter.ChatAdapter;
import com.hong.bo.shi.ui.fragments.ChatBottomFragment;
import com.hong.bo.shi.widget.CommonTitle;

import java.util.List;

/**
 * Created by andy on 2016/12/13.
 */

public class ChatView extends RootView<ChatContract.Presenter> implements ChatContract.View,AbsListView.OnScrollListener{

    private ListView mListView;
    private CommonTitle mCommonTitle;
    private ChatAdapter mAdapter;
    private boolean isLoadingMore = true;//是不是可以加载更多

    public ChatView(Context context) {
        super(context);
    }

    public ChatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        mPresenter = presenter;
        mAdapter.setPresenter(mPresenter);
    }

    @Override
    protected void getLayout() {
        inflate(mContext, R.layout.activity_chat_view, this);
    }

    @Override
    protected void initView() {
        mListView = findViewByIds(R.id.listView);
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setLeftView(R.string.ac_chat_left_title);
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        mAdapter = new ChatAdapter();
        mListView.setAdapter(mAdapter);
        mCommonTitle.setOnLeftClickListener((CommonTitle.OnLeftClickListener) mContext);
        mCommonTitle.setOnRightClickListener((CommonTitle.OnRightClickListener) mContext);
        mListView.setOnScrollListener(this);
    }

    @Override
    public ChatBottomFragment initEmotionMainFragment(FragmentManager manager) {
        //创建修改实例
        final ChatBottomFragment fragment = new ChatBottomFragment();
        fragment.bindToContentView(mListView);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return fragment.isInterceptBackPress();
            }
        });
        return fragment;
    }

    @Override
    public void updateData(final List<GroupMessage> list, final boolean isLoadMore) {
        isLoadingMore = list.size() == ChatPresenter.DEFAULT_SIZE;
        if (isLoadMore) {
            mAdapter.addAll(list);
        } else {
            mAdapter.setData(list);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoadMore) {
                    if (list.size() > 0) {
                        mListView.setSelection(list.size() - 1);
                    }
                } else {
                    mListView.setSelection(mAdapter.getCount());
                }
            }
        }, 30);
    }

    @Override
    public void init(GroupInfo info) {
        mCommonTitle.setTitle(info.getGroupName());
        if (info.getGroupAttribute() == 2) {
            mCommonTitle.setRightView(R.mipmap.single);
        } else {
            mCommonTitle.setRightView(R.mipmap.doubles);
        }
    }

    @Override
    public void onSuccess() {
        dismissDialog();
    }

    @Override
    public void onFailed() {
        dismissDialog();
    }

    @Override
    public void onPause() {
        if (mAdapter != null) {
            mAdapter.onPause();
        }
    }

    @Override
    public void setBackTitle(String backTitle) {
        if(!TextUtils.isEmpty(backTitle)) {
            mCommonTitle.setLeftView(backTitle);
        }
    }

    @Override
    public GroupMessage getFirst() {
        if (mAdapter.getCount() == 0) {
            return null;
        }
        return mAdapter.getItem(0);
    }

    @Override
    public void onDestory() {
        super.onDestory();
        mCommonTitle = null;
        mListView = null;
        mAdapter = null;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && !(ViewCompat.canScrollVertically(mListView, -1)) && isLoadingMore) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int start = mAdapter.getCount();
                    mPresenter.loadMore(start);
                }
            }, 300);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
