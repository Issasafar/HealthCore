package com.issasafar.healthcore.ui.medicalStaff;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.issasafar.healthcore.NetworkChangeReceiver;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.databinding.ActivityStaffMainBinding;

public class StaffMainActivity extends AppCompatActivity {

    private ActivityStaffMainBinding binding;
    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStaffMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mNetworkChangeReceiver = new NetworkChangeReceiver(this);
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkChangeReceiver, intentFilter);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_staff_main);

        NavigationUI.setupWithNavController(binding.navView, navController);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkChangeReceiver);
    }
}