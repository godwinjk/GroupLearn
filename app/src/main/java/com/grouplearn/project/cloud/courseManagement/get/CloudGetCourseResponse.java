package com.grouplearn.project.cloud.courseManagement.get;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.bean.GLCourse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 03-12-2016 22:46 for GroupLearn application.
 */

public class CloudGetCourseResponse extends CloudConnectResponse {
    int courseCount;
    ArrayList<GLCourse> glCourses = new ArrayList<>();

    public ArrayList<GLCourse> getGlCourses() {
        return glCourses;
    }

    public void setGlCourses(ArrayList<GLCourse> glCourses) {
        this.glCourses = glCourses;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }
}
