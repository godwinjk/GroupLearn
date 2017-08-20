package com.grouplearn.project.app.uiManagement.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.search.SearchGroupsActivity;

public class GroupMenuActivity extends BaseActivity implements View.OnClickListener {
    LinearLayout llMyCourse, llSearchCourse, llCreateCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_menu);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Groups");
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        llMyCourse = (LinearLayout) findViewById(R.id.ll_my_group);
        llCreateCourse = (LinearLayout) findViewById(R.id.ll_create_group);
        llSearchCourse = (LinearLayout) findViewById(R.id.ll_search_group);
    }

    @Override
    public void registerListeners() {
        llMyCourse.setOnClickListener(this);
        llCreateCourse.setOnClickListener(this);
        llSearchCourse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_my_group:
                startActivity(new Intent(getApplicationContext(), GroupListActivity.class));
                break;
            case R.id.ll_create_group:
                startActivity(new Intent(getApplicationContext(), AddGroupActivity.class));
                break;
            case R.id.ll_search_group:
                startActivity(new Intent(getApplicationContext(), SearchGroupsActivity.class));
                break;
        }
    }
}
