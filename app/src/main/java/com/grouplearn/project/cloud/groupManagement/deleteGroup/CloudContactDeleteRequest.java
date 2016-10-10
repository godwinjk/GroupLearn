package com.grouplearn.project.cloud.groupManagement.deleteGroup;

import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 00:13 for Group Learn application.
 */
public class CloudContactDeleteRequest extends CloudConnectRequest {
    ArrayList<String> groupUniqueIdList = new ArrayList<>();

    public ArrayList<String> getGroupUniqueIdList() {
        return groupUniqueIdList;
    }

    public void setGroupUniqueIdList(ArrayList<String> groupUniqueIdList) {
        this.groupUniqueIdList = groupUniqueIdList;
    }

    @Override
    public int validate() {
        return 0;
    }
}
