package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserLocationModel {
    @SerializedName("_id")
    @Expose
    private String uid;
    private String latitude;
    private String longitude;
    private long timestamp;

    public UserLocationModel() {
    }

    public UserLocationModel(String uid, String latitude, String longitude, long timestamp) {
        this.uid = uid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
