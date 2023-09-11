package com.issasafar.healthcore.ui.patient;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.WorkManager;

import com.google.android.material.navigation.NavigationView;
import com.issasafar.healthcore.LocationTrack;
import com.issasafar.healthcore.NetworkChangeReceiver;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.databinding.ActivityPatientMainBinding;

public class PatientMainActivity extends AppCompatActivity {

    private final String GPS_WORKER_TAG = "gps_worker_tag";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPatientMainBinding binding;
    private WorkManager mWorkManager;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPatientMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mNetworkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, intentFilter);
        setSupportActionBar(binding.appBarPatientMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_report, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        LocationTrack locationTrack = new LocationTrack(getApplicationContext());


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_patient_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeReceiver);
    }
}