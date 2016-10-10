package com.grouplearn.project.cloud.userManagement.signUp;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudSignUpResponse extends CloudConnectResponse {
    String userToken;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
}
