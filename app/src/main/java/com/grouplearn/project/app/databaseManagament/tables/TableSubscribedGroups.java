package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;

/**
 * Created by Godwin Joseph on 09-05-2016 10:55 for Group Learn application.
 */
public class TableSubscribedGroups {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_GROUPS;
    public static final String TABLE_NAME = "SUBSCRIBED_GROUPS_DETAILS";

    public static final String GROUP_ID = "GROUP_ID";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String GROUP_DESCRIPTION = "GROUP_DESCRIPTION";
    public static final String NEW_MSG_CONT = "NEW_MSG_CONT";
    public static final String CREATED_TIME = "CREATED_TIME";
    public static final String GROUP_ICON_URI = "GROUP_ICON_URI";

    public static final String UPDATED_TIME = "UPDATED_TIME";

    public static final String GROUP_ICON_ID = "GROUP_ICON_ID";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "(%s text , %s text, %s text,%s text," +
                    "%s text ,%s text,%s text, %s text)", TABLE_NAME,
            GROUP_ID, GROUP_NAME, GROUP_DESCRIPTION, GROUP_ICON_URI,
            NEW_MSG_CONT, CREATED_TIME, UPDATED_TIME, GROUP_ICON_ID);
}
