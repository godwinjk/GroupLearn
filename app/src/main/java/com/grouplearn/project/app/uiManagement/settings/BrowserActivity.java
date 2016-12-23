package com.grouplearn.project.app.uiManagement.settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.utilities.InputValidator;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class BrowserActivity extends BaseActivity {
    WebView wvBrowser;
    String uri;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        Toolbar toolbar = setupToolbar("GroupLearn", true);
        toolbar.setSubtitle("Browser");
        mContext = this;
//        toolbar.setNavigationIcon(R.drawable.ic_cross);
        String s = getIntent().getStringExtra("uri");
        if (s != null) {
            if (InputValidator.isURI(s)) {
                uri = s;
            } else {
                DisplayInfo.showToast(mContext, "Invalid uri passed");
                finish();
                return;
            }
        } else {
            DisplayInfo.showToast(mContext, "No uri passed");
            finish();
            return;
        }
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        wvBrowser = (WebView) findViewById(R.id.wv_browser);
        wvBrowser.setWebViewClient(new WebViewClient());
        wvBrowser.loadUrl(uri);
    }

    @Override
    public void registerListeners() {

    }

}
