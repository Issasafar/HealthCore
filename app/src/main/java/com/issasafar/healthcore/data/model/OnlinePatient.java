package com.issasafar.healthcore.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class OnlinePatient implements Parcelable {
    public static final Creator<OnlinePatient> CREATOR = new Creator<OnlinePatient>() {
        @Override
        public OnlinePatient createFromParcel(Parcel in) {
            return new OnlinePatient(in);
        }

        @Override
        public OnlinePatient[] newArray(int size) {
            return new OnlinePatient[size];
        }
    };
    private HealthStatus mHealthStatus;
    private GpsLocation gpsLocation;

    protected OnlinePatient(Parcel in) {
        gpsLocation = in.readParcelable(GpsLocation.class.getClassLoader());
    }

    public OnlinePatient() {

    }

    public OnlinePatient(HealthStatus healthStatus, GpsLocation gpsLocation) {
        mHealthStatus = healthStatus;
        this.gpsLocation = gpsLocation;
    }

    public HealthStatus getHealthStatus() {
        return mHealthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        mHealthStatus = healthStatus;
    }

    public GpsLocation getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(GpsLocation gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeParcelable(gpsLocation, i);
    }
}
