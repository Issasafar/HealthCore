package com.issasafar.healthcore.ui.patient.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.data.model.GpsLocation;
import com.issasafar.healthcore.data.model.HealthStatus;
import com.issasafar.healthcore.data.model.OnlinePatient;
import com.issasafar.healthcore.databinding.ActivityMapsBinding;
import com.issasafar.healthcore.ui.patient.PatientMainActivity;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private ArrayList<OnlinePatient> nearbyPatientsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, PatientMainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("online");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        nearbyPatientsList = new ArrayList<>();
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                OnlinePatient currentUser = null;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (snapshot1.toString().contains(firebaseUser.getUid())) {
                        currentUser = snapshot1.getValue(OnlinePatient.class);
                    }
                }
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    OnlinePatient temp = snapshot1.getValue(OnlinePatient.class);

                    if (!snapshot1.toString().contains(firebaseUser.getUid())) {
                        if (currentUser != null) {
                            GpsLocation currentLocation = currentUser.getGpsLocation();
                            GpsLocation tempLocation = temp.getGpsLocation();
                            if (DistanceCalculator.calculateDistanceInMeters(currentLocation.getLatitude(), currentLocation.getLongitude(), tempLocation.getLatitude(), tempLocation.getLongitude()) <= 200) {
                                Log.e("list added", "added");
                                nearbyPatientsList.add(temp);
                            }
                        }
                    }

                }
                LatLng currentLocation = new LatLng(currentUser.getGpsLocation().getLatitude(), currentUser.getGpsLocation().getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("YOU: " + currentUser.getHealthStatus().getValue()));
                if (!nearbyPatientsList.isEmpty()) {

                    for (OnlinePatient patient : nearbyPatientsList) {
                        LatLng patientLocation = new LatLng(patient.getGpsLocation().getLatitude(), patient.getGpsLocation().getLongitude());

                        if (patient.getHealthStatus() == HealthStatus.HEALTHY) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(patientLocation)
                                    .title(patient.getHealthStatus().getValue())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                        } else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(patientLocation)
                                    .title(patient.getHealthStatus().getValue())
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                    }
                    nearbyPatientsList.clear();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }
}