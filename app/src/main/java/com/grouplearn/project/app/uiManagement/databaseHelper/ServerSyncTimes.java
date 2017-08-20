package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.tables.TableServerSyncDetails;


/**
 * Created by Godwin Joseph on 07-07-2016 17:52 for Cms_Android application.
 */
public class ServerSyncTimes {

    public static final int LOGIN_TIME = 1000;
    public static final int MESSAGE_GET = 3000;
    public static final int GET_ALL_INVITATION = 3001;
    public static final int GET_ALL_REQUESTS = 3002;
    public static final int CONTACT_GET = 3003;
    public static final int CONTACT_REQUEST_GET = 3004;

    ContentResolver mContentResolver;
    Context mContext;

    public ServerSyncTimes(Context mContext) {
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
    }

    public String getLastUpdatedTime(int apiId) {
        String where = TableServerSyncDetails.API_ID + "=" + apiId;
        Cursor cursor = mContentResolver.query(TableServerSyncDetails.CONTENT_URI, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
//            int id = cursor.getInt(cursor.getColumnIndex(TableServerSyncDetails.API_ID));
            String time = cursor.getString(cursor.getColumnIndex(TableServerSyncDetails.LAST_UPDATED_TIME));
            cursor.close();
            return time;
        } else
            return "0.0";

    }

    public void updateLastServerSyncTimeForAPICall(int apiId, String lastUpdatedTime) {
        ContentValues cv = new ContentValues();
        cv.put(TableServerSyncDetails.LAST_UPDATED_TIME, lastUpdatedTime);

        String where = TableServerSyncDetails.API_ID + " = " + apiId;
        int numberOfRowsUpdated = mContentResolver.update(TableServerSyncDetails.CONTENT_URI, cv, where, null);
        if (numberOfRowsUpdated <= 0)
            insertLastServerSyncTimeForAPICall(apiId, lastUpdatedTime);
    }

    private Uri insertLastServerSyncTimeForAPICall(int apiId, String lastUpdatedTime) {
        ContentValues cv = new ContentValues();
        cv.put(TableServerSyncDetails.API_ID, apiId);
        cv.put(TableServerSyncDetails.LAST_UPDATED_TIME, lastUpdatedTime);
        String where = TableServerSyncDetails.API_ID + "=" + apiId;
        mContentResolver.delete(TableServerSyncDetails.CONTENT_URI, where, null);
        return mContentResolver.insert(TableServerSyncDetails.CONTENT_URI, cv);
    }
}
