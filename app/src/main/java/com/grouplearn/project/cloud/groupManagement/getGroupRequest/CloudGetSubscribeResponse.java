package com.grouplearn.project.cloud.groupManagement.getGroupRequest;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.RequestModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 05-09-2016 09:16 for Group Learn application.
 */
public class CloudGetSubscribeResponse extends CloudConnectResponse {
    int numberOfRequests;
    ArrayList<RequestModel> requestModels = new ArrayList<>();

    public ArrayList<RequestModel> getRequestModels() {
        return requestModels;
    }

    public void setRequestModels(ArrayList<RequestModel> requestModels) {
        this.requestModels = requestModels;
    }

    public int getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(int numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }
}
