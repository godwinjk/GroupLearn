package com.grouplearn.project.app.databaseManagament.tables;

import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;

/**
 * Created by WiSilica on 17-12-2016 22:19 for GroupLearn application.
 */

public class TableCourse {
    public static final int TABLE_INDEX = DatabaseConstants.TABLE_COURSE;
    public static final String TABLE_NAME = "COURSE_DETAILS";

    public static final String COURSE_ID = "COURSE_ID";
    public static final String COURSE_NAME = "COURSE_NAME";
    public static final String COURSE_ICON_ID = "COURSE_ICON_ID";
    public static final String COURSE_USER_ID = "COURSE_USER_ID";
    public static final String COURSE_DESCRIPTION = "COURSE_DESCRIPTION";
    public static final String COURSE_CONTACT = "COURSE_CONTACT";
    public static final String COURSE_URL = "COURSE_URL";
    public static final String COURSE_GROUP_NAME = "COURSE_GROUP_NAME";
    public static final String COURSE_GROUP_DESCRIPTION = "COURSE_GROUP_DESCRIPTION";
    public static final String COURSE_GROUP_ICON_ID = "COURSE_GROUP_ICON_ID";
    public static final String COURSE_GROUP_ID = "COURSE_GROUP_ID";
    public static final String COURSE_STATUS = "COURSE_STATUS";

    public static final String CREATED_TIME = "CREATED_TIME";
    public static final String UPDATED_TIME = "UPDATED_TIME";

    public static final Uri CONTENT_URI = Uri.parse("content://" + DatabaseConstants.AUTHORITY + "/" + TABLE_NAME);
    public static final String SQL_CREATE_TABLE = String.format("create table %s"
                    + "(%s text , %s text,%s text,%s text,%s text," +
                    "%s text , %s text,%s text,%s text,%s text," +
                    "%s text ,%s text, %s text, %s text)", TABLE_NAME,
            COURSE_ID, COURSE_NAME, COURSE_ICON_ID, COURSE_USER_ID, COURSE_DESCRIPTION,
            COURSE_CONTACT, COURSE_URL, COURSE_GROUP_NAME, COURSE_GROUP_DESCRIPTION, COURSE_GROUP_ICON_ID,
            COURSE_GROUP_ID, COURSE_STATUS, CREATED_TIME, UPDATED_TIME);
}
