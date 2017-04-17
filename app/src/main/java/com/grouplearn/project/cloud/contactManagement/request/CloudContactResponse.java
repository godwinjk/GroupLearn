package com.grouplearn.project.cloud.contactManagement.request;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectResponse;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 10:32 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudContactResponse extends CloudConnectResponse {
    ArrayList<GLContact> contacts = new ArrayList<>();

    public ArrayList<GLContact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<GLContact> contacts) {
        this.contacts = contacts;
    }
    public void setContacts(GLContact contact) {
        this.contacts.add(contact);
    }
}
