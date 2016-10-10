package com.grouplearn.project.app.uiManagement.settingsManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.userManagement.UserProfileActivity;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;
    TextView tvUserSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = setupToolbar("Settings", true);
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvUserSettings = (TextView) findViewById(R.id.tv_user_settings);
    }

    @Override
    public void registerListeners() {
        tvUserSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_settings:
                startActivity(new Intent(mContext, UserProfileActivity.class));
                break;
        }
    }
}
