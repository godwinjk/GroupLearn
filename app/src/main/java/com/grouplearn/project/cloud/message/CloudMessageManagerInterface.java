package com.grouplearn.project.cloud.message;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.message.messageGet.CloudGetMessageRequest;
import com.grouplearn.project.cloud.message.messageSet.CloudSetMessageRequest;

/**
 * Created by Godwin Joseph on 03-10-2016 10:21 for Group Learn application.
 */
public interface CloudMessageManagerInterface {
    public void getAllMessages(CloudGetMessageRequest request, CloudResponseCallback responseCallback);

    public void setAllMessages(CloudSetMessageRequest request, CloudResponseCallback responseCallback);
}
