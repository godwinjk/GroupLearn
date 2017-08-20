package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableMessage;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.ChatUtilities;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 25-09-2016 20:46 for Group Learn application.
 */
public class ChatDbHelper extends DataBaseHelper {
    ContentResolver contentResolver;

    public ChatDbHelper(Context mContext) {
        super(mContext);
        contentResolver = mContext.getContentResolver();
    }

    public void addMessageToDb(GLMessage messageModel) {
        ContentValues cv = getContentValues(messageModel);
        contentResolver.insert(TableMessage.CONTENT_URI, cv);
    }

    public void addMessageToDb(ArrayList<GLMessage> messageModels) {
        BigDecimal updatedTime = new BigDecimal("0");
        for (GLMessage model : messageModels) {

            if (updateMessage(model) <= 0) {
                addMessageToDb(model);
            }
            BigDecimal lastUpdatedTime = new BigDecimal(model.getTimeStamp());
            updatedTime = updatedTime.max(lastUpdatedTime);
        }
        new ServerSyncTimes(mContext).updateLastServerSyncTimeForAPICall(ServerSyncTimes.MESSAGE_GET, updatedTime.toPlainString());
    }

    public long updateMessageInDb(GLMessage model) {
        String where = TableMessage.CHAT_ID + "=" + model.getMessageId();
        ContentValues cv = getContentValues(model);

        return contentResolver.update(TableMessage.CONTENT_URI, cv, where, null);
    }

    public long updateMessage(GLMessage model) {
        String where = TableMessage.CHAT_ID + "=" + model.getMessageId();
        ContentValues cv = new ContentValues();
        cv.put(TableMessage.TIME_STAMP, model.getTimeStamp());
        return contentResolver.update(TableMessage.CONTENT_URI, cv, where, null);
    }

    public long updateMessageByTempId(GLMessage model) {
        String where = TableMessage.TEMP_ID + "=" + model.getTempId();
        ContentValues cv = getContentValues(model);
        cv.put(TableMessage.USER_NAME, model.getSenderName());
        return contentResolver.update(TableMessage.CONTENT_URI, cv, where, null);
    }

    public void updateAllRead(long groupId) {
        String where = TableMessage.GROUP_ID + "='" + groupId + "'";
        ContentValues cv = new ContentValues();
        cv.put(TableMessage.READ_STATUS, ChatUtilities.READ);
        contentResolver.update(TableMessage.CONTENT_URI, cv, where, null);
    }

    public void updateAllSent(ArrayList<GLMessage> messageModels) {
        for (GLMessage model : messageModels) {
            String where = TableMessage.TEMP_ID + "=" + model.getTempId();
            ContentValues cv = new ContentValues();
            cv.put(TableMessage.SENT_STATUS, ChatUtilities.SENT_SUCCESS);
            cv.put(TableMessage.CHAT_ID, model.getMessageId());
            contentResolver.update(TableMessage.CONTENT_URI, cv, where, null);
        }
    }

    public ArrayList<GLMessage> getMessages(long groupId) {
        updateAllRead(groupId);

        ArrayList<GLMessage> messageModels = new ArrayList<>();
        String where = TableMessage.GROUP_ID + "='" + groupId + "'";
        String sort = TableMessage.TIME_STAMP + " ASC";
        Cursor cursor = contentResolver.query(TableMessage.CONTENT_URI, null, where, null, sort);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                messageModels.add(getMessageFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return messageModels;
    }

    public GLMessage getLastMessages(long groupId) {
        GLMessage messageModel = null;
        String where = TableMessage.GROUP_ID + "='" + groupId + "'";
        String sort = TableMessage.TIME_STAMP + " DESC";
        Cursor cursor = contentResolver.query(TableMessage.CONTENT_URI, null, where, null, sort);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            messageModel = getMessageFromCursor(cursor);
        }
        return messageModel;
    }

    public long getNumberOfNewMessage(long groupId) {
        String where = TableMessage.GROUP_ID + "='" + groupId + "' AND " + TableMessage.READ_STATUS + "=" + ChatUtilities.NOT_READ;
        return getNumberOfRowsInDatabase(TableMessage.TABLE_NAME, where);
    }

    public long isTempIdExist(long tempId) {
        String where = TableMessage.TEMP_ID + "=" + tempId;
        return getNumberOfRowsInDatabase(TableMessage.TABLE_NAME, where);
    }

    private ContentValues getContentValues(GLMessage model) {
        ContentValues cv = new ContentValues();
        cv.put(TableMessage.USER_NAME, model.getSenderName());
        cv.put(TableMessage.USER_ID, model.getSenderId());
        cv.put(TableMessage.GROUP_ID, model.getReceiverId());
        cv.put(TableMessage.CHAT_ID, model.getMessageId());
        cv.put(TableMessage.SENT_STATUS, model.getSentStatus());
        cv.put(TableMessage.CHAT_MESSAGE, model.getMessageBody());
        cv.put(TableMessage.MESSAGE_TYPE, model.getMessageType());
        cv.put(TableMessage.READ_STATUS, model.getReadStatus());
        cv.put(TableMessage.CLOUD_FILE_PATH, model.getCloudFilePath());
        cv.put(TableMessage.LOCAL_FILE_PATH, model.getLocalFilePath());
        cv.put(TableMessage.READ_STATUS, model.getReadStatus());
        cv.put(TableMessage.TIME_STAMP, model.getTimeStamp());
        cv.put(TableMessage.TEMP_ID, model.getTempId());
        return cv;
    }

    private GLMessage getMessageFromCursor(Cursor cursor) {
        GLMessage model = new GLMessage();
        String message = cursor.getString(cursor.getColumnIndex(TableMessage.CHAT_MESSAGE));

        model.setSenderName(cursor.getString(cursor.getColumnIndex(TableMessage.USER_NAME)));
        model.setMessageId(cursor.getLong(cursor.getColumnIndex(TableMessage.CHAT_ID)));
        model.setReceiverId(cursor.getLong(cursor.getColumnIndex(TableMessage.GROUP_ID)));
        model.setSenderId(cursor.getLong(cursor.getColumnIndex(TableMessage.USER_ID)));
        model.setMessageType(cursor.getInt(cursor.getColumnIndex(TableMessage.MESSAGE_TYPE)));
        model.setMessageStatus(cursor.getInt(cursor.getColumnIndex(TableMessage.SENT_STATUS)));
        model.setTimeStamp(cursor.getString(cursor.getColumnIndex(TableMessage.TIME_STAMP)));
        model.setMessageBody(cursor.getString(cursor.getColumnIndex(TableMessage.CHAT_MESSAGE)));
        model.setSentStatus(cursor.getInt(cursor.getColumnIndex(TableMessage.SENT_STATUS)));
        model.setReadStatus(cursor.getInt(cursor.getColumnIndex(TableMessage.READ_STATUS)));
        model.setCloudFilePath(cursor.getString(cursor.getColumnIndex(TableMessage.CLOUD_FILE_PATH)));
        model.setLocalFilePath(cursor.getString(cursor.getColumnIndex(TableMessage.LOCAL_FILE_PATH)));
        model.setTempId(cursor.getLong(cursor.getColumnIndex(TableMessage.TEMP_ID)));

        return model;
    }

    public Object getUnSyncedMessages() {
        ArrayList<GLMessage> messageModels = new ArrayList<>();
        String where = TableMessage.SENT_STATUS
                + "="
                + ChatUtilities.NOT_SENT
                + " AND "
                + TableMessage.MESSAGE_TYPE
                + "="
                + GLMessage.MESSAG;
        Cursor cursor = contentResolver.query(TableMessage.CONTENT_URI, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                messageModels.add(getMessageFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return messageModels;
    }

    public void deleteMessages(ArrayList<GLMessage> messages) {
        for (GLMessage message : messages) {
            deleteMessage(message);
        }
    }

    public void deleteMessage(GLMessage message) {
        String where = TableMessage.CHAT_ID + "=" + message.getMessageId();
        contentResolver.delete(TableMessage.CONTENT_URI, where, null);
    }
}
