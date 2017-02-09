package com.grouplearn.project.app.uiManagement.user;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.changePin.CloudChangePinRequest;
import com.grouplearn.project.cloud.userManagement.changePin.CloudChangePinResponse;
import com.grouplearn.project.cloud.userManagement.forgotPasswordRequest.CloudForgotPasswordRequest;
import com.grouplearn.project.cloud.userManagement.forgotPasswordRequest.CloudForgotPasswordResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.InputValidator;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class ForgotActivity extends BaseActivity {
    public static final String TAG = "ForgotActivity";
    public static final int FORGOT_REQUEST = 1;
    public static final int OTP_SUCCESS = 2;

    TextInputLayout etOtp, etUserName, etPassword, etConfirmPassword;
    Button btnSubmit;
    int step = FORGOT_REQUEST;
    LinearLayout llForgotRequest, llChangePin;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Forgot password");

        mContext = this;

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        etOtp = (TextInputLayout) findViewById(R.id.et_otp);
        etUserName = (TextInputLayout) findViewById(R.id.et_user_name);
        etPassword = (TextInputLayout) findViewById(R.id.et_password);
        etConfirmPassword = (TextInputLayout) findViewById(R.id.et_confirm_password);

        llChangePin = (LinearLayout) findViewById(R.id.ll_change_pin);
        llForgotRequest = (LinearLayout) findViewById(R.id.ll_forgot_request);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        updateUiAccordingToStep(step);
    }

    @Override
    public void registerListeners() {
        etOtp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                etOtp.setError(null);
                if (editable.toString().length() != 5) {
                    btnSubmit.setEnabled(false);
                } else {
                    btnSubmit.setEnabled(true);
                }
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (step == FORGOT_REQUEST) {
                    if (new InputValidator(mContext).validateEmail(etUserName.getEditText().getText().toString()) < 0) {
                        etUserName.setError("Enter valid user name");
                    } else {
                        requestForgotPassword();
                    }
                } else {
                    if (validate()) {
                        processOtpConfirmation();
                    }
                }
            }
        });
    }

    private boolean validate() {
        boolean status = true;
        String password = etPassword.getEditText().getText().toString();
        String confirmPassword = etConfirmPassword.getEditText().getText().toString();

        try {
            int otp = Integer.parseInt(etOtp.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            etOtp.setError("Enter 5 digit number");
            return false;
        } catch (Exception e) {
            etOtp.setError("Enter valid otp");
            return false;
        }
        InputValidator validator = new InputValidator(mContext);
        if (validator.validatePassword(etPassword.getEditText()) < 0) {
            etPassword.setError("Enter valid password");
            status = false;
        } else if (validator.validatePassword(etConfirmPassword.getEditText()) < 0) {
            etConfirmPassword.setError("Enter valid password");
            status = false;
        } else if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Password is not matching");
            status = false;
        } else if (TextUtils.isEmpty(etOtp.getEditText().getText().toString())) {
            etOtp.setError("Enter 5 digit valid otp");
            status = false;
        }
        return status;
    }

    public void updateUiAccordingToStep(int step) {
        if (step == FORGOT_REQUEST) {
            llForgotRequest.setVisibility(View.VISIBLE);
            llChangePin.setVisibility(View.GONE);
        } else {
            etUserName.setEnabled(false);
            etUserName.getEditText().setEnabled(false);
            llChangePin.setVisibility(View.VISIBLE);
        }
    }

    private void requestForgotPassword() {
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudForgotPasswordResponse response = (CloudForgotPasswordResponse) cloudResponse;
                if (response.getResponseStatus() == 20001) {
                    step = OTP_SUCCESS;
                    updateUiAccordingToStep(step);

                } else {
                    DisplayInfo.showToast(mContext, "Invalid user name or user not found");
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "something went wrong");
            }
        };
        String userName = etUserName.getEditText().getText().toString();
        CloudForgotPasswordRequest request = new CloudForgotPasswordRequest();
        request.setUserName(userName);
        if (AppUtility.checkInternetConnection()) {
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
            CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).forgotPassword(request, callback);
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }

    private void processOtpConfirmation() {
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudChangePinResponse response = (CloudChangePinResponse) cloudResponse;
                if (response.getResponseStatus() == 20001) {
                    step = OTP_SUCCESS;
                    updateUiAccordingToStep(step);
                } else {
                    DisplayInfo.showToast(mContext, "Otp mismatch");
                    step = FORGOT_REQUEST;
                    updateUiAccordingToStep(step);
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                DisplayInfo.showToast(mContext, "something went wrong");
                updateUiAccordingToStep(step);
            }
        };
        String userName = etUserName.getEditText().getText().toString();
        String password = etPassword.getEditText().getText().toString();
        int otp = Integer.parseInt(etOtp.getEditText().getText().toString());
        CloudChangePinRequest request = new CloudChangePinRequest();
        request.setUserName(userName);
        request.setPassword(password);
        request.setOtp(otp);

        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).changePin(request, callback);
            DisplayInfo.showLoader(mContext, getString(R.string.please_wait));
        } else {
            DisplayInfo.showToast(mContext, getString(R.string.no_network));
        }
    }
}
