package com.grouplearn.project.cloud.groupManagement.deleteGroup;

import com.grouplearn.project.cloud.CloudConnectResponse;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 00:13 for Group Learn application.
 */
public class CloudDeleteGroupResponse extends CloudConnectResponse {
    ArrayList<String> groupUniqueIdList = new ArrayList<>();
    private int groupCount;

    public ArrayList<String> getGroupUniqueIdList() {
        return groupUniqueIdList;
    }

    public void setGroupUniqueIdList(ArrayList<String> groupUniqueIdList) {
        this.groupUniqueIdList = groupUniqueIdList;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
}
