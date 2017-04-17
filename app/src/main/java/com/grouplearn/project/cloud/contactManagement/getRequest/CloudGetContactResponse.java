package com.grouplearn.project.cloud.contactManagement.getRequest;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectResponse;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 10:32 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudGetContactResponse extends CloudConnectResponse {
    ArrayList<GLContact> contacts = new ArrayList<>();
    int contactCount;

    public void setContactCount(int contactCount) {
        this.contactCount = contactCount;
    }

    public int getContactCount() {
        return contactCount;
    }
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
