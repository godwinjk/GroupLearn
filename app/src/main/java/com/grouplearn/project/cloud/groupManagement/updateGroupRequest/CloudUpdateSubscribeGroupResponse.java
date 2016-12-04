package com.grouplearn.project.cloud.groupManagement.updateGroupRequest;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-09-2016 09:16 for Group Learn application.
 */
public class CloudUpdateSubscribeGroupResponse extends CloudConnectResponse {
    ArrayList<GLRequest> requestModels = new ArrayList<>();

    public ArrayList<GLRequest> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<GLRequest> requestModels) {
        this.requestModels = requestModels;
    }
}
