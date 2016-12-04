package com.grouplearn.project.cloud.groupManagement.addSubscribedGroup;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLGroup;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-05-2016 17:02 for Group Learn application.
 */
public class CloudAddSubscribedGroupRequest extends CloudConnectRequest {
    ArrayList<GLGroup> groupModelArrayList = new ArrayList<>();

    public ArrayList<GLGroup> getGroupModelArrayList() {
        return groupModelArrayList;
    }

    public void setGroupModels(ArrayList<GLGroup> groupModelArrayList) {
        this.groupModelArrayList = groupModelArrayList;
    }

    public void setGroupModels(GLGroup model) {
        groupModelArrayList.add(model);
        setGroupModels(groupModelArrayList);
    }

    @Override
    public int validate() {
        return 0;
    }
}
