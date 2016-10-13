package com.grouplearn.project.app.uiManagement.cloudHelper;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.ServerSyncTimes;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.MessageConversationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.message.messageGet.CloudGetMessageRequest;
import com.grouplearn.project.cloud.message.messageGet.CloudGetMessageResponse;
import com.grouplearn.project.cloud.message.messageSet.CloudSetMessageRequest;
import com.grouplearn.project.cloud.message.messageSet.CloudSetMessageResponse;
import com.grouplearn.project.models.MessageModel;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:46 for Group Learn application.
 */
public class CloudMessageHelper {
    Context mContext;
    final int LIMIT = 100;

    public CloudMessageHelper(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    public void getAllMessages(final long groupId, @NonNull final MessageConversationCallback conversationCallback) {
        CloudGetMessageRequest request = new CloudGetMessageRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setGroupId(groupId);
        request.setLimit(LIMIT);
        long serverSyncTime = new ServerSyncTimes(mContext).getLastUpdatedTime(ServerSyncTimes.MESSAGE_GET);
        request.setStartTime(serverSyncTime);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetMessageResponse response = (CloudGetMessageResponse) cloudResponse;
                int messageCount = response.getMessageCount();
                BigDecimal updatedTime = new BigDecimal("0");
                if (messageCount > 0 && response.getMessageModels() != null) {
                    new ChatDbHelper(mContext).addMessageToDb(response.getMessageModels());
                    if (conversationCallback != null) {
                        conversationCallback.onGetAllMessagesSuccess(response.getMessageModels());
                    }
                    if (messageCount > LIMIT) {
                        getAllMessages(groupId, conversationCallback);
                    }
                } else {
                    if (conversationCallback != null) {
                        conversationCallback.onGetAllMessagesFailed(new AppError(ErrorHandler.NO_ITEMS, ErrorHandler.ErrorMessage.NO_ITEMS));
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (conversationCallback != null) {
                    conversationCallback.onGetAllMessagesFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                }
                if (cloudError.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(mContext);
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(mContext, SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(i);
                        }

                        @Override
                        public void onSignOutFailed() {

                        }

                        @Override
                        public void onSignOutCanceled() {

                        }
                    });
                }
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudMessageManager(mContext).getAllMessages(request, callback);
    }

    public void setAllMessages(ArrayList<MessageModel> messages, @NonNull final MessageConversationCallback conversationCallback) {
        CloudSetMessageRequest request = new CloudSetMessageRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setMessageModels(messages);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudSetMessageResponse response = (CloudSetMessageResponse) cloudResponse;
                int contactCount = response.getMessageCount();
                if (response.getMessageModels() != null) {
                    if (conversationCallback != null) {
                        conversationCallback.onSetAllMessagesSuccess(response.getMessageModels());
                    }
                } else {
                    if (conversationCallback != null) {
                        conversationCallback.onSetAllMessagesFailed(new AppError(ErrorHandler.NO_ITEMS, ErrorHandler.ErrorMessage.NO_ITEMS));
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (conversationCallback != null) {
                    conversationCallback.onSetAllMessagesFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                }
                if (cloudError.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(mContext);
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(mContext, SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(i);
                        }

                        @Override
                        public void onSignOutFailed() {

                        }

                        @Override
                        public void onSignOutCanceled() {

                        }
                    });
                }
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudMessageManager(mContext).setAllMessages(request, callback);
    }
}
