package com.grouplearn.project.cloud.interest.get;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudGetInterestRequest extends CloudConnectRequest {
    long userId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int validate() {
        return 0;
    }
}
