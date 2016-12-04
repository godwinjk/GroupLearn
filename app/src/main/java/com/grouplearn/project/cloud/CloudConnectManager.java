package com.grouplearn.project.cloud;

import android.content.Context;

import com.grouplearn.project.cloud.appManagement.AppRegistrationInterface;
import com.grouplearn.project.cloud.appManagement.AppRegistrationManager;
import com.grouplearn.project.cloud.contactManagement.CloudContactManager;
import com.grouplearn.project.cloud.contactManagement.CloudContactManagerInterface;
import com.grouplearn.project.cloud.courseManagement.CloudCourseManager;
import com.grouplearn.project.cloud.courseManagement.CloudCourseManagerInterface;
import com.grouplearn.project.cloud.groupManagement.CloudGroupManager;
import com.grouplearn.project.cloud.groupManagement.CloudGroupManagerInterface;
import com.grouplearn.project.cloud.interest.CloudInterestManager;
import com.grouplearn.project.cloud.interest.CloudInterestManagerInterface;
import com.grouplearn.project.cloud.message.CloudMessageManager;
import com.grouplearn.project.cloud.message.CloudMessageManagerInterface;
import com.grouplearn.project.cloud.userManagement.CloudUserManager;
import com.grouplearn.project.cloud.userManagement.CloudUserManagerInterface;

/**
 * Created by Godwin Joseph on 13-05-2016 15:48 for Group Learn application.
 */
public class CloudConnectManager {
    private static CloudConnectManager mManager;
    private static Context mContext;

    private CloudConnectManager(Context context) {
        mContext = context;
    }

    public static CloudConnectManager getInstance(Context context) {
        if (mManager == null) mManager = new CloudConnectManager(context);
        return mManager;
    }

    public AppRegistrationInterface getCloudAppRegistrationManager(Context context) {
        return new AppRegistrationManager(context);
    }

    public CloudUserManagerInterface getCloudUserManager(Context context) {
        return new CloudUserManager(context);
    }

    public CloudGroupManagerInterface getCloudGroupManager(Context context) {
        return new CloudGroupManager(context);
    }

    public CloudContactManagerInterface getCloudContactManager(Context context) {
        return new CloudContactManager(context);
    }

    public CloudMessageManagerInterface getCloudMessageManager(Context context) {
        return new CloudMessageManager(context);
    }

    public CloudCourseManagerInterface getCloudCourseManager(Context context) {
        return new CloudCourseManager(context);
    }

    public CloudInterestManagerInterface getCloudInterestManager(Context context) {
        return new CloudInterestManager(context);
    }
}
