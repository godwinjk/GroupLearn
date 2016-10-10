package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationRequest;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Helper;
import com.grouplearn.project.utilities.Log;

/**
 * Created by Godwin Joseph on 08-10-2016 15:52 for GroupLearn application.
 */

public class AppRegistrationTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "AppRegistrationTask";
    Context mContext;
    CloudAppRegistrationRequest request;
    AppSharedPreference mPref;

    public AppRegistrationTask(Context mContext) {
        this.mContext = mContext;
        this.mPref = new AppSharedPreference(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        registerApp();
        return null;
    }

    private void registerApp() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        CloudAppRegistrationRequest request = new CloudAppRegistrationRequest();
        request.setPhoneOsVersion("ANDROID_" + Build.VERSION.RELEASE);
        request.setPhoneModel(Build.MODEL);
        request.setAppVersion(Helper.getAppVersion(mContext));
        request.setPhoneUniqueId(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));

        request.setImeiNumber("9192929929");
        String gcmToken = (refreshedToken == null) ? mPref.getStringPrefValue(PreferenceConstants.GCM_TOKEN) : refreshedToken;
        mPref.setStringPrefValue(PreferenceConstants.GCM_TOKEN, gcmToken);
        request.setGcmToken(gcmToken);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudAppRegistrationResponse response = (CloudAppRegistrationResponse) cloudResponse;
                Log.i(TAG, "APP UNIQUE ID" + response.getAppUniqueId());
                mPref.setIntegerPrefValue(PreferenceConstants.APP_ID, response.getAppUniqueId());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(mContext).getCloudAppRegistrationManager(mContext).registerApp(request, callback);
        }
    }
}
