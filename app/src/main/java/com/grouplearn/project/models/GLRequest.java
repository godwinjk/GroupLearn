package com.grouplearn.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 05-08-2016 11:07 for Group Learn application.
 */
public class GLRequest extends BaseModel implements Parcelable {
    String userName;
    String userId;
    String userMessage;
    String groupId;
    String groupName;
    String groupIconId;
    String requestStatus;
    int action;
    private String definition;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupIconId() {
        return groupIconId;
    }

    public void setGroupIconId(String groupIconId) {
        this.groupIconId = groupIconId;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    protected GLRequest(Parcel in) {
        super(in);
        userName = in.readString();
        userId = in.readString();
        userMessage = in.readString();
        groupId = in.readString();
        groupName = in.readString();
        groupIconId = in.readString();
        requestStatus = in.readString();
        action = in.readInt();
    }

    @Override
    public int describeContents() {
        describeContents();
        return 0;
    }

    public GLRequest() {
        super();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcel(dest, flags);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(userMessage);
        dest.writeString(groupId);
        dest.writeString(groupName);
        dest.writeString(groupIconId);
        dest.writeString(requestStatus);
        dest.writeInt(action);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLRequest> CREATOR = new Parcelable.Creator<GLRequest>() {
        @Override
        public GLRequest createFromParcel(Parcel in) {
            return new GLRequest(in);
        }

        @Override
        public GLRequest[] newArray(int size) {
            return new GLRequest[size];
        }
    };

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }
}