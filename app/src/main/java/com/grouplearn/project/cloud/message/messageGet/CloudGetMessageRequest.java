package com.grouplearn.project.cloud.message.messageGet;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.MessageModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:19 for Group Learn application.
 */
public class CloudGetMessageRequest extends CloudConnectRequest {
    ArrayList<MessageModel> messageModels = new ArrayList<>();
    private int type = 0;
    private long groupId=0;

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
