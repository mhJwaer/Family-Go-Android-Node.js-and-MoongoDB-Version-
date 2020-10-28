package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("error")
    @Expose
    private ErrorModel error;

    public ErrorResponse(ErrorModel error) {
        this.error = error;
    }

    public ErrorResponse() {
    }

    public ErrorModel getError() {
        return error;
    }

    public void setError(ErrorModel error) {
        this.error = error;
    }
}
