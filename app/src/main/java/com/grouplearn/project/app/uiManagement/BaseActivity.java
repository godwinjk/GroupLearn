package com.grouplearn.project.app.uiManagement;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.grouplearn.project.R;
import com.grouplearn.project.utilities.GeneralAlert;

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    public static final int APP_ACTIVE = 0;
    public static final int APP_NOT_ACTIVE = 0;
    public static final int APP_PAUSED = 1;
    public static final int APP_DESTROYED = 2;

    public static int appState = APP_NOT_ACTIVE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void setupToolbar() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    public abstract void initializeWidgets();

    public abstract void registerListeners();

    public Toolbar setupToolbar(int resourceTitleId, boolean showNavigationButton) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showNavigationButton);
        getSupportActionBar().setTitle(resourceTitleId);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return mToolbar;
    }

    public Toolbar setupToolbar(String title, boolean showNavigationButton) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showNavigationButton);
        getSupportActionBar().setTitle(title);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        return mToolbar;
    }

    @Override
    protected void onPause() {
        super.onPause();
        appState = APP_PAUSED;
        appState = APP_NOT_ACTIVE;
        hideSoftKeyboard();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideSoftKeyboard();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appState = APP_ACTIVE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appState = APP_DESTROYED;
    }

    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void openSoftKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(view.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public void showNoInternetConnectionAlert(Context context) {
        GeneralAlert.showAlert(context, "Warning !!!", "No internet connection. Please connect to any network and proceed.");
    }
}
