package com.grouplearn.project.cloud.courseManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseRequest;
import com.grouplearn.project.cloud.courseManagement.get.CloudGetCourseRequest;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsRequest;

/**
 * Created by WiSilica on 03-12-2016 22:28 for GroupLearn application.
 */

public interface CloudCourseManagerInterface {
    public void addCourses(CloudAddCourseRequest request, CloudResponseCallback callback);

    public void editCourses(CloudAddCourseRequest request, CloudResponseCallback callback);

    public void deleteCourses(CloudGetGroupsRequest request, CloudResponseCallback callback);

    public void getCourses(CloudGetCourseRequest request, CloudResponseCallback callback);
}
