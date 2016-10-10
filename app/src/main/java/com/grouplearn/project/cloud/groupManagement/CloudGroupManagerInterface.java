package com.grouplearn.project.cloud.groupManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupRequest;
import com.grouplearn.project.cloud.groupManagement.addSubscribedGroup.CloudAddSubscribedGroupRequest;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudContactDeleteRequest;
import com.grouplearn.project.cloud.groupManagement.exitGroup.CloudExitGroupRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersRequest;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.getSubscribedGroups.CloudGetSubscribedGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.updateGroupRequest.CloudUpdateSubscribeGroupRequest;

/**
 * Created by Godwin Joseph on 18-05-2016 23:25 for Group Learn application.
 */
public interface CloudGroupManagerInterface {
    public void addGroup(CloudAddGroupRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void addSubscribedGroup(CloudAddSubscribedGroupRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void updateGroupRequest(CloudUpdateSubscribeGroupRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void getSubscribedGroups(CloudGetSubscribedGroupsRequest cloudRequest, CloudResponseCallback cloudCallback);

    public void getAllSubscribeGroupRequest(CloudGetSubscribeRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void exitFromGroup(CloudExitGroupRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void getAllGroups(CloudGetGroupsRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void getAllSubscribers(GetGroupSubscribersRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

    public void deleteGroup(CloudContactDeleteRequest cloudRequest, CloudResponseCallback cloudResponseCallback);

}
