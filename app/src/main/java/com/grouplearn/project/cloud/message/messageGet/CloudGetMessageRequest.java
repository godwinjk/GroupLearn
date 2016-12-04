package com.grouplearn.project.cloud.message.messageGet;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLMessage;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:19 for Group Learn application.
 */
public class CloudGetMessageRequest extends CloudConnectRequest {
    ArrayList<GLMessage> messageModels = new ArrayList<>();
    private int type = 0;
    private long groupId=0;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
