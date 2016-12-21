package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.grouplearn.project.app.databaseManagament.tables.TableCourse;
import com.grouplearn.project.bean.GLCourse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 17-12-2016 22:33 for GroupLearn application.
 */

public class CourseDbHelper extends DataBaseHelper {
    Context mContext;
    ContentResolver mContentResolver;

    public CourseDbHelper(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
    }

    public void addCourse(GLCourse course) {
        ContentValues cv = getContentValuesForContacts(course);
        mContentResolver.insert(TableCourse.CONTENT_URI, cv);
    }

    public void updateCourse(GLCourse course) {
        String where = TableCourse.COURSE_ID + "=" + course.getCourseId();
        ContentValues cv = getContentValuesForContacts(course);
        int count = mContentResolver.update(TableCourse.CONTENT_URI, cv, where, null);
        if (count <= 0) {
            addCourse(course);
        }
    }

    public void deleteCourse(GLCourse course) {
        String where = TableCourse.COURSE_ID + "=" + course.getCourseId();
        int count = mContentResolver.delete(TableCourse.CONTENT_URI, where, null);
    }

    public ArrayList<GLCourse> getCourses() {
        ArrayList<GLCourse> cou = new ArrayList<>();
        Cursor cursor = mContentResolver.query(TableCourse.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                cou.add(getContactFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        return cou;
    }

    private ContentValues getContentValuesForContacts(GLCourse course) {
        ContentValues cv = new ContentValues();
        cv.put(TableCourse.COURSE_ID, course.getCourseId());
        cv.put(TableCourse.COURSE_NAME, course.getCourseName());
        cv.put(TableCourse.COURSE_CONTACT, course.getContactDetails());
        cv.put(TableCourse.COURSE_DESCRIPTION, course.getDefinition());
        cv.put(TableCourse.COURSE_GROUP_DESCRIPTION, course.getGroupDescription());
        cv.put(TableCourse.COURSE_ICON_ID, course.getCourseIconId());
        cv.put(TableCourse.COURSE_STATUS, course.getCourseStatus());
        cv.put(TableCourse.COURSE_USER_ID, course.getCourseUserId());
        cv.put(TableCourse.COURSE_URL, course.getUrl());
        cv.put(TableCourse.COURSE_GROUP_NAME, course.getGroupName());
        cv.put(TableCourse.COURSE_GROUP_ICON_ID, course.getGroupIconId());
        cv.put(TableCourse.COURSE_GROUP_ID, course.getGroupId());
        return cv;
    }

    private GLCourse getContactFromCursor(Cursor cursor) {
        if (cursor != null) {
            GLCourse model = new GLCourse();
            model.setCourseName(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_NAME)));
            model.setGroupIconId(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_GROUP_ICON_ID)));
            model.setGroupDescription(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_GROUP_DESCRIPTION)));
            model.setDefinition(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_DESCRIPTION)));
            model.setCourseId(cursor.getLong(cursor.getColumnIndex(TableCourse.COURSE_ID)));
            model.setGroupName(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_GROUP_NAME)));
            model.setUrl(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_URL)));
            model.setContactDetails(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_CONTACT)));
            model.setCourseIconId(cursor.getString(cursor.getColumnIndex(TableCourse.COURSE_ICON_ID)));
            model.setCourseUserId(cursor.getLong(cursor.getColumnIndex(TableCourse.COURSE_USER_ID)));
            model.setCourseStatus(cursor.getInt(cursor.getColumnIndex(TableCourse.COURSE_STATUS)));
            model.setGroupId(cursor.getLong(cursor.getColumnIndex(TableCourse.COURSE_GROUP_ID)));

            return model;
        }
        return null;
    }
}
