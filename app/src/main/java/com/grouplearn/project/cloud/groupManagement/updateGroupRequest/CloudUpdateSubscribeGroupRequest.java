package com.grouplearn.project.cloud.groupManagement.updateGroupRequest;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-09-2016 09:15 for Group Learn application.
 */
public class CloudUpdateSubscribeGroupRequest extends CloudConnectRequest {
    ArrayList<GLRequest> requestModels = new ArrayList<>();

    public ArrayList<GLRequest> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<GLRequest> requestModels) {
        this.requestModels = requestModels;
    }

    public void setRequestModels(GLRequest requestModel) {
        this.requestModels.add(requestModel);
    }

    @Override
    public int validate() {
        return 0;
    }
}
