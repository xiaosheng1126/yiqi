package com.hong.bo.shi.base;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangds on 2016/3/19.
 */
public abstract class RecyclerViewBaseAdapter<T extends Object> extends RecyclerView.Adapter<BaseViewHolderHelper> {

    protected List<T> mData = new ArrayList<T>();
    protected Context mContext;
    protected AdapterView.OnItemClickListener mOnItemClickListener;
    protected BaseActivity mActivity;

    public RecyclerViewBaseAdapter(List<T> data) {
        if (data != null) {
            mData.addAll(data);
        }
    }

    public RecyclerViewBaseAdapter() {
        this(null);
    }

    /**
     * 设置BaseActivity对象
     *
     * @param activity
     */
    public void setActivity(BaseActivity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置Item点击事件监听器
     *
     * @param itemClickListener
     */
    public void setOnItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.mOnItemClickListener = itemClickListener;
    }

    /**
     * 布局ID
     *
     * @param viewType
     * @return
     */
    @LayoutRes
    public abstract int layoutResId(int viewType);

    //=============覆盖的方法==============

    @Override
    public final BaseViewHolderHelper onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        BaseViewHolderHelper helper = new BaseViewHolderHelper(
                LayoutInflater.from(mContext).inflate(layoutResId(viewType), parent, false));
        helper.setViewType(viewType);
        return helper;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    //======================== 操作数据的基本方法 ==========================

    /**
     * 刷新数据
     *
     * @param list
     */
    public void setData(List<T> list) {
        if (mData.size() > 0) {
            mData.clear();
        }
        if (list != null) {
            mData.addAll(list);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取指定项对象
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return mData.get(position);
    }

    /**
     * 获取适配器数据
     *
     * @return
     */
    public List<T> getData() {
        return mData;
    }

    /**
     * 适配器数据是否包含某一项
     *
     * @param t
     * @return
     */
    public boolean isContains(@NonNull T t) {
        if (t != null) {
            return mData.contains(t);
        } else {
            return false;
        }
    }

    /**
     * 这个对象在适配器第一个索引值
     *
     * @param t
     * @return
     */
    public int indexOf(@NonNull T t) {
        if (t == null) {
            return -1;
        }
        return mData.indexOf(t);
    }

    /**
     * 添加集合到指定位置
     *
     * @param list
     */
    public void addAll(int position, @NonNull List<T> list) {
        if (position < 0 || position > getItemCount() || list == null) {
            return;
        }
        mData.addAll(position, list);
        notifyDataSetChanged();
    }

    /**
     * 添加一个集合
     *
     * @param list
     */
    public void addAll(@NonNull List<T> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    /**
     * 添加一项到指定位置
     *
     * @param position
     * @param t
     */
    public void add(int position, @NonNull T t) {
        if (position < 0 || position > getItemCount() || t == null) {
            return;
        }
        mData.add(position, t);
        notifyDataSetChanged();
    }

    /**
     * 添加一项
     *
     * @param t
     */
    public void add(@NonNull T t) {
        if (t != null) {
            mData.add(t);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除指定的一项
     *
     * @param position
     */
    public void remove(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        mData.remove(position);
    }

    /**
     * 移除一项
     *
     * @param t
     */
    public void remove(@NonNull T t) {
        if (t != null) {
            mData.remove(t);
            notifyDataSetChanged();
        }
    }

    //=============== 辅助方法 =========================

    public String getString(@StringRes int resId) {
        return mContext.getResources().getString(resId);
    }

    public String getString(@StringRes int resId, Object... objects) {
        return mContext.getResources().getString(resId, objects);
    }

    public Spanned getHtmlStr(@StringRes int redId, Object... objects) {
        return Html.fromHtml(getString(redId, objects));
    }

    public int getColor(@ColorRes int resId) {
        return mContext.getResources().getColor(resId);
    }

    /**
     * Toast 提示
     *
     * @param resId
     */
    public void toast(@StringRes int resId) {
        Toast.makeText(mContext.getApplicationContext(), resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Toast 提示
     *
     * @param text
     */
    public void toast(@NonNull String text) {
        Toast.makeText(mContext.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 销毁适配器数据
     */
    @CallSuper
    public void onDestory() {
        mActivity = null;
        mContext = null;
        if(mData != null) {
            mData.clear();
        }
        mData = null;
        mOnItemClickListener = null;
    }

    protected void onItemClick(BaseViewHolderHelper holder, int position){
    }

    public boolean onLongClicked(BaseViewHolderHelper holder, int position){
        return false;
    }

}
