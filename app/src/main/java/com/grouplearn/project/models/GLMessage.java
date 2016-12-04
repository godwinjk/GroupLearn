package com.grouplearn.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Godwin Joseph on 25-09-2016 13:31 for Group Learn application.
 */
public class GLMessage extends BaseModel implements Parcelable {
    String messageBody;
    int messageType;
    long senderId;
    long receiverId;
    String senderName;
    long messageId;
    long tempId;
    int messageStatus;
    int readStatus = 0;
    int sentStatus = 0;

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public GLMessage() {
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public long getTempId() {
        return tempId;
    }

    public void setTempId(long tempId) {
        this.tempId = tempId;
    }

    protected GLMessage(Parcel in) {
        super(in);
        messageBody = in.readString();
        messageType = in.readInt();
        senderId = in.readLong();
        receiverId = in.readLong();
        senderName = in.readString();
        messageId = in.readLong();
        tempId = in.readLong();
        messageStatus = in.readInt();
        readStatus = in.readInt();
        sentStatus = in.readInt();
    }


    @Override
    public int describeContents() {
        super.describeContents();
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(messageBody);
        dest.writeInt(messageType);
        dest.writeLong(senderId);
        dest.writeLong(receiverId);
        dest.writeString(senderName);
        dest.writeLong(messageId);
        dest.writeLong(tempId);
        dest.writeInt(messageStatus);
        dest.writeInt(readStatus);
        dest.writeInt(sentStatus);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GLMessage> CREATOR = new Parcelable.Creator<GLMessage>() {
        @Override
        public GLMessage createFromParcel(Parcel in) {
            return new GLMessage(in);
        }

        @Override
        public GLMessage[] newArray(int size) {
            return new GLMessage[size];
        }
    };
}