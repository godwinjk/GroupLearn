package com.grouplearn.project.cloud.groupManagement.getGroups;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GroupModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-05-2016 17:03 for Group Learn application.
 */
public class CloudGetGroupsResponse extends CloudConnectResponse {
    ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
    private int groupCount;

    public ArrayList<GroupModel> getGroupModelArrayList() {
        return groupModelArrayList;
    }

    public void setGroupModelArrayList(ArrayList<GroupModel> groupModelArrayList) {
        this.groupModelArrayList = groupModelArrayList;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
}
