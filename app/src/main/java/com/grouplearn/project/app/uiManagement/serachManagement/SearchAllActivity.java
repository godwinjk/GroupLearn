package com.grouplearn.project.app.uiManagement.serachManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.SearchPagerAdapter;
import com.grouplearn.project.app.uiManagement.contactManagement.ContactListActivity;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.groupManagement.GroupListActivity;
import com.grouplearn.project.app.uiManagement.groupManagement.InvitationActivity;
import com.grouplearn.project.app.uiManagement.groupManagement.RequestAcceptingActivity;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.settingsManagement.AboutActivity;
import com.grouplearn.project.app.uiManagement.settingsManagement.SettingsActivity;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class SearchAllActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "SearchAllActivity";
    SearchPagerAdapter mPagerAdapter;
    ViewPager vpSearchPager;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView mNavigationView;
    Toolbar mToolbar;

    NavigationMenuController mNavController;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mToolbar = setupToolbar("Search", true);
        mContext = this;

        initializeWidgets();
        registerListeners();
        processForSub();
        createNavigationView();
    }

    private void processForSub() {
        int showDrawerLayout = getIntent().getIntExtra("showDrawer", -1);
        if (showDrawerLayout != -1) {
            mDrawerToggle.setDrawerIndicatorEnabled(false);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if (vpSearchPager != null) {
                vpSearchPager.setCurrentItem(1);
            }
        }
    }

    private void createNavigationView() {
        if (mNavController != null) {
            mNavigationView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mNavController.refreshMenu();
                }
            }, 100);
        } else {
            mNavController = new NavigationMenuController(mContext, mNavigationView);
        }
        mNavigationView = mNavController.createNavigationMenu();
    }

    @Override
    public void initializeWidgets() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mPagerAdapter = new SearchPagerAdapter(getSupportFragmentManager());
        vpSearchPager = (ViewPager) findViewById(R.id.vp_search);
        vpSearchPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void registerListeners() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_status) {
            startActivity(new Intent(mContext, StatusActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(mContext, AboutActivity.class));
        } else if (id == R.id.nav_groups) {
            startActivity(new Intent(mContext, GroupListActivity.class));
        } else if (id == R.id.nav_contacts) {
            startActivity(new Intent(mContext, ContactListActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(mContext, SettingsActivity.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(mContext, RequestAcceptingActivity.class));
        } else if (id == R.id.nav_invitations) {
            startActivity(new Intent(mContext, InvitationActivity.class));
        } else if (id == R.id.nav_signout) {
            new SignOutInteractor(mContext).doSignOut(new SignOutListener() {
                @Override
                public void onSignOutSuccessful() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(mContext, SplashScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            DisplayInfo.showToast(mContext, "Sign out successfully.");
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @Override
                public void onSignOutFailed() {

                }

                @Override
                public void onSignOutCanceled() {

                }
            });
        }
        mNavigationView.postInvalidate();
        mNavigationView.invalidate();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
