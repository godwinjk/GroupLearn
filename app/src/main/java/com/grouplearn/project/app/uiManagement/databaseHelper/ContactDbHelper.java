package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableContacts;
import com.grouplearn.project.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 07-06-2016 21:48 for Group Learn application.
 */
public class ContactDbHelper extends DataBaseHelper {
    Context mContext;
    ContentResolver mContentResolver;

    public ContactDbHelper(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
    }

    public void addContact(ArrayList<ContactModel> models) {
        for (ContactModel model : models) {
            addContact(model);
        }
    }

    public void addContact(ContactModel model) {
        ContentValues cv = getContentValuesForContacts(model);
        int count = updateContact(model);
        if (count <= 0)
            mContentResolver.insert(TableContacts.CONTENT_URI, cv);
    }

    public int updateContact(ContactModel model) {
        ContentValues cv = new ContentValues();
        String where = TableContacts.CONTACT_NUMBER + " = '" + model.getContactNumber() + "'";
        cv.put(TableContacts.CONTACT_FOUND, model.getStatus());
        cv.put(TableContacts.CONTACT_CLOUD_ID, model.getContactUniqueId());
        return mContentResolver.update(TableContacts.CONTENT_URI, cv, where, null);
    }

    public ArrayList<ContactModel> getContacts() {
        String sort = TableContacts.CONTACT_FOUND + " ASC";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, null, null, sort);
        ArrayList<ContactModel> contactModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ContactModel contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactModels;
    }

    private ContentValues getContentValuesForContacts(ContactModel model) {
        ContentValues cv = new ContentValues();
        cv.put(TableContacts.CONTACT_ID, model.getContactId());
        cv.put(TableContacts.CONTACT_CLOUD_ID, model.getContactUniqueId());
        cv.put(TableContacts.CONTACT_ICON_ID, model.getContactIconId());
        cv.put(TableContacts.CONTACT_NAME, model.getContactName());
        cv.put(TableContacts.CONTACT_STATUS, model.getContactStatus());
        cv.put(TableContacts.CONTACT_NUMBER, model.getContactNumber());
        cv.put(TableContacts.CONTACT_FOUND, model.getStatus());
        return cv;
    }

    private ContactModel getContactFromCursor(Cursor cursor) {
        if (cursor != null) {
            ContactModel model = new ContactModel();
            model.setContactIconId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ICON_ID)));
            model.setContactId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ID)));
            model.setContactUniqueId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_CLOUD_ID)));
            model.setContactName(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_NAME)));
            model.setContactStatus(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_STATUS)));
            model.setContactNumber(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_NUMBER)));
            model.setStatus(cursor.getInt(cursor.getColumnIndex(TableContacts.CONTACT_FOUND)));
            return model;
        }
        return null;
    }

    public long isContactFound(String contactNumber) {
        String where = TableContacts.CONTACT_NUMBER + "'" + contactNumber + "'";
        return getNumberOfRowsInDatabase(TableContacts.TABLE_NAME, where);
    }

    public ArrayList<ContactModel> getContactsInCloud() {
        String where = TableContacts.CONTACT_FOUND + "=1";
        String sort = TableContacts.CONTACT_FOUND + " ASC";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, where, null, sort);
        ArrayList<ContactModel> contactModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                ContactModel contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactModels;
    }
}
