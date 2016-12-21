package com.grouplearn.project.app.databaseManagament;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableGroups;
import com.grouplearn.project.app.databaseManagament.tables.TableSubscribedGroups;

/**
 * Created by Godwin Joseph on 07-05-2016 13:05 for Group Learn application.
 */
public class DatabaseHandler {
    Context mContext;
    ContentResolver mContentResolver;

    public DatabaseHandler(Context mContext) {
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
    }

    public Cursor getAllSubscribedGroups() {
        Cursor cursor = mContentResolver.query(TableSubscribedGroups.CONTENT_URI, null, null, null, null);
        return cursor;
    }

    public Cursor getAllGroupNames(String where) {
        Cursor cursor = mContentResolver.query(TableGroups.CONTENT_URI, null, where, null, null);
        return cursor;
    }

    public Cursor getAllGroupWithName(String topicName) {
        String where = TableGroups.GROUP_NAME + " LIKE '%" + topicName + "%'";
        Cursor cursor = mContentResolver.query(TableGroups.CONTENT_URI, null, where, null, null);
        return cursor;
    }

    public Cursor getSubscribedGroupInfo(long groupUniqueId) {
        String where = TableSubscribedGroups.GROUP_ID + " = '" + groupUniqueId + "'";
        Cursor cursor = mContentResolver.query(TableSubscribedGroups.CONTENT_URI, null, where, null, null);
        return cursor;
    }
}
