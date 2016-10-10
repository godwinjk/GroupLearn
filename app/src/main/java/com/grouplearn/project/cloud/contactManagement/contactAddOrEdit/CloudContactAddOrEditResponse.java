package com.grouplearn.project.cloud.contactManagement.contactAddOrEdit;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:44 for Group Learn application.
 */
public class CloudContactAddOrEditResponse extends CloudConnectResponse {
    ArrayList<ContactModel> contactModels = new ArrayList<>();

    public ArrayList<ContactModel> getContactModels() {
        return contactModels;
    }

    public void setContactModels(ArrayList<ContactModel> contactModels) {
        this.contactModels = contactModels;
    }
}
