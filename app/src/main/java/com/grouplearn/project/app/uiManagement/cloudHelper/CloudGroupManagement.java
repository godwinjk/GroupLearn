package com.grouplearn.project.app.uiManagement.cloudHelper;

import android.content.Context;
import android.content.Intent;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.SplashScreenActivity;
import com.grouplearn.project.app.uiManagement.databaseHelper.GroupDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.CloudOperationCallback;
import com.grouplearn.project.app.uiManagement.interfaces.GroupRequestCallback;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.bean.GLRequest;
import com.grouplearn.project.cloud.CloudConnectManager;
import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupRequest;
import com.grouplearn.project.cloud.groupManagement.addSubscribedGroup.CloudAddSubscribedGroupRequest;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudDeleteGroupResponse;
import com.grouplearn.project.cloud.groupManagement.exitGroup.CloudExitGroupGroupRequest;
import com.grouplearn.project.cloud.groupManagement.exitGroup.CloudExitGroupResponse;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeResponse;
import com.grouplearn.project.cloud.groupManagement.updateGroupRequest.CloudUpdateSubscribeGroupRequest;
import com.grouplearn.project.cloud.groupManagement.updateGroupRequest.CloudUpdateSubscribeGroupResponse;
import com.grouplearn.project.cloud.groupManagement.updateInvitation.CloudUpdateGroupInvitationRequest;
import com.grouplearn.project.cloud.groupManagement.updateInvitation.CloudUpdateGroupInvitationResponse;
import com.grouplearn.project.utilities.errorManagement.AppError;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

import java.util.ArrayList;

/**
 * Created by Godwin Joseph on 19-05-2016 22:19 for Group Learn application.
 */
public class CloudGroupManagement {
    Context mContext;
    AppSharedPreference mPref;

    public CloudGroupManagement(Context mContext) {
        this.mContext = mContext;
        this.mPref = new AppSharedPreference(mContext);
    }

    public void addGroup(GLGroup model, CloudResponseCallback callback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        CloudAddGroupRequest request = new CloudAddGroupRequest();
        request.setGroupModels(model);
        request.setToken(token);
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).addGroup(request, callback);
    }

    public void addSubscribedGroup(GLGroup groupModel, final CloudOperationCallback operationCallback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudAddSubscribedGroupRequest request = new CloudAddSubscribedGroupRequest();
        request.setToken(token);
        request.setGroupModels(groupModel);
        DisplayInfo.showLoader(mContext, "Adding group...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);

                operationCallback.onCloudOperationSuccess();
                DisplayInfo.showToast(mContext, "Group subscription sent successfully");
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onCloudOperationFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                DisplayInfo.dismissLoader(mContext);
                if (cloudError.getErrorCode() == 20021) {
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
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).addSubscribedGroup(request, callback);
    }

    public void getSubscribeGroupRequest(final GroupRequestCallback operationCallback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudAddSubscribedGroupRequest request = new CloudAddSubscribedGroupRequest();
        request.setToken(token);

        DisplayInfo.showLoader(mContext, "Adding group...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudGetSubscribeResponse response = (CloudGetSubscribeResponse) cloudResponse;

                operationCallback.onGroupRequestFetchSuccess(response.getRequestModels());
                DisplayInfo.showToast(mContext, "Group added successfully");
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onGroupRequestFetchFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                DisplayInfo.dismissLoader(mContext);
                AppAlertDialog.getAlertDialog(mContext).showWarningAlert(cloudError.getErrorMessage());
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).addSubscribedGroup(request, callback);
    }

    public void updateSubscribeGroups(GLRequest requestModel, final CloudOperationCallback operationCallback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudUpdateSubscribeGroupRequest request = new CloudUpdateSubscribeGroupRequest();
        request.setToken(token);
        request.setRequestModels(requestModel);
        DisplayInfo.showLoader(mContext, "Please wait...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudUpdateSubscribeGroupResponse response = (CloudUpdateSubscribeGroupResponse) cloudResponse;
                ArrayList<GLRequest> requestModels = response.getRequestModels();
                if (requestModels.size() > 0) {
                    for (GLRequest model : requestModels) {
                        if (model.getStatus() == 0) {
                            operationCallback.onCloudOperationFailed(new AppError(model.getStatus(), model.getMessage()));
                            DisplayInfo.showToast(mContext, "Failed");
                        } else {
                            operationCallback.onCloudOperationSuccess();
                            DisplayInfo.showToast(mContext, "Success");
                        }
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onCloudOperationFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                DisplayInfo.dismissLoader(mContext);
                AppAlertDialog.getAlertDialog(mContext).showWarningAlert(cloudError.getErrorMessage());
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).updateGroupRequest(request, callback);
    }

    public void updateInvitations(GLRequest requestModel, final CloudOperationCallback operationCallback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudUpdateGroupInvitationRequest request = new CloudUpdateGroupInvitationRequest();
        request.setToken(token);
        request.setRequestModels(requestModel);
        DisplayInfo.showLoader(mContext, "Please wait...");
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                DisplayInfo.dismissLoader(mContext);
                CloudUpdateGroupInvitationResponse response = (CloudUpdateGroupInvitationResponse) cloudResponse;
                ArrayList<GLRequest> requestModels = response.getRequestModels();
                if (requestModels.size() > 0) {
                    for (GLRequest model : requestModels) {
                        if (model.getStatus() == 0) {
                            operationCallback.onCloudOperationFailed(new AppError(model.getStatus(), model.getMessage()));
                            DisplayInfo.showToast(mContext, "Failed");
                        } else {
                            operationCallback.onCloudOperationSuccess();
                            DisplayInfo.showToast(mContext, "Success");
                        }
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onCloudOperationFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
                DisplayInfo.dismissLoader(mContext);
                AppAlertDialog.getAlertDialog(mContext).showWarningAlert(cloudError.getErrorMessage());
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).updateInvitation(request, callback);
    }

    public void exitFromSubscribedGroup(long uniqueId, final CloudOperationCallback operationCallback) {
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudExitGroupGroupRequest request = new CloudExitGroupGroupRequest();
        request.setToken(token);
        GLGroup group=new GLGroup();
        group.setGroupUniqueId(uniqueId);
        request.setGroupUniqueIdList(group);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudExitGroupResponse response = (CloudExitGroupResponse) cloudResponse;
                ArrayList<Long> requestModels = response.getGroupUniqueIdList();
                if (requestModels.size() > 0) {
                    for (Long model : requestModels) {
                        operationCallback.onCloudOperationSuccess();
                        DisplayInfo.showToast(mContext, "Success");
                        new GroupDbHelper(mContext).deleteSubscribedGroup(model);
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onCloudOperationFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).exitFromGroup(request, callback);
    }
    public void deleteSubscribedGroup(long uniqueId,final  CloudOperationCallback operationCallback){
        String token = mPref.getStringPrefValue(PreferenceConstants.USER_TOKEN);
        final CloudExitGroupGroupRequest request = new CloudExitGroupGroupRequest();
        request.setToken(token);
        GLGroup group=new GLGroup();
        group.setGroupUniqueId(uniqueId);
        request.setGroupUniqueIdList(group);
        CloudResponseCallback callback = new CloudResponseCallback() {
            @Override
            public void onSuccess(CloudConnectRequest cloudRequest, CloudConnectResponse cloudResponse) {
                CloudDeleteGroupResponse response = (CloudDeleteGroupResponse) cloudResponse;
                ArrayList<Long> requestModels = response.getGroupUniqueIdList();
                if (requestModels.size() > 0) {
                    for (Long model : requestModels) {
                        operationCallback.onCloudOperationSuccess();
                        DisplayInfo.showToast(mContext, "Success");
                        new GroupDbHelper(mContext).deleteSubscribedGroup(model);
                    }
                }
            }

            @Override
            public void onFailure(CloudConnectRequest cloudRequest, CloudError cloudError) {
                operationCallback.onCloudOperationFailed(new AppError(cloudError.getErrorCode(), cloudError.getErrorMessage()));
            }
        };
        CloudConnectManager.getInstance(mContext).getCloudGroupManager(mContext).deleteGroup(request, callback);
    }
}
