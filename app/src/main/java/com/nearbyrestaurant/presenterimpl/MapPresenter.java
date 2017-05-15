package com.nearbyrestaurant.presenterimpl;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.nearbyrestaurant.R;
import com.nearbyrestaurant.contracts.MapContract;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by SHRENIK on 13-05-2017.
 */

public class MapPresenter extends BasePresenterImpl implements MapContract.Presenter {

    private MapContract.View mapView;

    Call<SearchResponse> call;

    public MapPresenter(MapContract.View view, Context mContext) {
        super(view, mContext);
        this.mapView = view;
    }

    @Override
    public boolean isAppHasLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean isGPSEnabled() {
        LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void getRestaurantDataFromServer(BoundingBoxOptions mapBounds, boolean shouldShowProgressBar) {

        if (!isInternetAvailable()) {
            showToastMessage(mContext.getString(R.string.error_internet_unavailable));
            return;
        }

        if (call != null)
            call.cancel();

        if (shouldShowProgressBar)
            baseView.showProgressBar();

        Map<String, String> params = new HashMap<>();

        params.put("term", "food");

        call = yelpAPI.search(mapBounds, params);
        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                baseView.hideProgressBar();
                if (response.body() != null) {
                    SearchResponse searchResponse = response.body();

                    ArrayList<Business> businessList = searchResponse.businesses();

                    mapView.displayPinOnMap(businessList);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                baseView.hideProgressBar();
            }
        });
    }
}
