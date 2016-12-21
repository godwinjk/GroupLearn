package com.grouplearn.project.app.uiManagement.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.contact.ContactListActivity;

public class ProfileMenuActivity extends BaseActivity implements View.OnClickListener {
    TextView tvStatus, tvContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_menu);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Profile");
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvContacts = (TextView) findViewById(R.id.tv_contacts);

    }

    @Override
    public void registerListeners() {
        tvStatus.setOnClickListener(this);
        tvContacts.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_status:
                startActivity(new Intent(getApplicationContext(), StatusActivity.class));
                break;
            case R.id.tv_contacts:
                startActivity(new Intent(getApplicationContext(), ContactListActivity.class));
                break;
        }
    }
}
