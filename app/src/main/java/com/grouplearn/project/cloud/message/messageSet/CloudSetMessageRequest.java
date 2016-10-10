package com.grouplearn.project.cloud.message.messageSet;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.MessageModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:19 for Group Learn application.
 */
public class CloudSetMessageRequest extends CloudConnectRequest {
    ArrayList<MessageModel> messageModels = new ArrayList<>();

    public ArrayList<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public void setMessageModels(MessageModel messageModel) {
        this.messageModels.add(messageModel);
    }
    @Override
    public int validate() {
        return 0;
    }
}
