package com.grouplearn.project.cloud.contactManagement.get;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.bean.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:44 for Group Learn application.
 */
public class CloudContactGetResponse extends CloudConnectResponse {
    ArrayList<GLContact> contacts = new ArrayList<>();
    private int contactCount;

    public ArrayList<GLContact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<GLContact> contacts) {
        this.contacts = contacts;
    }

    public int getContactCount() {
        return contactCount;
    }

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }
}
