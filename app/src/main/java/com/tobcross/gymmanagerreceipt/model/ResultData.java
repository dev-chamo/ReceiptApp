package com.tobcross.gymmanagerreceipt.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Koo on 2016. 11. 25..
 */

public class ResultData {

    @SerializedName("isRequest")
    @Expose
    private boolean isRequest = false;

    @SerializedName("err_message")
    @Expose
    private String err_message;

    public boolean getIsRequest() {
        return isRequest;
    }

    public void setIsRequest(boolean request) {
        isRequest = request;
    }

    public String getErr_message() {
        return err_message;
    }

    public void setErr_message(String err_message) {
        this.err_message = err_message;
    }

}
