package com.grouplearn.project.app.uiManagement.message;

import android.os.Build;
import android.provider.Settings;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
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
 * Created by Godwin Joseph on 20-09-2016 16:58 for Group Learn application.
 */
public class FcmInstantIdService extends FirebaseInstanceIdService {
    private static final String TAG = "FcmInstantIdService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        registerApp(refreshedToken);
    }

    private void registerApp(String fcmToken) {
        final AppSharedPreference mPref = new AppSharedPreference(this);
        mPref.setStringPrefValue(PreferenceConstants.GCM_TOKEN, fcmToken);

        CloudAppRegistrationRequest request = new CloudAppRegistrationRequest();
        request.setPhoneOsVersion("ANDROID_" + Build.VERSION.RELEASE);
        request.setPhoneModel(Build.MODEL);
        request.setAppVersion(Helper.getAppVersion(this));
        request.setPhoneUniqueId(Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID));

        request.setGcmToken(fcmToken);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudAppRegistrationResponse response = (CloudAppRegistrationResponse) cloudResponse;
                Log.i(TAG, "APP UNIQUE ID" + response.getAppUniqueId());
                mPref.setIntegerPrefValue(PreferenceConstants.APP_ID, response.getAppUniqueId());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                Log.i(TAG, "APP REG |||||||| FAILED FAILED FAILED FAILED FAILED FAILED");
            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(this).getCloudAppRegistrationManager(this).registerApp(request, callback);
        }
    }
}
