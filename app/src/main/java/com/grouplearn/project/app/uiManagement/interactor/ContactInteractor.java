package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;

import com.grouplearn.project.app.uiManagement.cloudHelper.CloudContactManager;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudResponseCallback;

/**
 * Created by Godwin Joseph on 07-06-2016 22:07 for Group Learn application.
 */
public class ContactInteractor {
    Context mContext;
    CloudContactManager contactManager;

    public ContactInteractor(Context mContext) {
        this.mContext = mContext;
        contactManager = new CloudContactManager(mContext);
    }

    public void getAllContacts(ContactViewInterface contactViewInterface) {
        contactViewInterface.onGetAllContactsFromDb(new ContactDbHelper(mContext).getContacts());
        contactManager.getAllContact(contactViewInterface);
    }

    public void getRequests(ContactViewInterface contactViewInterface) {
        contactManager.getAllRequests(contactViewInterface);
    }

    public void acceptRequests(ContactViewInterface contactViewInterface) {
        contactManager.getAllRequests(contactViewInterface);
    }

    public void requestToConnect(GLContact contact, CloudResponseCallback callback) {
    contactManager.requestToConnect(contact,callback);
    }

    public void getRandomUsers(ContactViewInterface contactViewInterface) {
        contactManager.getRandomUsers("0", contactViewInterface);
    }

    public void searchUsers(String keyWord, ContactViewInterface contactViewInterface) {
        contactManager.getRandomUsers(keyWord, contactViewInterface);
    }
}
