package com.grouplearn.project.cloud.userManagement.status;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudStatusResponse extends CloudConnectResponse {
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
