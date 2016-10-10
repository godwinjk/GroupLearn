package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 10-05-2016 11:26 for Group Learn application.
 */
public interface GroupViewInterface {
    public void onGroupFetchSuccess(ArrayList<GroupModel> groupModelArrayList);

    public void onGroupFetchFailed(AppError error);
}
