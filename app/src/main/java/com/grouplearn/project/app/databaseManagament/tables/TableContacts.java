package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;

/**
 * Created by Godwin Joseph on 07-06-2016 21:51 for Group Learn application.
 */
public class TableContacts {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_CONTACTS;
    public static final String TABLE_NAME = "CONTACTS_DETAILS";

    public static final String CONTACT_ID = "CONTACT_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CREATED_TIME = "CREATED_TIME";

    public static final String UPDATED_TIME = "UPDATED_TIME";

    public static final String CONTACT_ICON_ID = "CONTACT_ICON_ID";
    public static final String CONTACT_STATUS = "CONTACT_STATUS";
    public static final String CONTACT_NUMBER = "CONTACT_NUMBER";
    public static final String CONTACT_FOUND = "CONTACT_FOUND";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "(%s text , %s text," +
                    "%s text ,%s text,%s text, " +
                    "%s text,%s text,%s text)", TABLE_NAME,
            CONTACT_ID, CONTACT_NAME,
            CREATED_TIME, UPDATED_TIME, CONTACT_FOUND,
            CONTACT_ICON_ID, CONTACT_STATUS, CONTACT_NUMBER);
}
