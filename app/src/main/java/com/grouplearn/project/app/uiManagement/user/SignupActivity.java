package com.grouplearn.project.app.uiManagement.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.signUp.CloudSignUpRequest;
import com.grouplearn.project.utilities.ConnectionUtilities;
import com.grouplearn.project.utilities.InputValidator;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class SignupActivity extends BaseActivity implements View.OnClickListener {
    EditText etDisplayName, etMailId,etPassword;
    Button btnSignUp;
    Context mContext;
    AppSharedPreference mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        setupToolbar("Sign Up", true);
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        mPref = new AppSharedPreference(mContext);

        etDisplayName = (EditText) findViewById(R.id.et_display_name);
        etMailId = (EditText) findViewById(R.id.et_mail_id);
        etPassword = (EditText) findViewById(R.id.pf_password);

        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
    }

    @Override
    public void registerListeners() {
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign_up:
                if (ConnectionUtilities.checkInternetConnection(mContext)) {
                    if (validateUserCredentials()) {
                        doSignUp();
                    }
                }
                break;
        }
    }

    private void doSignUp() {
        CloudSignUpRequest request = new CloudSignUpRequest();
        request.setAppUniqueId(mPref.getIntegerPrefValue(PreferenceConstants.APP_ID));
        String displayName = etDisplayName.getText().toString();
        String mailId = etMailId.getText().toString();
        String password = etPassword.getText().toString();

        request.setDisplayName(displayName);
        request.setMailId(mailId);

        request.setPassword(password);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                startActivity(new Intent(mContext, LoginActivity.class));
                DisplayInfo.showToast(mContext, "User Created Successfully.");
                DisplayInfo.dismissLoader(mContext);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "User Created Failed.");
            }
        };
        DisplayInfo.showLoader(mContext, "Please wait...");
        CloudConnectManager connectManager = CloudConnectManager.getInstance(mContext);
        connectManager.getCloudUserManager(mContext).singUp(request, callback);
    }

    private boolean validateUserCredentials() {
        InputValidator validator = new InputValidator(mContext);
        int status = 0;

        status = validator.validateFullName(etDisplayName);
        if (status != 0)
            return false;
        status = validator.validateEmail(etMailId);
        if (status != 0)
            return false;
        status = validator.validatePassword(etPassword);
        if (status != 0)
            return false;
        return true;
    }
}
