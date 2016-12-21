package com.grouplearn.project.cloud.userManagement.changePin;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by WiSilica on 06-12-2016 16:14 for GroupLearn application.
 */

public class CloudChangePinRequest extends CloudConnectRequest {
    String userName;
    int otp;
    String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int validate() {
        return 0;
    }
}
