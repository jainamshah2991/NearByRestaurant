package com.nearbyrestaurant.contracts;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;

import java.util.ArrayList;

/**
 * Created by SHRENIK on 13-05-2017.
 */

public interface MapContract extends BaseContract{

    interface View extends BaseContract.BaseView
    {
        void requestLocationPermission();

        void requestForEnableGPS();

        void displayPinOnMap(ArrayList<Business> businessList);
    }

    interface Presenter extends BaseContract.BasePresenter
    {
        boolean isAppHasLocationPermission();

        boolean isGPSEnabled();

        void getRestaurantDataFromServer(BoundingBoxOptions mapBounds, boolean shouldShowProgressBar);
    }
}
