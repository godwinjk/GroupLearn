package com.grouplearn.project.app.uiManagement.interfaces;

import com.grouplearn.project.models.GLCourse;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 09:18 for GroupLearn application.
 */

public interface CourseViewInterface {
    public void onCourseGetSucces(ArrayList<GLCourse> courses);

    public void onCourseGetFailed(AppError error);
}
