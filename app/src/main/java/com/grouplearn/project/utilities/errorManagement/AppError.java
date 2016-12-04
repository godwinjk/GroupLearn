package com.grouplearn.project.utilities.errorManagement;

import com.grouplearn.project.cloud.CloudError;

/**
 * Created by Godwin Joseph on 10-05-2016 11:28 for Group Learn application.
 */
public class AppError {
    int errorCode;
    String errorMessage;

    public AppError(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AppError(CloudError error) {
        this.errorCode = error.getErrorCode();
        this.errorMessage = error.getErrorMessage();
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
