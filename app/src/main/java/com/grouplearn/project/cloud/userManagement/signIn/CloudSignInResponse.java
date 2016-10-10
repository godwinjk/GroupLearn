package com.grouplearn.project.cloud.userManagement.signIn;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudSignInResponse extends CloudConnectResponse {
    String userToken;
    String userName;
    String userDisplayName;
    String userEmail;
    private long userId;
    private int userPrivacy;
    private String userStatus;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getUserPrivacy() {
        return userPrivacy;
    }

    public void setUserPrivacy(int userPrivacy) {
        this.userPrivacy = userPrivacy;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
