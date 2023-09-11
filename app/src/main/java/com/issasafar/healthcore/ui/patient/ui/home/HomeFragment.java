package com.issasafar.healthcore.ui.patient.ui.home;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.issasafar.healthcore.R;
import com.issasafar.healthcore.data.model.GpsLocation;
import com.issasafar.healthcore.data.model.OnlinePatient;
import com.issasafar.healthcore.databinding.FragmentPatientHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    final int NOTIFICATION_ID = 2003;
    private final String CHANNEL_ID = "healthCoreNotification";
    private FragmentPatientHomeBinding binding;
    private ArrayList<OnlinePatient> tempList;
    private Context mContext;
    private LinearLayout homeMainLayout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentPatientHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        homeMainLayout = binding.homeMainLayout;
        mContext = getActivity().getApplicationContext();
        createNotificationChannel();
        fetchNearbyPatients();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void fetchNearbyPatients() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Query query = firebaseDatabase.getReference("online");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        tempList = new ArrayList<>();
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
                                tempList.add(temp);
                            }
                        }
                    }
                    if (!tempList.isEmpty()) {
                        break;
                    }
                }

                if (!tempList.isEmpty() && getContext() != null) {


                    Intent mapsActivityIntent = new Intent(mContext, MapsActivity.class);
                    mapsActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, mapsActivityIntent, PendingIntent.FLAG_IMMUTABLE);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
                    builder.setSmallIcon(R.drawable.outline_map_24)
                            .setContentTitle(getActivity().getString(R.string.nearby_patients))
                            .setContentText(getString(R.string.nearby_patients_notification_text))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(mContext);

                    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
                    homeMainLayout.setVisibility(View.GONE);
                } else {
                    homeMainLayout.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addValueEventListener(valueEventListener);
        // Log.e("list first element",tempList.get(0).getHealthStatus().getValue());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = mContext.getSystemService((NotificationManager.class));
            notificationManager.createNotificationChannel(channel);
        }
    }

}
