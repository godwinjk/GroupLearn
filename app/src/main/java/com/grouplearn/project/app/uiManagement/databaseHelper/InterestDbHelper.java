package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableInterests;
import com.grouplearn.project.bean.GLInterest;

import java.util.ArrayList;

/**
 * Created by Godwin on 16-04-2017 12:45 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public class InterestDbHelper extends DataBaseHelper {
    private final ContentResolver mContentResolver;

    public InterestDbHelper(Context mContext) {
        super(mContext);
        this.mContentResolver = mContext.getContentResolver();
    }

    public void addInterests(ArrayList<GLInterest> glInterests) {
        for (GLInterest interest : glInterests) {
            deleteAllInterests(interest.getUserId(), interest.getInterestType());
            mContentResolver.insert(TableInterests.CONTENT_URI, getContentValues(interest));
        }
    }

    public void deleteAllInterests() {
        mContentResolver.delete(TableInterests.CONTENT_URI, null, null);
    }

    public ArrayList<GLInterest> getInterests(long userId, int type) {
        ArrayList<GLInterest> interests = new ArrayList<>();
        String where = TableInterests.INTEREST_USER_ID + "=" + userId;
        if (type != GLInterest.BOTH)
            where += " AND " + TableInterests.INTEREST_TYPE + "=" + type;
        Cursor cursor = mContentResolver.query(TableInterests.CONTENT_URI, null, where, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                interests.add(getInterestFromCursor(cursor));
            } while (cursor.moveToNext());

        }
        return interests;
    }

    private GLInterest getInterestFromCursor(Cursor cursor) {
        GLInterest interest = new GLInterest();
        interest.setUserId(cursor.getLong(cursor.getColumnIndex(TableInterests.INTEREST_USER_ID)));
        interest.setInterestId(cursor.getLong(cursor.getColumnIndex(TableInterests.INTEREST_ID)));
        interest.setInterestType(cursor.getInt(cursor.getColumnIndex(TableInterests.INTEREST_TYPE)));
        interest.setInterestName(cursor.getString(cursor.getColumnIndex(TableInterests.INTEREST_NAME)));
        interest.setIconUrl(cursor.getString(cursor.getColumnIndex(TableInterests.INTEREST_ICON_URI)));
        return interest;
    }

    public ContentValues getContentValues(GLInterest interest) {
        ContentValues cv = new ContentValues();
        cv.put(TableInterests.INTEREST_ICON_URI, interest.getIconUrl());
        cv.put(TableInterests.INTEREST_ID, interest.getInterestId());
        cv.put(TableInterests.INTEREST_NAME, interest.getInterestName());
        cv.put(TableInterests.INTEREST_USER_ID, interest.getUserId());
        cv.put(TableInterests.INTEREST_TYPE, interest.getInterestType());
        return cv;
    }

    public void deleteAllInterests(long userId, int type) {
        String where = TableInterests.INTEREST_USER_ID + "=" + userId;
        if (type != GLInterest.BOTH)
            where += " AND " + TableInterests.INTEREST_TYPE + "=" + type;
        mContentResolver.delete(TableInterests.CONTENT_URI, where, null);
    }
}
