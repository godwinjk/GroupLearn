package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.bean.GLRequest;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 21-08-2016 19:42 for Group Learn application.
 */
public interface GroupRequestCallback {
    public void onGroupRequestFetchSuccess(ArrayList<GLRequest> requestModels);

    public void onGroupRequestFetchFailed(AppError error);
}
