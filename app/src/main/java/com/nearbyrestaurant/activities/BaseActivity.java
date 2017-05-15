package com.nearbyrestaurant.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.nearbyrestaurant.R;
import com.nearbyrestaurant.contracts.BaseContract;

public class BaseActivity extends AppCompatActivity implements BaseContract.BaseView {

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void showProgressBar() {
        if (dialog == null) {
            dialog = new Dialog(BaseActivity.this,
                    R.style.TransparentProgressDialog);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.view_progress_dialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        if (!dialog.isShowing())
            dialog.show();
    }

    @Override
    public void hideProgressBar() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }
}
