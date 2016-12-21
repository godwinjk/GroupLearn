package com.grouplearn.project.app.uiManagement.settings;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;

public class BrowserActivity extends BaseActivity {
    WebView wvBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Browser");
        initializeWidgets();
        registerListeners();

    }

    @Override
    public void initializeWidgets() {
        wvBrowser = (WebView) findViewById(R.id.wv_browser);
        wvBrowser.loadUrl("https://developer.android.com/reference/android/webkit/WebView.html");
    }

    @Override
    public void registerListeners() {

    }

}
