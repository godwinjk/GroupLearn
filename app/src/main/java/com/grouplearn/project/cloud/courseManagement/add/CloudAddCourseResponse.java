package com.grouplearn.project.cloud.courseManagement.add;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GLCourse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 03-12-2016 22:35 for GroupLearn application.
 */

public class CloudAddCourseResponse extends CloudConnectResponse {
    ArrayList<GLCourse> glCourses = new ArrayList<>();

    public ArrayList<GLCourse> getGlCourses() {
        return glCourses;
    }

    public void setGlCourses(ArrayList<GLCourse> glCourses) {
        this.glCourses = glCourses;
    }
}
