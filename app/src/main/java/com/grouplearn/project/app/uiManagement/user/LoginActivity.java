package com.grouplearn.project.app.uiManagement.user;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInRequest;
import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.ConnectionUtilities;
import com.grouplearn.project.utilities.DisplayUtilities;
import com.grouplearn.project.utilities.GeneralAlert;
import com.grouplearn.project.utilities.InputValidator;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 10;
    Context mContext;
    EditText etUserName;
    EditText etPassword;
    Button btnSignIn, btnSignUp;
    AppSharedPreference mPref;
    TextView tvTitle, btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    public void registerListeners() {
        btnSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnForgotPassword.setOnClickListener(this);
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showCustomUrlDialog();
                return false;
            }
        });
    }

    private void showCustomUrlDialog() {
        DisplayInfo.showToast(mContext, "Dev option. Please discard.");
        AppAlertDialog alertDialog = AppAlertDialog.getAlertDialog(mContext);
        alertDialog.setTitle(getString(R.string.title_custom_url));
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_dialog_custom_url, null);

        final TextInputLayout etCustomUrl = (TextInputLayout) v.findViewById(R.id.et_url);
        etCustomUrl.getEditText().setText(CloudConstants.getBaseUrl());
        alertDialog.setView(v);
        alertDialog.setTitle("Custom Url");
        alertDialog.setPositiveButton(getString(R.string.btn_title_Okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton(getString(R.string.btn_title_Okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.setNegativeButton(getString(R.string.btn_title_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton(getString(R.string.btn_title_Okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertDialog.create();
        alertDialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etCustomUrl.getEditText().getText().toString();
                if (!TextUtils.isEmpty(url))
                    mPref.setStringPrefValue(PreferenceConstants.CUSTOM_URL, url);
                else {
                    etCustomUrl.setError("Please Enter a valid ip");
                }
            }
        });
    }

    public void initializeWidgets() {
        mPref = new AppSharedPreference(mContext);
        etUserName = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnSignIn = (Button) findViewById(R.id.btn_login);
        btnSignUp = (Button) findViewById(R.id.btn_sign_up);
        btnForgotPassword = (TextView) findViewById(R.id.btn_forgot_password);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        String userName = mPref.getStringPrefValue(PreferenceConstants.USER_NAME);
        if (userName != null) {
            etUserName.setText(userName);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (ConnectionUtilities.checkInternetConnection(mContext)) {
                    if (validateUserCredentials()) {
                        doLogin();
                    }
                } else {
                    showNoInternetConnectionAlert(mContext);
                }
                break;
            case R.id.btn_sign_up:
                startActivity(new Intent(mContext, SignupActivity.class));
                break;
            case R.id.btn_forgot_password:
                startActivity(new Intent(mContext, ForgotActivity.class));
                break;
        }
    }

    private boolean validateUserCredentials() {
        InputValidator validator = new InputValidator(mContext);
        int status = validator.validateUserName(etUserName);
        if (status != 0)
            return false;
        status = validator.validatePassword(etPassword);
        if (status != 0)
            return false;
        return true;
    }

    private void doLogin() {
        CloudSignInRequest request = new CloudSignInRequest();
        int appId = mPref.getIntegerPrefValue(PreferenceConstants.APP_ID);
        request.setAppUniqueId(appId);
        request.setPassword(etPassword.getText().toString().trim());
        request.setUserName(etUserName.getText().toString().trim());

        DisplayInfo.showLoader(mContext, "Signing in...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayUtilities.dismissDialog();

                CloudSignInResponse response = (CloudSignInResponse) cloudResponse;
                String userToken = response.getUserToken();
                String userName = response.getUserName();
                String userDisplayName = response.getUserDisplayName();
                String userEmail = response.getUserEmail();
                String url = response.getIconUrl();
                long userId = response.getUserId();
                boolean userPrivacy = (response.getUserPrivacy() == 0) ? false : true;
                String userStatus = response.getUserStatus();
                if (userToken != null) {

                    mPref.setStringPrefValue(PreferenceConstants.USER_TOKEN, userToken);
                    mPref.setBooleanPrefValue(PreferenceConstants.IS_LOGIN, true);
                    mPref.setStringPrefValue(PreferenceConstants.USER_NAME, userName);
                    mPref.setStringPrefValue(PreferenceConstants.USER_DISPLAY_NAME, userDisplayName);
                    mPref.setStringPrefValue(PreferenceConstants.USER_EMAIL_ID, userEmail);
                    mPref.setLongPrefValue(PreferenceConstants.USER_ID, userId);
                    mPref.setStringPrefValue(PreferenceConstants.USER_STATUS, userStatus);
                    mPref.setBooleanPrefValue(PreferenceConstants.USER_PRIVACY_STATUS, userPrivacy);

                    mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_CONTENT_IN_NOTIFICATION, true);
                    mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_NOTIFICATION, true);
                    mPref.setBooleanPrefValue(PreferenceConstants.IS_NOTIFICATION_SOUND, true);
                    mPref.setBooleanPrefValue(PreferenceConstants.IS_SHOW_IN_APP_NOTIFICATION, true);

                    mPref.setBooleanPrefValue(PreferenceConstants.IS_ENTER_KEY_SEND_MESSAGE, true);
                    mPref.setBooleanPrefValue(PreferenceConstants.IS_SPEAK_ENABLED, false);
                    mPref.setStringPrefValue(PreferenceConstants.DP_PATH, url);

                    if (checkPermission()) {
                        startMainActivity();
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                GeneralAlert.showAlert(mContext, "Invalid password or phone number");
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).signIn(request, callback);
    }

    private void startMainActivity() {
        Intent intent = new Intent(mContext, UserListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);

    }

    private boolean checkPermission() {
        if (AppUtility.checkPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS
                    },
                    MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
            return false;
        } else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startMainActivity();
    }
}
