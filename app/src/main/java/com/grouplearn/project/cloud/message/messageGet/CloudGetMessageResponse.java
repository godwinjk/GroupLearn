package com.grouplearn.project.cloud.message.messageGet;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GLMessage;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:20 for Group Learn application.
 */
public class CloudGetMessageResponse extends CloudConnectResponse {
    ArrayList<GLMessage> messageModels = new ArrayList<>();
    int messageCount = 0;

    public ArrayList<GLMessage> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<GLMessage> messageModels) {
        this.messageModels = messageModels;
    }

    public void setMessageModels(GLMessage messageModel) {
        this.messageModels.add(messageModel);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
