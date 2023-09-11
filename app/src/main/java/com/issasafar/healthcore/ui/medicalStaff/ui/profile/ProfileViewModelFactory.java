package com.issasafar.healthcore.ui.medicalStaff.ui.profile;


import android.content.SharedPreferences;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.issasafar.healthcore.ui.patient.ui.profile.ProfileViewModel;

public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    private final SharedPreferences mSharedPreferences;

    public ProfileViewModelFactory(SharedPreferences sharedPreferences) {
        this.mSharedPreferences = sharedPreferences;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ProfileViewModel(mSharedPreferences);
    }
}
