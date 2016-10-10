package com.grouplearn.project.cloud.appManagement.gcmRegistration;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by Godwin Joseph on 13-05-2016 16:00 for Group Learn application.
 */
public class CloudGcmRegistrationResponse extends CloudConnectResponse {
    String gcmToken;

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }
}
