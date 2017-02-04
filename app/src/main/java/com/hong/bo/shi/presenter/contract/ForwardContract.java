package com.hong.bo.shi.presenter.contract;

import com.hong.bo.shi.base.BasePresenter;
import com.hong.bo.shi.base.BaseView;

/**
 * Created by andy on 2017/1/21.
 */

public class ForwardContract {

    public interface Presenter extends BasePresenter{

    }

    public interface View extends BaseView<Presenter>{

    }
}
