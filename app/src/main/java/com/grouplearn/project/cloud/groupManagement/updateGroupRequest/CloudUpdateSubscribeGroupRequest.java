package com.grouplearn.project.cloud.groupManagement.updateGroupRequest;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-09-2016 09:15 for Group Learn application.
 */
public class CloudUpdateSubscribeGroupRequest extends CloudConnectRequest {
    ArrayList<RequestModel> requestModels = new ArrayList<>();

    public ArrayList<RequestModel> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<RequestModel> requestModels) {
        this.requestModels = requestModels;
    }

    public void setRequestModels(RequestModel requestModel) {
        this.requestModels.add(requestModel);
    }

    @Override
    public int validate() {
        return 0;
    }
}
