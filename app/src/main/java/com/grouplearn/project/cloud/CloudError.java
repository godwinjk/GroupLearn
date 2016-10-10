package com.grouplearn.project.cloud;

/**
 * Created by Godwin Joseph on 13-05-2016 13:04 for Group Learn application.
 */
public class CloudError {
    int errorCode;
    String errorMessage;

    public CloudError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
