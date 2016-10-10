package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.grouplearn.project.app.MyApplication;
import com.grouplearn.project.app.databaseManagament.DatabaseProvider;
import com.grouplearn.project.app.databaseManagament.tables.TableChat;
import com.grouplearn.project.app.databaseManagament.tables.TableContacts;
import com.grouplearn.project.app.databaseManagament.tables.TableGroups;
import com.grouplearn.project.app.databaseManagament.tables.TableServerSyncDetails;
import com.grouplearn.project.app.databaseManagament.tables.TableSubscribedGroups;

/**
 * Created by Godwin Joseph on 08-09-2016 15:01 for Group Learn application.
 */
public class DataBaseHelper {
    Context mContext;

    public DataBaseHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void clearDatabase() {
        ContentResolver mResolver = MyApplication.getAppContext().getContentResolver();
        mResolver.delete(TableChat.CONTENT_URI, null, null);
        mResolver.delete(TableSubscribedGroups.CONTENT_URI, null, null);
        mResolver.delete(TableContacts.CONTENT_URI, null, null);
        mResolver.delete(TableGroups.CONTENT_URI, null, null);
        mResolver.delete(TableServerSyncDetails.CONTENT_URI, null, null);

    }

    public long getNumberOfRowsInDatabase(String tableName, String where) {
        DatabaseProvider.DbHelper mDbHelper = new DatabaseProvider.DbHelper(mContext);
        SQLiteDatabase mSqldb = mDbHelper.getReadableDatabase();
        long cnt = DatabaseUtils.queryNumEntries(mSqldb, tableName, where);
        mSqldb.close();
        return cnt;
    }

}
