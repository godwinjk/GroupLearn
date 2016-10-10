package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.utilities.errorManagement.AppError;

/**
 * Created by Godwin Joseph on 17-08-2016 10:12 for Group Learn application.
 */
public interface CloudOperationCallback {
    public void onCloudOperationSuccess();

    public void onCloudOperationFailed(AppError error);
}
