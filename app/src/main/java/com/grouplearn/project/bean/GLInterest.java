package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by WiSilica on 04-12-2016 20:48 for GroupLearn application.
 */

public class GLInterest extends BaseModel implements Parcelable {
    long interestId;
    String interestName;
    long userId;
    private String iconUrl;
    private int interestType;
    public static final int SKILL = 1;
    public static final int INTEREST = 0;
    public static final int BOTH = 2;

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getInterestType() {
        return interestType;
    }

    public boolean isSkill() {
        return interestType == SKILL;
    }

    public void setInterestType(int interestType) {
        this.interestType = interestType;
    }

    public GLInterest() {
    }

    protected GLInterest(Parcel in) {
        interestId = in.readLong();
        interestName = in.readString();
        userId = in.readLong();
        iconUrl = in.readString();
        interestType = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(interestId);
        dest.writeString(interestName);
        dest.writeLong(userId);
        dest.writeString(iconUrl);
        dest.writeInt(interestType);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLInterest> CREATOR = new Parcelable.Creator<GLInterest>() {
        @Override
        public GLInterest createFromParcel(Parcel in) {
            return new GLInterest(in);
        }

        @Override
        public GLInterest[] newArray(int size) {
            return new GLInterest[size];
        }
    };
}