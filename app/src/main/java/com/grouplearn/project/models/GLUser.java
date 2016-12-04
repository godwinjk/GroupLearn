package com.grouplearn.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 07-10-2016 18:46 for GroupLearn application.
 */

public class GLUser extends BaseModel implements Parcelable {
    long userId;
    String userName;
    String userStatus;
    String userEmail;
    String userDisplayName;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public GLUser() {
        super();
    }

    protected GLUser(Parcel in) {
        super(in);
        userId = in.readLong();
        userName = in.readString();
        userStatus = in.readString();
        userEmail = in.readString();
        userDisplayName = in.readString();
    }

    @Override
    public int describeContents() {
        super.describeContents();
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(userId);
        dest.writeString(userName);
        dest.writeString(userStatus);
        dest.writeString(userEmail);
        dest.writeString(userDisplayName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLUser> CREATOR = new Parcelable.Creator<GLUser>() {
        @Override
        public GLUser createFromParcel(Parcel in) {
            return new GLUser(in);
        }

        @Override
        public GLUser[] newArray(int size) {
            return new GLUser[size];
        }
    };
}