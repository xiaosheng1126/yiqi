package com.hong.bo.shi.emoji;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.hong.bo.shi.model.bean.Emoji;
import com.hong.bo.shi.ui.adapter.EmojiGridAdapter;
import com.hong.bo.shi.ui.fragments.ChatBottomFragment;
import com.hong.bo.shi.utils.EmojiUtils;

/**
 * Created by zejian
 * Time  16/1/8 下午5:05
 * Email shinezejian@163.com
 * Description:点击表情的全局监听管理类
 */
public class GlobalOnItemClickManagerUtils {

    private static GlobalOnItemClickManagerUtils instance;
    private EditText mEditText;//输入框
    private static Context mContext;
    private ChatBottomFragment.OnBottomOperationListener mListener;
    private ChatBottomFragment mChatBottomFragment;

    public static GlobalOnItemClickManagerUtils getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            synchronized (GlobalOnItemClickManagerUtils.class) {
                if (instance == null) {
                    instance = new GlobalOnItemClickManagerUtils();
                }
            }
        }
        return instance;
    }

    public GlobalOnItemClickManagerUtils setListener(ChatBottomFragment.OnBottomOperationListener mListener) {
        this.mListener = mListener;
        return this;
    }

    public GlobalOnItemClickManagerUtils attachToChatBottomFragment(ChatBottomFragment fragment) {
        this.mChatBottomFragment = fragment;
        return this;
    }

    private static final String TAG = "GlobalOnItemClickManagerUtils";

    public void attachToEditText(EditText editText) {
        mEditText = editText;
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object itemAdapter = parent.getAdapter();
                if (itemAdapter instanceof EmojiGridAdapter) {
                    //点击的是表情
                    EmojiGridAdapter emotionGvAdapter = (EmojiGridAdapter) itemAdapter;
                    if (position == emotionGvAdapter.getCount() - 1) {
                        // 如果点击了最后一个回退按钮,则调用删除键事件
                        mEditText.dispatchKeyEvent(new KeyEvent(
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                    } else {
                        Emoji item = emotionGvAdapter.getItem(position);
                        // 获取当前光标位置,在指定位置上添加表情图片文本
                        int curPosition = mEditText.getSelectionStart();
                        StringBuilder sb = new StringBuilder(mEditText.getText().toString() + " ");
                        sb.insert(curPosition, item.text);
                        // 获取当前光标位置,在指定位置上添加表情图片文本
                        int size = (int) mEditText.getTextSize() * 13 / 10;
                        mEditText.setText(EmojiUtils.getEmotionContent(mContext, size, sb.toString()));
                        //将光标设置到新增完表情的右侧
                        mEditText.setSelection(curPosition + item.text.length());
                    }
                }else if(itemAdapter instanceof OtherBeanAdapter){
                    if(mListener != null){
                        if(position == 0){
                            mListener.openCamera();
                        }
                        if(position == 1){
                            mListener.openAlbum();
                        }
                        if(position == 2){
                            mListener.openVideo();
                        }
                        if(position == 3){
                            mListener.openLocation();
                        }
                        if(mChatBottomFragment != null){
                            mChatBottomFragment.isInterceptBackPress();
                        }
                    }
                }
            }
        };
    }

}
