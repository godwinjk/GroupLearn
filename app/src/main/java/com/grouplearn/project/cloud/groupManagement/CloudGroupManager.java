package com.grouplearn.project.cloud.groupManagement;

import android.content.Context;

import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupRequest;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupResponse;
import com.grouplearn.project.cloud.groupManagement.addSubscribedGroup.CloudAddSubscribedGroupRequest;
import com.grouplearn.project.cloud.groupManagement.addSubscribedGroup.CloudAddSubscribedGroupResponse;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudContactDeleteRequest;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudDeleteGroupResponse;
import com.grouplearn.project.cloud.groupManagement.exitGroup.CloudExitGroupRequest;
import com.grouplearn.project.cloud.groupManagement.exitGroup.CloudExitGroupResponse;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupRequest.CloudGetSubscribeResponse;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersRequest;
import com.grouplearn.project.cloud.groupManagement.getGroupSubscribersList.GetGroupSubscribersResponse;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.getGroups.CloudGetGroupsResponse;
import com.grouplearn.project.cloud.groupManagement.getInvitations.CloudGetGroupInvitationRequest;
import com.grouplearn.project.cloud.groupManagement.getInvitations.CloudGetGroupInvitationsResponse;
import com.grouplearn.project.cloud.groupManagement.getSubscribedGroups.CloudGetSubscribedGroupsRequest;
import com.grouplearn.project.cloud.groupManagement.getSubscribedGroups.CloudGetSubscribedGroupsResponse;
import com.grouplearn.project.cloud.groupManagement.inviteGroup.CloudGroupInvitationRequest;
import com.grouplearn.project.cloud.groupManagement.inviteGroup.CloudGroupInvitationResponse;
import com.grouplearn.project.cloud.groupManagement.updateGroupRequest.CloudUpdateSubscribeGroupRequest;
import com.grouplearn.project.cloud.groupManagement.updateGroupRequest.CloudUpdateSubscribeGroupResponse;
import com.grouplearn.project.cloud.groupManagement.updateInvitation.CloudUpdateGroupInvitationRequest;
import com.grouplearn.project.cloud.groupManagement.updateInvitation.CloudUpdateGroupInvitationResponse;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.models.RequestModel;
import com.grouplearn.project.models.UserModel;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Godwin Joseph on 18-05-2016 23:25 for Group Learn application.
 */
public class CloudGroupManager extends BaseManager implements CloudGroupManagerInterface {
    private static final String TAG = "CloudGroupManager";
    Context mContext;
    String mBaseurl;

    public CloudGroupManager(Context mContext) {
        this.mContext = mContext;
        mBaseurl = CloudConstants.getBaseUrl();
    }

    @Override
    public void addGroup(final CloudAddGroupRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudAddGroupResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GroupModel groupModel = new GroupModel();
                                    groupModel.setGroupCreatedTime(modelObject.optString("createdTime"));
                                    groupModel.setGroupUpdatedTime(modelObject.optString("updatedTime"));
                                    groupModel.setGroupUniqueId(modelObject.optString("groupId"));
                                    groupModel.setGroupName(modelObject.optString("groupName"));
                                    groupModel.setGroupIconId(modelObject.optString("groupIconId"));

                                    groupModelArrayList.add(groupModel);
                                }
                                ((CloudAddGroupResponse) response).setGroupModelArrayList(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(mBaseurl + "group");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GroupModel model : cloudRequest.getGroupModelArrayList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("groupName", model.getGroupName());
                modelObject.put("groupIconId", 10/*model.getGroupIconId()*/);
                modelObject.put("definition", model.getGroupDescription());
                modelObject.put("privacy", model.getPrivacy());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void addSubscribedGroup(final CloudAddSubscribedGroupRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudAddSubscribedGroupResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONArray dataArray = jsonObject.optJSONArray(DATA);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                                return;
                            }
                        }
                        if (dataArray != null) {

//                            JSONArray dataArray = dataArray.optJSONArray("groupDetails");

                            ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GroupModel groupModel = new GroupModel();
                                groupModel.setGroupCreatedTime(modelObject.optString("createdTime"));
                                groupModel.setGroupUpdatedTime(modelObject.optString("updatedTime"));
                                groupModel.setGroupUniqueId(modelObject.optString("groupId"));
                                groupModel.setGroupName(modelObject.optString("groupName"));
                                groupModel.setGroupDescription(modelObject.optString("definition"));
                                groupModel.setGroupAdminId(modelObject.optString("groupUserId"));
                                groupModel.setGroupIconId(modelObject.optString("groupIconId"));

                                groupModelArrayList.add(groupModel);
                            }
                            ((CloudAddSubscribedGroupResponse) response).setGroupModelArrayList(groupModelArrayList);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else

                {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(mBaseurl + "groupsubscribe");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GroupModel model : cloudRequest.getGroupModelArrayList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("groupId", model.getGroupUniqueId());
                modelObject.put("groupName", model.getGroupName());
                modelObject.put("groupIconId", model.getGroupIconId());
                modelObject.put("groupUserId", model.getGroupAdminId());
                modelObject.put("definition", model.getGroupDescription());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void updateGroupRequest(final CloudUpdateSubscribeGroupRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudUpdateSubscribeGroupResponse response = new CloudUpdateSubscribeGroupResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudUpdateSubscribeGroupResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<RequestModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    RequestModel groupModel = new RequestModel();
                                    groupModel.setStatus(modelObject.optInt("status"));
                                    groupModel.setMessage(modelObject.optString("message"));
                                    groupModel.setGroupId(modelObject.optString("requestedGroupId"));
                                    groupModel.setGroupName(modelObject.optString("requestedGroupName"));
                                    groupModel.setUserId(modelObject.optString("requestedUserId"));
                                    groupModel.setAction(modelObject.optInt("action"));

                                    groupModelArrayList.add(groupModel);
                                }
                                response.setRequestModels(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.PUT_METHOD);
        httpMethod.setUrl(mBaseurl + "groupsubscribe/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (RequestModel model : cloudRequest.getRequestModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("requestedGroupId", model.getGroupId());
                modelObject.put("requestedGroupName", model.getGroupName());
                modelObject.put("requestedUserId", model.getUserId());
                modelObject.put("action", model.getAction());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void getSubscribedGroups(final CloudGetSubscribedGroupsRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudGetSubscribedGroupsResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {
                            int groupCount = dataObject.optInt("subscribedGroupCount", 0);
                            JSONArray dataArray = dataObject.optJSONArray("subscribedGroupDetails");
                            ((CloudGetSubscribedGroupsResponse) response).setGroupCount(groupCount);

                            ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && groupCount > 0 && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GroupModel groupModel = new GroupModel();
                                groupModel.setGroupCreatedTime(modelObject.optString("createdTime"));
                                groupModel.setGroupUpdatedTime(modelObject.optString("updatedTime"));
                                groupModel.setGroupUniqueId(modelObject.optString("subscribedGroupId"));
                                groupModel.setGroupName(modelObject.optString("subscribedGroupName"));
                                groupModel.setGroupIconId(modelObject.optString("subscribedGroupIconId"));
                                groupModel.setGroupAdminId(modelObject.optString("subscribedGroupUserId"));

                                groupModelArrayList.add(groupModel);
                            }
                            ((CloudGetSubscribedGroupsResponse) response).setGroupModelArrayList(groupModelArrayList);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else

                {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "group-subscribe-details/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void getAllSubscribeGroupRequest(final CloudGetSubscribeRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetSubscribeResponse response = new CloudGetSubscribeResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudGetSubscribeResponse) getUpdatedResponse(statusObject, response);
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {
                            int groupCount = dataObject.optInt("subscribedGroupRequestCount", 0);
                            JSONArray dataArray = dataObject.optJSONArray("subscribedGroupDetails");
                            response.setNumberOfRequests(groupCount);

                            ArrayList<RequestModel> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && groupCount > 0 && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                RequestModel groupModel = new RequestModel();
//                                groupModel.setCreatedTime(modelObject.optString("createdTime"));
//                                groupModel.setUpdatedTime(modelObject.optString("updatedTime"));
                                groupModel.setGroupId(modelObject.optString("requestedGroupId"));
                                groupModel.setGroupName(modelObject.optString("requestedGroupName"));
                                groupModel.setGroupIconId(modelObject.optString("requestedGroupIconId"));
                                groupModel.setUserId(modelObject.optString("requestedUserId"));
                                groupModel.setUserName(modelObject.optString("requestedUserName"));

                                groupModelArrayList.add(groupModel);
                            }
                            response.setRequestModels(groupModelArrayList);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else

                {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "groupsubscribe/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit() + "&groupId=" + cloudRequest.getGroupId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void exitFromGroup(final CloudExitGroupRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudExitGroupResponse response = (CloudExitGroupResponse) new CloudConnectResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudExitGroupResponse) getUpdatedResponse(statusObject, response);
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(response.getResponseStatus(), response.getResponseMessage()));
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {
                            int groupCount = dataObject.optInt("groupCount", 0);
                            JSONArray dataArray = dataObject.optJSONArray("groupDetails");
                            response.setGroupCount(groupCount);
                            ArrayList<String> groupUniqueIdList = new ArrayList<>();
                            for (int i = 0; dataArray != null && groupCount > 0 && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                groupUniqueIdList.add(modelObject.optString("groupUniqueId"));
                            }
                            response.setGroupUniqueIdList(groupUniqueIdList);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl("");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (String uniqueId : cloudRequest.getGroupUniqueIdList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("groupUniqueId", uniqueId);
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void getAllGroups(final CloudGetGroupsRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudGetGroupsResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                            if (dataObject != null) {
                                int groupCount = dataObject.optInt("groupCount", 0);
                                JSONArray dataArray = dataObject.optJSONArray("groupDetails");
                                ((CloudGetGroupsResponse) response).setGroupCount(groupCount);

                                ArrayList<GroupModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null /*&& groupCount > 0*/ && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GroupModel groupModel = new GroupModel();
                                    groupModel.setGroupCreatedTime(modelObject.optString("createdTime"));
                                    groupModel.setGroupUpdatedTime(modelObject.optString("updatedTime"));
                                    groupModel.setGroupUniqueId(modelObject.optString("groupId"));
                                    groupModel.setGroupName(modelObject.optString("groupName"));
                                    groupModel.setGroupAdminId(modelObject.optString("groupUserId"));
                                    groupModel.setGroupDescription(modelObject.optString("definition"));
                                    groupModel.setGroupIconId(modelObject.optString("groupIconId"));

                                    groupModelArrayList.add(groupModel);
                                }
                                ((CloudGetGroupsResponse) response).setGroupModelArrayList(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }

                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "group/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());
        hashMap.put("key", "" + cloudRequest.getKey());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();

    }

    @Override
    public void getAllSubscribers(final GetGroupSubscribersRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                GetGroupSubscribersResponse response = new GetGroupSubscribersResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (GetGroupSubscribersResponse) getUpdatedResponse(statusObject, response);
                            if (dataObject != null) {
                                int userCount = dataObject.optInt("groupSubscribedUsersCount", 0);
                                JSONArray dataArray = dataObject.optJSONArray("groupSubscribedUsersDetails");
                                ((GetGroupSubscribersResponse) response).setUserCount(userCount);

                                ArrayList<UserModel> userModels = new ArrayList<>();
                                for (int i = 0; dataArray != null /*&& groupCount > 0*/ && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    UserModel userModel = new UserModel();
                                    userModel.setUserId(modelObject.optLong("userId"));
                                    userModel.setUserName(modelObject.optString("userName"));
                                    userModel.setUserStatus(modelObject.optString("userStatus"));
                                    userModel.setUserEmail(modelObject.optString("userEmail"));
                                    userModel.setUserDisplayName(modelObject.optString("userDisplayName"));

                                    userModels.add(userModel);
                                }
                                ((GetGroupSubscribersResponse) response).setUserModels(userModels);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }

                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "group-subscribe-users/" + cloudRequest.getGroupId() + "?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void getAllInvitations(final CloudGetGroupInvitationRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetGroupInvitationsResponse response = new CloudGetGroupInvitationsResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudGetGroupInvitationsResponse) getUpdatedResponse(statusObject, response);
                            if (dataObject != null) {
                                int invitationCount = dataObject.optInt("invitationGroupRequestCount", 0);
                                JSONArray dataArray = dataObject.optJSONArray("invitationGroupDetails");
                                response.setInvitationCount(invitationCount);

                                ArrayList<RequestModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null && invitationCount > 0 && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    RequestModel groupModel = new RequestModel();
//                                groupModel.setCreatedTime(modelObject.optString("createdTime"));
//                                groupModel.setUpdatedTime(modelObject.optString("updatedTime"));
                                    groupModel.setGroupId(modelObject.optString("invitedGroupId"));
                                    groupModel.setGroupName(modelObject.optString("invitedGroupName"));
                                    groupModel.setGroupIconId(modelObject.optString("invitedGroupIconId"));
                                    groupModel.setUserId(modelObject.optString("invitedUserId"));
                                    groupModel.setUserName(modelObject.optString("invitedGroupUserName"));
                                    groupModel.setDefinition(modelObject.optString("definition"));

                                    groupModelArrayList.add(groupModel);
                                }
                                response.setRequestModels(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }

                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "groupinvite/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void inviteToGroup(final CloudGroupInvitationRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGroupInvitationResponse response = new CloudGroupInvitationResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudGroupInvitationResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<RequestModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    RequestModel groupModel = new RequestModel();
                                    groupModel.setStatus(modelObject.optInt("status"));
                                    groupModel.setMessage(modelObject.optString("message"));

                                    groupModel.setGroupId(modelObject.optString("groupId"));
                                    groupModel.setGroupIconId(modelObject.optString("groupIconId"));
                                    groupModel.setGroupName(modelObject.optString("groupName"));
                                    groupModel.setUserId(modelObject.optString("inviteUserId"));

                                    groupModelArrayList.add(groupModel);
                                }
                                response.setRequestModels(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.PUT_METHOD);
        httpMethod.setUrl(mBaseurl + "groupinvite");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (RequestModel model : cloudRequest.getRequestModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("groupId", model.getGroupId());
                modelObject.put("groupName", model.getGroupName());
                modelObject.put("groupIconId", model.getGroupIconId());
                modelObject.put("inviteUserId", model.getUserId());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void updateInvitation(final CloudUpdateGroupInvitationRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudUpdateGroupInvitationResponse response = new CloudUpdateGroupInvitationResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudUpdateGroupInvitationResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<RequestModel> groupModelArrayList = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    RequestModel groupModel = new RequestModel();
                                    groupModel.setStatus(modelObject.optInt("status"));
                                    groupModel.setMessage(modelObject.optString("message"));
                                    groupModel.setGroupId(modelObject.optString("invitedGroupId"));
                                    groupModel.setGroupName(modelObject.optString("invitedGroupName"));
                                    groupModel.setUserId(modelObject.optString("invitedUserId"));
                                    groupModel.setAction(modelObject.optInt("action"));

                                    groupModelArrayList.add(groupModel);
                                }
                                response.setRequestModels(groupModelArrayList);
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.PUT_METHOD);
        httpMethod.setUrl(mBaseurl + "groupinvite/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (RequestModel model : cloudRequest.getRequestModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("invitedGroupId", model.getGroupId());
                modelObject.put("invitedGroupName", model.getGroupName());
                modelObject.put("invitedUserId", model.getUserId());
                modelObject.put("action", model.getAction());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void deleteGroup(final CloudContactDeleteRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudDeleteGroupResponse response = (CloudDeleteGroupResponse) new CloudConnectResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudDeleteGroupResponse) getUpdatedResponse(statusObject, response);
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(response.getResponseStatus(), response.getResponseMessage()));
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {
                            int groupCount = dataObject.optInt("groupCount", 0);
                            JSONArray dataArray = dataObject.optJSONArray("groupDetails");
                            response.setGroupCount(groupCount);
                            ArrayList<String> groupUniqueIdList = new ArrayList<>();
                            for (int i = 0; dataArray != null && groupCount > 0 && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                groupUniqueIdList.add(modelObject.optString("groupUniqueId"));
                            }
                            response.setGroupUniqueIdList(groupUniqueIdList);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (responseCallback != null) {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (responseCallback != null) {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl("");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (String uniqueId : cloudRequest.getGroupUniqueIdList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("groupUniqueId", uniqueId);
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }
}
