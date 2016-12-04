package com.grouplearn.project.app.uiManagement.search;

import com.grouplearn.project.models.GoogleSearchResult;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 16-09-2016 15:19 for Group Learn application.
 */
public interface GoogleSearchCallback {
    public void onGetGoogleResultSuccess(ArrayList<GoogleSearchResult> googleSearchResults);

    public void onGetGoogleResultFailed(AppError error);
}
