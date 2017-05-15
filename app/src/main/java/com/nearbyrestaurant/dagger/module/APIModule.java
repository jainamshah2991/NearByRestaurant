package com.nearbyrestaurant.dagger.module;


import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class APIModule
{

    private String yelpConsumerKey, yelpConsumerSecret, yelpToken, yelpTokenSecret;

    public APIModule(String yelpConsumerKey, String yelpConsumerSecret, String yelpToken, String yelpTokenSecret) {
        this.yelpConsumerKey = yelpConsumerKey;
        this.yelpConsumerSecret = yelpConsumerSecret;
        this.yelpToken = yelpToken;
        this.yelpTokenSecret = yelpTokenSecret;
    }

    @Provides
    @Singleton
    public YelpAPI providesYelpApi() {
        YelpAPIFactory apiFactory = new YelpAPIFactory(yelpConsumerKey, yelpConsumerSecret, yelpToken, yelpTokenSecret);
        YelpAPI yelpAPI = apiFactory.createAPI();

        return yelpAPI;
    }
}
