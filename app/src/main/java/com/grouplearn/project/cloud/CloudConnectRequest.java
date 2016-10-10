package com.grouplearn.project.cloud;

/**
 * Created by Godwin Joseph on 13-05-2016 12:51 for Group Learn application.
 */
public abstract class CloudConnectRequest {
    String token = CloudConstants.DEFAULT_STRING_VALUE;
    private long startTime;
    private int limit=100;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public abstract int validate();

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
