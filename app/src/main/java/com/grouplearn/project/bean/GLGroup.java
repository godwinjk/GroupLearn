package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 18-05-2016 23:40 for Group Learn application.
 */
public class GLGroup extends BaseModel implements Parcelable, Comparable<GLGroup> {
    private long groupUniqueId;
    private String groupName;
    private String groupDescription;
    private String groupIconId;
    private long groupAdminId;
    private int privacy;
    private String groupCreatedTime;
    private String groupUpdatedTime;
    private int newMessage = 0;
    private String lastMessage = null;
    private GLMessage model;
    private String iconUrl;
    private boolean isMine = true;

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

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

    public long getGroupAdminId() {
        return groupAdminId;
    }

    public void setGroupAdminId(long groupAdminId) {
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
        groupAdminId = in.readLong();
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
        dest.writeLong(groupAdminId);
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

    @Override
    public int compareTo(GLGroup group) {
        if (this.geMessageModel() != null && this.geMessageModel().getTimeStamp() != null || group.geMessageModel() != null && group.geMessageModel().getTimeStamp() != null) {
            if (this.geMessageModel() == null)
                return 1;
            else if (group.geMessageModel() == null)
                return -1;
            else
                return group.geMessageModel().getTimeStamp().compareTo(this.geMessageModel().getTimeStamp());
        }
        return 1;
    }
}