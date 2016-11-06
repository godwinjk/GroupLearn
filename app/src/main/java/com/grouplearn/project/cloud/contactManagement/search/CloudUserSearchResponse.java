package com.grouplearn.project.cloud.contactManagement.search;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 06-11-2016 12:08 for GroupLearn application.
 */

public class CloudUserSearchResponse extends CloudConnectResponse {
    int contactCount = 0;
    ArrayList<ContactModel> contactModels = new ArrayList<>();

    public ArrayList<ContactModel> getContactModels() {
        return contactModels;
    }

    public void setContactModels(ArrayList<ContactModel> contactModels) {
        this.contactModels = contactModels;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }
}
