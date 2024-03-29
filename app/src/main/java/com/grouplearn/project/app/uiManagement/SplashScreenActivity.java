package com.grouplearn.project.app.uiManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;

import com.google.firebase.iid.FirebaseInstanceId;
import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.interactor.AppRegistrationTask;
import com.grouplearn.project.app.uiManagement.user.LoginActivity;
import com.grouplearn.project.app.uiManagement.user.UserListActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationRequest;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.GeneralAlert;
import com.grouplearn.project.utilities.Helper;
import com.grouplearn.project.utilities.Log;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends BaseActivity {

    private static final String TAG = "SplashScreenActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 10;
    private Context mContext;
    AppSharedPreference mPref;
    CoordinatorLayout splashCoordinate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mContext = this;
        initializeWidgets();
        registerListeners();
        doInitialTask();
//        startSplashAnimation();
    }

    private void registerApp() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        CloudAppRegistrationRequest request = new CloudAppRegistrationRequest();
        request.setPhoneOsVersion("ANDROID_" + Build.VERSION.RELEASE);
        request.setPhoneModel(Build.MODEL);
        request.setAppVersion(Helper.getAppVersion(mContext));
        request.setPhoneUniqueId(Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID));
//        TelephonyManager mngr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        request.setImeiNumber("9192929929");
        String gcmToken = (refreshedToken == null) ? mPref.getStringPrefValue(PreferenceConstants.GCM_TOKEN) : refreshedToken;
        mPref.setStringPrefValue(PreferenceConstants.GCM_TOKEN, gcmToken);
        request.setGcmToken(gcmToken);

        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudAppRegistrationResponse response = (CloudAppRegistrationResponse) cloudResponse;
                Log.i(TAG, "APP UNIQUE ID" + response.getAppUniqueId());
                mPref.setIntegerPrefValue(PreferenceConstants.APP_ID, response.getAppUniqueId());
                Intent i = new Intent(mContext, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                try {
                    Log.i(TAG, "FAILED FAILED FAILED FAILED FAILED FAILED");
                    GeneralAlert.showAlert(mContext, "No Internet", "Please provide a healthy internet connection");
                    Snackbar.make(splashCoordinate, "No Internet connection please provide a healthy internet connection", Snackbar.LENGTH_LONG)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    doInitialTask();
                                }
                            }).show();
                } catch (Exception e) {
                    Log.i(TAG, "FAILED FAILED FAILED FAILED FAILED FAILED" + e.getLocalizedMessage());
                }

            }
        };
        if (AppUtility.checkInternetConnection()) {
            CloudConnectManager.getInstance(mContext).getCloudAppRegistrationManager(mContext).registerApp(request, callback);
        } else {
            Snackbar.make(splashCoordinate, "No Internet connection please provide a healthy internet connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            doInitialTask();
                        }
                    }).show();
        }
    }

    @Override
    public void initializeWidgets() {
        splashCoordinate = (CoordinatorLayout) findViewById(R.id.splash_coordinate);
        mPref = new AppSharedPreference(mContext);
    }

    private void doInitialTask() {
        boolean isLogin = mPref.getBooleanPrefValue(PreferenceConstants.IS_LOGIN);
        if (!isLogin) {
            if (checkPermission()) {
                registerApp();
            }
        } else {
            new AppRegistrationTask(mContext).execute();
            Intent i = new Intent(mContext, UserListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
            finish();
        }
    }

    @Override
    public void registerListeners() {
    }


    Timer mTimer;

    @Override
    protected void onResume() {
        super.onResume();
//        startTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
            final TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    doInitialTask();
                }
            };
            mTimer.schedule(task, 5 * 1000, 5 * 1000);
        }
    }

    private boolean checkPermission() {
//        if (AppUtility.checkPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
//            return false;
//        } else
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (checkPermission()) {
            registerApp();
        }
    }

    private void startScaleAnimation(final View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ScaleAnimation animation = new ScaleAnimation(.5f, 1f, .5f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                animation.setDuration(600);
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatCount(20000);

                v.startAnimation(animation);
            }
        });

    }
}
