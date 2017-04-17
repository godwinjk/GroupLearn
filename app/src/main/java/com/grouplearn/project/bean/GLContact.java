package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 16:33 for Group Learn application.
 */
public class GLContact extends BaseModel implements Parcelable {
    private String contactName;
    private long contactUserId;
    private String contactStatus = null;
    private String contactMailId;
    private int privacy;
    private int status;
    private String iconUrl;
    private int action;
    private ArrayList<GLInterest> interests = new ArrayList<>();
    private ArrayList<GLInterest> skills = new ArrayList<>();

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public long getContactUserId() {
        return contactUserId;
    }

    public void setContactUserId(long contactUserId) {
        this.contactUserId = contactUserId;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }

    public int getPrivacy() {
        return privacy;
    }

    public void setPrivacy(int privacy) {
        this.privacy = privacy;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContactMailId() {
        return contactMailId;
    }

    public void setContactMailId(String contactMailId) {
        this.contactMailId = contactMailId;
    }

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    }

    public ArrayList<GLInterest> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<GLInterest> skills) {
        this.skills = skills;
    }

    public GLContact() {
    }

    protected GLContact(Parcel in) {
        contactName = in.readString();
        contactUserId = in.readLong();
        contactStatus = in.readString();
        contactMailId = in.readString();
        privacy = in.readInt();
        status = in.readInt();
        iconUrl = in.readString();
        if (in.readByte() == 0x01) {
            interests = new ArrayList<GLInterest>();
            in.readList(interests, GLInterest.class.getClassLoader());
        } else {
            interests = null;
        }
        if (in.readByte() == 0x01) {
            skills = new ArrayList<GLInterest>();
            in.readList(skills, GLInterest.class.getClassLoader());
        } else {
            skills = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contactName);
        dest.writeLong(contactUserId);
        dest.writeString(contactStatus);
        dest.writeString(contactMailId);
        dest.writeInt(privacy);
        dest.writeInt(status);
        dest.writeString(iconUrl);
        if (interests == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(interests);
        }
        if (skills == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(skills);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLContact> CREATOR = new Parcelable.Creator<GLContact>() {
        @Override
        public GLContact createFromParcel(Parcel in) {
            return new GLContact(in);
        }

        @Override
        public GLContact[] newArray(int size) {
            return new GLContact[size];
        }
    };
}