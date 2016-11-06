package com.grouplearn.project.cloud.contactManagement.search;

import com.grouplearn.project.cloud.CloudConnectRequest;

/**
 * Created by Godwin Joseph on 06-11-2016 12:07 for GroupLearn application.
 */

public class CloudUserSearchRequest extends CloudConnectRequest {
    String keyWord;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public int validate() {
        return 0;
    }
}
