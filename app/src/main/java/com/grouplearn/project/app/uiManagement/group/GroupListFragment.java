package com.grouplearn.project.app.uiManagement.group;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseFragment;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.cloudHelper.CloudGroupManagement;
import com.grouplearn.project.app.uiManagement.databaseHelper.ChatDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interactor.MessageInteractor;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.app.uiManagement.search.SearchGroupsActivity;
import com.grouplearn.project.app.uiManagement.search.SearchUserActivity;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupListFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "GroupListFragment";
    private FloatingActionButton mFab;

    private GroupListFrontAdapter mGroupListAdapter;
    RecyclerView lvGroupListView;
    TextView tvNoGroups;
    private static final long TRANSLATE_DURATION_MILLIS = 500;
    private boolean mVisible = true;
    View shape;
    private LinearLayout llLoaading;
    LinearLayout llAddGroup, llSearchGroup, llSearchUser, llAddCourse;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeWidgets(view);
        registerListeners();
        getGroupsFromDb();
        getMessages();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void initializeWidgets(View v) {

        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        lvGroupListView = (RecyclerView) v.findViewById(R.id.lv_group_list);
        tvNoGroups = (TextView) v.findViewById(R.id.tv_no_groups);
        mGroupListAdapter = new GroupListFrontAdapter(getContext());
        lvGroupListView.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        lvGroupListView.setAdapter(mGroupListAdapter);

        llAddGroup = (LinearLayout) v.findViewById(R.id.ll_add_group);
        llAddCourse = (LinearLayout) v.findViewById(R.id.ll_add_course);
        llSearchUser = (LinearLayout) v.findViewById(R.id.ll_search_group);
        llSearchGroup = (LinearLayout) v.findViewById(R.id.ll_search_user);
        llLoaading = (LinearLayout) v.findViewById(R.id.ll_loading);

        shape = v.findViewById(R.id.reveal_layout);
        llAddCourse.setVisibility(View.GONE);
        mFab.setVisibility(View.GONE);
        revealHide(0);
        getGroupData();

    }

    @Override
    protected void registerListeners() {
        mFab.setOnClickListener(this);
        mGroupListAdapter.setOnRecyclerItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, int action, View v) {
                Intent chatIntent = new Intent(getActivity(), GroupChatActivity.class);
                GLGroup group = (GLGroup) model;
                long groupUniqueId = group.getGroupUniqueId();
                if (group.isMine()) {
                    chatIntent.putExtra("groupCloudId", groupUniqueId);

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), v, "transition_group_icon");
                    startActivity(chatIntent, options.toBundle());
                    new ChatDbHelper(getActivity()).updateAllRead(group.getGroupUniqueId());
                }
                if (v instanceof TextView) {
                    showAlertToAddGroup(group);
                }
            }

            @Override
            public void onItemLongClicked(int position, Object model, int action, View v) {
                GLGroup group = (GLGroup) model;
                if (group.isMine()) {
                    showContextMenu(group);
                }
            }
        });
    }

    private void showAlertToAddGroup(final GLGroup group) {
        AppAlertDialog dialog = AppAlertDialog.getAlertDialog(getActivity());
        dialog.setTitle("Confirm")
                .setMessage("Are you sure want to request to subscribe this group?")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CloudGroupManagement groupManagement = new CloudGroupManagement(getContext());
                        groupManagement.addSubscribedGroup(group, new CloudOperationCallback() {
                            @Override
                            public void onCloudOperationSuccess() {

                            }

                            @Override
                            public void onCloudOperationFailed(AppError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }

    private void showContextMenu(final GLGroup group) {
        AppAlertDialog dialog = AppAlertDialog.getAlertDialog(getActivity());
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.add("Mark as read");
        arrayAdapter.add("Group info");

        long userId = new AppSharedPreference(getContext()).getLongPrefValue(PreferenceConstants.USER_ID);
        if (userId == group.getGroupAdminId()) {
            arrayAdapter.add("Delete group");
        } else {
            arrayAdapter.add("Exit from group");
        }

        dialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        if (strName.equals("Mark as read")) {
                            new ChatDbHelper(getActivity()).updateAllRead(group.getGroupUniqueId());
                            getGroupsFromDb();
                        } else if (strName.equals("Group info")) {
                            Intent i = new Intent(getActivity(), GroupInfoActivity.class);
                            i.putExtra("groupCloudId", group.getGroupUniqueId());
                            startActivity(i);
                        }

                        CloudOperationCallback callback = new CloudOperationCallback() {
                            @Override
                            public void onCloudOperationSuccess() {
                                DisplayInfo.dismissLoader(getActivity());
                                mGroupListAdapter.removeItem(group);
                            }

                            @Override
                            public void onCloudOperationFailed(AppError error) {
                                DisplayInfo.dismissLoader(getActivity());
                                DisplayInfo.showToast(getActivity(), "Something went wrong. Please try again later.");
                            }
                        };

                        if (AppUtility.checkInternetConnection()) {
                            if (strName.equals("Exit from group")) {
                                DisplayInfo.showLoader(getActivity(), getString(R.string.please_wait));
                                new CloudGroupManagement(getActivity()).exitFromSubscribedGroup(group.getGroupUniqueId(), callback);
                            } else if (strName.equals("Delete group")) {
                                DisplayInfo.showLoader(getActivity(), getString(R.string.please_wait));
                                new CloudGroupManagement(getActivity()).deleteSubscribedGroup(group.getGroupUniqueId(), callback);
                            }
                        } else {
                            DisplayInfo.showToast(getActivity(), getString(R.string.no_network));
                        }
                    }
                }
        );
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(chatReceiver, new IntentFilter("chat"));
        getActivity().registerReceiver(chatReceiver, new IntentFilter("chatRefresh"));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getActivity() != null) {
            getGroupsFromDb();
            if (isVisibleToUser) {
                hideSoftKeyboard();
                revealHide(0);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterForContextMenu(lvGroupListView);
        getActivity().unregisterReceiver(chatReceiver);
    }

    BroadcastReceiver chatReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("chat")) {
                getGroupData();
            }
            if (intent.getAction().equals("chatRefresh")) {
                getMessages();
            }
        }
    };

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

    private void getGroupsFromDb() {
        GroupListInteractor interactor = GroupListInteractor.getInstance(getActivity());
        interactor.getSubscribedGroupsFromDatabase(new GroupViewInterface() {
            @Override
            public void onGroupFetchSuccess(ArrayList<GLGroup> groupModelArrayList) {
                if (getActivity() == null)
                    return;
                tvNoGroups.setVisibility(View.GONE);
                try {
                    if (groupModelArrayList != null && groupModelArrayList.size() > 0) {
                        updateGroupListData(groupModelArrayList);
                    } else {
                        if (mGroupListAdapter.getGroupList().size() <= 0) {
                            tvNoGroups.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    Log.e(TAG, ex.toString());
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.toString());
                }
            }

            @Override
            public void onGroupFetchFailed(AppError error) {
                if (getActivity() == null)
                    return;
                llLoaading.setVisibility(View.GONE);
                if (mGroupListAdapter.getGroupList().size() <= 0) {
                    tvNoGroups.setVisibility(View.VISIBLE);
                }
                if (error.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(getActivity());
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(getActivity(), SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(i);
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
        });
    }

    private void getGroupData() {
        GroupListInteractor interactor = GroupListInteractor.getInstance(getActivity());
        interactor.getSubscribedGroups(new GroupViewInterface() {
            @Override
            public void onGroupFetchSuccess(ArrayList<GLGroup> groupModelArrayList) {
                if (getActivity() == null)
                    return;
                tvNoGroups.setVisibility(View.GONE);
                try {
                    if (groupModelArrayList != null && groupModelArrayList.size() > 0) {
                        updateGroupListData(groupModelArrayList);
                    } else {
                        if (mGroupListAdapter.getGroupList().size() <= 0) {
                            tvNoGroups.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    Log.e(TAG, ex.toString());
                } catch (NullPointerException ex) {
                    Log.e(TAG, ex.toString());
                }
                getRandomGroupData();
            }

            @Override
            public void onGroupFetchFailed(AppError error) {
                if (getActivity() == null)
                    return;
                if (mGroupListAdapter.getGroupList().size() <= 0) {
                    tvNoGroups.setVisibility(View.VISIBLE);
                }
                if (error.getErrorCode() == 20021) {
                    SignOutInteractor interactor = new SignOutInteractor(getActivity());
                    interactor.doForceSignOut(new SignOutListener() {
                        @Override
                        public void onSignOutSuccessful() {
                            Intent i = new Intent(getActivity(), SplashScreenActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(i);
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
        });
    }

    private void getRandomGroupData() {
        if (AppUtility.checkInternetConnection()) {
            llLoaading.post(new Runnable() {
                @Override
                public void run() {
                    llLoaading.setVisibility(View.VISIBLE);
                }
            });
            GroupListInteractor.getInstance(getContext()).getGroups("852", new GroupViewInterface() {
                @Override
                public void onGroupFetchSuccess(ArrayList<GLGroup> groupModelArrayList) {
                    if (getActivity() == null)
                        return;
                    llLoaading.setVisibility(View.GONE);
                    tvNoGroups.setVisibility(View.GONE);
                    try {
                        if (groupModelArrayList != null && groupModelArrayList.size() > 0) {
                            updateGroupListData(groupModelArrayList);
                        } else {
                            if (mGroupListAdapter.getGroupList().size() <= 0) {
                                tvNoGroups.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        Log.e(TAG, ex.toString());
                    } catch (NullPointerException ex) {
                        Log.e(TAG, ex.toString());
                    }
                }

                @Override
                public void onGroupFetchFailed(AppError error) {
                    llLoaading.setVisibility(View.GONE);
                    if (getActivity() == null)
                        return;
                    if (mGroupListAdapter.getGroupList().size() <= 0) {
                        tvNoGroups.setVisibility(View.VISIBLE);
                    }
                    if (error.getErrorCode() == 20021) {
                        SignOutInteractor interactor = new SignOutInteractor(getActivity());
                        interactor.doForceSignOut(new SignOutListener() {
                            @Override
                            public void onSignOutSuccessful() {
                                Intent i = new Intent(getActivity(), SplashScreenActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getActivity().startActivity(i);
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
            });
        }
    }

    private void getMessages() {
        MessageInteractor.getInstance().getAllMessages();
        getGroupsFromDb();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                if (lvGroupListView.isEnabled())
                    revealVisible();
                else
                    revealHide(200);
                break;
            case R.id.ll_search_group:
                Intent intent = new Intent(getActivity(), SearchGroupsActivity.class);
                intent.putExtra("showDrawer", 1);
                startActivity(intent);
                revealHide(0);
                break;
            case R.id.ll_search_user:
                intent = new Intent(getActivity(), SearchUserActivity.class);
                intent.putExtra("showDrawer", 1);
                startActivity(intent);
                revealHide(0);
                break;
            case R.id.ll_add_group:
                startActivity(new Intent(getActivity(), AddGroupActivity.class));
                revealHide(0);
                break;

        }
    }

    private void updateGroupListData(final ArrayList<GLGroup> groupModelList) {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mGroupListAdapter.clearAndInsert();
                    mGroupListAdapter.setGroupList(groupModelList);
                }
            });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        GLGroup model = mGroupListAdapter.getGroupList().get(position);
        if (!model.isMine())
            return;
        if (model.getGroupUniqueId() != -11223344) {
            menu.add(1, 1, 1, "Mark as read");
            menu.add(1, 2, 1, "Group info");
            menu.add(1, 3, 1, "Exit group");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        final GLGroup model = (GLGroup) mGroupListAdapter.getGroupList().get(position);
        switch (item.getItemId()) {
            case 1:
                new ChatDbHelper(getActivity()).updateAllRead(model.getGroupUniqueId());
                getGroupData();
                break;
            case 2:
                Intent i = new Intent(getActivity(), GroupInfoActivity.class);
                i.putExtra("groupCloudId", model.getGroupUniqueId());
                startActivity(i);
                break;
            case 3:
                CloudOperationCallback callback = new CloudOperationCallback() {
                    @Override
                    public void onCloudOperationSuccess() {
                        DisplayInfo.dismissLoader(getActivity());
                        GroupDbHelper helper = new GroupDbHelper(getActivity());
                        helper.deleteSubscribedGroup(model.getGroupUniqueId());
                        getGroupsFromDb();
                    }

                    @Override
                    public void onCloudOperationFailed(AppError error) {
                        DisplayInfo.dismissLoader(getActivity());
                        DisplayInfo.showToast(getActivity(), "Something went wrong. Please try again later.");
                    }
                };
                if (AppUtility.checkInternetConnection()) {
                    DisplayInfo.showLoader(getActivity(), getString(R.string.please_wait));
                    new CloudGroupManagement(getActivity()).exitFromSubscribedGroup(model.getGroupUniqueId(), callback);
                } else {
                    DisplayInfo.showToast(getActivity(), getString(R.string.no_network));
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void revealVisible() {

        shape.setOnClickListener(this);

        llAddCourse.setOnClickListener(this);
        llSearchGroup.setOnClickListener(this);
        llSearchUser.setOnClickListener(this);
        llAddGroup.setOnClickListener(this);

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

}
