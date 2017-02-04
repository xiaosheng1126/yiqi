package com.hong.bo.shi.dialog;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.utils.DensityUtils;
/**
 * Created by Administrator on 2016/9/20.
 */
public class CenterDialog extends BaseDialog {

    public CenterDialog(Context context, @MenuRes int menuRes, final PopupMenu.OnMenuItemClickListener listener) {
        super(context);
        ListView listView = new ListView(context);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        listView.setSelector(R.drawable.center_dialog_item_bg);
        listView.setBackgroundResource(R.drawable.center_dialog_bg);
        setContentView(listView, lp);
        MenuBuilder menu = new MenuBuilder(context);
        new SupportMenuInflater(context).inflate(menuRes, menu);
        final MenuItem[] menuItems = new MenuItem[menu.size()];
        for (int i = 0; i < menuItems.length; i++) {
            menuItems[i] = menu.getItem(i);
        }
        listView.setAdapter(new MyAdapter(menuItems));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dismiss();
                if(listener != null){
                    listener.onMenuItemClick(menuItems[i]);
                }
            }
        });
        setDialogLocation(DensityUtils.getScreenWidth(context) - DensityUtils.dp2px(context, 80),
                Gravity.CENTER);
    }

    static class MyAdapter extends BaseAdapter{
        private MenuItem[] mData;
        public MyAdapter(MenuItem[] mData) {
            this.mData = mData;
        }
        @Override
        public int getCount() {
            return mData == null ? 0 : mData.length;
        }
        @Override
        public MenuItem getItem(int i) {
            return mData[i];
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.center_layout_item, null);
            TextView textView = (TextView) view.findViewById(R.id.tvText);
            textView.setText(getItem(i).getTitle());
            if(i == (getCount() - 1)){
                view.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            }else{
                view.findViewById(R.id.divider).setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

}
