package com.grouplearn.project.app.uiManagement.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.grouplearn.project.R;
import com.grouplearn.project.app.MyApplication;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.contact.ContactRequestActivity;
import com.grouplearn.project.app.uiManagement.group.GroupListActivity;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.group.InvitationActivity;
import com.grouplearn.project.app.uiManagement.group.RequestAcceptingActivity;

/**
 * Created by Godwin Joseph on 13-10-2016 12:39 for GroupLearn application.
 */

public class NotificationManager {

    private static final int TYPE_NEW_GROUP_REQUEST = 1;
    private static final int TYPE_GROUP_ADDED = 2;
    private static final int TYPE_NEW_MESSAGE = 3;
    private static final int TYPE_NEW_GROUP_INVITATION = 5;

    int number = 0;
    private static final int NOTIFICATION_ID = -1101;
    private static final String NOTIFICATION_GROUP_ID = "NOTIFICATION_GROUP_ID";
    private static NotificationManager mNotificationManager;
    Context mContext;
    AppSharedPreference mPref;

    NotificationCompat.Builder mBuilder;

    public static NotificationManager getInstance() {
        if (mNotificationManager == null)
            mNotificationManager = new NotificationManager();
        return mNotificationManager;
    }

    private NotificationManager() {
        mContext = MyApplication.getAppContext();
        mPref = new AppSharedPreference(mContext);
    }

    public void showNotification(int notificationType, String message) {
        boolean isShowNotification = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_NOTIFICATION);
        boolean isNotificationSound = mPref.getBooleanPrefValue(PreferenceConstants.IS_NOTIFICATION_SOUND);
        boolean isInAppNotification = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_IN_APP_NOTIFICATION);
        boolean isShowContent = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_CONTENT_IN_NOTIFICATION);

        if (!isShowNotification) {
            return;
        }
        if (!isInAppNotification && BaseActivity.appState == BaseActivity.APP_ACTIVE) {
            return;
        }

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        int resId = R.drawable.grouplearn_notification_white;
        Intent groupIntentIntent = new Intent(mContext, GroupListNewActivity.class);
        if (notificationType == TYPE_NEW_GROUP_REQUEST) {
            resId = R.drawable.group_request_white_32;
            groupIntentIntent = new Intent(mContext, RequestAcceptingActivity.class);
        } else if (notificationType == TYPE_NEW_GROUP_INVITATION) {
            resId = R.drawable.invitation_white_32;
            groupIntentIntent = new Intent(mContext, InvitationActivity.class);
        } else if (notificationType == 7) {
            groupIntentIntent = new Intent(mContext, ContactRequestActivity.class);
        }
        groupIntentIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        groupIntentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        groupIntentIntent.putExtra("fromNotification", 1);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, uniqueInt, groupIntentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Drawable drawable = null;
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = mContext.getDrawable(R.drawable.grouplearn_icon_128);
            if (drawable != null)
                bitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        long[] pattern = {1000, 100, 500, 50};
        mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(resId)
                .setLargeIcon(bitmap)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).setVibrate(pattern)
                .setGroup(NOTIFICATION_GROUP_ID)
                .setNumber(++number)
                .setTicker("Group Learn");

        if (isNotificationSound) {
            mBuilder.setSound(soundUri);
        }
        if (isShowContent) {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message)).setContentText(message).setAutoCancel(true);
        } else {
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText("You have new messages")).setContentText("You have new messages");
        }
        //Setting notification for wearables
        mBuilder.extend(notifyForWearables(mContext, message));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private static NotificationCompat.WearableExtender notifyForWearables(Context context, String messageContent) {
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(context.getString(R.string.app_name)).bigText(messageContent);

        Notification secondPageNotification = new NotificationCompat.Builder(context).setStyle(secondPageStyle).build();

//        String[] replyChoices = context.getResources().getStringArray(R.array.reply_choices);

        Intent intent = new Intent(context, GroupListActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.group_128, context.getString(R.string.app_name), pendingIntent)
                .build();

        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender().addPage(secondPageNotification).addAction(action);
        return extender;
    }

    public void cancelNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
