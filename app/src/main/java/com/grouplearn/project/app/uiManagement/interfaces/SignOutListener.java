package com.grouplearn.project.app.uiManagement.interfaces;

/**
 * Created by Godwin Joseph on 08-09-2016 14:40 for Group Learn application.
 */
public interface SignOutListener {
    public void onSignOutSuccessful();

    public void onSignOutFailed();
    public void onSignOutCanceled();

}
