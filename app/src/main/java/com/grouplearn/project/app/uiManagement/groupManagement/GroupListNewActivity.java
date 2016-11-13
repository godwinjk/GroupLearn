package com.grouplearn.project.app.uiManagement.groupManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.GroupSectionPagerAdapter;
import com.grouplearn.project.app.uiManagement.contactManagement.ContactListActivity;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.settingsManagement.AboutActivity;
import com.grouplearn.project.app.uiManagement.settingsManagement.SettingsActivity;
import com.grouplearn.project.utilities.views.DisplayInfo;

public class GroupListNewActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    Context mContext;
    TabLayout tlGroups;
    ViewPager vpGroups;
    GroupSectionPagerAdapter mSectionPagerAdapter;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView mNavigationView;

    NavigationMenuController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        setupToolbar("Groups", true);
        mContext = this;
        initializeWidgets();
        registerListeners();
        makeGodwinBot();
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
        vpGroups = (ViewPager) findViewById(R.id.vp_group);
        tlGroups = (TabLayout) findViewById(R.id.tl_groups);
        tlGroups.setupWithViewPager(vpGroups, true);

        mSectionPagerAdapter = new GroupSectionPagerAdapter(getSupportFragmentManager());
        vpGroups.setAdapter(mSectionPagerAdapter);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

    }

    @Override
    public void registerListeners() {
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        vpGroups.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNavigationView();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_groups) {
            startActivity(new Intent(mContext, GroupListActivity.class));
        } else if (id == R.id.nav_status) {
            startActivity(new Intent(mContext, StatusActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(mContext, AboutActivity.class));
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

    private void makeGodwinBot() {
        GroupDbHelper mDbHelper = new GroupDbHelper(mContext);
        mDbHelper.getGodwinBot();
    }
}
