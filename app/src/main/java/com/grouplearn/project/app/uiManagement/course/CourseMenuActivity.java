package com.grouplearn.project.app.uiManagement.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;

public class CourseMenuActivity extends BaseActivity implements View.OnClickListener {
    TextView tvMyCourse, tvSearchCourse, tvCreateCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_menu);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Course");

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvMyCourse = (TextView) findViewById(R.id.tv_my_course);
        tvCreateCourse = (TextView) findViewById(R.id.tv_create_course);
        tvSearchCourse = (TextView) findViewById(R.id.tv_search_course);
    }

    @Override
    public void registerListeners() {
        tvMyCourse.setOnClickListener(this);
        tvCreateCourse.setOnClickListener(this);
        tvSearchCourse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_course:
                startActivity(new Intent(getApplicationContext(), CourseAddingActivity.class));
                break;
            case R.id.tv_my_course:
                startActivity(new Intent(getApplicationContext(), MyCourseActivity.class));
                break;
            case R.id.tv_search_course:
                startActivity(new Intent(getApplicationContext(), CourseSearchActivity.class));
                break;
        }
    }
}
