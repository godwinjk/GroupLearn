package com.grouplearn.project.app.uiManagement.cloudHelper;

import android.content.Context;
import android.content.Intent;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.accept.CloudContactAcceptRequest;
import com.grouplearn.project.cloud.contactManagement.get.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.get.CloudContactGetResponse;
import com.grouplearn.project.cloud.contactManagement.getRequest.CloudGetContactRequest;
import com.grouplearn.project.cloud.contactManagement.request.CloudContactRequest;
import com.grouplearn.project.utilities.errorManagement.AppError;

/**
 * Created by Godwin on 07-06-2016 21:09 for Group Learn application 11:47 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class CloudContactManager {
    Context mContext;

    public CloudContactManager(Context mContext) {
        this.mContext = mContext;
    }

    public void getAllContact(final ContactViewInterface contactViewInterface) {
        CloudContactGetRequest request = new CloudContactGetRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudContactGetResponse response = (CloudContactGetResponse) cloudResponse;
                ContactDbHelper helper = new ContactDbHelper(mContext);
                int contactCount = response.getContactCount();
                if (contactCount > 0 && response.getContacts() != null) {
                    for (int i = 0; i < response.getContacts().size(); i++)
                        helper.addContact(response.getContacts().get(i));
                }
                contactViewInterface.onGetAllContactsFromCloud(helper.getContacts());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                contactViewInterface.onGetContactsFailed(new AppError(cloudError));
                if (cloudError.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(mContext);
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(mContext, SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(i);
                        }

                        @Override
                        public void onSignOutFailed() {

                        }

                        @Override
                        public void onSignOutCanceled() {

                        }
                    });
                }
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).getContacts(request, callback);
    }

    public void getRandomUsers(String keyWord, final ContactViewInterface contactViewInterface) {
        CloudContactGetRequest request = new CloudContactGetRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setKey(keyWord);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudContactGetResponse response = (CloudContactGetResponse) cloudResponse;
                int contactCount = response.getContactCount();
                if (contactCount > 0 && response.getContacts() != null) {
                    contactViewInterface.onGetAllContactsFromCloud(response.getContacts());
                } else {
                    contactViewInterface.onGetContactsFailed(new AppError(response.getResponseStatus(), response.getResponseMessage()));
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (cloudError.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(mContext);
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(mContext, SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(i);
                        }

                        @Override
                        public void onSignOutFailed() {

                        }

                        @Override
                        public void onSignOutCanceled() {

                        }
                    });
                }
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).getUsers(request, callback);
    }

    public void getAllRequests(final ContactViewInterface contactViewInterface) {
        CloudGetContactRequest request = new CloudGetContactRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudContactGetResponse response = (CloudContactGetResponse) cloudResponse;
                int contactCount = response.getContactCount();
                if (contactCount > 0 && response.getContacts() != null) {
                    contactViewInterface.onGetAllContactsFromCloud(response.getContacts());
                } else {
                    contactViewInterface.onGetContactsFailed(new AppError(response.getResponseStatus(), response.getResponseMessage()));
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (cloudError.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(mContext);
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(mContext, SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(i);
                        }

                        @Override
                        public void onSignOutFailed() {

                        }

                        @Override
                        public void onSignOutCanceled() {

                        }
                    });
                }
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).getContactRequests(request, callback);
    }

    public void acceptContactRequest(GLContact contact, CloudResponseCallback callback) {
        CloudContactAcceptRequest request = new CloudContactAcceptRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setContacts(contact);
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).acceptContactRequest(request, callback);
    }

    public void requestToConnect(GLContact contact, CloudResponseCallback callback) {
        CloudContactRequest request = new CloudContactRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setContacts(contact);
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).requestContact(request, callback);
    }
}
