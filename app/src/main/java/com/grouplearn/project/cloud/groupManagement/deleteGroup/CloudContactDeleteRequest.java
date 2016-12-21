package com.grouplearn.project.cloud.groupManagement.deleteGroup;

import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 00:13 for Group Learn application.
 */
public class CloudContactDeleteRequest extends CloudConnectRequest {
    ArrayList<Long> groupUniqueIdList = new ArrayList<>();

    public ArrayList<Long> getGroupUniqueIdList() {
        return groupUniqueIdList;
    }

    public void setGroupUniqueIdList(ArrayList<Long> groupUniqueIdList) {
        this.groupUniqueIdList = groupUniqueIdList;
    }

    public void setGroupUniqueIdList(Long groupId) {
        this.groupUniqueIdList.add(groupId);
    }

    @Override
    public int validate() {
        return 0;
    }
}
