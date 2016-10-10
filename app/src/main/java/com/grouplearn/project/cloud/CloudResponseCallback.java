package com.grouplearn.project.cloud;

/**
 * Created by Godwin Joseph on 13-05-2016 13:02 for Group Learn application.
 */
public interface CloudResponseCallback {
    public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse);

    public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError);
}
