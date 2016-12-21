package com.grouplearn.project.app.uiManagement.group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.search.SearchGroupsActivity;

public class GroupMenuActivity extends BaseActivity implements View.OnClickListener {
    TextView tvMyCourse, tvSearchCourse, tvCreateCourse;

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
        tvMyCourse = (TextView) findViewById(R.id.tv_my_group);
        tvCreateCourse = (TextView) findViewById(R.id.tv_create_group);
        tvSearchCourse = (TextView) findViewById(R.id.tv_search_group);
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
            case R.id.tv_my_group:
                startActivity(new Intent(getApplicationContext(), GroupListActivity.class));
                break;
            case R.id.tv_create_group:
                startActivity(new Intent(getApplicationContext(), AddGroupActivity.class));
                break;
            case R.id.tv_search_group:
                startActivity(new Intent(getApplicationContext(), SearchGroupsActivity.class));
                break;
        }
    }
}
