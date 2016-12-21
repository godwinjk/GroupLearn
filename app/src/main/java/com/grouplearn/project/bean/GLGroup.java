package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 18-05-2016 23:40 for Group Learn application.
 */
public class GLGroup extends BaseModel implements Parcelable {
    long groupUniqueId;
    String groupName;
    String groupDescription;
    String groupIconId;
    String groupAdminId;
    int privacy;
    String groupCreatedTime;
    String groupUpdatedTime;
    int newMessage = 0;
    String lastMessage = null;
    GLMessage model;
    private String iconUrl;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public int getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(int newMessage) {
        this.newMessage = newMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getGroupUniqueId() {
        return groupUniqueId;
    }

    public void setGroupUniqueId(long groupUniqueId) {
        this.groupUniqueId = groupUniqueId;
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

    public String getGroupCreatedTime() {
        return groupCreatedTime;
    }

    public void setGroupCreatedTime(String groupCreatedTime) {
        this.groupCreatedTime = groupCreatedTime;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupUpdatedTime() {
        return groupUpdatedTime;
    }

    public void setGroupUpdatedTime(String groupUpdatedTime) {
        this.groupUpdatedTime = groupUpdatedTime;
    }

    public String getGroupAdminId() {
        return groupAdminId;
    }

    public void setGroupAdminId(String groupAdminId) {
        this.groupAdminId = groupAdminId;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public GLMessage geMessageModel() {
        return model;
    }

    public void setMessageModel(GLMessage model) {
        this.model = model;
    }

    protected GLGroup(Parcel in) {
        super(in);
        groupUniqueId = in.readLong();
        groupName = in.readString();
        groupDescription = in.readString();
        groupIconId = in.readString();
        groupAdminId = in.readString();
        privacy = in.readInt();
        groupCreatedTime = in.readString();
        groupUpdatedTime = in.readString();
        newMessage = in.readInt();
        lastMessage = in.readString();
        model = (GLMessage) in.readValue(GLMessage.class.getClassLoader());
    }

    public GLGroup() {
        super();
    }

    @Override
    public int describeContents() {
        describeContents();
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(groupUniqueId);
        dest.writeString(groupName);
        dest.writeString(groupDescription);
        dest.writeString(groupIconId);
        dest.writeString(groupAdminId);
        dest.writeInt(privacy);
        dest.writeString(groupCreatedTime);
        dest.writeString(groupUpdatedTime);
        dest.writeInt(newMessage);
        dest.writeString(lastMessage);
        dest.writeValue(model);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLGroup> CREATOR = new Parcelable.Creator<GLGroup>() {
        @Override
        public GLGroup createFromParcel(Parcel in) {
            return new GLGroup(in);
        }

        @Override
        public GLGroup[] newArray(int size) {
            return new GLGroup[size];
        }
    };
}