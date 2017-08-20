package com.grouplearn.project.app.uiManagement.message;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.app.uiManagement.notification.NotificationManager;
import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.AesHelper;
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

    private String processNotification(String body) {

        GLMessage model = new GLMessage();
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
                        if (object != null) {

                            String encryptedMessage = object.optString("message");

                            long groupId = object.optLong("groupId");
                            String origMessage = AesHelper.decrypt(groupId, encryptedMessage);
                            model.setReceiverId(groupId);

                            model.setMessageBody(origMessage);

                            model.setSenderName(object.optString("senderName"));
                            model.setMessageType(object.optInt("messageType"));
                            model.setSenderId(object.optInt("senderId", 12));
                            model.setReadStatus(ChatUtilities.NOT_READ);
                            model.setTimeStamp(object.optString("timestamp"));
                            message = new StringBuilder();

                            message.append(model.getSenderName()).append(" : ");
                            if (Math.abs(model.getMessageType()) == GLMessage.IMAGE) {
                                message.append("Image ");
                            } else if (Math.abs(model.getMessageType()) == GLMessage.VIDEO) {
                                message.append("Video ");
                            } else if (Math.abs(model.getMessageType()) == GLMessage.DOCUMENT) {
                                message.append("Document ");
                            }
                            message.append(model.getMessageBody()).append("\n");
                        }
                        Intent intent = new Intent("chatRefresh");
                        sendBroadcast(intent);
                        if (!TextUtils.isEmpty(message.toString())) {
                            NotificationManager.getInstance().showNotification(notificationType, message.toString());
                        }
                    }
                } else if (notificationType == 1) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have a new group request");
                } else if (notificationType == 2) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have been added to new group");
                } else if (notificationType == 5) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have a new invitation");
                } else if (notificationType == 6) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have been added to new group");
                } else if (notificationType == 7) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have a new contact request");
                } else if (notificationType == 8) {
                    NotificationManager.getInstance().showNotification(notificationType, "You have new contacts");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message.toString();
    }
}

