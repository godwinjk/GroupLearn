package com.grouplearn.project.cloud.contactManagement.contactGet;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:43 for Group Learn application.
 */
public class CloudContactGetRequest extends CloudConnectRequest {
    ArrayList<ContactModel> contactModels = new ArrayList<>();

    public ArrayList<ContactModel> getContactModels() {
        return contactModels;
    }

    public void setContactModels(ArrayList<ContactModel> contactModels) {
        this.contactModels = contactModels;
    }

    @Override
    public int validate() {
        return 0;
    }
}
