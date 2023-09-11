package com.issasafar.healthcore.ui.medicalStaff.ui.profile;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private final MutableLiveData<String> fullName;
    private final MutableLiveData<String> email;
    private final MutableLiveData<String> gender;
    private final MutableLiveData<String> accountType;

    public ProfileViewModel(SharedPreferences sharedPreferences) {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is slideshow fragment");
        fullName = new MutableLiveData<>();
        email = new MutableLiveData<>();
        gender = new MutableLiveData<>();
        accountType = new MutableLiveData<>();


        fullName.setValue(sharedPreferences.getString("fullName", ""));
        email.setValue(sharedPreferences.getString("email", ""));
        gender.setValue(sharedPreferences.getString("gender", ""));
        accountType.setValue(sharedPreferences.getString("accountType", ""));
    }

    public MutableLiveData<String> getFullName() {
        return fullName;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<String> getGender() {
        return gender;
    }

    public MutableLiveData<String> getAccountType() {
        return accountType;
    }

//    public LiveData<String> getText() {
//        return mText;
//    }
}