package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.grouplearn.project.app.databaseManagament.tables.TableContacts;
import com.grouplearn.project.bean.GLContact;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 21:48 for Group Learn application.
 */
public class ContactDbHelper extends DataBaseHelper {

    private ContentResolver mContentResolver;

    public ContactDbHelper(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
    }

    public void addContact(ArrayList<GLContact> models) {
        for (GLContact model : models) {
            addContact(model);
        }
    }

    public void addContact(GLContact model) {
        int count = updateContact(model);
        if (count <= 0) {
            ContentValues cv = getContentValuesForContacts(model);
            mContentResolver.insert(TableContacts.CONTENT_URI, cv);
        }
    }

    public int updateContact(GLContact model) {
        ContentValues cv = new ContentValues();
        String where = TableContacts.CONTACT_NUMBER + " = '" + model.getContactNumber() + "'";
        cv.put(TableContacts.CONTACT_CLOUD_ID, model.getContactUniqueId());
        cv.put(TableContacts.CONTACT_FOUND, model.getStatus());
        cv.put(TableContacts.CONTACT_ICON_URI, model.getIconUrl());
        if (!TextUtils.isEmpty(model.getContactStatus()))
            cv.put(TableContacts.CONTACT_STATUS, model.getContactStatus());
        return mContentResolver.update(TableContacts.CONTENT_URI, cv, where, null);
    }

    public ArrayList<GLContact> getContacts() {
        String sort = TableContacts.CONTACT_CLOUD_ID + " ASC";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, null, null, sort);
        ArrayList<GLContact> contactModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                GLContact contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactModels;
    }

    private ContentValues getContentValuesForContacts(GLContact model) {
        ContentValues cv = new ContentValues();
        cv.put(TableContacts.CONTACT_ID, model.getContactId());
        cv.put(TableContacts.CONTACT_CLOUD_ID, model.getContactUniqueId());
        cv.put(TableContacts.CONTACT_ICON_ID, model.getContactIconId());
        cv.put(TableContacts.CONTACT_NAME, model.getContactName());
        cv.put(TableContacts.CONTACT_STATUS, model.getContactStatus());
        cv.put(TableContacts.CONTACT_NUMBER, model.getContactNumber());
        cv.put(TableContacts.CONTACT_FOUND, model.getStatus());
        cv.put(TableContacts.CONTACT_ICON_URI, model.getIconUrl());
        return cv;
    }

    private GLContact getContactFromCursor(Cursor cursor) {
        if (cursor != null) {
            GLContact model = new GLContact();
            model.setContactIconId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ICON_ID)));
            model.setContactId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ID)));
            model.setContactUniqueId(cursor.getLong(cursor.getColumnIndex(TableContacts.CONTACT_CLOUD_ID)));
            model.setContactName(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_NAME)));
            model.setContactStatus(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_STATUS)));
            model.setContactNumber(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_NUMBER)));
            model.setStatus(cursor.getInt(cursor.getColumnIndex(TableContacts.CONTACT_FOUND)));
            model.setIconUrl(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ICON_URI)));

            return model;
        }
        return null;
    }

    private GLContact getContact(String contactId) {
        String where = TableContacts.CONTACT_ID + "='" + contactId + "'";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return getContactFromCursor(cursor);
        }
        return null;
    }

    public long isContactFound(String contactNumber) {
        String where = TableContacts.CONTACT_NUMBER + "'" + contactNumber + "'";
        return getNumberOfRowsInDatabase(TableContacts.TABLE_NAME, where);
    }

    public ArrayList<GLContact> getContactsInCloud() {
        String where = TableContacts.CONTACT_FOUND + "=1";
        String sort = TableContacts.CONTACT_FOUND + " ASC";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, where, null, sort);
        ArrayList<GLContact> contactModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                GLContact contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactModels;
    }
}
