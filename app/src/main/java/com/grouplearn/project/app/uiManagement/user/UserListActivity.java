package com.grouplearn.project.app.uiManagement.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.contact.ContactRequestActivity;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.course.CourseMenuActivity;
import com.grouplearn.project.app.uiManagement.group.GroupListNewActivity;
import com.grouplearn.project.app.uiManagement.group.GroupMenuActivity;
import com.grouplearn.project.app.uiManagement.group.InvitationActivity;
import com.grouplearn.project.app.uiManagement.group.RequestAcceptingActivity;
import com.grouplearn.project.app.uiManagement.interactor.ContactInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.search.SearchUserActivity;
import com.grouplearn.project.app.uiManagement.settings.SettingsActivity;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.SpaceItemDecoration;

import java.util.ArrayList;

public class UserListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ContactViewInterface {
    private Toolbar toolbar;
    private Context mContext;

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView mNavigationView;

    NavigationMenuController mNavController;
    UserListAdapter mAdapter;
    RecyclerView rvUsers;
    SwipeRefreshLayout srlMain;
    ContactInteractor contactInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        mContext = this;
        toolbar = setupToolbar("Users", true);
        toolbar.setSubtitle("Users");
        initializeWidgets();
        registerListeners();
        fetchUsers();
    }

    @Override
    public void initializeWidgets() {
        contactInteractor = new ContactInteractor(mContext);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mNavController = new NavigationMenuController(mContext, mNavigationView, 1);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView = mNavController.createNavigationMenu();

        rvUsers = (RecyclerView) findViewById(R.id.rv_users);
        rvUsers.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        rvUsers.addItemDecoration(new SpaceItemDecoration(10));
        mAdapter = new UserListAdapter();
        rvUsers.setAdapter(mAdapter);

        srlMain = (SwipeRefreshLayout) findViewById(R.id.srl_recycler);
    }

    @Override
    public void registerListeners() {
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchUsers();
            }
        });
        mAdapter.setOnItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model,int action, View v) {
                GLContact contact = (GLContact) model;
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("user", contact);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action,View v) {

            }
        });
    }

    private void fetchUsers() {
        contactInteractor.getRandomUsers(this);
        srlMain.setRefreshing(true);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_dash_board) {

            startActivity(new Intent(mContext, GroupListNewActivity.class));
            finish();
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(mContext, UserProfileActivity.class));
        } else if (id == R.id.nav_group) {
            startActivity(new Intent(mContext, GroupMenuActivity.class));
        } else if (id == R.id.nav_course) {
            startActivity(new Intent(mContext, CourseMenuActivity.class));
        } else if (id == R.id.nav_users) {
            startActivity(new Intent(mContext, SearchUserActivity.class));
        } else if (id == R.id.nav_request) {
            startActivity(new Intent(mContext, RequestAcceptingActivity.class));
        } else if (id == R.id.nav_request_contact) {
            startActivity(new Intent(mContext, ContactRequestActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(mContext, SettingsActivity.class));
        } else if (id == R.id.nav_invitations) {
            startActivity(new Intent(mContext, InvitationActivity.class));
        }
        mNavigationView.postInvalidate();
        mNavigationView.invalidate();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onGetAllContactsFromCloud(ArrayList<GLContact> contactModels) {
        srlMain.setRefreshing(false);
        mAdapter.setContacts(contactModels);
    }

    @Override
    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {
        srlMain.setRefreshing(false);
        mAdapter.setContacts(contactModels);
    }

    @Override
    public void onGetContactsFailed(AppError error) {
        srlMain.setRefreshing(false);
    }
}
