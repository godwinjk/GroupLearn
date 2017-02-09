package com.grouplearn.project.cloud;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Godwin Joseph on 13-05-2016 12:51 for Group Learn application.
 */
public class CloudConnectResponse {
    private int responseStatus = CloudConstants.DEFAULT_INTEGER_VALUE;
    private String responseMessage = CloudConstants.DEFAULT_STRING_VALUE;
    private long timeStamp = CloudConstants.DEFAULT_INTEGER_VALUE;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Context mContext, int responseStatus) {
        this.responseStatus = responseStatus;
        if (responseStatus == 20021) {
            Intent intent = new Intent("INVALID TOKEN");
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
