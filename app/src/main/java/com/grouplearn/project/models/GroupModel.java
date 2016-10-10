package com.grouplearn.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 18-05-2016 23:40 for Group Learn application.
 */
public class GroupModel extends BaseModel implements Parcelable {
    String groupUniqueId;
    String groupName;
    String groupDescription;
    String groupIconId;
    String groupAdminId;
    int privacy;
    String groupCreatedTime;
    String groupUpdatedTime;
    int newMessage = 0;
    String lastMessage = null;
    MessageModel model;

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

    public String getGroupUniqueId() {
        return groupUniqueId;
    }

    public void setGroupUniqueId(String groupUniqueId) {
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

    public MessageModel geMessageModel() {
        return model;
    }

    public void setMessageModel(MessageModel model) {
        this.model = model;
    }

    protected GroupModel(Parcel in) {
        super(in);
        groupUniqueId = in.readString();
        groupName = in.readString();
        groupDescription = in.readString();
        groupIconId = in.readString();
        groupAdminId = in.readString();
        privacy = in.readInt();
        groupCreatedTime = in.readString();
        groupUpdatedTime = in.readString();
        newMessage = in.readInt();
        lastMessage = in.readString();
        model = (MessageModel) in.readValue(MessageModel.class.getClassLoader());
    }

    public GroupModel() {
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
        dest.writeString(groupUniqueId);
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
    public static final Parcelable.Creator<GroupModel> CREATOR = new Parcelable.Creator<GroupModel>() {
        @Override
        public GroupModel createFromParcel(Parcel in) {
            return new GroupModel(in);
        }

        @Override
        public GroupModel[] newArray(int size) {
            return new GroupModel[size];
        }
    };
}