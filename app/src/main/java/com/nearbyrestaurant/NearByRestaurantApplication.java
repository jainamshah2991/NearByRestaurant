package com.nearbyrestaurant;

import android.app.Application;

import com.nearbyrestaurant.dagger.component.AppComponent;
import com.nearbyrestaurant.dagger.component.DaggerAppComponent;
import com.nearbyrestaurant.dagger.module.APIModule;

public class NearByRestaurantApplication extends Application {

    public static NearByRestaurantApplication nearByRestaurantApplication;
    public AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        nearByRestaurantApplication = this;

        initApplicationComponent();
    }

    /**
     * This function is used to initialize app component
     **/
    private void initApplicationComponent() {
        appComponent = DaggerAppComponent.builder()
                .aPIModule(new APIModule(getResources().getString(R.string.yelp_consumer_key),
                        getResources().getString(R.string.yelp_consumer_secret),
                        getResources().getString(R.string.yelp_token),
                        getResources().getString(R.string.yelp_token_secret)))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
