package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.GroupInfo;
import com.hong.bo.shi.model.bean.UserInfo;
import com.hong.bo.shi.model.bean.WebBean;

import java.util.List;

/**
 * Created by andy on 2016/12/14.
 */

public class GroupDetailsContract {

    public interface Presenter extends BasePresenter{
        void query(String index);
        void exit();
        void getGroupMembers(String groupGuid);
        void getAllMessage();
        void getData_1030();
    }

    public interface View extends BaseView<Presenter>{
        void init(GroupInfo info);
        void update(List<UserInfo> list);
        void onSucess();
        void toWebView(WebBean bean);
    }
}
