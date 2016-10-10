package com.grouplearn.project.app.uiManagement.serachManagement;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.SearchPagerAdapter;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.Log;

import java.util.ArrayList;

public class SearchAllActivity extends BaseActivity {
    private static final String TAG = "SearchAllActivity";
    SearchPagerAdapter mPagerAdapter;
    ViewPager vpSearchPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_all);
        Toolbar toolbar = setupToolbar("Search", true);

        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        mPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        vpSearchPager = (ViewPager) findViewById(R.id.vp_search);
        vpSearchPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void registerListeners() {

    }

}
