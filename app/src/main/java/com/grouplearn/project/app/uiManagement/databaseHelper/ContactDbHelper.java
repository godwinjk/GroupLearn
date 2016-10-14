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
public class ContactDbHelper {
    Context mContext;
    ContentResolver mContentResolver;

    public ContactDbHelper(Context mContext) {
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
        String where = TableContacts.CONTACT_NUMBER + " = '" + model.getContactNumber() + "'";
        int count = mContentResolver.update(TableContacts.CONTENT_URI, cv, where, null);
        if (count <= 0)
            mContentResolver.insert(TableContacts.CONTENT_URI, cv);
    }

    public ArrayList<ContactModel> getContacts() {
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, null, null, null);
        ArrayList<ContactModel> contactModels = new ArrayList<>();
        if (cursor != null) {
            cursor.moveToFirst();
            do {
                ContactModel contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
            } while (cursor.moveToNext());
        }
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
            model.setStatus(cursor.getInt(cursor.getColumnIndex(TableContacts.CONTACT_FOUND)));
            model.setStatus(cursor.getInt(cursor.getColumnIndex(TableContacts.CONTACT_FOUND)));
            return model;
        }
        return null;
    }

    public void updateInvitationDetails(String groupId) {

    }
}
