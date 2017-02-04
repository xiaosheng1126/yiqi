package com.hong.bo.shi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hong.bo.shi.R;
import com.hong.bo.shi.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 16/10/14.
 */

public class PutaoDialog extends Dialog {

    private Builder builder;

    private PutaoDialog(Builder builder) {
        super(builder.getContext());
        this.builder = builder;
        initView();
    }

    private PutaoDialog(Builder builder, int styleId) {
        super(builder.getContext(), styleId);
        this.builder = builder;
        initView();
        Window mWindow = getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = DensityUtils.getScreenWidth(builder.getContext());
        mWindow.setGravity(Gravity.BOTTOM);
        // 添加动画
        mWindow.setWindowAnimations(R.style.dialogAnim);
        mWindow.setAttributes(lp);
    }

    private void initView() {
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        View view = LayoutInflater.from(builder.getContext()).
                inflate(R.layout.custom_dialog_layout, null);
        //根据items动态创建对话框
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
        setContentView(view);
        int i = 0;
        int size = builder.getItems().size();
        for (Item item : builder.getItems()) {
            layout.addView(createItem(item));
            if (i != size - 1) {
                layout.addView(createLine(true));
            }
            i++;
        }
        if (builder.getCancelItem() != null) {
            layout.addView(createLine(false));
            layout.addView(createItem(builder.getCancelItem()));
        }
    }

    private View createLine(boolean isNormal) {
        Line line = builder.getLine();
        if (line == null) {
            line = new Line();
            line.setLineHeight(DensityUtils.dp2px(builder.getContext(), 2));
            line.setLineHeight2(DensityUtils.dp2px(builder.getContext(), 5));
            line.setColorIdRes(R.color.color_d7c2c2c2);
            line.setColorId2Res(R.color.color_d7c2c2c2);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, isNormal ?
                line.getLineHeight() : line.getLineHeight2());
        View view = new View(builder.getContext());
        view.setLayoutParams(params);
        if (isNormal) {
            if (line.getColorIdRes() != -1) {
                view.setBackgroundResource(line.getColorIdRes());
            } else {
                view.setBackgroundResource(R.color.color_d7c2c2c2);
            }
        } else {
            if (line.getColorIdRes() != -1) {
                view.setBackgroundResource(line.getColorId2Res());
            } else {
                view.setBackgroundResource(R.color.color_d7c2c2c2);
            }
        }
        return view;
    }

    private View createItem(final Item item) {
        int itemWidth = item.getItemWidth() == 0 ? -1 : item.getItemWidth();
        int itemHeight = item.getItemHeight() == 0 ? DensityUtils.dp2px(builder.getContext(), 43) :
                item.getItemHeight();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(itemWidth, itemHeight);
        TextView tv = new TextView(builder.getContext());
        tv.setLayoutParams(params);
        if (item.getTextSize() == -1) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext().getResources().getDimensionPixelSize(R.dimen.text_size_16));
        } else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, builder.getContext().getResources().getDimensionPixelSize(item.getTextSize()));
        }
        if (!TextUtils.isEmpty(item.getText())) {
            tv.setText(item.getText());
        } else if (item.getTextRes() != -1) {
            tv.setText(item.getTextRes());
        } else {
            tv.setText("");
        }
        if (item.getTextColorRes() != -1) {
            tv.setTextColor(builder.getContext().getResources().getColor(item.getTextColorRes()));
        } else {
            tv.setTextColor(Color.BLACK);
        }
        tv.setSingleLine(item.isSingle());
        tv.setEnabled(item.isEnable());
        tv.setGravity(item.getGravity() == 0 ? Gravity.CENTER : item.getGravity());
        if (item.getBackgroupRes() != -1) {
            tv.setBackgroundResource(item.getBackgroupRes());
        } else {
            tv.setBackgroundResource(R.drawable.custom_dialog_item_seletor);
        }
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder.getListener() != null) {
                    boolean dismiss = builder.getListener().onItemClick(item);
                    if (dismiss) {
                        dismiss();
                    }
                }
            }
        });
        return tv;
    }

    public static class Builder {
        private Context context;
        private
        @StyleRes
        int style = -1;
        private List<Item> items;
        private OnDialogItemClickListener listener;
        private Line line;
        private Item cancelItem;

        public Builder(Context context) {
            this.context = context;
            items = new ArrayList<>();
            this.style = R.style.DialogStyle;
        }

        public Line getLine() {
            return line;
        }

        public Item getCancelItem() {
            return cancelItem;
        }

        public Builder setCancelItem(Item cancelItem) {
            this.cancelItem = cancelItem;
            return this;
        }

        public Builder setLine(Line line) {
            this.line = line;
            return this;
        }

        public Context getContext() {
            return context;
        }

        public int getStyle() {
            return style;
        }

        public Builder setStyle(int style) {
            this.style = style;
            return this;
        }

        public List<Item> getItems() {
            return items;
        }

        public Builder setItems(List<Item> items) {
            this.items.clear();
            if (items != null) {
                this.items.addAll(items);
            }
            return this;
        }

        public Builder addItem(Item item) {
            this.items.add(item);
            return this;
        }

        public OnDialogItemClickListener getListener() {
            return listener;
        }

        public Builder setListener(OnDialogItemClickListener listener) {
            this.listener = listener;
            return this;
        }

        public PutaoDialog build() {
            if (context == null) {
                throw new RuntimeException("context is null");
            }
            if (style == -1) {
                return new PutaoDialog(this);
            }
            return new PutaoDialog(this, style);
        }
    }

    public static class Item {
        private
        @IdRes
        int id = -1;
        private String text;
        private
        @StringRes
        int textRes = -1;
        private
        @ColorRes
        int textColorRes = -1;
        private
        @Dimension
        int textSize = -1;
        private int gravity;
        private int itemWidth;
        private int itemHeight;
        private
        @DrawableRes
        int backgroupRes = -1;
        private boolean isEnable = true;
        private boolean isSingle = true;
        private String object;

        public String getObject() {
            return object;
        }

        public Item setObject(String object) {
            this.object = object;
            return this;
        }

        public int getId() {
            return id;
        }

        public Item setId(int id) {
            this.id = id;
            return this;
        }

        public boolean isSingle() {
            return isSingle;
        }

        public Item setSingle(boolean single) {
            isSingle = single;
            return this;
        }

        public String getText() {
            return text;
        }

        public Item setText(String text) {
            this.text = text;
            return this;
        }

        public int getTextRes() {
            return textRes;
        }

        public Item setTextRes(int textRes) {
            this.textRes = textRes;
            return this;
        }

        public int getTextColorRes() {
            return textColorRes;
        }

        public Item setTextColorRes(@ColorRes int textColorRes) {
            this.textColorRes = textColorRes;
            return this;
        }


        public
        @Dimension
        int getTextSize() {
            return textSize;
        }

        public Item setTextSize(@Dimension int textSize) {
            this.textSize = textSize;
            return this;
        }

        public int getGravity() {
            return gravity;
        }

        public Item setGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        public int getItemWidth() {
            return itemWidth;
        }

        public Item setItemWidth(int itemWidth) {
            this.itemWidth = itemWidth;
            return this;
        }

        public int getItemHeight() {
            return itemHeight;
        }

        public Item setItemHeight(int itemHeight) {
            this.itemHeight = itemHeight;
            return this;
        }


        public int getBackgroupRes() {
            return backgroupRes;
        }

        public Item setBackgroupRes(int backgroupRes) {
            this.backgroupRes = backgroupRes;
            return this;
        }

        public boolean isEnable() {
            return isEnable;
        }

        public Item setEnable(boolean enable) {
            isEnable = enable;
            return this;
        }
    }

    public static class Line {
        /**
         * 普通分割线的高度
         */
        private int lineHeight;
        /**
         * 和取消item分割线之间的高度
         */
        private int lineHeight2;
        /**
         * 普通分割线的颜色
         */
        private
        @ColorRes
        int colorIdRes = -1;
        /**
         * 和取消item分割线之间的颜色
         */
        private
        @ColorRes
        int colorId2Res = -1;

        public int getLineHeight() {
            return lineHeight;
        }

        public Line setLineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public int getLineHeight2() {
            return lineHeight2;
        }

        public Line setLineHeight2(int lineHeight2) {
            this.lineHeight2 = lineHeight2;
            return this;
        }

        public int getColorIdRes() {
            return colorIdRes;
        }

        public Line setColorIdRes(int colorIdRes) {
            this.colorIdRes = colorIdRes;
            return this;
        }

        public int getColorId2Res() {
            return colorId2Res;
        }

        public Line setColorId2Res(int colorId2Res) {
            this.colorId2Res = colorId2Res;
            return this;
        }
    }

    public interface OnDialogItemClickListener {
        /**
         * 返回true 关闭对话框
         *
         * @return
         */
        boolean onItemClick(Item item);
    }

    public static void selectPic(Context context, OnDialogItemClickListener listener){
        new PutaoDialog.Builder(context).
                setCancelItem(new PutaoDialog.Item().setTextRes(R.string.putao_cancel)).
                addItem(new PutaoDialog.Item().setTextRes(R.string.putao_camera).setId(R.id.open_camera)).
                addItem(new PutaoDialog.Item().setTextRes(R.string.putao_album).setId(R.id.open_album)).
                setListener(listener).build().show();
    }

    public static void clickMobile(Context context, OnDialogItemClickListener listener){
        new PutaoDialog.Builder(context).
                addItem(new PutaoDialog.Item().setTextRes(R.string.putao_call_mobile).setId(R.id.call_mobile)).
                addItem(new PutaoDialog.Item().setTextRes(R.string.putao_send_message).setId(R.id.send_message)).
                setListener(listener).build().show();
    }

}
