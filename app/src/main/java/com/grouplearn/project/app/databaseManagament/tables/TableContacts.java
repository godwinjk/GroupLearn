package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;

/**
 * Created by Godwin Joseph on 07-06-2016 21:51 for Group Learn application.
 */
public class TableContacts {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_CONTACTS;
    public static final String TABLE_NAME = "CONTACTS_DETAILS";

    public static final String CONTACT_USER_ID = "CONTACT_USER_ID";
    public static final String CONTACT_MAIL_ID = "CONTACT_MAIL_ID";
    public static final String CONTACT_NAME = "CONTACT_NAME";
    public static final String CREATED_TIME = "CREATED_TIME";

    public static final String UPDATED_TIME = "UPDATED_TIME";

    public static final String CONTACT_ICON_URI = "CONTACT_ICON_URI";
    public static final String CONTACT_STATUS = "CONTACT_STATUS";

        public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
        public static final String SQL_CREATE_TABLE = String.format("create table %s"
                        + "( %s text," +
                        "%s text ,%s text," +
                        "%s text,%s text, " +
                        "%s text,%s text)", TABLE_NAME,
                CONTACT_USER_ID,
                CONTACT_MAIL_ID, CONTACT_NAME,
                CONTACT_ICON_URI, CONTACT_STATUS,
                CREATED_TIME, UPDATED_TIME);
}
