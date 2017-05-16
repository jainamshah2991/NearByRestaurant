package com.nearbyrestaurant.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nearbyrestaurant.R;
import com.nearbyrestaurant.contracts.MapContract;
import com.nearbyrestaurant.presenterimpl.MapPresenter;
import com.nearbyrestaurant.utils.AppConstant;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapActivity extends BaseActivity implements MapContract.View, OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnInfoWindowCloseListener {

    private final int ACCESS_LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private final int TURN_ON_GPS_ACTIVITY_REQUEST_CODE = 2001;

    @BindView(R.id.tv_app_toolbar_title)
    TextView tvAppToolbarTitle;
    @BindView(R.id.tb_map_screen)
    Toolbar tbMapScreen;

    private MapPresenter mapPresenter;

    private GoogleMap googleMap;
    private LocationManager locationManager;
    private Location currentLocation;

    public boolean isScreenLoadedFirstTime = true;

    /*  This variable is used to identify if any info window is open or not?
    *   On click of marker the map will auto center and bounds will be change
    *   on change of bound the app will fetch new data
    *   to prevent this "shouldRefreshData" variable is used
    * */
    public boolean shouldRefreshData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mapPresenter = new MapPresenter(this, this);

        setSupportActionBar(tbMapScreen);

        tvAppToolbarTitle.setText(getResources().getString(R.string.scree_title_map));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);

        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void getCurrentLocation() {

        /*  Check if app has location permission or not?    */
        if (mapPresenter.isAppHasLocationPermission()) {

            /*  Check if Gps of device is on or off?    */
            if (mapPresenter.isGPSEnabled()) {

                /*  Get last know location of device    */
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (currentLocation != null) {

                    googleMap.setMyLocationEnabled(true);
                   /*   Zoom map to user's current location */
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 7);
                    googleMap.animateCamera(cameraUpdate);

                    /*  Call Yelp api to get list of restaurants    */
                    mapPresenter.getRestaurantDataFromServer(getMapBounds(), isScreenLoadedFirstTime);
                }

            } else
                requestForEnableGPS();
        } else
            requestLocationPermission();
    }

    /*********************************************************************************
     *   This function is used to get bounds of displayed map
     *   This bounds used in Yelp API to get list of restaurant in displayed map area
     * **/
    private BoundingBoxOptions getMapBounds() {

        if (googleMap != null) {
            LatLngBounds latLngBounds = googleMap.getProjection().getVisibleRegion().latLngBounds;

            BoundingBoxOptions bounds = BoundingBoxOptions.builder()
                    .swLatitude(latLngBounds.southwest.latitude)
                    .swLongitude(latLngBounds.southwest.longitude)
                    .neLatitude(latLngBounds.northeast.latitude)
                    .neLongitude(latLngBounds.northeast.longitude).build();

            return bounds;
        }

        return null;
    }

    @Override
    public void displayPinOnMap(ArrayList<Business> businessList) {

        /*  Check if google map is not null */
        if (googleMap != null) {

            /*  Clear all recent pins so that we can add new ones   */
            googleMap.clear();

            for (Business business : businessList) {

                if (business.location() != null && business.location().coordinate() != null) {
                    double latitude = business.location().coordinate().latitude();
                    double longitude = business.location().coordinate().longitude();

                    /*  Add marker to the business's location   */
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longitude))).setTag(business);
                }
            }
        }
    }

    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private View mapInfoWindowView;

        private TextView tvBusinessName, tvBusinessAddress;
        private ImageView ivBusinessPic;

        private AppCompatRatingBar rbBusinessRatings;

        public CustomInfoWindowAdapter() {
            mapInfoWindowView = getLayoutInflater().inflate(R.layout.view_map_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            tvBusinessName = (TextView) mapInfoWindowView.findViewById(R.id.tv_business_name);
            tvBusinessAddress = (TextView) mapInfoWindowView.findViewById(R.id.tv_business_address);

            ivBusinessPic = (ImageView) mapInfoWindowView.findViewById(R.id.iv_business_pic);

            rbBusinessRatings = (AppCompatRatingBar) mapInfoWindowView.findViewById(R.id.rb_business_ratings);

            if (marker.getTag() instanceof Business) {
                Business business = (Business) marker.getTag();

                if (business != null) {
                    /*  Set Business Name   */
                    if (!TextUtils.isEmpty(business.name()))
                        tvBusinessName.setText(business.name());
                    else
                        tvBusinessName.setText(getResources().getString(R.string.not_available_label));

                    /*  Set Business Address    */
                    if (business.location() != null && business.location().displayAddress() != null)
                        tvBusinessAddress.setText(TextUtils.join(", ", business.location().displayAddress()));
                    else
                        tvBusinessAddress.setText(getResources().getString(R.string.not_available_label));

                    /*  Set image   */
                    if (!TextUtils.isEmpty(business.imageUrl()))
                        Picasso.with(MapActivity.this).load(business.imageUrl()).placeholder(R.drawable.ic_place_holder)
                                .into(ivBusinessPic);
                    else
                        ivBusinessPic.setImageResource(R.drawable.ic_place_holder);

                    /*  Set ratings */
                    if (business.rating() != null)
                        rbBusinessRatings.setRating((float) (business.rating().doubleValue()));
                    else
                        rbBusinessRatings.setRating(0);
                }
            }

            return mapInfoWindowView;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            return null;
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        /*  Hide existing displayed info window */
        marker.hideInfoWindow();

        /*  Check if tag is not null and the tag is of type Business    */
        if (marker.getTag() != null && marker.getTag() instanceof Business) {
            /*  Get business from marker's tag  */
            Business business = (Business) marker.getTag();

           /*   Start Business detail screen and pass business id in intent */
            Intent intent = new Intent(MapActivity.this, BusinessDetailActivity.class);
            intent.putExtra(AppConstant.INTENT_KEY_BUSINESS_ID, business.id());
            startActivity(intent);
        }
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        shouldRefreshData = true;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        shouldRefreshData = false;
        return false;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setOnMarkerClickListener(this);
        googleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setOnInfoWindowCloseListener(this);

        /*  Set minimum zoom level to 7
        *   In Yelp API, if the distance between south-west latitude-longitude and north-east latitude-longitude
        *   is bigger than it will throw an error
        *   To prevent this we have fixed the minimum zoom level of google map  */
        googleMap.setMinZoomPreference(7);

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if (googleMap != null) {
                    if (shouldRefreshData) {
                        isScreenLoadedFirstTime = false;
                        mapPresenter.getRestaurantDataFromServer(getMapBounds(), isScreenLoadedFirstTime);
                    }
                }
            }
        });

        getCurrentLocation();
    }

    @Override
    public void requestLocationPermission() {
        ActivityCompat.requestPermissions(MapActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                ACCESS_LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void requestForEnableGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message_request_gps)
                .setPositiveButton(R.string.dialog_button_yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), TURN_ON_GPS_ACTIVITY_REQUEST_CODE);
                    }
                })
                .setNegativeButton(R.string.dialog_button_no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_LOCATION_PERMISSION_REQUEST_CODE) {
            boolean isPermissionGranted = true;
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        isPermissionGranted = false;
                        break;
                    }
                }
                if (isPermissionGranted) {
                    getCurrentLocation();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TURN_ON_GPS_ACTIVITY_REQUEST_CODE) {
            if (mapPresenter.isGPSEnabled()) {
                getCurrentLocation();
            }
        }
    }

}
