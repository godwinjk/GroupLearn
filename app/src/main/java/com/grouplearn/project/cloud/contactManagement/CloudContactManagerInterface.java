package com.grouplearn.project.cloud.contactManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.accept.CloudContactAcceptRequest;
import com.grouplearn.project.cloud.contactManagement.get.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.getRequest.CloudGetContactRequest;
import com.grouplearn.project.cloud.contactManagement.request.CloudContactRequest;

/**
 * Created by Godwin Joseph on 07-06-2016 15:45 for Group Learn application.
 */
public interface CloudContactManagerInterface {
    public void requestContact(CloudContactRequest request, CloudResponseCallback callback);

    public void acceptContactRequest(CloudContactAcceptRequest request, CloudResponseCallback callback);

    public void getContactRequests(CloudGetContactRequest request, CloudResponseCallback callback);

    public void getContacts(CloudContactGetRequest request, CloudResponseCallback callback);

    public void getUsers(CloudContactGetRequest request, CloudResponseCallback callback);

}
