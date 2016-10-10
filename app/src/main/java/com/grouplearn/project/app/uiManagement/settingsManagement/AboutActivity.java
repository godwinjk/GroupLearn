package com.grouplearn.project.app.uiManagement.settingsManagement;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.utilities.AppUtility;

public class AboutActivity extends BaseActivity {
    EditText etFcmToken;
    TextView tvVersion;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvVersion.setText("Version : " + AppUtility.getAppVersion(this));

        etFcmToken = (EditText) findViewById(R.id.et_fcm_token);
        etFcmToken.setVisibility(View.GONE);
        etFcmToken.setText(new AppSharedPreference(this).getStringPrefValue(PreferenceConstants.GCM_TOKEN));
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {

    }

    @Override
    public void registerListeners() {
        tvVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (count > 10) {
                    etFcmToken.setVisibility(View.VISIBLE);
                }
                count++;
            }
        });
    }

}
