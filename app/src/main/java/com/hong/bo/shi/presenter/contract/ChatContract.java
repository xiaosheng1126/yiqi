package com.hong.bo.shi.presenter.contract;

import android.support.v4.app.FragmentManager;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.GroupMessage;
import com.hong.bo.shi.ui.fragments.ChatBottomFragment;

import java.util.List;

/**
 * Created by andy on 2016/12/13.
 */

public class ChatContract {

    public interface Presenter extends BasePresenter{
        void query(String index);
        void sendMessage(GroupMessage message);
        void deleteMsg(String guid);
        void getMessage(String guid);
        void realTimeQuery();
        void cleraGroupInfoUnReadCount();
        void loadMore(int start);
    }

    public interface View extends BaseView<Presenter>{
        ChatBottomFragment initEmotionMainFragment(FragmentManager manager);
        void updateData(List<GroupMessage> list, boolean isLoadMore);
        void init(GroupInfo info);
        void onSuccess();
        void onFailed();
        void onPause();
        void setBackTitle(String backTitle);
        GroupMessage getFirst();
    }
}
