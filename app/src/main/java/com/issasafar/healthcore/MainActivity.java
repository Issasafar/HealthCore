package com.issasafar.healthcore;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.issasafar.healthcore.databinding.ActivityMainBinding;
import com.issasafar.healthcore.ui.login.LoginActivity;
import com.issasafar.healthcore.ui.medicalStaff.StaffMainActivity;
import com.issasafar.healthcore.ui.patient.PatientMainActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static int INITIAL_PERMISSIONS_RESULTS = 101;
    private final String SHARED_PREF_FILE = "com.issasafar.healthcore";
    private final ArrayList<String> permissions = new ArrayList<>();
    private final ArrayList<String> permissionsRejected = new ArrayList<>();
    com.issasafar.healthcore.databinding.ActivityMainBinding mActivityMainBinding;
    private SharedPreferences mPreferences;
    private ArrayList<String> permissionsToRequest;
    private LocationTrack mLocationTrack;
    private String accountDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);

        // create sharedpref instance
        SharedPreferences mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        // add permissions to permissions arraylist
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(POST_NOTIFICATIONS);
        permissionsToRequest = findDeniedPermissions(permissions);
        if (permissionsToRequest.size() > 0) {
            requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), INITIAL_PERMISSIONS_RESULTS);
        }
        mLocationTrack = new LocationTrack(MainActivity.this);
        double latitude = 0;
        double longitude = 0;
        if (mLocationTrack.canGetLocation()) {
            latitude = mLocationTrack.getLatitude();
            longitude = mLocationTrack.getLongitude();
        } else {
            mLocationTrack.showSettingsAlert();
        }
        // storing data longitude and latitude locally
        SharedPreferences.Editor spEditor = mPreferences.edit();
        spEditor.putString("latitude", String.valueOf(latitude));
        spEditor.putString("longitude", String.valueOf(longitude));
        spEditor.apply();
        ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // you will get result here in result.getData()
                startUserActivity();
                finish();
            } else if (result.getResultCode() == 10) {
                finish();
            } else {
                Snackbar snackbar = Snackbar.make(mActivityMainBinding.getRoot(), R.string.something_went_wrong, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startForResult.launch(loginActivityIntent);
        } else {
            startUserActivity();
            finish();
        }


    }


    private void startUserActivity() {

        SharedPreferences mPreferences = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String accountType = mPreferences.getString("accountType", "");
        Intent userActivityIntent;
        switch (accountType) {
            case "patient":
                userActivityIntent = new Intent(this, PatientMainActivity.class);

                break;
            case "medicalStaff":
                userActivityIntent = new Intent(this, StaffMainActivity.class);

                break;
            default:
                userActivityIntent = new Intent(this, MainActivity.class);
        }
        startActivity(userActivityIntent);
        finish();
    }


    private ArrayList<String> findDeniedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();
        for (String item : wanted) {
            if (!hasPermission(item)) {
                result.add(item);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


}