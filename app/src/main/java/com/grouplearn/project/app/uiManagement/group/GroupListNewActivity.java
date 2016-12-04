package com.grouplearn.project.app.uiManagement.group;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.Toast;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.GroupSectionPagerAdapter;
import com.grouplearn.project.app.uiManagement.contact.ContactListActivity;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.course.CourseAddingActivity;
import com.grouplearn.project.app.uiManagement.course.CourseSearchActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.search.SearchGroupsActivity;
import com.grouplearn.project.app.uiManagement.search.SearchUserActivity;
import com.grouplearn.project.app.uiManagement.settings.AboutActivity;
import com.grouplearn.project.app.uiManagement.settings.SettingsActivity;
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
        setupToolbar("GroupLearn", true);

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
        if (id == R.id.nav_my_groups) {
            startActivity(new Intent(mContext, GroupListActivity.class));
        } else if (id == R.id.nav_add_group) {
            startActivity(new Intent(mContext, AddGroupActivity.class));
        } else if (id == R.id.nav_search_group) {
            startActivity(new Intent(mContext, SearchGroupsActivity.class));
        } else if (id == R.id.nav_add_course) {
            startActivity(new Intent(mContext, CourseAddingActivity.class));
        } else if (id == R.id.nav_search_course) {
            startActivity(new Intent(mContext, CourseSearchActivity.class));
        } else if (id == R.id.nav_status) {
            startActivity(new Intent(mContext, StatusActivity.class));
        } else if (id == R.id.nav_contacts_list) {
            startActivity(new Intent(mContext, ContactListActivity.class));
        } else if (id == R.id.nav_contacts_search) {
            startActivity(new Intent(mContext, SearchUserActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(mContext, AboutActivity.class));
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

    boolean isBackPressed;
    Toast mToast;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (isBackPressed) {
            if (mToast != null) {
                mToast.cancel();
            }
            super.onBackPressed();
        } else {
            isBackPressed = true;
            if (mToast != null) {
                mToast.cancel();
                mToast = null;
            }
            mToast = Toast.makeText(mContext, "Press again to exit from GroupLearn", Toast.LENGTH_SHORT);
            mToast.show();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isBackPressed = false;
            }
        }, 2 * 1000);
    }   
}
