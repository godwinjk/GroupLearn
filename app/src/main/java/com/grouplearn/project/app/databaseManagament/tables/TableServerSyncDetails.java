package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;
import com.grouplearn.project.app.uiManagement.databaseHelper.DataBaseHelper;

/**
 * Created by Dell on 02-03-2016.
 */
public class TableServerSyncDetails {

    public static final int TABLE_INDEX = DatabaseConstants.TABLE_SERVER_SYNC_DETAILS;
    public static final String TABLE_NAME = "SERVER_SYNC_DETAILS";

    public static final String API_ID = "api_id";
    public static final String LAST_UPDATED_TIME = "last_updated_time";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);

    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "(%s integer PRIMARY KEY ,%s text)", TABLE_NAME,
            API_ID, LAST_UPDATED_TIME);
}
