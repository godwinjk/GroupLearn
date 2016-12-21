package com.grouplearn.project.cloud.message.messageSet;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.bean.GLMessage;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:19 for Group Learn application.
 */
public class CloudSetMessageRequest extends CloudConnectRequest {
    ArrayList<GLMessage> messageModels = new ArrayList<>();

    public ArrayList<GLMessage> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<GLMessage> messageModels) {
        this.messageModels = messageModels;
    }

    public void setMessageModels(GLMessage messageModel) {
        this.messageModels.add(messageModel);
    }
    @Override
    public int validate() {
        return 0;
    }
}
