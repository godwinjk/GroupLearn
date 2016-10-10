package com.grouplearn.project.app.uiManagement.settingsManagement;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;

public class PrivacySettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_settings);
        Toolbar toolbar = setupToolbar("Privacy",true);
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
