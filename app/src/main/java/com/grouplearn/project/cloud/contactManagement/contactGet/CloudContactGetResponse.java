package com.grouplearn.project.cloud.contactManagement.contactGet;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:44 for Group Learn application.
 */
public class CloudContactGetResponse extends CloudConnectResponse {
    ArrayList<ContactModel> contactModels = new ArrayList<>();
    private int contactCount;

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
