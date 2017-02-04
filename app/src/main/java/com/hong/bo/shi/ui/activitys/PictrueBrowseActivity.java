package com.hong.bo.shi.ui.activitys;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseActivity;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.model.db.RealmHelper;
import com.hong.bo.shi.ui.fragments.PictrueBrowseFragment;
import com.hong.bo.shi.widget.CommonTitle;
import com.hong.bo.shi.widget.ViewPagerFixed;

import java.util.List;

/**
 * 图片浏览
 * Created by guchenkai on 2015/12/24.
 */
public class PictrueBrowseActivity extends BaseActivity {

    private ViewPagerFixed mViewPager;
    private int mCurrentIndex;
    private MyFragmentPagerAdapter mAdapter;
    private GroupMessage mGroupMessage;
    private int mCount;
    private List<GroupMessage> mData;
    private CommonTitle mCommonTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null) {
            mGroupMessage = getIntent().getParcelableExtra(GroupMessage.class.getSimpleName());
        }else{
            mGroupMessage = savedInstanceState.getParcelable(GroupMessage.class.getSimpleName());
        }
        initView();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GroupMessage.class.getSimpleName(), mGroupMessage);
    }

    private void initView() {
        mCommonTitle = findViewByIds(R.id.commonTitle);
        mCommonTitle.setLeftView("会话");
        mCommonTitle.setOnLeftClickListener(this);
        mViewPager = findViewByIds(R.id.viewPager);
        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        addOnPagerChangeListener();
        setPageTransformer();
        initComplete();
    }


    private void initComplete() {
        mData = RealmHelper.getChatPics(mGroupMessage.getGroupGuid());
        mCount = mData.size();
        mCurrentIndex = mData.indexOf(mGroupMessage);
        mCommonTitle.setTitle((mCurrentIndex + 1)+ "/" + mCount);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mCurrentIndex);
    }

    private void setPageTransformer() {
        mViewPager.setPageTransformer(true, new ViewPager.PageTransformer() {
                    private static final float MIN_SCALE = 0.85f;
                    private static final float MIN_ALPHA = 0.5f;
                    @Override
                    public void transformPage(View page, float position) {
                        int pageWidth = page.getWidth();
                        int pageHeight = page.getHeight();
                        if (position < -1) {
                            page.setAlpha(0);
                        } else if (position <= 1) {
                            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                            if (position < 0) {
                                page.setTranslationX(horzMargin - vertMargin / 2);
                            } else {
                                page.setTranslationX(-horzMargin + vertMargin / 2);
                            }
                            page.setScaleX(scaleFactor);
                            page.setScaleY(scaleFactor);
                            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
                                    / (1 - MIN_SCALE) * (1 - MIN_ALPHA));
                        } else {
                            page.setAlpha(0);
                        }
                    }
                }
        );
    }

    private void addOnPagerChangeListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCurrentIndex = position;
                mCommonTitle.setTitle((mCurrentIndex + 1)+ "/" + mCount);
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            GroupMessage info = mData.get(position);
            Bundle bundle = new Bundle();
            bundle.putParcelable(GroupMessage.class.getSimpleName(), info);
            return Fragment.instantiate(getApplicationContext(),
                    PictrueBrowseFragment.class.getName(), bundle);
        }

        @Override
        public int getCount() {
            return mData == null ? 0 : mData.size();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_picture_browse;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager = null;
        mGroupMessage = null;
        if(mAdapter != null){
            mAdapter = null;
        }
        mData= null;
    }
}
