package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;

import com.grouplearn.project.app.uiManagement.cloudHelper.CloudContactManager;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditResponse;
import com.grouplearn.project.cloud.contactManagement.search.CloudUserSearchResponse;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 22:07 for Group Learn application.
 */
public class ContactListInteractor {
    Context mContext;

    public ContactListInteractor(Context mContext) {
        this.mContext = mContext;
    }

    public void getAllContacts(ContactViewInterface contactViewInterface) {
        contactViewInterface.onGetAllContacts(new ContactDbHelper(mContext).getContacts());
        new CloudContactManager(mContext).getAllContact();
    }

    public void addAllContacts(final ArrayList<GLContact> contactModels, final ContactViewInterface contactViewInterface) {

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudContactAddOrEditResponse response = (CloudContactAddOrEditResponse) cloudResponse;
                if (response.getContactModels().size() > 0) {
                    ContactDbHelper dbHelper = new ContactDbHelper(mContext);
                    for (GLContact model : response.getContactModels()) {
                        if (model.getStatus() != 0) {
                            dbHelper.addContact(model);
                        }
                    }
                    contactViewInterface.onGetAllContacts(response.getContactModels());
                } else
                    contactViewInterface.onGetContactsFailed(new AppError(ErrorHandler.NO_ITEMS, ErrorHandler.ErrorMessage.NO_ITEMS));
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

            }
        };
        new CloudContactManager(mContext).addEditContact(contactModels, callback);
    }

    public void searchAllContacts(String keyWord, final ContactViewInterface contactViewInterface) {
        CloudContactManager manager = new CloudContactManager(mContext);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudUserSearchResponse response = (CloudUserSearchResponse) cloudResponse;
                if (response.getContactCount() > 0) {
                    contactViewInterface.onGetAllContacts(response.getContactModels());
                } else {
                    contactViewInterface.onGetContactsFailed(new AppError(ErrorHandler.NO_ITEMS, ErrorHandler.ErrorMessage.NO_ITEMS));
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                contactViewInterface.onGetContactsFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
            }
        };
        manager.searchContact(keyWord, callback);
    }
}
