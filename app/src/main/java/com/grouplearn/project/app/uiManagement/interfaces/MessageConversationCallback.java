package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.models.GLMessage;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 03-10-2016 10:49 for Group Learn application.
 */
public interface MessageConversationCallback {
    public void onGetAllMessagesSuccess(ArrayList<GLMessage> messageModels);

    public void onGetAllMessagesFailed(AppError error);

    public void onSetAllMessagesSuccess(ArrayList<GLMessage> messageModels);

    public void onSetAllMessagesFailed(AppError error);
}
