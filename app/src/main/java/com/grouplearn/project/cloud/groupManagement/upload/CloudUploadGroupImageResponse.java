package com.grouplearn.project.cloud.groupManagement.upload;

import com.grouplearn.project.cloud.CloudConnectResponse;

/**
 * Created by WiSilica on 18-12-2016 17:24 for GroupLearn application.
 */

public class CloudUploadGroupImageResponse extends CloudConnectResponse {
    String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
