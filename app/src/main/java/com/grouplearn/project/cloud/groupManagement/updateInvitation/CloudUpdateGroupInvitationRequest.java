package com.grouplearn.project.cloud.groupManagement.updateInvitation;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-10-2016 19:58 for GroupLearn application.
 */

public class CloudUpdateGroupInvitationRequest extends CloudConnectRequest {
    ArrayList<RequestModel> requestModels = new ArrayList<>();

    public ArrayList<RequestModel> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<RequestModel> requestModels) {
        this.requestModels = requestModels;
    }

    public void setRequestModels(RequestModel requestModels) {
        this.requestModels.add(requestModels);
    }

    @Override
    public int validate() {
        return 0;
    }
}
