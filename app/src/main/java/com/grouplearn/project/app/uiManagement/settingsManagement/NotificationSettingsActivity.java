package com.grouplearn.project.app.uiManagement.settingsManagement;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;

public class NotificationSettingsActivity extends BaseActivity implements View.OnClickListener {
    TextView tvShowNotification, tvShowInAppNotification, tvNotificationSound, tvShowContent;
    CheckBox cbShowNotification, cbShowInAppNotification, cbNotificationSound, cbNotificationContent;

    Context mContext;
    AppSharedPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);
        Toolbar toolbar = setupToolbar("Privacy", true);

        mContext = this;
        mPref = new AppSharedPreference(mContext);

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvNotificationSound = (TextView) findViewById(R.id.tv_notification_sound);
        tvShowNotification = (TextView) findViewById(R.id.tv_show_notification);
        tvShowInAppNotification = (TextView) findViewById(R.id.tv_in_app_notification);
        tvShowContent = (TextView) findViewById(R.id.tv_show_content);

        cbNotificationSound = (CheckBox) findViewById(R.id.cb_notification_sound);
        cbShowNotification = (CheckBox) findViewById(R.id.cb_show_notification);
        cbShowInAppNotification = (CheckBox) findViewById(R.id.cb_in_app_notification);
        cbNotificationContent = (CheckBox) findViewById(R.id.cb_show_content);

        boolean isShowNotification = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_NOTIFICATION);
        boolean isNotificationSound = mPref.getBooleanPrefValue(PreferenceConstants.IS_NOTIFICATION_SOUND);
        boolean isInAppNotification = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_IN_APP_NOTIFICATION);
        boolean isShowContent = mPref.getBooleanPrefValue(PreferenceConstants.IS_SHOW_CONTENT_IN_NOTIFICATION);

        cbNotificationSound.setChecked(isNotificationSound);
        cbShowInAppNotification.setChecked(isInAppNotification);
        cbShowNotification.setChecked(isShowNotification);
        cbNotificationContent.setChecked(isShowContent);
    }

    @Override
    public void registerListeners() {
        tvNotificationSound.setOnClickListener(this);
        tvShowNotification.setOnClickListener(this);
        tvShowInAppNotification.setOnClickListener(this);
        tvShowContent.setOnClickListener(this);

        cbNotificationSound.setOnClickListener(this);
        cbShowNotification.setOnClickListener(this);
        cbShowInAppNotification.setOnClickListener(this);
        cbNotificationContent.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_notification_sound:
                cbNotificationSound.setChecked(!cbNotificationSound.isChecked());
                mPref.setBooleanPrefValue(PreferenceConstants.IS_NOTIFICATION_SOUND, cbNotificationSound.isChecked());
                break;
            case R.id.tv_show_notification:
                cbShowNotification.setChecked(!cbShowNotification.isChecked());
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_NOTIFICATION, cbShowNotification.isChecked());
                break;
            case R.id.tv_in_app_notification:
                cbShowInAppNotification.setChecked(!cbShowInAppNotification.isChecked());
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_IN_APP_NOTIFICATION, cbShowInAppNotification.isChecked());
                break;
            case R.id.tv_show_content:
                cbNotificationContent.setChecked(!cbNotificationContent.isChecked());
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_CONTENT_IN_NOTIFICATION, cbNotificationContent.isChecked());
                break;

            case R.id.cb_notification_sound:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_NOTIFICATION_SOUND, cbNotificationSound.isChecked());
                break;
            case R.id.cb_show_notification:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_NOTIFICATION, cbShowNotification.isChecked());
                break;
            case R.id.cb_in_app_notification:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_IN_APP_NOTIFICATION, cbShowInAppNotification.isChecked());
                break;
            case R.id.cb_show_content:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_CONTENT_IN_NOTIFICATION, cbNotificationContent.isChecked());
                break;
        }
    }
}
