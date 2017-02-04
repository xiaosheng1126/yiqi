package com.hong.bo.shi.ui.adapter;

import android.view.View;

import com.amap.api.services.core.PoiItem;
import com.hong.bo.shi.R;
import com.hong.bo.shi.base.BaseViewHolderHelper;
import com.hong.bo.shi.base.RecyclerViewBaseAdapter;

/**
 * Created by andy on 2016/12/23.
 */

public class LocationAdapter extends RecyclerViewBaseAdapter<PoiItem> {

    private int mPosition;
    private OnSelectListener mListener;

    public void setOnSelectListener(OnSelectListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int layoutResId(int viewType) {
        return R.layout.activity_location_item;
    }

    @Override
    public void onBindViewHolder(BaseViewHolderHelper holder, final int position) {
        final PoiItem item = getItem(position);
        holder.setTextView(R.id.tvAddress, item.getTitle());
        holder.setTextView(R.id.tvDetailsAddress, item.getCityName() + item.getAdName() + item.getSnippet());
        if(mPosition == position){
            holder.setVisibility(R.id.ivSelect, true);
        }else{
            holder.setVisibility(R.id.ivSelect, false);
        }
        holder.setItemViewOnClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPosition != position){
                    mPosition = position;
                    if(mListener != null){
                        mListener.onChanged(item);
                    }
                    notifyDataSetChanged();
                }
            }
        });
    }

    public interface OnSelectListener{
        void onChanged(PoiItem item);
    }

    public PoiItem getSelectData(){
        return getItem(mPosition);
    }
}
