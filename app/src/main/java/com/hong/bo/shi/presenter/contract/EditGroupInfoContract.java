package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2017/1/22.
 */

public class EditGroupInfoContract {

    public interface Presenter extends BasePresenter{
        void editGroupName(String guid, String groupName);
        void editGroupPublic(String guid, String publics);
    }

    public interface View extends BaseView<Presenter>{
        public void setType(int type, String context, String guid);
        public String getGroupName();
        public String getGroupPublic();
        public void onSuccess();
    }
}

