package com.grouplearn.project.cloud.groupManagement.upload;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by WiSilica on 18-12-2016 17:23 for GroupLearn application.
 */

public class CloudUploadGroupImageRequest extends CloudConnectRequest {
    long groupId;
    String imageBase64;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public int validate() {
        return 0;
    }
}
