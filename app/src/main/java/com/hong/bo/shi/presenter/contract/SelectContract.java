package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by andy on 2016/12/16.
 */

public class SelectContract  {

    public interface Presenter extends BasePresenter{
        void operation(String guid);
        boolean isSelect(String guid);
        void setExitUsres(String groupGuid);
        int selectCount();
        void operation();
        void createGroup();
        void inviteGroupMembers();
        void setSelect(String guids);
        String getSelectGuids();
        String getGroupGuid();
        Map<String, String> getSelectUsers();
    }

    public interface View extends BaseView<Presenter>{
        void update();
        void updateLocal(List<DepartmentBean> list);
        void updateClond(List<DepartmentBean> list);
        void onSuccess(GroupInfo info);
        void onFailed();
        void setBackView(String text);
    }
}
