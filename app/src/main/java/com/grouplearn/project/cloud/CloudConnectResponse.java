package com.grouplearn.project.cloud;

/**
 * Created by Godwin Joseph on 13-05-2016 12:51 for Group Learn application.
 */
public class CloudConnectResponse {
    int responseStatus = CloudConstants.DEFAULT_INTEGER_VALUE;
    String responseMessage = CloudConstants.DEFAULT_STRING_VALUE;
    long timeStamp = CloudConstants.DEFAULT_INTEGER_VALUE;

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
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
