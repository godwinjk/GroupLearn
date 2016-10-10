package com.grouplearn.project.cloud.message;

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
import android.support.v4.app.RemoteInput;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.groupManagement.GroupListActivity;
import com.grouplearn.project.app.uiManagement.groupManagement.RequestAcceptingActivity;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.models.MessageModel;
import com.grouplearn.project.utilities.AppConstants;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Godwin Joseph on 20-09-2016 17:02 for Group Learn application.
 */
public class FcmMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        AppSharedPreference mPref = new AppSharedPreference(this);
        boolean isLoggedIn = mPref.getBooleanPrefValue(PreferenceConstants.IS_LOGIN);
        if (isLoggedIn) {
            ArrayMap<String, String> data = (ArrayMap<String, String>) message.getData();
            String body = data.get("data");
            String from = data.get("from");
            String messge = data.get("message");
            Log.d("NOTIFICATION GOT GOT GOT", body);

            RemoteMessage.Notification notification = message.getNotification();

            Log.i("MESSGE FCM", "" + notification);
            String fromMessage = processNotification(body);
        }
    }

    public void notificationAlert(int notificationType, String remoteMessage) {

        // define sound URI, the sound to be played when there's a notification
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // intent triggered, you can add other intent for other actions

        Intent notificationIntent = new Intent(this.getApplicationContext(), GroupListActivity.class);
        if (notificationType == 1) {
            notificationIntent = new Intent(this.getApplicationContext(), RequestAcceptingActivity.class);
        }
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pIntent = PendingIntent.getActivity(this, uniqueInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // this is it, we'll build the notification!

        // in the addAction method, if you don't want any icon, just set the first param to 0
        Drawable drawable = null;
        Bitmap bitmap = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            drawable = getDrawable(R.drawable.grouplearn_icon_128);
            if (drawable != null)
                bitmap = ((BitmapDrawable) drawable).getBitmap();

        }

        long[] pattern = {500, 0, 500, 0};
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.grouplearn_notification_white)
                .setLargeIcon(bitmap)
                .setContentTitle(getString(R.string.app_name))
                .setTicker("Group Learn")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage))
                .setSound(soundUri).setVibrate(pattern)
                .setContentIntent(pIntent)
                .setContentText(remoteMessage).setAutoCancel(true);

        //Setting notification for wearables
        mBuilder.extend(notifyForWearables(this, remoteMessage));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // If you want to hide the notification after it was selected, do the code below

        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, mBuilder.build());
    }

    private static NotificationCompat.WearableExtender notifyForWearables(Context context, String messageContent) {
        NotificationCompat.BigTextStyle secondPageStyle = new NotificationCompat.BigTextStyle();
        secondPageStyle.setBigContentTitle(context.getString(R.string.app_name)).bigText(messageContent);

// Create second page notification
        Notification secondPageNotification = new NotificationCompat.Builder(context).setStyle(secondPageStyle).build();
// Create the action


//        String[] replyChoices = context.getResources().getStringArray(R.array.reply_choices);
        RemoteInput remoteInput = new RemoteInput.Builder(AppConstants.KEY_QUICK_REPLY_TEXT)
                .setLabel("Do Operation")
//                .setChoices(replyChoices)
                .build();
        Intent intent = new Intent(context, GroupListActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.group_128, context.getString(R.string.app_name), pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

// Extend the notification builder with the second page
        NotificationCompat.WearableExtender extender = new NotificationCompat.WearableExtender().addPage(secondPageNotification).addAction(action);
        return extender;
    }

    private String processNotification(String body) {

        ChatDbHelper mDbHelper = new ChatDbHelper(this);
        MessageModel model = new MessageModel();
        StringBuilder message = new StringBuilder();
        try {
            JSONObject bodyObject = new JSONObject(body);
            if (bodyObject != null) {
                JSONArray array = bodyObject.optJSONArray("messages");
                int notificationType = bodyObject.optInt("notificationId", 0);
                if (notificationType == 3) {
                    MessageInteractor.getInstance().getAllMessages();
                    for (int i = 0; array != null && i < array.length(); i++) {
                        JSONObject object = array.optJSONObject(i);
//            JSONObject object = new JSONObject(body);
                        if (object != null) {
                            model.setMessageBody(object.optString("message"));
                            model.setReceiverId(object.optLong("groupId"));
                            model.setSenderName(object.optString("senderName"));
                            model.setMessageType(object.optInt("messageType"));
                            model.setSenderId(object.optInt("senderId", 12));
                            model.setReadStatus(ChatUtilities.NOT_READ);
                            model.setTimeStamp(object.optString("timestamp"));
//                mDbHelper.addMessageToDb(model);
                            message = new StringBuilder();

                            message.append(model.getSenderName() + " : ");
                            message.append(model.getMessageBody() + "\n");
                        }
                        Intent intent = new Intent("chatRefresh");
                        sendBroadcast(intent);
                        if (!TextUtils.isEmpty(message.toString())) {
                            notificationAlert(notificationType, message.toString());
                        }
                    }
                } else if (notificationType == 1) {
                    notificationAlert(notificationType, "You have a new request");
                } else if (notificationType == 2) {
                    notificationAlert(notificationType, "You have added to new group");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message.toString();
    }
}

