package com.grouplearn.project.cloud.groupManagement.getInvitations;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-10-2016 19:59 for GroupLearn application.
 */

public class CloudGetGroupInvitationsResponse extends CloudConnectResponse {
    int invitationCount = 0;
    ArrayList<GLRequest> requestModels = new ArrayList<>();

    public ArrayList<GLRequest> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<GLRequest> requestModels) {
        this.requestModels = requestModels;
    }

    public void setRequestModels(GLRequest requestModels) {
        this.requestModels.add(requestModels);
    }

    public int getInvitationCount() {
        return invitationCount;
    }

    public void setInvitationCount(int invitationCount) {
        this.invitationCount = invitationCount;
    }
}
