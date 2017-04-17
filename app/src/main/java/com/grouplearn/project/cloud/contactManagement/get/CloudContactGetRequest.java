package com.grouplearn.project.cloud.contactManagement.get;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 15:43 for Group Learn application.
 */
public class CloudContactGetRequest extends CloudConnectRequest {
    ArrayList<GLContact> contactModels = new ArrayList<>();
    long userId;
    String key;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public ArrayList<GLContact> getContactModels() {
        return contactModels;
    }

    public void setContactModels(ArrayList<GLContact> contactModels) {
        this.contactModels = contactModels;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int validate() {
        return 0;
    }
}
