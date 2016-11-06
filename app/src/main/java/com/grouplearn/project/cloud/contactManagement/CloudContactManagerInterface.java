package com.grouplearn.project.cloud.contactManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditRequest;
import com.grouplearn.project.cloud.contactManagement.contactGet.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.search.CloudUserSearchRequest;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudContactDeleteRequest;

/**
 * Created by Godwin Joseph on 07-06-2016 15:45 for Group Learn application.
 */
public interface CloudContactManagerInterface {
    public void addOrEditContact(CloudContactAddOrEditRequest request, CloudResponseCallback callback);

    public void getContacts(CloudContactGetRequest request, CloudResponseCallback callback);

    public void deleteContact(CloudContactDeleteRequest request, CloudResponseCallback callback);
    public void searchContact(CloudUserSearchRequest request, CloudResponseCallback callback);
}
