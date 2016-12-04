package com.grouplearn.project.cloud.courseManagement.add;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLCourse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 03-12-2016 22:32 for GroupLearn application.
 */

public class CloudAddCourseRequest extends CloudConnectRequest {
    ArrayList<GLCourse> glCourses = new ArrayList<>();

    public ArrayList<GLCourse> getGlCourses() {
        return glCourses;
    }

    public void setGlCourses(ArrayList<GLCourse> glCourses) {
        this.glCourses = glCourses;
    }

    public void setGlCourses(GLCourse glCourses) {
        this.glCourses.add(glCourses);
    }

    @Override
    public int validate() {
        return 0;
    }
}
