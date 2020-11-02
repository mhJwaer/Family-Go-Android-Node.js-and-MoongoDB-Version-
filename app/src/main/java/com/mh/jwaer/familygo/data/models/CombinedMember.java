package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CombinedMember {

    @SerializedName("_id")
    @Expose
    private String uid;
    private String name;
    private String photoUrl;
    private String latitude;
    private String longitude;
    private boolean isSharing;
    private boolean isAdmin;
    private long timestamp;

    public CombinedMember(String name, String uid, String photoUrl,
                          String latitude, String longitude, boolean isSharing, boolean isAdmin, long timestamp) {
        this.name = name;
        this.uid = uid;
        this.photoUrl = photoUrl;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSharing = isSharing;
        this.isAdmin = isAdmin;
        this.timestamp = timestamp;
    }

    public CombinedMember() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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

    public boolean isSharing() {
        return isSharing;
    }

    public void setSharing(boolean sharing) {
        isSharing = sharing;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
