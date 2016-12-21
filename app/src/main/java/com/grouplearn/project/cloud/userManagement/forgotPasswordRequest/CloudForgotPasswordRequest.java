package com.grouplearn.project.cloud.userManagement.forgotPasswordRequest;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by WiSilica on 06-12-2016 16:14 for GroupLearn application.
 */

public class CloudForgotPasswordRequest extends CloudConnectRequest {
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int validate() {
        return 0;
    }
}
