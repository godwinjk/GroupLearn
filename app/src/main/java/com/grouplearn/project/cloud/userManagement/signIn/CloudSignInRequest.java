package com.grouplearn.project.cloud.userManagement.signIn;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudSignInRequest extends CloudConnectRequest {
    String userName;
    String password;
    int appUniqueId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAppUniqueId() {
        return appUniqueId;
    }

    public void setAppUniqueId(int appUniqueId) {
        this.appUniqueId = appUniqueId;
    }

    @Override
    public int validate() {
        return 0;
    }
}
