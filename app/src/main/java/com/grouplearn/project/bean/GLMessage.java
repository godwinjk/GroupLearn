package com.grouplearn.project.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Godwin Joseph on 25-09-2016 13:31 for Group Learn application.
 */
public class GLMessage extends BaseModel implements Parcelable {
    public static final int MESSAG = 1;
    public static final int MESSAGE_OTHER = -1;

    public static final int IMAGE = 2;
    public static final int MAGE_OTHER = -2;

    public static final int VIDEO = 3;
    public static final int VIDEO_OTHER = -3;

    public static final int DOCUMENT = 4;
    public static final int DOCUMENT_OTHER = -4;

    public static final int GIF = -5;
    public static final int GIF_OTHER = -5;

    private String messageBody;
    private int messageType;
    private long senderId;
    private long receiverId;
    private String senderName;
    private long messageId;
    private long tempId;
    private int messageStatus;
    private int readStatus = 0;
    private int sentStatus = 0;
    private String localFilePath = null;
    private String cloudFilePath = null;
    private File file = null;
    private int progress = 0;
    private boolean isOperationOnProgress = false;
    private boolean isSelected = false;
    private String downloadSize = "";

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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

    public boolean isMe(long userId) {
        return senderId == userId;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isOperationOnProgress() {
        return isOperationOnProgress;
    }

    public void setOperationOnProgress(boolean operationOnProgress) {
        isOperationOnProgress = operationOnProgress;
    }

    public String getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(String downloadSize) {
        this.downloadSize = downloadSize;
    }

    public File getFile() {
        if (file == null && localFilePath != null)
            file = new File(localFilePath);
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCloudFilePath() {
        return cloudFilePath;
    }

    public void setCloudFilePath(String cloudFilePath) {
        this.cloudFilePath = cloudFilePath;
    }


    protected GLMessage(Parcel in) {
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
        localFilePath = in.readString();
        cloudFilePath = in.readString();
        downloadSize = in.readString();
        progress = in.readInt();
        isOperationOnProgress = in.readByte() != 0x00;
        isSelected = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
        dest.writeString(localFilePath);
        dest.writeString(cloudFilePath);
        dest.writeString(downloadSize);
        dest.writeInt(progress);
        dest.writeByte((byte) (isOperationOnProgress ? 0x01 : 0x00));
        dest.writeByte((byte) (isSelected ? 0x01 : 0x00));
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