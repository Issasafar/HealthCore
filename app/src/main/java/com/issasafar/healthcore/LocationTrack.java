package com.issasafar.healthcore;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.issasafar.healthcore.data.model.GpsLocation;
import com.issasafar.healthcore.data.model.HealthStatus;
import com.issasafar.healthcore.data.model.OnlinePatient;

import java.util.ArrayList;


public class LocationTrack extends Service implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private final Context mContext;
    private final String SHARED_PREF_FILE = "com.issasafar.healthcore";
    protected LocationManager mLocationManager;
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location location;
    HealthStatus healthStatus;
    double latitude;
    double longitude;

    public LocationTrack(Context mContext) {
        this.mContext = mContext;
        getLocation();
    }

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(mContext);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    public void stopListener() {
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                askForPermissions();

            }
            mLocationManager.removeUpdates(LocationTrack.this);
        }
    }

    private void askForPermissions() {
        ArrayList<String> permissionsList = new ArrayList<>();
        permissionsList.add(ACCESS_FINE_LOCATION);
        permissionsList.add(ACCESS_FINE_LOCATION);
        //  ActivityCompat.requestPermissions(mActivity, permissionsList.toArray(new String[permissionsList.size()]), 101);
    }

    private Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            checkGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            checkNetwork = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                Log.e("Loc::Track", "No Service provider");
            } else {
                this.canGetLocation = true;
                if (checkGPS) {

                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        askForPermissions();

                    }
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }

                }
                if (checkNetwork) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        askForPermissions();

                    }
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    if (mLocationManager != null) {
                        location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Location trk:", e.getMessage());
        }
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        boolean isPatient = false;
        if (sharedPreferences != null) {

            String account = sharedPreferences.getString("accountType", "");

            isPatient = account.equals("patient");

        }

        if ((FirebaseAuth.getInstance().getCurrentUser() != null) && isPatient) {
            this.location = getLocation();
            double latitude = 0;
            double longitude = 0;
            latitude = this.getLatitude();
            longitude = this.getLongitude();
            GpsLocation gpsLocation = new GpsLocation(latitude, longitude);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            DatabaseReference userNode = FirebaseDatabase.getInstance().getReference("users/patient").child(firebaseUser.getUid());
            DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("online").child(firebaseUser.getUid());

            userNode.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    healthStatus = snapshot.child("healthStatus").getValue(HealthStatus.class);
                    OnlinePatient onlinePatient = new OnlinePatient(healthStatus, gpsLocation);
                    onlineRef.setValue(onlinePatient);
                    onlineRef.onDisconnect().removeValue();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//
        }

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        this.location = getLocation();
//        double latitude = 0;
//        double longitude = 0;
//       // latitude = this.getLatitude();
//       // longitude = this.getLongitude();
//        GpsLocation gpsLocation = new GpsLocation(latitude, longitude);
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//        DatabaseReference userNode = FirebaseDatabase.getInstance().getReference("users/patient").child(firebaseUser.getUid());
//        DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference("online").child(firebaseUser.getUid());
//
//        userNode.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                HealthStatus healthStatus = snapshot.child("healthStatus").getValue(HealthStatus.class);
//                Log.e("Location::healthstats", healthStatus.getValue());
//                OnlinePatient onlinePatient = new OnlinePatient(healthStatus, gpsLocation);
//                onlineRef.setValue(onlinePatient);
//                onlineRef.onDisconnect().removeValue();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return super.onStartCommand(intent, flags, startId);
//    }
}
