package com.issasafar.healthcore.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GpsLocation implements Parcelable {

    public static final Creator<GpsLocation> CREATOR = new Creator<GpsLocation>() {
        @Override
        public GpsLocation createFromParcel(Parcel in) {
            return new GpsLocation(in);
        }

        @Override
        public GpsLocation[] newArray(int size) {
            return new GpsLocation[size];
        }
    };
    private Double latitude;
    private Double longitude;

    protected GpsLocation(Parcel in) {
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    public GpsLocation() {

    }

    public GpsLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
