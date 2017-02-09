package com.grouplearn.project.cloud.groupManagement.deleteGroup;

import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 00:13 for Group Learn application.
 */
public class CloudDeleteGroupRequest extends CloudConnectRequest {
    ArrayList<GLGroup> groupUniqueIdList = new ArrayList<>();

    public ArrayList<GLGroup> getGroupUniqueIdList() {
        return groupUniqueIdList;
    }

    public void setGroupUniqueIdList(ArrayList<GLGroup> groupUniqueIdList) {
        this.groupUniqueIdList = groupUniqueIdList;
    }

    public void setGroupUniqueIdList(GLGroup groupId) {
        this.groupUniqueIdList.add(groupId);
    }

    @Override
    public int validate() {
        return 0;
    }
}
