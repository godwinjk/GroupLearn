package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;

/**
 * Created by Godwin on 09-05-2016 10:49 for Group Learn application 17:10 for GroupLearn.
 * @author : Godwin Joseph Kurinjikattu
 */
public class TableMessage {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_CHATS;
    public static final String TABLE_NAME = "CHAT_DETAILS";

    public static final String GROUP_ID = "GROUP_ID";
    public static final String CHAT_ID = "CHAT_ID";
    public static final String TEMP_ID = "TEMP_ID";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    public static final String READ_STATUS = "READ_STATUS";
    public static final String SENT_STATUS = "SENT_STATUS";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String LOCAL_FILE_PATH = "LOCAL_FILE_PATH";
    public static final String CLOUD_FILE_PATH = "CLOUD_FILE_PATH";
    public static final String TIME_STAMP = "TIME_STAMP";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "(%s text ,%s text, %s text," +
                    "%s text,%s text,%s text," +
                    "%s text,%s text,%s text," +
                    "%s text,%s text,%s text)", TABLE_NAME,
            CHAT_ID, GROUP_ID, USER_ID,
            CHAT_MESSAGE, SENT_STATUS, TEMP_ID,
            MESSAGE_TYPE, USER_NAME, READ_STATUS,
            LOCAL_FILE_PATH, CLOUD_FILE_PATH,TIME_STAMP);


}
