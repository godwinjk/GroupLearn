package com.grouplearn.project.cloud.groupManagement.addGroup;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GroupModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-05-2016 17:02 for Group Learn application.
 */
public class CloudAddGroupRequest extends CloudConnectRequest {
    ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();

    public ArrayList<GroupModel> getGroupModelArrayList() {
        return groupModelArrayList;
    }

    public void setGroupModels(ArrayList<GroupModel> groupModelArrayList) {
        this.groupModelArrayList = groupModelArrayList;
    }

    public void setGroupModels(GroupModel model) {
        groupModelArrayList.add(model);
        setGroupModels(groupModelArrayList);
    }

    @Override
    public int validate() {
        return 0;
    }
}
