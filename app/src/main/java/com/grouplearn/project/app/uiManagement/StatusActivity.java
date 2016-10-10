package com.grouplearn.project.app.uiManagement;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.utilities.views.DisplayInfo;


public class StatusActivity extends BaseActivity implements View.OnClickListener {
    EditText etStatus;
    TextView tvRemainingChars, tvLater, tvUpdate;
    AppSharedPreference mPref;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mContext = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        tvLater = (TextView) findViewById(R.id.tv_later);
        tvUpdate = (TextView) findViewById(R.id.tv_update);
        etStatus = (EditText) findViewById(R.id.et_status);
        tvRemainingChars = (TextView) findViewById(R.id.tv_num_of_char);
        mPref = new AppSharedPreference(mContext);

        String status = mPref.getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);
        if (status != null)
            etStatus.setText(status);
    }

    @Override
    public void registerListeners() {
        tvUpdate.setOnClickListener(this);
        tvLater.setOnClickListener(this);
        etStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String status = s.toString().trim();
                int length = status.length();
                tvRemainingChars.setText(length + "/140");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_later:
                onBackPressed();
                break;
            case R.id.tv_update:
                updateStatusToCloud(etStatus.getText().toString());
                break;
        }
    }

    private void updateStatusToCloud(final String status) {
        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        DisplayInfo.showLoader(mContext, "Setting status...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                mPref.setStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS, status);
                finish();
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).setStatus(request, callback);
    }
}
