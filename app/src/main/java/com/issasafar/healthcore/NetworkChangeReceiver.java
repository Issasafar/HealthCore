package com.issasafar.healthcore;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkChangeReceiver extends BroadcastReceiver {
    private final Activity mActivity;


    public NetworkChangeReceiver(Activity activity) {
        mActivity = activity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkInternet(context)) {
            SnackbarHelper.showTopSnackbar(mActivity, 1);
        } else {
            SnackbarHelper.showTopSnackbar(mActivity, 0);
        }
    }

    private boolean checkInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        return false;
    }
}
