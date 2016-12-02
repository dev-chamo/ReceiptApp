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
    private String errMessage;

    public boolean getIsRequest() {
        return isRequest;
    }

    public void setIsRequest(boolean request) {
        isRequest = request;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
