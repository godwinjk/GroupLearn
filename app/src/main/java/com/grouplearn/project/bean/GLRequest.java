package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 05-08-2016 11:07 for Group Learn application.
 */
public class GLRequest extends BaseModel implements Parcelable {
    String userName;
    String userDisplayName;
    long userId;
    String userMessage;
    long groupId;
    String groupName;
    String groupIconId;
    String requestStatus;
    int action;
    private String iconUrl;

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    private String definition;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
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
        userId = in.readLong();
        userMessage = in.readString();
        groupId = in.readLong();
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
        dest.writeLong(userId);
        dest.writeString(userMessage);
        dest.writeLong(groupId);
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