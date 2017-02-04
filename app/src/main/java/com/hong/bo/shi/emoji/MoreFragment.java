package com.hong.bo.shi.emoji;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.hong.bo.shi.R;
import com.hong.bo.shi.model.bean.OtherBean;
import com.hong.bo.shi.ui.adapter.EmotionPagerAdapter;
import com.hong.bo.shi.utils.DensityUtils;
import com.hong.bo.shi.widget.EmojiIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zejian
 * Time  16/1/5 下午4:32
 * Email shinezejian@163.com
 * Description:可替换的模板表情，gridview实现
 */
public class MoreFragment extends Fragment {

    private EmotionPagerAdapter mPagerGvAdapter;
    private ViewPager mViewPager;
    private EmojiIndicatorView mIndicator;//更多对应的点
    /**
     * 创建与Fragment对象关联的View视图时调用
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complate_emotion, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    /**
     * 初始化view控件
     */
    protected void initView(View rootView){
        mViewPager = (ViewPager) rootView.findViewById(R.id.vp_complate_emotion_layout);
        mIndicator = (EmojiIndicatorView) rootView.findViewById(R.id.ll_point_group);
        mIndicator.setVisibility(View.INVISIBLE);
        init();
    }

    /**
     * 初始化监听器
     */
    protected void initListener(){
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int oldPagerPos=0;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mIndicator.playByStartPointToNext(oldPagerPos, position);
                oldPagerPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init() {
        // 获取屏幕宽度
        int screenWidth = DensityUtils.getScreenWidth(getActivity());
        // item的间距
        int spacing = DensityUtils.dp2px(getActivity(), 16);
        // 动态计算item的宽度和高度
        int itemWidth = (screenWidth - spacing * 5) / 4;
        //动态计算gridview的总高度
        int gvHeight = itemWidth * 2 + spacing * 4;
        List<GridView> gridViews = new ArrayList<>();
        List<OtherBean> otherBeens = new ArrayList();
        otherBeens.add(new OtherBean("相机","camera", R.mipmap.camera_g));
        otherBeens.add(new OtherBean("相册","album", R.mipmap.picture_g));
        otherBeens.add(new OtherBean("视频","video", R.mipmap.video_g));
        otherBeens.add(new OtherBean("定位","location", R.mipmap.location_g));
        GridView gv = createEmotionGridView(otherBeens, screenWidth, spacing, itemWidth, gvHeight);
        gridViews.add(gv);
        //初始化指示器
        mIndicator.initIndicator(gridViews.size());
        // 将多个GridView添加显示到ViewPager中
        mPagerGvAdapter = new EmotionPagerAdapter(gridViews);
        mViewPager.setAdapter(mPagerGvAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        mViewPager.setLayoutParams(params);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<OtherBean> list, int gvWidth, int padding, int itemWidth, int gvHeight) {
        // 创建GridView
        GridView gv = new GridView(getActivity());
        //设置点击背景透明
        gv.setSelector(android.R.color.transparent);
        //设置列
        gv.setNumColumns(4);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding * 2);
        //设置GridView的宽高
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);
        // 给GridView设置表情图片
        OtherBeanAdapter adapter = new OtherBeanAdapter(list, itemWidth);
        gv.setAdapter(adapter);
//        //设置全局点击事件
        gv.setOnItemClickListener(GlobalOnItemClickManagerUtils.getInstance(getActivity()).getOnItemClickListener());
        return gv;
    }



}
