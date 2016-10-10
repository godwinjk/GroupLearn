package com.grouplearn.project.cloud.groupManagement.getGroups;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 13-05-2016 17:03 for Group Learn application.
 */
public class CloudGetGroupsRequest extends CloudConnectRequest {
    private String key;

    @Override
    public int validate() {
        return 0;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
