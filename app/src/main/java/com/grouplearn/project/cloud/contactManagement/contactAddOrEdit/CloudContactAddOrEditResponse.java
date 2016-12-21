package com.grouplearn.project.cloud.contactManagement.contactAddOrEdit;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.bean.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:44 for Group Learn application.
 */
public class CloudContactAddOrEditResponse extends CloudConnectResponse {
    ArrayList<GLContact> contactModels = new ArrayList<>();

    public ArrayList<GLContact> getContactModels() {
        return contactModels;
    }

    public void setContactModels(ArrayList<GLContact> contactModels) {
        this.contactModels = contactModels;
    }
}
