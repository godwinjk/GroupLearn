package com.grouplearn.project.app.databaseManagament;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.grouplearn.project.app.databaseManagament.constants.DatabaseConstants;
import com.grouplearn.project.app.databaseManagament.tables.TableChat;
import com.grouplearn.project.app.databaseManagament.tables.TableContacts;
import com.grouplearn.project.app.databaseManagament.tables.TableGroups;
import com.grouplearn.project.app.databaseManagament.tables.TableServerSyncDetails;
import com.grouplearn.project.app.databaseManagament.tables.TableSubscribedGroups;

/**
 * Created by Godwin Joseph on 07-05-2016 13:15 for Group Learn application.
 */
public class DatabaseProvider extends ContentProvider {
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(DatabaseConstants.AUTHORITY, TableSubscribedGroups.TABLE_NAME,
                TableSubscribedGroups.TABLE_INDEX);
        uriMatcher.addURI(DatabaseConstants.AUTHORITY, TableChat.TABLE_NAME,
                TableChat.TABLE_INDEX);
        uriMatcher.addURI(DatabaseConstants.AUTHORITY, TableGroups.TABLE_NAME,
                TableGroups.TABLE_INDEX);
        uriMatcher.addURI(DatabaseConstants.AUTHORITY, TableServerSyncDetails.TABLE_NAME,
                TableServerSyncDetails.TABLE_INDEX);
        uriMatcher.addURI(DatabaseConstants.AUTHORITY, TableContacts.TABLE_NAME,
                TableContacts.TABLE_INDEX);

    }

    private DbHelper mDbHelper;
    private SQLiteDatabase mSqldb;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        mSqldb = mDbHelper.getWritableDatabase();
        String databasetable;
        switch (uriMatcher.match(uri)) {
            case TableSubscribedGroups.TABLE_INDEX:
                databasetable = uri.getPathSegments().get(0);
                count = mSqldb.delete(databasetable, selection, selectionArgs);
                break;
            case TableChat.TABLE_INDEX:
                databasetable = uri.getPathSegments().get(0);
                count = mSqldb.delete(databasetable, selection, selectionArgs);
                break;
            case TableGroups.TABLE_INDEX:
                databasetable = uri.getPathSegments().get(0);
                count = mSqldb.delete(databasetable, selection, selectionArgs);
                break;
            case TableServerSyncDetails.TABLE_INDEX:
                databasetable = uri.getPathSegments().get(0);
                count = mSqldb.delete(databasetable, selection, selectionArgs);
                break;
            case TableContacts.TABLE_INDEX:
                databasetable = uri.getPathSegments().get(0);
                count = mSqldb.delete(databasetable, selection, selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        mSqldb = mDbHelper.getWritableDatabase();
        long rowID = -1;
        Uri retUri = null;
        switch (uriMatcher.match(uri)) {
            case TableChat.TABLE_INDEX:
                rowID = mSqldb.insert(TableChat.TABLE_NAME, "", values);
                // ---if added successfully---
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(TableChat.CONTENT_URI,
                            rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return retUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case TableGroups.TABLE_INDEX:
                rowID = mSqldb.insert(TableGroups.TABLE_NAME, "", values);
                // ---if added successfully---
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(TableGroups.CONTENT_URI,
                            rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return retUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case TableSubscribedGroups.TABLE_INDEX:
                rowID = mSqldb.insert(TableSubscribedGroups.TABLE_NAME, "", values);
                // ---if added successfully---
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(TableSubscribedGroups.CONTENT_URI,
                            rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return retUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case TableServerSyncDetails.TABLE_INDEX:
                rowID = mSqldb.insert(TableServerSyncDetails.TABLE_NAME, "", values);
                // ---if added successfully---
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(TableServerSyncDetails.CONTENT_URI,
                            rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return retUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            case TableContacts.TABLE_INDEX:
                rowID = mSqldb.insert(TableContacts.TABLE_NAME, "", values);
                // ---if added successfully---
                if (rowID > 0) {
                    retUri = ContentUris.withAppendedId(TableContacts.CONTENT_URI,
                            rowID);
                    getContext().getContentResolver().notifyChange(uri, null);
                    return retUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
        }
        return retUri;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mDbHelper = new DbHelper(context);
        /*
         * Create a write able database which will trigger its creation if it
		 * doesn't already exist.
		 */
        mSqldb = mDbHelper.getWritableDatabase();
        return (mSqldb == null) ? false : true;

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int choice = uriMatcher.match(uri);

        String tableName = null;
        Cursor cursor = null;
        switch (choice) {
            case TableChat.TABLE_INDEX:
                mSqldb = mDbHelper.getReadableDatabase();
                tableName = uri.getLastPathSegment();
                queryBuilder.setTables(tableName);
                cursor = queryBuilder.query(mSqldb, projection, selection,
                        selectionArgs, null, null, sortOrder);
                if (cursor != null)
                    cursor.setNotificationUri(getContext().getContentResolver(),
                            uri);
                break;
            case TableSubscribedGroups.TABLE_INDEX:
                mSqldb = mDbHelper.getReadableDatabase();
                tableName = uri.getLastPathSegment();
                queryBuilder.setTables(tableName);
                cursor = queryBuilder.query(mSqldb, projection, selection,
                        selectionArgs, null, null, sortOrder);
                if (cursor != null)
                    cursor.setNotificationUri(getContext().getContentResolver(),
                            uri);
                break;
            case TableGroups.TABLE_INDEX:
                mSqldb = mDbHelper.getReadableDatabase();
                tableName = uri.getLastPathSegment();
                queryBuilder.setTables(tableName);
                cursor = queryBuilder.query(mSqldb, projection, selection,
                        selectionArgs, null, null, sortOrder);
                if (cursor != null)
                    cursor.setNotificationUri(getContext().getContentResolver(),
                            uri);
                break;
            case TableServerSyncDetails.TABLE_INDEX:
                mSqldb = mDbHelper.getReadableDatabase();
                tableName = uri.getLastPathSegment();
                queryBuilder.setTables(tableName);
                cursor = queryBuilder.query(mSqldb, projection, selection,
                        selectionArgs, null, null, sortOrder);
                if (cursor != null)
                    cursor.setNotificationUri(getContext().getContentResolver(),
                            uri);
                break;
            case TableContacts.TABLE_INDEX:
                mSqldb = mDbHelper.getReadableDatabase();
                tableName = uri.getLastPathSegment();
                queryBuilder.setTables(tableName);
                cursor = queryBuilder.query(mSqldb, projection, selection,
                        selectionArgs, null, null, sortOrder);
                if (cursor != null)
                    cursor.setNotificationUri(getContext().getContentResolver(),
                            uri);
                break;
        }
        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        mSqldb = mDbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case TableSubscribedGroups.TABLE_INDEX:
                count = mSqldb.update(TableSubscribedGroups.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TableChat.TABLE_INDEX:
                count = mSqldb.update(TableChat.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TableGroups.TABLE_INDEX:
                count = mSqldb.update(TableGroups.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TableServerSyncDetails.TABLE_INDEX:
                count = mSqldb.update(TableServerSyncDetails.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TableContacts.TABLE_INDEX:
                count = mSqldb.update(TableContacts.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    // Helper Class to operate on DB
    public static class DbHelper extends SQLiteOpenHelper {
        @SuppressWarnings("unused")
        private Context mContext;

        public DbHelper(Context context) {

            super(context, DatabaseConstants.DB_NAME, null, DatabaseConstants.DB_VERSION);
            this.mContext = context;

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // db.execSQL("drop if exists "+DataBaseConstants.TABLE_CATEGORY);
            db.execSQL("drop table  if exists " + TableSubscribedGroups.TABLE_NAME);

            db.execSQL("drop table  if exists " + TableChat.TABLE_NAME);

            db.execSQL("drop table  if exists " + TableGroups.TABLE_NAME);
            db.execSQL("drop table  if exists " + TableServerSyncDetails.TABLE_NAME);
            db.execSQL("drop table  if exists " + TableContacts.TABLE_NAME);

            onCreate(db);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TableSubscribedGroups.SQL_CREATE_TABLE);
            db.execSQL(TableGroups.SQL_CREATE_TABLE);
            db.execSQL(TableChat.SQL_CREATE_TABLE);
            db.execSQL(TableServerSyncDetails.SQL_CREATE_TABLE);
            db.execSQL(TableContacts.SQL_CREATE_TABLE);

        }
    }
}
