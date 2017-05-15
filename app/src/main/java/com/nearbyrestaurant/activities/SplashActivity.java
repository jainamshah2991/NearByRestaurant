package com.nearbyrestaurant.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.android.gms.maps.MapView;
import com.nearbyrestaurant.R;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_splash);

        startSplashTask();
    }

    private void startSplashTask() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadDummyMap();

                Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }

    /**
     * Load Dummy Map - This method dummy map so that it will not take long time into the application
     * This method will take time only once
     */
    public void loadDummyMap() {
        try {
            MapView mv = new MapView(getApplicationContext());
            mv.onCreate(null);
            mv.onPause();
            mv.onDestroy();
        } catch (Exception ignored) {

        }
    }
}
