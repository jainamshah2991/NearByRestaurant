package com.nearbyrestaurant.dagger.component;


import com.nearbyrestaurant.activities.BaseActivity;
import com.nearbyrestaurant.dagger.module.APIModule;
import com.nearbyrestaurant.presenterimpl.BasePresenterImpl;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {APIModule.class})
public interface AppComponent
{
    void inject(BaseActivity baseActivity);
    void inject(BasePresenterImpl basePresenter);
}
