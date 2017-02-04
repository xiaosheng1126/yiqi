package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;
import com.hong.bo.shi.model.bean.DepartmentBean;
import com.hong.bo.shi.model.bean.GroupInfo;

import java.util.List;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardContract {

    public interface Presenter extends BasePresenter{
        boolean personIsSelect(String guid);
        boolean groupIsSelect(String guid);
        void operationPerson(String guid);
        void operationGroup(String guid);
        int getForwardCount();
        void forward();
        void setMsgGuid(String guid);
    }

    public interface View extends BaseView<Presenter>{
        void updateLocal(List<DepartmentBean> userInfos);
        void updateClond(List<DepartmentBean> userInfos);
        void updateGroup(List<GroupInfo> groupInfos);
        void updateGroup();
        void updatePerson();
        void onSuccess();
    }
}
