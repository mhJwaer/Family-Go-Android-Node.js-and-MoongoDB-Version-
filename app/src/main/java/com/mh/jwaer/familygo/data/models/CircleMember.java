package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CircleMember {

    @SerializedName("_id")
    @Expose
    private String uid;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("circle")
    @Expose
    private String circle;
    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("isSharing")
    @Expose
    private Boolean isSharing;
    @SerializedName("photoUrl")
    @Expose
    private String photoUrl;
    @SerializedName("messageToken")
    @Expose
    private String messageToken;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean isSharing() {
        return isSharing;
    }

    public void setIsSharing(Boolean isSharing) {
        this.isSharing = isSharing;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

}
