package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;
import android.support.annotation.NonNull;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.databaseHelper.ServerSyncTimes;
import com.grouplearn.project.app.uiManagement.interfaces.GroupRequestCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupViewInterface;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLRequest;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupRequest;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupResponse;
import com.grouplearn.project.cloud.groupManagement.addSubscribedGroup.CloudAddSubscribedGroupRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeResponse;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersRequest;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsResponse;
import com.grouplearn.project.cloud.groupManagement.getInvitations.CloudGetGroupInvitationRequest;
import com.grouplearn.project.cloud.groupManagement.getInvitations.CloudGetGroupInvitationsResponse;
import com.grouplearn.project.cloud.groupManagement.getSubscribedGroups.CloudGetSubscribedGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.getSubscribedGroups.CloudGetSubscribedGroupsResponse;
import com.grouplearn.project.cloud.groupManagement.inviteGroup.CloudGroupInvitationRequest;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 21:53 for Group Learn application.
 */
public class GroupListInteractor implements CloudResponseCallback {
    private static final int LIMIT = 100;
    private static GroupListInteractor mGroupInteractor;
    private static Context mContext;
    GroupDbHelper dbHelper;
    GroupViewInterface mGroupViewInterface;
    AppSharedPreference mPref;

    private GroupListInteractor(Context context) {
        this.mContext = context;
        this.dbHelper = new GroupDbHelper(mContext);
        this.mPref = new AppSharedPreference(mContext);
    }

    public static GroupListInteractor getInstance(Context context) {
        mContext = context;
        if (mGroupInteractor == null)
            mGroupInteractor = new GroupListInteractor(context);
        return mGroupInteractor;
    }

    @NonNull
    public void getSubscribedGroups(GroupViewInterface groupViewInterface) {
        mGroupViewInterface = groupViewInterface;
        mGroupViewInterface.onGroupFetchSuccess(dbHelper.getSubscribedGroups());
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        CloudGetSubscribedGroupsRequest request = new CloudGetSubscribedGroupsRequest();
        request.setToken(token);
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).getSubscribedGroups(request, this);
    }

    @NonNull
    public void getSubscribedGroupsFromDatabase(GroupViewInterface groupViewInterface) {
        mGroupViewInterface = groupViewInterface;
        mGroupViewInterface.onGroupFetchSuccess(dbHelper.getSubscribedGroups());
    }

    @NonNull
    public void getGroups(String query, GroupViewInterface groupViewInterface) {
        mGroupViewInterface = groupViewInterface;
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        CloudGetGroupsRequest request = new CloudGetGroupsRequest();
        request.setToken(token);
        request.setKey(query);
        request.setStartTime(0);
        request.setLimit(100);
        DisplayInfo.showLoader(mContext, "Searching...");
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).getAllGroups(request, this);
    }

    @Override
    public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
        DisplayInfo.dismissLoader(mContext);

        if (cloudRequest instanceof CloudGetGroupsRequest) {
            insertGroupsToDb((CloudGetGroupsResponse) cloudResponse);

        } else if (cloudRequest instanceof CloudGetSubscribedGroupsRequest) {
            insertSubscribedGroupsToDb((CloudGetSubscribedGroupsResponse) cloudResponse);
        }
    }

    @Override
    public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {

    }

    public void getGroupSubscribers(long groupId, CloudResponseCallback callback) {
        final GetGroupSubscribersRequest request = new GetGroupSubscribersRequest();

        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        request.setGroupId(groupId);
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).getAllSubscribers(request, callback);
    }

    private void insertSubscribedGroupsToDb(CloudGetSubscribedGroupsResponse response) {
        ArrayList<GLGroup> groupModels = response.getGroupModelArrayList();
        for (GLGroup model : groupModels) {
            dbHelper.addSubscribedGroup(model);
        }
        mGroupViewInterface.onGroupFetchSuccess(dbHelper.getSubscribedGroups());
    }

    public void addGroup(GLGroup model) {
        final CloudAddGroupRequest request = new CloudAddGroupRequest();
        request.setGroupModels(model);
        request.setToken(mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN));
        DisplayInfo.showLoader(mContext, "Adding group...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                GroupDbHelper dbHelper = new GroupDbHelper(mContext);
                dbHelper.addSubscribedGroup(((CloudAddGroupResponse) cloudResponse).getGroupModelArrayList().get(0));
                DisplayInfo.showToast(mContext, "Group added successfully");
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                AppAlertDialog.getAlertDialog(mContext).showWarningAlert(cloudError.getErrorMessage());
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).addGroup(request, callback);
    }

    private void insertGroupsToDb(CloudGetGroupsResponse response) {
        ArrayList<GLGroup> groupModels = response.getGroupModelArrayList();
//        for (GroupModel model : groupModels) {
//            dbHelper.addGroup(model);
//        }
        mGroupViewInterface.onGroupFetchSuccess(groupModels);
    }

    public void addSubscribedGroup(GLGroup groupModel) {
        final CloudAddSubscribedGroupRequest request = new CloudAddSubscribedGroupRequest();
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setGroupModels(groupModel);
        request.setToken(token);
        DisplayInfo.showLoader(mContext, "Adding group...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                GroupDbHelper dbHelper = new GroupDbHelper(mContext);
                dbHelper.addSubscribedGroup(((CloudAddGroupResponse) cloudResponse).getGroupModelArrayList().get(0));
                DisplayInfo.showToast(mContext, "Group added successfully");
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                DisplayInfo.dismissLoader(mContext);
                AppAlertDialog.getAlertDialog(mContext).showWarningAlert(cloudError.getErrorMessage());
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).addSubscribedGroup(request, callback);
    }

    public void getGroupRequests(final GroupRequestCallback groupRequestCallback) {
        final CloudGetSubscribeRequest request = new CloudGetSubscribeRequest();
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetSubscribeResponse response = (CloudGetSubscribeResponse) cloudResponse;
                groupRequestCallback.onGroupRequestFetchSuccess(response.getRequestModels());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                groupRequestCallback.onGroupRequestFetchFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).getAllSubscribeGroupRequest(request, callback);
    }

    public void getGroupInvitations(final GroupRequestCallback groupRequestCallback) {
        final CloudGetGroupInvitationRequest request = new CloudGetGroupInvitationRequest();
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        long lastTime = new ServerSyncTimes(mContext).getLastUpdatedTime(ServerSyncTimes.GET_ALL_INVITATION);
        request.setStartTime(lastTime);
        request.setLimit(LIMIT);
        request.setToken(token);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudGetGroupInvitationsResponse response = (CloudGetGroupInvitationsResponse) cloudResponse;
                groupRequestCallback.onGroupRequestFetchSuccess(response.getRequestModels());
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                groupRequestCallback.onGroupRequestFetchFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).getAllInvitations(request, callback);
    }

    public void inviteToGroup(GLRequest model, CloudResponseCallback callback) {
        CloudGroupInvitationRequest request = new CloudGroupInvitationRequest();
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        request.setToken(token);
        request.setRequestModels(model);
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).inviteToGroup(request, callback);
    }

}
