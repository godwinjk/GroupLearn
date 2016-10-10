package com.grouplearn.project.utilities.errorManagement;

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
