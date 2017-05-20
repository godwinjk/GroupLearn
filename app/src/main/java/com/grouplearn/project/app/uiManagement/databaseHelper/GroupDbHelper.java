package com.grouplearn.project.app.uiManagement.databaseHelper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.DatabaseHandler;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.databaseManagament.tables.TableSubscribedGroups;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Godwin Joseph on 19-05-2016 21:57 for Group Learn application.
 */
public class GroupDbHelper extends DataBaseHelper {
    Context mContext;
    ContentResolver mContentResolver;
    DatabaseHandler dbHandler;

    public GroupDbHelper(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mContentResolver = mContext.getContentResolver();
        dbHandler = new DatabaseHandler(mContext);
    }

    public ArrayList<GLGroup> getSubscribedGroups() {
        ArrayList<GLGroup> groupModels = new ArrayList<>();
        Cursor cursor = dbHandler.getAllSubscribedGroups(null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupModels.add(makeGroupModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return getSortedListBasedOnMessage(groupModels);
    }

    public ArrayList<GLGroup> getMyGroups() {
        long userId = new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID);
        String where = TableSubscribedGroups.GROUP_USER_ID + "=" + userId;
        Cursor cursor = dbHandler.getAllSubscribedGroups(where);
        ArrayList<GLGroup> groupModels = new ArrayList<>();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupModels.add(makeGroupModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return groupModels;
    }

    public ArrayList<GLGroup> getSubscribedGroupsWithName(String name) {
        ArrayList<GLGroup> groupModels = new ArrayList<>();
        Cursor cursor = dbHandler.getAllGroupWithName(name);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                groupModels.add(makeGroupModelFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return groupModels;
    }

    public GLGroup getGroupInfo(long groupUniqueId) {
        Cursor cursor = dbHandler.getSubscribedGroupInfo(groupUniqueId);
        GLGroup group = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            group = makeGroupModelFromCursor(cursor);
        }
        return group;
    }

    private GLGroup makeGroupModelFromCursor(Cursor cursor) {
        String groupName = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.GROUP_NAME));
        String groupIconId = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.GROUP_ICON_ID));
        long groupUniqueId = cursor.getLong(cursor.getColumnIndex(TableSubscribedGroups.GROUP_ID));
        long groupUserId = cursor.getLong(cursor.getColumnIndex(TableSubscribedGroups.GROUP_USER_ID));
        String groupCreatedTime = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.CREATED_TIME));
        String groupUpdatedTime = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.UPDATED_TIME));
        String groupDescription = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.UPDATED_TIME));
        String groupIconUri = cursor.getString(cursor.getColumnIndex(TableSubscribedGroups.GROUP_ICON_URI));

        GLGroup model = new GLGroup();
        model.setGroupName(groupName);
        model.setGroupAdminId(groupUserId);
        model.setGroupIconId(groupIconId);
        model.setGroupDescription(groupDescription);
        model.setGroupUniqueId(groupUniqueId);
        model.setGroupCreatedTime(groupCreatedTime);
        model.setGroupUpdatedTime(groupUpdatedTime);
        model.setIconUrl(groupIconUri);

        ChatDbHelper chatDbHelper = new ChatDbHelper(mContext);
        long numberOfNewMessage = chatDbHelper.getNumberOfNewMessage(groupUniqueId);
        GLMessage lastMessage = chatDbHelper.getLastMessages(groupUniqueId);
        model.setMessageModel(lastMessage);
        if (lastMessage != null) {
            StringBuilder message = new StringBuilder();
            message.append(lastMessage.getSenderName());
            message.append(" : ");
            if (!TextUtils.isEmpty(lastMessage.getMessageBody())) {

                message.append(lastMessage.getMessageBody());

                model.setLastMessage(message.toString());
            } else if (Math.abs(lastMessage.getMessageType()) == GLMessage.IMAGE) {
                message.append("Image ");
                model.setLastMessage(message.toString());
            } else if (Math.abs(lastMessage.getMessageType()) == GLMessage.VIDEO) {
                message.append("Video ");
                model.setLastMessage(message.toString());
            } else if (Math.abs(lastMessage.getMessageType()) == GLMessage.DOCUMENT) {
                message.append("Document ");
                model.setLastMessage(message.toString());
            } else {
                model.setLastMessage("");
            }
        } else {
            model.setLastMessage("");
        }

        model.setNewMessage((int) numberOfNewMessage);
        return model;
    }

    public void addSubscribedGroup(GLGroup model) {
        String where = TableSubscribedGroups.GROUP_ID + " = " + model.getGroupUniqueId();
        ContentValues cv = getContentValuesForGroup(model);
        int count = mContentResolver.update(TableSubscribedGroups.CONTENT_URI, cv, where, null);
        if (count <= 0) {
            cv.put(TableSubscribedGroups.CREATED_TIME, model.getGroupCreatedTime());
            mContentResolver.insert(TableSubscribedGroups.CONTENT_URI, cv);
        }
    }

    private ContentValues getContentValuesForGroup(GLGroup model) {
        ContentValues cv = new ContentValues();
        cv.put(TableSubscribedGroups.GROUP_NAME, model.getGroupName());
        cv.put(TableSubscribedGroups.GROUP_ICON_ID, model.getGroupIconId());
        cv.put(TableSubscribedGroups.GROUP_USER_ID, model.getGroupAdminId());
        cv.put(TableSubscribedGroups.GROUP_ID, model.getGroupUniqueId());
        cv.put(TableSubscribedGroups.GROUP_DESCRIPTION, model.getGroupDescription());

        cv.put(TableSubscribedGroups.UPDATED_TIME, model.getGroupUpdatedTime());
        cv.put(TableSubscribedGroups.GROUP_ICON_URI, model.getIconUrl());
        return cv;
    }

    private ArrayList<GLGroup> getSortedListBasedOnMessage(ArrayList<GLGroup> groupModels) {
//        for (GroupModel model : groupModels) {
//            if (model.geMessageModel() != null && model.geMessageModel().getTimeStamp() != null) {
        Comparator<GLGroup> comparator = new Comparator<GLGroup>() {
            @Override
            public int compare(GLGroup lhs, GLGroup rhs) {
                if (lhs.geMessageModel() != null && lhs.geMessageModel().getTimeStamp() != null || rhs.geMessageModel() != null && rhs.geMessageModel().getTimeStamp() != null) {
                    if (lhs.geMessageModel() == null)
                        return 1;
                    else if (rhs.geMessageModel() == null)
                        return -1;
                    else
                        return rhs.geMessageModel().getTimeStamp().compareTo(lhs.geMessageModel().getTimeStamp());
                }
                return -1;
            }
        };
        Collections.sort(groupModels, comparator);
        return groupModels;
//            }
//        }
    }

    public int updateImageUri(long groupCloudId, String imageUri) {
        String where = TableSubscribedGroups.GROUP_ID + "=" + groupCloudId;
        ContentValues cv = new ContentValues();
        cv.put(TableSubscribedGroups.GROUP_ICON_URI, imageUri);
        new CourseDbHelper(mContext).updateImageUri(groupCloudId, imageUri);
        return mContentResolver.update(TableSubscribedGroups.CONTENT_URI, cv, where, null);
    }

    public GLGroup getGodwinBot() {
        String where = TableSubscribedGroups.GROUP_ID + "=" + -11223344;
        long num = getNumberOfRowsInDatabase(TableSubscribedGroups.TABLE_NAME, where);
        if (num > 0) {
            Cursor godwinBotCursor = mContentResolver.query(TableSubscribedGroups.CONTENT_URI, null, where, null, null);
            if (godwinBotCursor != null && godwinBotCursor.getCount() > 0) {
                godwinBotCursor.moveToFirst();
                return makeGroupModelFromCursor(godwinBotCursor);
            }
        }
        return makeGodwinBot();
    }

    private GLGroup makeGodwinBot() {
        ContentValues cv = new ContentValues();
        cv.put(TableSubscribedGroups.GROUP_ID, -11223344);
        cv.put(TableSubscribedGroups.GROUP_ICON_ID, 10);
        cv.put(TableSubscribedGroups.GROUP_NAME, "Aimi");
        cv.put(TableSubscribedGroups.GROUP_DESCRIPTION, "Chat with Aimi");
        cv.put(TableSubscribedGroups.CREATED_TIME, System.currentTimeMillis());
        cv.put(TableSubscribedGroups.UPDATED_TIME, System.currentTimeMillis());
        mContentResolver.insert(TableSubscribedGroups.CONTENT_URI, cv);
        return getGodwinBot();
    }

    public int deleteSubscribedGroup(long groupId) {
        String where = TableSubscribedGroups.GROUP_ID + "='" + groupId + "'";
        return mContentResolver.delete(TableSubscribedGroups.CONTENT_URI, where, null);
    }
}
