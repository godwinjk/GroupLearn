package com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 07-10-2016 18:44 for GroupLearn application.
 */

public class GetGroupSubscribersRequest extends CloudConnectRequest {
    long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    @Override
    public int validate() {
        return 0;
    }
}
