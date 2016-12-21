package com.grouplearn.project.app.uiManagement.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    Context mContext;
    TextView tvUserSettings;
    TextView tvNotificationSettings;
    TextView tvChatSettings;
    TextView tvAbout;
    TextView tvSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Settings");
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvUserSettings = (TextView) findViewById(R.id.tv_user_settings);
        tvNotificationSettings = (TextView) findViewById(R.id.tv_notification_settings);
        tvChatSettings = (TextView) findViewById(R.id.tv_chat_settings);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvSignOut = (TextView) findViewById(R.id.tv_logout);
    }

    @Override
    public void registerListeners() {
        tvUserSettings.setOnClickListener(this);
        tvNotificationSettings.setOnClickListener(this);
        tvChatSettings.setOnClickListener(this);
        tvAbout.setOnClickListener(this);
        tvSignOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_user_settings:
                startActivity(new Intent(mContext, UserProfileActivity.class));
                break;
            case R.id.tv_notification_settings:
                startActivity(new Intent(mContext, NotificationSettingsActivity.class));
                break;
            case R.id.tv_chat_settings:
                startActivity(new Intent(mContext, ChatSettings.class));
                break;
            case R.id.tv_about:
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
            case R.id.tv_logout:
                new SignOutInteractor(mContext).doSignOut(new SignOutListener() {
                    @Override
                    public void onSignOutSuccessful() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(mContext, SplashScreenActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                DisplayInfo.showToast(mContext, "Sign out successfully.");
                                startActivity(intent);
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onSignOutFailed() {

                    }

                    @Override
                    public void onSignOutCanceled() {

                    }
                });
                break;
        }
    }
}
