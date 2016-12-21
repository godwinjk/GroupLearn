package com.grouplearn.project.app.uiManagement.cloudHelper;

import android.content.Context;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.databaseHelper.CourseDbHelper;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.CourseViewInterface;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseRequest;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseResponse;
import com.grouplearn.project.cloud.courseManagement.get.CloudGetCourseRequest;
import com.grouplearn.project.cloud.courseManagement.get.CloudGetCourseResponse;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.utilities.errorManagement.AppError;

/**
 * Created by WiSilica on 04-12-2016 09:09 for GroupLearn application.
 */

public class CloudCourseManager {
    Context mContext;

    public CloudCourseManager(Context mContext) {
        this.mContext = mContext;
    }

    public void createCourse(GLCourse course, final CloudOperationCallback callback) {
        CloudResponseCallback cloudResponseCallback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudAddCourseResponse response = (CloudAddCourseResponse) cloudResponse;
                if (response.getGlCourses().get(0).getStatus() == 1) {
                    if (callback != null) {
                        callback.onCloudOperationSuccess();
                    }
                } else {
                    if (callback != null) {
                        callback.onCloudOperationFailed(new AppError(000, "Course name exist"));
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (callback != null) {
                    callback.onCloudOperationFailed(new AppError(cloudError));
                }
            }
        };
        CloudAddCourseRequest request = new CloudAddCourseRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setGlCourses(course);
        CloudConnectManager.getInstance(mContext).getCloudCourseManager(mContext).addCourses(request, cloudResponseCallback);
    }

    public void deleteCourse(GLCourse course, CloudOperationCallback callback) {
        CloudResponseCallback cloudResponseCallback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {

            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

            }
        };
    }

    public void getCourse(String key, final CourseViewInterface courseViewInterface) {
        CloudResponseCallback cloudResponseCallback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetCourseResponse response = (CloudGetCourseResponse) cloudResponse;

                if (courseViewInterface != null) {
                    courseViewInterface.onCourseGetSucces(response.getGlCourses());
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (courseViewInterface != null) {
                    courseViewInterface.onCourseGetFailed(new AppError(cloudError));
                }
            }
        };
        CloudGetCourseRequest request = new CloudGetCourseRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setKey(key);
        CloudConnectManager.getInstance(mContext).getCloudCourseManager(mContext).getCourses(request, cloudResponseCallback);
    }

    public void getCourse(final CourseViewInterface courseViewInterface) {
        CloudResponseCallback cloudResponseCallback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetCourseResponse response = (CloudGetCourseResponse) cloudResponse;
                CourseDbHelper helper = new CourseDbHelper(mContext);
                for (GLCourse course : response.getGlCourses()) {
                    helper.updateCourse(course);
                }
                if (courseViewInterface != null) {
                    courseViewInterface.onCourseGetSucces(response.getGlCourses());
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                if (courseViewInterface != null) {
                    courseViewInterface.onCourseGetFailed(new AppError(cloudError));
                }
            }
        };
        CloudGetCourseRequest request = new CloudGetCourseRequest();
        String token = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setUserId(new AppSharedPreference(mContext).getLongPrefValue(PreferenceConstants.USER_ID));
        CloudConnectManager.getInstance(mContext).getCloudCourseManager(mContext).getCourses(request, cloudResponseCallback);
    }
}
