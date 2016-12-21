package com.grouplearn.project.cloud.userManagement.status;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudStatusRequest extends CloudConnectRequest {
    String status;
    int privacyValue = -1;
    String userDisplayName;

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPrivacyValue() {
        return privacyValue;
    }

    public void setPrivacyValue(int privacyValue) {
        this.privacyValue = privacyValue;
    }

    @Override
    public int validate() {
        return 0;
    }
}
