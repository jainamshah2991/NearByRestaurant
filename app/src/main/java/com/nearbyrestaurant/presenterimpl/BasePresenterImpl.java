package com.nearbyrestaurant.presenterimpl;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.nearbyrestaurant.NearByRestaurantApplication;
import com.nearbyrestaurant.contracts.BaseContract;
import com.yelp.clientlib.connection.YelpAPI;

import javax.inject.Inject;


public class BasePresenterImpl implements BaseContract.BasePresenter {

    @Inject
    public YelpAPI yelpAPI;

    public BaseContract.BaseView baseView;
    public Context mContext;

    public BasePresenterImpl(BaseContract.BaseView baseView, Context mContext) {
        this.baseView = baseView;
        this.mContext = mContext;

        NearByRestaurantApplication.nearByRestaurantApplication.getAppComponent().inject(this);
    }

    @Override
    public boolean isInternetAvailable() {
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void showToastMessage(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
