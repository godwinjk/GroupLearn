package com.grouplearn.project.cloud.groupManagement.getGroupRequest;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 05-09-2016 09:15 for Group Learn application.
 */
public class CloudGetSubscribeRequest extends CloudConnectRequest {
    long groupId;

    @Override
    public int validate() {
        return 0;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
