package com.grouplearn.project.cloud.userManagement.signUp;

import android.text.TextUtils;

import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInRequest;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

/**
 * Created by Godwin Joseph on 13-05-2016 13:09 for Group Learn application.
 */
public class CloudSignUpRequest extends CloudSignInRequest {
    String displayName;
    String mailId;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    @Override
    public int validate() {
        if (TextUtils.isEmpty(getToken()))
            return ErrorHandler.TOKEN_MISSING;

        return 0;
    }
}
