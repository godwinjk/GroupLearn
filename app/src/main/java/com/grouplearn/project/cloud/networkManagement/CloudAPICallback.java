package com.grouplearn.project.cloud.networkManagement;

import com.grouplearn.project.cloud.CloudError;

import org.json.JSONObject;

/**
 * Created by Godwin on 02-02-2016.
 */
public interface CloudAPICallback {
    public void onSuccess(JSONObject jsonObject);

    public void onFailure(CloudError cloudError);
}
