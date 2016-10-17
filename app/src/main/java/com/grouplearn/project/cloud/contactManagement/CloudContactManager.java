package com.grouplearn.project.cloud.contactManagement;

import android.content.Context;

import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditRequest;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditResponse;
import com.grouplearn.project.cloud.contactManagement.contactDelete.CloudContactDeleteResponse;
import com.grouplearn.project.cloud.contactManagement.contactGet.CloudContactGetRequest;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupResponse;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudContactDeleteRequest;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.models.ContactModel;
import com.grouplearn.project.models.GroupModel;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Godwin Joseph on 07-06-2016 15:45 for Group Learn application.
 */
public class CloudContactManager extends BaseManager implements CloudContactManagerInterface {
    private static final String TAG = "CloudContactManager";
    private String mBaseurl;
    private Context mContext;

    public CloudContactManager(Context mContext) {
        this.mContext = mContext;
        this.mBaseurl = CloudConstants.getBaseUrl();
    }

    @Override
    public void addOrEditContact(final CloudContactAddOrEditRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudContactAddOrEditResponse();
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
                            }
                        }
                        if (dataArray != null) {
                            ArrayList<ContactModel> contactModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                ContactModel contactModel = new ContactModel();

                                contactModel.setContactNumber(modelObject.optString("contactName"));
                                contactModel.setContactUniqueId(modelObject.optString("contactUserId"));
                                contactModel.setContactIconId(modelObject.optString("contactIconId"));
                                contactModel.setPrivacy(modelObject.optInt("privacy"));
                                contactModel.setContactStatus(modelObject.optString("userStatus"));
                                contactModel.setStatus(modelObject.optInt("status"));
                                contactModel.setMessage(modelObject.optString("message"));

                                contactModelArrayList.add(contactModel);
                            }
                            ((CloudContactAddOrEditResponse) response).setContactModels(contactModelArrayList);
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
        httpMethod.setUrl(mBaseurl + "contact");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (ContactModel model : cloudRequest.getContactModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("userName", model.getContactNumber());
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void getContacts(final CloudContactGetRequest cloudRequest, final CloudResponseCallback responseCallback) {
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
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(response.getResponseStatus(), response.getResponseMessage()));
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {

                            JSONArray dataArray = dataObject.optJSONArray("contactDetails");

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
        httpMethod.setUrl(mBaseurl + "contact/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (ContactModel model : cloudRequest.getContactModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("contactName", model.getContactName());
                modelObject.put("contactId", model.getContactUniqueId());
                modelObject.put("contactStatus", model.getContactStatus());
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void deleteContact(final CloudContactDeleteRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudContactDeleteResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONArray dataArray = jsonObject.optJSONArray(DATA);
                        if (statusObject != null) {
                            response = getUpdatedResponse(statusObject, response);
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(response.getResponseStatus(), response.getResponseMessage()));
                            }
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataArray != null) {

                            ArrayList<ContactModel> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                ContactModel contactModel = new ContactModel();
                                contactModel.setContactName(modelObject.optString("contactUserName"));
                                contactModel.setContactUniqueId(modelObject.optString("contactUserId"));
                                contactModel.setContactIconId(modelObject.optString("contactIconId"));

                                groupModelArrayList.add(contactModel);
                            }
                            ((CloudContactAddOrEditResponse) response).setContactModels(groupModelArrayList);
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
        httpMethod.setUrl(mBaseurl + "contact");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (String string : cloudRequest.getGroupUniqueIdList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("contactUserId", string);
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }
}
