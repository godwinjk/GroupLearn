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

public class ChatSettings extends BaseActivity implements View.OnClickListener {
    TextView tvSpeak, tvEnterKeySend;
    CheckBox cbSpeak, cbEnterKeySend;

    Context mContext;
    AppSharedPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        Toolbar toolbar = setupToolbar("Chat settings", true);

        mContext = this;
        mPref = new AppSharedPreference(mContext);
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvSpeak = (TextView) findViewById(R.id.tv_speak);
        tvEnterKeySend = (TextView) findViewById(R.id.tv_enter_key_send_message);

        cbSpeak = (CheckBox) findViewById(R.id.cb_speak);
        cbEnterKeySend = (CheckBox) findViewById(R.id.cb_enter_key_send_message);

        cbSpeak.setChecked(mPref.getBooleanPrefValue(PreferenceConstants.IS_SPEAK_ENABLED));
        cbEnterKeySend.setChecked(mPref.getBooleanPrefValue(PreferenceConstants.IS_ENTER_KEY_SEND_MESSAGE));
    }

    @Override
    public void registerListeners() {
        tvSpeak.setOnClickListener(this);
        tvEnterKeySend.setOnClickListener(this);

        cbSpeak.setOnClickListener(this);
        cbEnterKeySend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_speak:
                cbSpeak.setChecked(!cbSpeak.isChecked());
                break;
            case R.id.tv_enter_key_send_message:
                cbEnterKeySend.setChecked(!cbSpeak.isChecked());
                break;
            case R.id.cb_speak:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_SPEAK_ENABLED, cbSpeak.isChecked());
                break;
            case R.id.cb_enter_key_send_message:
                mPref.setBooleanPrefValue(PreferenceConstants.IS_ENTER_KEY_SEND_MESSAGE, cbEnterKeySend.isChecked());
                break;
        }
    }
}
