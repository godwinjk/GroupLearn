package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;
import android.content.Intent;

import com.grouplearn.project.app.MyApplication;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudMessageHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.MessageConversationCallback;
import com.grouplearn.project.models.MessageModel;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Godwin Joseph on 03-10-2016 10:59 for Group Learn application.
 */
public class MessageInteractor implements MessageConversationCallback {
    private static final String TAG = "MessageInteractor";
    Context mContext;
    static MessageInteractor interactor;
    private boolean isMessageUpdatingRunning = false;
    private boolean isMessageSyncing = false;
    private boolean isPaused = false;

    public static MessageInteractor getInstance() {
        if (interactor == null) {
            interactor = new MessageInteractor();
        }
        return interactor;
    }

    private MessageInteractor() {
        this.mContext = MyApplication.getAppContext();
    }

    public void getAllMessages() {
        isMessageUpdatingRunning = true;
        getAllMessages(0);
    }

    public void getAllMessages(long groupId) {
        isMessageUpdatingRunning = true;
//        startTimer();
        new CloudMessageHelper(mContext).getAllMessages(groupId, this);
    }

    public void updateMessageToCloud() {

        ChatDbHelper mDbHelper = new ChatDbHelper(mContext);
        ArrayList<MessageModel> messageModels = (ArrayList<MessageModel>) mDbHelper.getUnSyncedMessages();
        if (messageModels != null && messageModels.size() > 0) {
            isMessageSyncing = true;
            new CloudMessageHelper(mContext).setAllMessages(messageModels, this);
        }
    }

    @Override
    public void onGetAllMessagesSuccess(ArrayList<MessageModel> messageModels) {
        Intent i = new Intent("chat");
        mContext.sendBroadcast(i);
        isMessageUpdatingRunning = false;
    }

    @Override
    public void onGetAllMessagesFailed(AppError error) {
        isMessageUpdatingRunning = false;
    }

    @Override
    public void onSetAllMessagesSuccess(ArrayList<MessageModel> messageModels) {
        isMessageSyncing = false;
        new ChatDbHelper(mContext).updateAllSent(messageModels);
        Log.d(TAG, "SUCCESS TO UPDATE MESSAGE ||SUCCESS TO UPDATE MESSAGE ||SUCCESS TO UPDATE MESSAGE ||SUCCESS TO UPDATE MESSAGE ||");
    }


    @Override
    public void onSetAllMessagesFailed(AppError error) {
        isMessageSyncing = false;
        Log.d(TAG, "FAILED TO UPDATE MESSAGE ||FAILED TO UPDATE MESSAGE ||FAILED TO UPDATE MESSAGE ||FAILED TO UPDATE MESSAGE ||");
    }

    Timer mTimer;


    private void startTimer() {
        stopTimer();
        mTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                boolean isLoggedIn = new AppSharedPreference(mContext).getBooleanPrefValue(PreferenceConstants.IS_LOGIN);
                if (!isMessageSyncing && !isMessageUpdatingRunning && !isPaused && isLoggedIn) {
//                    updateMessageToCloud();
//                    getAllMessages();
                }
            }
        };
        mTimer.schedule(task, 2000, 5 * 1000);
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void pauseTimer() {
        isPaused = true;
    }

    public void resumeTimer() {
        isPaused = false;
    }
}
