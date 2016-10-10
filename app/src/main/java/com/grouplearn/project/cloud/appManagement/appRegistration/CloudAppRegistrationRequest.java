package com.grouplearn.project.cloud.appManagement.appRegistration;

import android.text.TextUtils;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

/**
 * Created by Godwin Joseph on 13-05-2016 12:50 for Group Learn application.
 */
public class CloudAppRegistrationRequest extends CloudConnectRequest {
    String phoneModel;
    String phoneOsVersion;
    String gcmToken;
    String phoneUniqueId;
    String appVersion;
    String imeiNumber;

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getPhoneOsVersion() {
        return phoneOsVersion;
    }

    public void setPhoneOsVersion(String phoneOsVersion) {
        this.phoneOsVersion = phoneOsVersion;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getPhoneUniqueId() {
        return phoneUniqueId;
    }

    public void setPhoneUniqueId(String phoneUniqueId) {
        this.phoneUniqueId = phoneUniqueId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getImeiNumber() {
        return imeiNumber;
    }

    public void setImeiNumber(String imeiNumber) {
        this.imeiNumber = imeiNumber;
    }

    @Override
    public int validate() {
        if (TextUtils.isEmpty(getToken()))
            return ErrorHandler.TOKEN_MISSING;
        else if (TextUtils.isEmpty(getAppVersion()))
            return ErrorHandler.APP_VERSION_MISSING;
        return 0;
    }
}
