package com.mh.jwaer.familygo.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseBody {
    @SerializedName("isSuccessfull")
    @Expose
    private Boolean isSuccessfull;
    @SerializedName("message")
    @Expose
    private String message;

    public ResponseBody(Boolean isSuccessfull, String message) {
        this.isSuccessfull = isSuccessfull;
        this.message = message;
    }

    public Boolean isSuccessfull() {
        return isSuccessfull;
    }

    public void setIsSuccessfull(Boolean isSuccessfull) {
        this.isSuccessfull = isSuccessfull;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
