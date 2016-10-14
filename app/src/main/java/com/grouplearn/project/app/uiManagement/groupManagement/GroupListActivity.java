package com.grouplearn.project.app.uiManagement.groupManagement;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.StatusActivity;
import com.grouplearn.project.app.uiManagement.adapter.GroupListAdapter;
import com.grouplearn.project.app.uiManagement.contactManagement.ContactListActivity;
import com.grouplearn.project.app.uiManagement.controllers.NavigationMenuController;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.serachManagement.SearchAllActivity;
import com.grouplearn.project.app.uiManagement.settingsManagement.AboutActivity;
import com.grouplearn.project.app.uiManagement.settingsManagement.SettingsActivity;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class GroupListActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GroupViewInterface {
    private static final String TAG = "GroupListActivity";
    private static final long TRANSLATE_DURATION_MILLIS = 500;
    FloatingActionButton mFab;
    Context mContext;
    GroupListAdapter mGroupListAdapter;
    ListView lvGroupListView;
    TextView tvNoGroups;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    NavigationView mNavigationView;
    Toolbar mToolbar;
    private boolean mVisible = true;
    NavigationMenuController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_group_list);
        mToolbar = setupToolbar(R.string.title_group_learn, false);
        mContext = this;

        initializeWidgets();
        registerListeners();

        revealHide(0);
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
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    private void toggle(final boolean visible) {
        if (mVisible != visible) {
            mVisible = visible;
            int height = mFab.getHeight();

            Log.d(TAG, "FLoating action button state : || :  visible=" + visible + "  -- height=Margin=" + (height + getMarginBottom()));
            int translationY = visible ? 0 : height + getMarginBottom();
            boolean show = visible ? true : false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                mFab.animate().setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(TRANSLATE_DURATION_MILLIS)
                        .translationY(translationY);
            } else {
                setCustomVisibility(show);
            }

            // On pre-Honeycomb a translated view is still clickable, so we need to disable clicks manually
            if (!hasHoneycombApi()) {
                mFab.setClickable(visible);
            }
        }
    }

    private void setCustomVisibility(boolean show) {
        if (show) {
            mFab.setVisibility(View.VISIBLE);
        } else {
            mFab.setVisibility(View.INVISIBLE);
        }
    }

    private boolean hasHoneycombApi() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    private int getMarginBottom() {
        int marginBottom = 0;
        final ViewGroup.LayoutParams layoutParams = mFab.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            marginBottom = ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin;
        }
        return marginBottom;
    }

    @Override
    protected void onResume() {
        super.onResume();
        createNavigationView();

        GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
        interactor.getSubscribedGroups(this);
        registerForContextMenu(lvGroupListView);
        registerReceiver(chatReceiver, new IntentFilter("chat"));
        registerReceiver(chatReceiver, new IntentFilter("chatRefresh"));
        MessageInteractor.getInstance().getAllMessages();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterForContextMenu(lvGroupListView);
        revealHide(200);
        unregisterReceiver(chatReceiver);
        MessageInteractor.getInstance().stopTimer();
    }

    BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("chat")) {
                GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
                interactor.getSubscribedGroupsFromDatabase(GroupListActivity.this);
                interactor.getSubscribedGroups(GroupListActivity.this);
            }
            if (intent.getAction().equals("chatRefresh")) {
                MessageInteractor.getInstance().getAllMessages();
            }
        }
    };

    @Override
    public void initializeWidgets() {

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        lvGroupListView = (ListView) findViewById(R.id.lv_group_list);
        tvNoGroups = (TextView) findViewById(R.id.tv_no_groups);
        mGroupListAdapter = new GroupListAdapter(mContext);
        lvGroupListView.setAdapter(mGroupListAdapter);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        onSearchRequested();
    }

    @Override
    public void registerListeners() {
        mFab.setOnClickListener(this);
        lvGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent chatIntent = new Intent(mContext, GroupChatActivity.class);
                String groupUniqueId = ((GroupModel) mGroupListAdapter.getItem(position)).getGroupUniqueId();

                chatIntent.putExtra("groupCloudId", groupUniqueId);
                startActivity(chatIntent);
//                }
            }
        });
        mNavigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        lvGroupListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount > visibleItemCount && firstVisibleItem > 0) {
                    if (mVisible)
                        toggle(false);
                } else {
                    if (!mVisible)
                        toggle(true);
                }
                int lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (lvGroupListView.isEnabled())
                    revealVisible();
                else
                    revealHide(200);
                break;
            case R.id.ll_search_group:
                startActivity(new Intent(mContext, SearchAllActivity.class));
                break;
            case R.id.ll_add_group:
                startActivity(new Intent(mContext, AddGroupActivity.class));
                break;

        }
    }

    private void updateGroupListData(final ArrayList<GroupModel> groupModelList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGroupListAdapter.setGroupListData(groupModelList);
                mGroupListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onGroupFetchSuccess(ArrayList<GroupModel> groupModelArrayList) {
        tvNoGroups.setVisibility(View.GONE);
        if (groupModelArrayList != null && groupModelArrayList.size() > 0) {
            updateGroupListData(groupModelArrayList);
        } else {
            if (mGroupListAdapter.getCount() <= 0) {
                tvNoGroups.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onGroupFetchFailed(AppError error) {
        if (mGroupListAdapter.getCount() <= 0) {
            tvNoGroups.setVisibility(View.VISIBLE);
        }
        if (error.getErrorCode() == 20021) {
            SignOutInteractor interactor = new SignOutInteractor(mContext);
            interactor.doForceSignOut(new SignOutListener() {
                @Override
                public void onSignOutSuccessful() {
                    Intent i = new Intent(mContext, SplashScreenActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }

                @Override
                public void onSignOutFailed() {

                }

                @Override
                public void onSignOutCanceled() {

                }
            });
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_status) {
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        GroupModel model = mGroupListAdapter.getGroupListData().get(position);
        if (!model.getGroupUniqueId().equals("-11223344")) {
            menu.add(1, 1, 1, "Mark as read");
            menu.add(1, 2, 1, "Group info");
//            menu.add(1, 3, 1, "Exit group");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case 1:
                new ChatDbHelper(mContext).updateAllRead(Long.parseLong(mGroupListAdapter.getGroupListData().get(position).getGroupUniqueId()));
                GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
                interactor.getSubscribedGroupsFromDatabase(GroupListActivity.this);
                break;
            case 2:
                Intent i = new Intent(mContext, GroupInfoActivity.class);
                GroupModel model = (GroupModel) mGroupListAdapter.getItem(position);
                i.putExtra("groupCloudId", model.getGroupUniqueId());
                startActivity(i);
                break;
            case 3:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void revealVisible() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mToolbar.setVisibility(View.GONE);
            }
        }, 300);

        View shape = findViewById(R.id.reveal_layout);
        shape.setOnClickListener(this);

        findViewById(R.id.ll_search_group).setOnClickListener(this);
        findViewById(R.id.ll_add_group).setOnClickListener(this);

        int centerXOnImage = mFab.getWidth() / 2;
        int centerYOnImage = mFab.getHeight() / 2;

        int centerXOfImageOnScreen = mFab.getLeft() + centerXOnImage;
        int centerYOfImageOnScreen = mFab.getTop() + centerYOnImage;

        ViewCompat.animate(mFab).rotation(45f).withLayer().setDuration(200).setInterpolator(/*interpolator[0]*/new AccelerateDecelerateInterpolator()).start();

        Animator animator = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            animator = ViewAnimationUtils.createCircularReveal(
                    shape,
                    centerXOfImageOnScreen,
                    centerYOfImageOnScreen,
                    0,
                    (float) Math.hypot(shape.getWidth(), shape.getHeight()));


            // Set a natural ease-in/ease-out interpolator.
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(500);
            // Finally start the animation

            shape.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    lvGroupListView.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();
        } else {
            shape.setVisibility(View.VISIBLE);
            lvGroupListView.setEnabled(false);
        }
    }

    private void revealHide(long duration) {
        mToolbar.setVisibility(View.VISIBLE);
        final View shape = findViewById(R.id.reveal_layout);
        try {

            int centerXOnImage = mFab.getWidth() / 2;
            int centerYOnImage = mFab.getHeight() / 2;

            int centerXOfImageOnScreen = mFab.getLeft() + centerXOnImage;
            int centerYOfImageOnScreen = mFab.getTop() + centerYOnImage;

            ViewCompat.animate(mFab).rotation(0f).withLayer().setDuration(200).setInterpolator(/*interpolator[0]*/new AccelerateDecelerateInterpolator()).start();

            Animator animator = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                animator = ViewAnimationUtils.createCircularReveal(
                        shape,
                        centerXOfImageOnScreen,
                        centerYOfImageOnScreen,
                        (float) Math.hypot(shape.getWidth(), shape.getHeight()),
                        0);


                // Set a natural ease-in/ease-out interpolator.
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(duration);

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        shape.setVisibility(View.GONE);
                        lvGroupListView.setEnabled(true);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        lvGroupListView.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator.start();
            } else {
                shape.setVisibility(View.GONE);
                lvGroupListView.setEnabled(true);
            }
        } catch (Exception e) {
            shape.setVisibility(View.GONE);
            Log.e(TAG, e.toString());
        }
    }

    private void makeGodwinBot() {
        GroupDbHelper mDbHelper = new GroupDbHelper(mContext);
        mDbHelper.getGodwinBot();

    }
}
