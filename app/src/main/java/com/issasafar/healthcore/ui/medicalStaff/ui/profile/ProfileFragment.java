package com.issasafar.healthcore.ui.medicalStaff.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.issasafar.healthcore.MainActivity;
import com.issasafar.healthcore.databinding.FragmentStaffProfileBinding;
import com.issasafar.healthcore.ui.patient.ui.profile.ProfileViewModel;
import com.issasafar.healthcore.ui.patient.ui.profile.ProfileViewModelFactory;

public class ProfileFragment extends Fragment {
    private final String SHARED_PREF_FILE = "com.issasafar.healthcore";
    private SharedPreferences sharedPreferences;
    private FragmentStaffProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this, new ProfileViewModelFactory(sharedPreferences)).get(ProfileViewModel.class);

        binding = FragmentStaffProfileBinding.inflate(inflater, container, false);

        View root = binding.getRoot();
        final TextView fullNameTextView = binding.nameTextView;
        final TextView emailTextView = binding.emailTextView;
        final TextView genderTextView = binding.genderTextView;
        final TextView accountTypeTextView = binding.accountTextView;
        profileViewModel.getFullName().observe(getViewLifecycleOwner(), fullNameTextView::setText);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), emailTextView::setText);
        profileViewModel.getGender().observe(getViewLifecycleOwner(), genderTextView::setText);
        profileViewModel.getAccountType().observe(getViewLifecycleOwner(), accountTypeTextView::setText);
        Button logoutLayout = binding.logoutLayout;
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();

                Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                SharedPreferences.Editor spEditor = sharedPreferences.edit();
                spEditor.clear().apply();
                startActivity(i);
                getActivity().finish();
            }
        });
        return root;
    }

    @Override
    public void onStop() {
        super.onStop();

//        stopService(new Intent(this, LocationTrack.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}