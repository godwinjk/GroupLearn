package com.grouplearn.project.cloud;

/**
 * Created by Godwin Joseph on 13-05-2016 12:51 for Group Learn application.
 */
public abstract class CloudConnectRequest {
    String token = CloudConstants.DEFAULT_STRING_VALUE;
    private String startTime;
    private int limit=100;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public abstract int validate();

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
