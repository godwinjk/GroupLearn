package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableChat;
import com.grouplearn.project.models.MessageModel;
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

    public void addMessageToDb(MessageModel messageModel) {
        ContentValues cv = getContentValues(messageModel);
        contentResolver.insert(TableChat.CONTENT_URI, cv);
    }

    public void addMessageToDb(ArrayList<MessageModel> messageModels) {
        BigDecimal updatedTime = new BigDecimal("0");
        for (MessageModel model : messageModels) {

            if (updateMessageInDb(model) <= 0) {
                addMessageToDb(model);
            }
            BigDecimal lastUpdatedTime = new BigDecimal(model.getTimeStamp());
            updatedTime = updatedTime.max(lastUpdatedTime);
        }
        new ServerSyncTimes(mContext).updateLastServerSyncTimeForAPICall(ServerSyncTimes.MESSAGE_GET, updatedTime.longValue());
    }

    public long updateMessageInDb(MessageModel model) {
        String where = TableChat.CHAT_ID + "=" + model.getMessageId();
        ContentValues cv = new ContentValues();
        cv.put(TableChat.USER_NAME, model.getSenderName());
        return contentResolver.update(TableChat.CONTENT_URI, cv, where, null);
    }

    public void updateAllRead(long groupId) {
        String where = TableChat.GROUP_ID + "='" + groupId + "'";
        ContentValues cv = new ContentValues();
        cv.put(TableChat.READ_STATUS, ChatUtilities.READ);
        contentResolver.update(TableChat.CONTENT_URI, cv, where, null);
    }

    public void updateAllSent(ArrayList<MessageModel> messageModels) {
        for (MessageModel model : messageModels) {
            String where = TableChat.TEMP_ID + "=" + model.getTempId();
            ContentValues cv = new ContentValues();
            cv.put(TableChat.SENT_STATUS, ChatUtilities.SENT_SUCCESS);
            cv.put(TableChat.CHAT_ID, model.getMessageId());
            contentResolver.update(TableChat.CONTENT_URI, cv, where, null);
        }
    }

    public ArrayList<MessageModel> getMessages(long groupId) {
        updateAllRead(groupId);

        ArrayList<MessageModel> messageModels = new ArrayList<>();
        String where = TableChat.GROUP_ID + "='" + groupId + "'";
        String sort = TableChat.TIME_STAMP + " ASC";
        Cursor cursor = contentResolver.query(TableChat.CONTENT_URI, null, where, null, sort);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                messageModels.add(getMessageFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return messageModels;
    }

    public MessageModel getLastMessages(long groupId) {
        MessageModel messageModel = null;
        String where = TableChat.GROUP_ID + "='" + groupId + "'";
        String sort = TableChat.TIME_STAMP + " DESC";
        Cursor cursor = contentResolver.query(TableChat.CONTENT_URI, null, where, null, sort);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            messageModel = getMessageFromCursor(cursor);
        }
        return messageModel;
    }

    public long getNumberOfNewMessage(long groupId) {
        String where = TableChat.GROUP_ID + "='" + groupId + "' AND " + TableChat.READ_STATUS + "=" + ChatUtilities.NOT_READ;
        return getNumberOfRowsInDatabase(TableChat.TABLE_NAME, where);
    }

    private ContentValues getContentValues(MessageModel model) {
        ContentValues cv = new ContentValues();
        cv.put(TableChat.USER_NAME, model.getSenderName());
        cv.put(TableChat.USER_ID, model.getSenderId());
        cv.put(TableChat.GROUP_ID, model.getReceiverId());
        cv.put(TableChat.CHAT_ID, model.getMessageId());
        cv.put(TableChat.SENT_STATUS, model.getSentStatus());
        cv.put(TableChat.CHAT_MESSAGE, model.getMessageBody());
        cv.put(TableChat.MESSAGE_TYPE, model.getMessageType());
        cv.put(TableChat.SENT_STATUS, model.getSentStatus());
        cv.put(TableChat.READ_STATUS, model.getReadStatus());
        cv.put(TableChat.TIME_STAMP, model.getTimeStamp());
        cv.put(TableChat.TEMP_ID, model.getTempId());
        return cv;
    }

    public long isTempIdExist(long tempId) {
        String where = TableChat.TEMP_ID + "=" + tempId;
        return getNumberOfRowsInDatabase(TableChat.TABLE_NAME, where);
    }

    private MessageModel getMessageFromCursor(Cursor cursor) {
        MessageModel model = new MessageModel();
        String message = cursor.getString(cursor.getColumnIndex(TableChat.CHAT_MESSAGE));

        model.setSenderName(cursor.getString(cursor.getColumnIndex(TableChat.USER_NAME)));
        model.setMessageId(cursor.getLong(cursor.getColumnIndex(TableChat.CHAT_ID)));
        model.setReceiverId(cursor.getLong(cursor.getColumnIndex(TableChat.GROUP_ID)));
        model.setSenderId(cursor.getLong(cursor.getColumnIndex(TableChat.USER_ID)));
        model.setMessageType(cursor.getInt(cursor.getColumnIndex(TableChat.MESSAGE_TYPE)));
        model.setMessageStatus(cursor.getInt(cursor.getColumnIndex(TableChat.SENT_STATUS)));
        model.setTimeStamp(cursor.getString(cursor.getColumnIndex(TableChat.TIME_STAMP)));
        model.setMessageBody(cursor.getString(cursor.getColumnIndex(TableChat.CHAT_MESSAGE)));
        model.setSentStatus(cursor.getInt(cursor.getColumnIndex(TableChat.SENT_STATUS)));
        model.setReadStatus(cursor.getInt(cursor.getColumnIndex(TableChat.READ_STATUS)));
        model.setTempId(cursor.getLong(cursor.getColumnIndex(TableChat.TEMP_ID)));

        return model;
    }

    public Object getUnSyncedMessages() {
        ArrayList<MessageModel> messageModels = new ArrayList<>();
        String where = TableChat.SENT_STATUS + "=" + ChatUtilities.NOT_SENT;
        Cursor cursor = contentResolver.query(TableChat.CONTENT_URI, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                messageModels.add(getMessageFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return messageModels;
    }
}
