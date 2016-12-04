package com.grouplearn.project.cloud.courseManagement.get;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by WiSilica on 03-12-2016 22:45 for GroupLearn application.
 */

public class CloudGetCourseRequest extends CloudConnectRequest {
    String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int validate() {
        return 0;
    }
}
