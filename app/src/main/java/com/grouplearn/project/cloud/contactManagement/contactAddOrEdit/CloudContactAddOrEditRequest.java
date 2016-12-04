package com.grouplearn.project.cloud.contactManagement.contactAddOrEdit;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:43 for Group Learn application.
 */
public class CloudContactAddOrEditRequest extends CloudConnectRequest {
    ArrayList<GLContact> contactModels = new ArrayList<>();

    public ArrayList<GLContact> getContactModels() {
        return contactModels;
    }

    public void setContactModels(GLContact contactModel) {
        this.contactModels.add(contactModel);
    }

    public void setContactModels(ArrayList<GLContact> contactModels) {
        this.contactModels = contactModels;
    }

    @Override
    public int validate() {
        return 0;
    }
}
