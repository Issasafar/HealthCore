package com.issasafar.healthcore;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.snackbar.Snackbar;
import com.issasafar.healthcore.databinding.OfflineSnackbarBinding;
import com.issasafar.healthcore.databinding.OnlineSnackbarBinding;

public class SnackbarHelper {
    public static final int OFFLINE_STATUS = 0;
    public static final int ONLINE_STATUS = 1;

    public static void showTopSnackbar(Activity activity, int status) {
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View snackbarView;
        if (status == OFFLINE_STATUS) {
            OfflineSnackbarBinding mOfflineSnackbarBinding = OfflineSnackbarBinding.inflate(layoutInflater);
            snackbarView = mOfflineSnackbarBinding.getRoot();
        } else {
            OnlineSnackbarBinding mOnlineSnackbarBinding = OnlineSnackbarBinding.inflate(layoutInflater);
            snackbarView = mOnlineSnackbarBinding.getRoot();
        }
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();

        layout.setPadding(0, 0, 0, 0);
        layout.addView(snackbarView, 0);

        snackbar.getView().setBackgroundColor(Color.WHITE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        params.gravity = Gravity.CENTER;

        snackbar.show();
    }
}
