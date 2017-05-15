package com.nearbyrestaurant.contracts;

public interface BaseContract {

    interface BaseView {
        void showProgressBar();

        void hideProgressBar();
    }

    interface BasePresenter {
        boolean isInternetAvailable();

        void showToastMessage(String message);
    }
}
