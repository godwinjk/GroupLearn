package com.grouplearn.project.app.uiManagement.group;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;

public class SubscribedGroupRequestListActivity extends BaseActivity {
    RecyclerView rvRequestedGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribed_group_request_list);
        setupToolbar("Requests", true);

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {

    }

    @Override
    public void registerListeners() {

    }

}
