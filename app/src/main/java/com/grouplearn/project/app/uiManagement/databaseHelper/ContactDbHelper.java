package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableContacts;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLInterest;

import java.util.ArrayList;

/**
 * Created by Godwin on 07-06-2016 21:48 for Group Learn application 13:17 for GroupLearn.
 * @author : Godwin Joseph Kurinjikattu
 */
public class ContactDbHelper extends DataBaseHelper {

    private final ContentResolver mContentResolver;
    private final InterestDbHelper interestDbHelper;

    public ContactDbHelper(Context mContext) {
        super(mContext);
        this.mContentResolver = mContext.getContentResolver();
        this.interestDbHelper = new InterestDbHelper(mContext);
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
            if (model.getSkills() != null && model.getSkills().size() > 0) {
                interestDbHelper.addInterests(model.getSkills());
            }
            if (model.getInterests() != null && model.getInterests().size() > 0) {
                interestDbHelper.addInterests(model.getInterests());
            }
        }
    }

    public int updateContact(GLContact model) {
        ContentValues cv = getContentValuesForContacts(model);
        String where = TableContacts.CONTACT_USER_ID + " = '" + model.getContactUserId() + "'";
        cv.put(TableContacts.UPDATED_TIME, System.currentTimeMillis());
        if (model.getSkills() != null && model.getSkills().size() > 0) {
            interestDbHelper.addInterests(model.getSkills());
        }
        if (model.getInterests() != null && model.getInterests().size() > 0) {
            interestDbHelper.addInterests(model.getInterests());
        }
        return mContentResolver.update(TableContacts.CONTENT_URI, cv, where, null);
    }

    public ArrayList<GLContact> getContacts() {
        String sort = TableContacts.CONTACT_NAME + " ASC";
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, null, null, sort);
        ArrayList<GLContact> contactModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                GLContact contactModel = getContactFromCursor(cursor);
                contactModels.add(contactModel);
                contactModel.setInterests(interestDbHelper.getInterests(contactModel.getContactUserId(), GLInterest.INTEREST));
                contactModel.setSkills(interestDbHelper.getInterests(contactModel.getContactUserId(), GLInterest.SKILL));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return contactModels;
    }

    private ContentValues getContentValuesForContacts(GLContact model) {
        ContentValues cv = new ContentValues();

        cv.put(TableContacts.CONTACT_USER_ID, model.getContactUserId());
        cv.put(TableContacts.CONTACT_MAIL_ID, model.getContactMailId());
        cv.put(TableContacts.CONTACT_NAME, model.getContactName());
        cv.put(TableContacts.CONTACT_STATUS, model.getContactStatus());
        cv.put(TableContacts.CONTACT_ICON_URI, model.getIconUrl());
        return cv;
    }

    private GLContact getContactFromCursor(Cursor cursor) {
        if (cursor != null) {
            GLContact model = new GLContact();

            model.setContactUserId(cursor.getLong(cursor.getColumnIndex(TableContacts.CONTACT_USER_ID)));
            model.setContactName(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_NAME)));
            model.setContactStatus(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_STATUS)));
            model.setContactMailId(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_MAIL_ID)));
            model.setIconUrl(cursor.getString(cursor.getColumnIndex(TableContacts.CONTACT_ICON_URI)));

            return model;
        }
        return null;
    }

    private GLContact getContact(long contactUserId) {
        String where = TableContacts.CONTACT_USER_ID + "=" + contactUserId;
        Cursor cursor = mContentResolver.query(TableContacts.CONTENT_URI, null, where, null, null);
        GLContact contact = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            contact = getContactFromCursor(cursor);
            contact.setInterests(interestDbHelper.getInterests(contact.getContactUserId(), GLInterest.INTEREST));
            contact.setSkills(interestDbHelper.getInterests(contact.getContactUserId(), GLInterest.SKILL));

        }
        return contact;
    }

    public long isContactExist(long contactId) {
        String where = TableContacts.CONTACT_USER_ID + "=" + contactId;
        return getNumberOfRowsInDatabase(TableContacts.TABLE_NAME, where);
    }

}
