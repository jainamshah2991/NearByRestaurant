package com.nearbyrestaurant.presenterimpl;

import android.content.Context;

import com.nearbyrestaurant.R;
import com.nearbyrestaurant.contracts.BusinessDetailContract;
import com.yelp.clientlib.entities.Business;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SHRENIK on 13-05-2017.
 */

public class BusinessDetailPresenter extends BasePresenterImpl implements BusinessDetailContract.Presenter {

    private BusinessDetailContract.View businessDetailView;

    public BusinessDetailPresenter(BusinessDetailContract.View view, Context mContext) {
        super(view, mContext);
        this.businessDetailView = view;
    }

    @Override
    public void getBusinessDetailFromServer(String businessId) {

        if (!isInternetAvailable()) {
            showToastMessage(mContext.getString(R.string.error_internet_unavailable));
            return;
        }

        baseView.showProgressBar();

        Call<Business> call = yelpAPI.getBusiness(businessId);
        call.enqueue(new Callback<Business>() {
            @Override
            public void onResponse(Call<Business> call, Response<Business> response) {
                baseView.hideProgressBar();
                if (response.body() != null) {
                    Business business = response.body();

                    businessDetailView.displayBusinessDetailData(business);
                }
            }

            @Override
            public void onFailure(Call<Business> call, Throwable t) {
                baseView.hideProgressBar();
            }
        });
    }
}
