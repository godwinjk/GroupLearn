package com.grouplearn.project.cloud.groupManagement.updateGroupRequest;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-09-2016 09:16 for Group Learn application.
 */
public class CloudUpdateSubscribeGroupResponse extends CloudConnectResponse {
    ArrayList<RequestModel> requestModels = new ArrayList<>();

    public ArrayList<RequestModel> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<RequestModel> requestModels) {
        this.requestModels = requestModels;
    }
}
