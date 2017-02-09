package com.grouplearn.project.app.uiManagement.group;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.widget.Toast;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.adapter.GroupSectionPagerAdapter;
import com.grouplearn.project.app.uiManagement.contact.ContactReadTask;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.course.CourseMenuActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.ContactListInteractor;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.search.SearchUserActivity;
import com.grouplearn.project.app.uiManagement.settings.SettingsActivity;
import com.grouplearn.project.app.uiManagement.user.UserProfileActivity;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class GroupListNewActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "GroupListNewActivity";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
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
//        makeGodwinBot();
        processFroNotification();

    }

    public void processFroNotification() {
        int i = getIntent().getIntExtra("fromNotification", -1);
        Log.i(TAG, "NOTIFICAATIOIN NOTIFICATION CAME  || HA HA HA ");
        if (vpGroups != null) {
            vpGroups.setCurrentItem(1);
        }
    }

    private void createNavigationView() {
        /**
         * Navigation view initialization
         */
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mNavController = new NavigationMenuController(mContext, mNavigationView);

        mNavigationView = mNavController.createNavigationMenu();
    }

    @Override
    public void initializeWidgets() {
        vpGroups = (ViewPager) findViewById(R.id.vp_group);
        tlGroups = (TabLayout) findViewById(R.id.tl_groups);
        tlGroups.setupWithViewPager(vpGroups, true);

        mSectionPagerAdapter = new GroupSectionPagerAdapter(getSupportFragmentManager());
        vpGroups.setAdapter(mSectionPagerAdapter);
        vpGroups.setOffscreenPageLimit(3);
    }

    @Override
    public void registerListeners() {
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

    private void getContactFromPhone() {
        if (checkPermissionForCotacts()) {
            ContactReadTask readTask = new ContactReadTask(mContext);
            readTask.setContactViewInterface(new ContactViewInterface() {
                @Override
                public void onGetAllContacts(ArrayList<GLContact> contactModels) {

                }

                @Override
                public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {

                }

                @Override
                public void onGetContactsFinished(ArrayList<GLContact> contactModels) {
                    new ContactListInteractor(mContext).addAllContacts(contactModels, this);
                }

                @Override
                public void onGetContactsFailed(AppError error) {

                }
            });
            readTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private boolean checkPermissionForCotacts() {
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
        DisplayInfo.showToast(mContext, "Try again");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getContactFromPhone();
        createNavigationView();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, UserProfileActivity.class));
        } else if (id == R.id.nav_group) {
            startActivity(new Intent(mContext, GroupMenuActivity.class));
        } else if (id == R.id.nav_course) {
            startActivity(new Intent(mContext, CourseMenuActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(mContext, SearchUserActivity.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(mContext, RequestAcceptingActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(mContext, SettingsActivity.class));
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
