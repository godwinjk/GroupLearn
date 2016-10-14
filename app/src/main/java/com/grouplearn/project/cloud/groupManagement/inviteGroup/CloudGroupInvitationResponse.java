package com.grouplearn.project.cloud.groupManagement.inviteGroup;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-10-2016 19:59 for GroupLearn application.
 */

public class CloudGroupInvitationResponse extends CloudConnectResponse {
    int invitationCount = 0;
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

    public int getInvitationCount() {
        return invitationCount;
    }

    public void setInvitationCount(int invitationCount) {
        this.invitationCount = invitationCount;
    }
}
