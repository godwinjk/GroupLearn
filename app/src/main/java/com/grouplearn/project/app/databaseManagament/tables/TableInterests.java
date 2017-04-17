package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;


/**
 * Created by Godwin on 16-04-2017 12:38 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class TableInterests {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_INTERESTS;
    public static final String TABLE_NAME = "INTERESTS_DETAILS";

    public static final String INTEREST_USER_ID = "INTEREST_USER_ID";
    public static final String INTEREST_ID = "INTEREST_ID";
    public static final String INTEREST_NAME = "INTEREST_NAME";
    public static final String CREATED_TIME = "CREATED_TIME";

    public static final String UPDATED_TIME = "UPDATED_TIME";

    public static final String INTEREST_ICON_URI = "INTEREST_ICON_URI";
    public static final String INTEREST_TYPE = "INTEREST_TYPE";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "( %s text," +
                    "%s text ,%s text," +
                    "%s text,%s text, " +
                    "%s text,%s text)", TABLE_NAME,
            INTEREST_ID,
            INTEREST_USER_ID, INTEREST_NAME,
            INTEREST_TYPE, INTEREST_ICON_URI,
            CREATED_TIME, UPDATED_TIME);
}
