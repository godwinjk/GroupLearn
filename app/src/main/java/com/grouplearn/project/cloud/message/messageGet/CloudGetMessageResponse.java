package com.grouplearn.project.cloud.message.messageGet;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.MessageModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:20 for Group Learn application.
 */
public class CloudGetMessageResponse extends CloudConnectResponse {
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    int messageCount = 0;

    public ArrayList<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public void setMessageModels(MessageModel messageModel) {
        this.messageModels.add(messageModel);
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
}
