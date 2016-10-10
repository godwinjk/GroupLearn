package com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.UserModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-10-2016 18:45 for GroupLearn application.
 */

public class GetGroupSubscribersResponse extends CloudConnectResponse {
    ArrayList<UserModel> userModels = new ArrayList<>();
    int userCount;

    public ArrayList<UserModel> getUserModels() {
        return userModels;
    }

    public void setUserModels(ArrayList<UserModel> userModels) {
        this.userModels = userModels;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
