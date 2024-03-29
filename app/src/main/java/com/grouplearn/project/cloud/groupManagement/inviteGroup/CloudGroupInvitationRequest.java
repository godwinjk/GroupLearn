package com.grouplearn.project.cloud.groupManagement.inviteGroup;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.bean.GLRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 13-10-2016 19:58 for GroupLearn application.
 */

public class CloudGroupInvitationRequest extends CloudConnectRequest{
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
    @Override
    public int validate() {
        return 0;
    }
}
