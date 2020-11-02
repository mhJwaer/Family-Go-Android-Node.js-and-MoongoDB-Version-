package com.mh.jwaer.familygo.data.models;

public class LogoutModel {
    private String refreshToken;

    public LogoutModel() {
    }

    public LogoutModel(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
