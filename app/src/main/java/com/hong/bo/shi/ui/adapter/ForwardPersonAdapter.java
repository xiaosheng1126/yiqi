package com.hong.bo.shi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.presenter.contract.ForwardContract;
import com.hong.bo.shi.widget.fresco.ImageDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 2017/2/4.
 */

public class ForwardPersonAdapter extends BaseExpandableListAdapter {

    private List<DepartmentBean> mList;
    private ForwardContract.Presenter mPresenter;
    private Context mContext;

    public void setPresenter(ForwardContract.Presenter mPresenter) {
        this.mPresenter = mPresenter;
    }

    public ForwardPersonAdapter() {
        this.mList = new ArrayList<>();
    }

    public void setData(List<DepartmentBean> list) {
        mList.clear();
        if (list != null) {
            this.mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mList.get(groupPosition).getList().size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mList.get(groupPosition).getDepartment();
    }

    @Override
    public UserInfo getChild(int groupPosition, int childPosition) {
        return mList.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (mContext == null) {
            mContext = parent.getContext();
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_contract_item_group, null);
            holder = new GroupViewHolder(convertView);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.tvName.setText(getGroup(groupPosition));
        if (!isExpanded) {
            holder.ivArrow.setPivotX(holder.ivArrow.getWidth() / 2);
            holder.ivArrow.setPivotY(holder.ivArrow.getHeight() / 2);//支点在图片中心
            holder.ivArrow.setRotation(90);
        } else {
            holder.ivArrow.setPivotX(holder.ivArrow.getWidth() / 2);
            holder.ivArrow.setPivotY(holder.ivArrow.getHeight() / 2);//支点在图片中心
            holder.ivArrow.setRotation(-90);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contract_item_child, null);
            holder = new ChildViewHolder(convertView);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        final UserInfo child = getChild(groupPosition, childPosition);
        holder.tvName.setText(child.getName());
        holder.ivPhoto.setImageDraweeView(50, 50, child.getAvaturl());
        holder.ivSelect.setVisibility(View.VISIBLE);
        if(mPresenter != null) {
            holder.ivSelect.setImageResource(mPresenter.personIsSelect(child.getGuid())
                    ? R.mipmap.radio_20_sel : R.mipmap.radio_20_nor);
        }
        int rating = child.getGrade();
        holder.imageView1.setVisibility(View.GONE);
        holder.imageView2.setVisibility(View.GONE);
        holder.imageView3.setVisibility(View.GONE);
        holder.imageView4.setVisibility(View.GONE);
        holder.imageView5.setVisibility(View.GONE);
        switch (rating) {
            case 5:
                holder.imageView5.setVisibility(View.VISIBLE);
            case 4:
                holder.imageView4.setVisibility(View.VISIBLE);
            case 3:
                holder.imageView3.setVisibility(View.VISIBLE);
            case 2:
                holder.imageView2.setVisibility(View.VISIBLE);
            case 1:
                holder.imageView1.setVisibility(View.VISIBLE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPresenter != null) {
                    mPresenter.operationPerson(child.getGuid());
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    static class GroupViewHolder {
        private TextView tvName;
        private ImageView ivArrow;

        public GroupViewHolder(View item) {
            this.ivArrow = (ImageView) item.findViewById(R.id.ivArrow);
            this.tvName = (TextView) item.findViewById(R.id.tvDepartment);
            item.setTag(this);
        }
    }

    static class ChildViewHolder {
        ImageDraweeView ivPhoto;
        TextView tvName;
        ImageView ivSelect;
        ImageView imageView1, imageView2, imageView3, imageView4, imageView5;

        public ChildViewHolder(View item) {
            this.tvName = (TextView) item.findViewById(R.id.tvName);
            ivPhoto = (ImageDraweeView) item.findViewById(R.id.ivPhoto);
            ivSelect = (ImageView) item.findViewById(R.id.ivSelect);
            imageView1 = (ImageView) item.findViewById(R.id.iv_start1);
            imageView2 = (ImageView) item.findViewById(R.id.iv_start2);
            imageView3 = (ImageView) item.findViewById(R.id.iv_start3);
            imageView4 = (ImageView) item.findViewById(R.id.iv_start4);
            imageView5 = (ImageView) item.findViewById(R.id.iv_start5);
            item.setTag(this);
        }
    }
}
