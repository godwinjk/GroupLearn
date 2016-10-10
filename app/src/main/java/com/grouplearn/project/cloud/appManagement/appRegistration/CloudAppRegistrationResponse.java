package com.grouplearn.project.cloud.appManagement.appRegistration;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by Godwin Joseph on 13-05-2016 12:52 for Group Learn application.
 */
public class CloudAppRegistrationResponse extends CloudConnectResponse {
    int appUniqueId;

    public int getAppUniqueId() {
        return appUniqueId;
    }

    public void setAppUniqueId(int appUniqueId) {
        this.appUniqueId = appUniqueId;
    }
}
