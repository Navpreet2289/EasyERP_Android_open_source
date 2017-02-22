package com.thinkmobiles.easyerp.presentation.screens.crm.orders;

import com.thinkmobiles.easyerp.data.model.crm.order.ResponseGetOrders;
import com.thinkmobiles.easyerp.presentation.base.BaseModel;
import com.thinkmobiles.easyerp.presentation.base.rules.ErrorType;
import com.thinkmobiles.easyerp.presentation.base.rules.MasterFlowSelectableBasePresenter;
import com.thinkmobiles.easyerp.presentation.base.rules.MasterFlowSelectableBaseView;
import com.thinkmobiles.easyerp.presentation.holders.data.crm.OrderDH;
import com.thinkmobiles.easyerp.presentation.utils.Constants;

import java.util.ArrayList;

import rx.Observable;

/**
 * @author michael.soyma@thinkmobiles.com (Created on 1/31/2017).
 */

public interface OrdersContract {
    interface OrdersView extends MasterFlowSelectableBaseView<OrdersPresenter> {
        void displayOrders(ArrayList<OrderDH> orderDHs, boolean needClear);
        void openOrderDetailsScreen(String orderID);

        void displayErrorState(final ErrorType errorType);
        void displayErrorToast(final String msg);
        void showProgress(Constants.ProgressType type);
    }
    interface OrdersPresenter extends MasterFlowSelectableBasePresenter<String, OrderDH> {
        void refresh();
        void loadNextPage();
    }
    interface OrdersModel extends BaseModel {
        Observable<ResponseGetOrders> getOrders(final int page);
    }
}
