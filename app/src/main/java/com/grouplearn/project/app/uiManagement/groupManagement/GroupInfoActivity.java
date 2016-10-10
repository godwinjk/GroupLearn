package com.grouplearn.project.app.uiManagement.groupManagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.BaseActivity;
import com.grouplearn.project.app.uiManagement.adapter.ProfileRecyclerAdapter;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.GroupListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.OnRecyclerItemClickListener;
import com.grouplearn.project.app.uiManagement.userManagement.UserProfileActivity;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersResponse;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.models.UserModel;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

public class GroupInfoActivity extends BaseActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar mToolbar;
    Context mContext;
    TextView tvName, tvStatus;
    ImageView ivNameEdit;
    RecyclerView rvSubscribedGroups;
    ProfileRecyclerAdapter mRecyclerAdapter;
    AppSharedPreference mPref;

    GroupModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        mToolbar = setupToolbar("Profile", true);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("User Info");
        mContext = this;
        String groupUniqueId = getIntent().getStringExtra("groupCloudId");
        mModel = new GroupDbHelper(mContext).getGroupInfo(groupUniqueId);
        if (mModel == null) {
            finish();
            return;
        }
        initializeWidgets();
        registerListeners();
    }

    @Override
    public void initializeWidgets() {
        mPref = new AppSharedPreference(mContext);

        tvName = (TextView) findViewById(R.id.tv_group_name);
        tvStatus = (TextView) findViewById(R.id.tv_details);
        ivNameEdit = (ImageView) findViewById(R.id.iv_name_edit);
        rvSubscribedGroups = (RecyclerView) findViewById(R.id.rv_subscribers);
        mRecyclerAdapter = new ProfileRecyclerAdapter(ProfileRecyclerAdapter.USER_LIST);
        rvSubscribedGroups.setLayoutManager(new StaggeredGridLayoutManager(1, 1));
        rvSubscribedGroups.setAdapter(mRecyclerAdapter);

        collapsingToolbarLayout.setTitle(mModel.getGroupName());
        tvName.setText(mModel.getGroupName());
        tvStatus.setText(mModel.getGroupDescription());
    }

    @Override
    public void registerListeners() {
        mRecyclerAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, Object model, View v) {
                UserModel userModel = (UserModel) model;
                Intent intent = new Intent(mContext, UserProfileActivity.class);
                intent.putExtra("user", userModel);
                startActivity(intent);
            }

            @Override
            public void onItemLongClicked(int position, Object model, View v) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GroupListInteractor interactor = GroupListInteractor.getInstance(mContext);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                GetGroupSubscribersResponse res = (GetGroupSubscribersResponse) cloudResponse;
                ArrayList<UserModel> userModels = res.getUserModels();
                setDataToAdapter(userModels);
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

            }
        };
        if (AppUtility.checkInternetConnection())
            interactor.getGroupSubscribers(Long.parseLong(mModel.getGroupUniqueId()), callback);
    }

    private void setVisibility(boolean value) {
        String status = new AppSharedPreference(mContext).getStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS);
        CloudStatusRequest request = new CloudStatusRequest();
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setStatus(status);
        int privacy = 0;
        if (value)
            privacy = 1;
        request.setPrivacyValue(privacy);
        DisplayInfo.showLoader(mContext, "Updating privacy...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);

//                mPref.setStringPrefValue(PreferenceConstants.USER_DISPLAY_STATUS, status);
                finish();
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudUserManager(mContext).setStatus(request, callback);
    }

    private void setDataToAdapter(ArrayList<UserModel> userModels) {
        mRecyclerAdapter.setUserList(userModels);
    }
}
