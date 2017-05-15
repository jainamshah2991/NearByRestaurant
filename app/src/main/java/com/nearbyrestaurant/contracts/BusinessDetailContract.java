package com.nearbyrestaurant.contracts;

import com.yelp.clientlib.entities.Business;

/**
 * Created by SHRENIK on 13-05-2017.
 */

public interface BusinessDetailContract extends BaseContract{

    interface View extends BaseView
    {
        void displayBusinessDetailData(Business business);
    }

    interface Presenter extends BasePresenter
    {
        void getBusinessDetailFromServer(String businessId);
    }
}
