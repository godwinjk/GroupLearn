package com.grouplearn.project.cloud.contactManagement.accept;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 10:32 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudContactAcceptRequest extends CloudConnectRequest {
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
    @Override
    public int validate() {
        return 0;
    }
}
