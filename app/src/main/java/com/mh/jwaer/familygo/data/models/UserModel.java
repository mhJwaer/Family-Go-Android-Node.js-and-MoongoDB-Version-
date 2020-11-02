package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("_id")
    @Expose
    private String uid;
    @SerializedName("circle")
    @Expose
    private String circle;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("messageToken")
    @Expose
    private String messageToken;
    @SerializedName("photoUrl")
    @Expose
    private String photoUrl;
    @SerializedName("isAdmin")
    @Expose
    private Boolean isAdmin;
    @SerializedName("isSharing")
    @Expose
    private Boolean isSharing;

    public UserModel(String uid, String circle, String name, String email, String messageToken, String photoUrl, Boolean isAdmin, Boolean isSharing) {
        this.uid = uid;
        this.circle = circle;
        this.name = name;
        this.email = email;
        this.messageToken = messageToken;
        this.photoUrl = photoUrl;
        this.isAdmin = isAdmin;
        this.isSharing = isSharing;
    }

    public UserModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
}
