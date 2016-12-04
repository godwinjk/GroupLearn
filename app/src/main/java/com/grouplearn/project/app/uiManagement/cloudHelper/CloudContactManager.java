package com.grouplearn.project.app.uiManagement.cloudHelper;

import android.content.Context;
import android.content.Intent;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditRequest;
import com.grouplearn.project.cloud.contactManagement.contactGet.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.contactGet.CloudContactGetResponse;
import com.grouplearn.project.cloud.contactManagement.search.CloudUserSearchRequest;
import com.grouplearn.project.models.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 21:09 for Group Learn application.
 */
public class CloudContactManager {
    Context mContext;

    public CloudContactManager(Context mContext) {
        this.mContext = mContext;
    }

    public void addEditContact(ArrayList<GLContact> contactModels, CloudResponseCallback callback) {
        CloudContactAddOrEditRequest request = new CloudContactAddOrEditRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setContactModels(contactModels);
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).addOrEditContact(request, callback);
    }

    public void getAllContact() {
        CloudContactGetRequest request = new CloudContactGetRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudContactGetResponse response = (CloudContactGetResponse) cloudResponse;
                int contactCount = response.getContactCount();
                if (contactCount > 0 && response.getContactModels() != null) {
                    for (int i = 0; i < response.getContactModels().size(); i++)
                        new ContactDbHelper(mContext).addContact(response.getContactModels().get(i));
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
        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).getContacts(request, callback);
    }

    public void searchContact(String keyWord, CloudResponseCallback callback) {
        CloudUserSearchRequest request = new CloudUserSearchRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setKeyWord(keyWord);
        request.setLimit(100);

        CloudConnectManager.getInstance(mContext).getCloudContactManager(mContext).searchContact(request, callback);
    }
}
