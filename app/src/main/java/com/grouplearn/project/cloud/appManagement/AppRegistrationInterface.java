package com.grouplearn.project.cloud.appManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationRequest;

/**
 * Created by Godwin Joseph on 13-05-2016 12:52 for Group Learn application.
 */
public interface AppRegistrationInterface {
    public void registerApp(CloudAppRegistrationRequest cloudRequest, CloudResponseCallback callback);

    public void registerGcm(CloudResponseCallback callback);
}
