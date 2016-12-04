package com.grouplearn.project.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 07-06-2016 16:33 for Group Learn application.
 */
public class GLContact extends BaseModel implements Parcelable {
    String contactName;
    String contactUniqueId;
    String contactIconId;
    String contactStatus;
    String contactNumber;
    String contactMailId;
    int privacy;
    int status;
    Bitmap contactImage;
    private String contactId;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactUniqueId() {
        return contactUniqueId;
    }

    public void setContactUniqueId(String contactUniqueId) {
        this.contactUniqueId = contactUniqueId;
    }

    public String getContactIconId() {
        return contactIconId;
    }

    public void setContactIconId(String contactIconId) {
        this.contactIconId = contactIconId;
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

    public Bitmap getContactImage() {
        return contactImage;
    }

    public void setContactImage(Bitmap contactImage) {
        this.contactImage = contactImage;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactMailId() {
        return contactMailId;
    }

    public void setContactMailId(String contactMailId) {
        this.contactMailId = contactMailId;
    }

    protected GLContact(Parcel in) {
        super(in);
        contactName = in.readString();
        contactMailId = in.readString();
        contactUniqueId = in.readString();
        contactIconId = in.readString();
        contactStatus = in.readString();
        contactNumber = in.readString();
        privacy = in.readInt();
        status = in.readInt();
        contactImage = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        contactId = in.readString();
    }

    @Override
    public int describeContents() {
        super.describeContents();
        return 0;
    }

    public GLContact() {
        super();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(contactName);
        dest.writeString(contactMailId);
        dest.writeString(contactUniqueId);
        dest.writeString(contactIconId);
        dest.writeString(contactStatus);
        dest.writeString(contactNumber);
        dest.writeInt(privacy);
        dest.writeInt(status);
        dest.writeValue(contactImage);
        dest.writeString(contactId);
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