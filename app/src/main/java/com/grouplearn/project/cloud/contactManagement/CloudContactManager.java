package com.grouplearn.project.cloud.contactManagement;

import android.content.Context;
import android.text.TextUtils;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLGroup;
import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditRequest;
import com.grouplearn.project.cloud.contactManagement.contactAddOrEdit.CloudContactAddOrEditResponse;
import com.grouplearn.project.cloud.contactManagement.contactDelete.CloudContactDeleteResponse;
import com.grouplearn.project.cloud.contactManagement.contactGet.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.search.CloudUserSearchRequest;
import com.grouplearn.project.cloud.contactManagement.search.CloudUserSearchResponse;
import com.grouplearn.project.cloud.groupManagement.addGroup.CloudAddGroupResponse;
import com.grouplearn.project.cloud.groupManagement.deleteGroup.CloudDeleteGroupRequest;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Godwin Joseph on 07-06-2016 15:45 for Group Learn application.
 */
public class CloudContactManager extends BaseManager implements CloudContactManagerInterface {
    private static final String TAG = "CloudContactManager";
    private String mBaseurl;


    public CloudContactManager(Context mContext) {
        super(mContext);
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
                            ArrayList<GLContact> contactModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contactModel = new GLContact();

                                contactModel.setContactNumber(modelObject.optString("contactName"));
                                contactModel.setContactUniqueId(modelObject.optLong("contactUserId"));
//                                contactModel.setContactIconId(modelObject.optString("contactIconId"));
//                                contactModel.setPrivacy(modelObject.optInt("privacy"));
                                contactModel.setContactStatus(modelObject.optString("userStatus"));
                                if (!TextUtils.isEmpty(modelObject.optString("userStatus"))) {
                                    Log.d(TAG, contactModel.getContactNumber() + "  :  " + modelObject.optString("userStatus"));
                                }
//                                contactModel.setIconUrl(modelObject.optString("contactIconUrl"));
                                String url = modelObject.optString("contactIconUrl");
                                if (!TextUtils.isEmpty(url)) {
                                    try {
                                        url = URLDecoder.decode(url, "UTF-8");
                                        url = CloudConstants.getProfileBaseUrl() + url;
                                        contactModel.setIconUrl(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                contactModel.setStatus(modelObject.optInt("status"));
                                contactModel.setMessage(modelObject.optString("message"));
//                                contactModel.setTimeStamp(modelObject.optString("timestamp"));

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
        for (GLContact model : cloudRequest.getContactModels()) {
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

                            ArrayList<GLGroup> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLGroup groupModel = new GLGroup();
                                groupModel.setGroupCreatedTime(modelObject.optString("createdTime"));
                                groupModel.setGroupUpdatedTime(modelObject.optString("updatedTime"));
                                groupModel.setGroupUniqueId(modelObject.optLong("groupId"));
                                groupModel.setGroupName(modelObject.optString("groupName"));
                                groupModel.setGroupIconId(modelObject.optString("groupIconId"));
                                groupModel.setIconUrl(modelObject.optString("groupIconId"));

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
        for (GLContact model : cloudRequest.getContactModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("contactName", model.getContactName());
                modelObject.put("contactId", model.getContactUniqueId());
                modelObject.put("contactStatus", model.getContactStatus());
                modelObject.put("userIconUrl", model.getIconUrl());
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void deleteContact(final CloudDeleteGroupRequest cloudRequest, final CloudResponseCallback responseCallback) {
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

                            ArrayList<GLContact> groupModelArrayList = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contactModel = new GLContact();
                                contactModel.setContactName(modelObject.optString("contactUserName"));
                                contactModel.setContactUniqueId(modelObject.optLong("contactUserId"));
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
        for (GLGroup string : cloudRequest.getGroupUniqueIdList()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("contactUserId", string.getGroupUniqueId());
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void searchContact(final CloudUserSearchRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudUserSearchResponse response = new CloudUserSearchResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);

                        if (dataObject != null) {

                            ArrayList<GLContact> contactModels = new ArrayList<>();
                            int count = dataObject.optInt("userCount");
                            response.setContactCount(count);
                            JSONArray userArray = dataObject.optJSONArray("userDetails");
                            for (int i = 0; userArray != null && i < userArray.length(); i++) {
                                JSONObject modelObject = userArray.optJSONObject(i);
                                GLContact contactModel = new GLContact();
                                contactModel.setContactNumber(modelObject.optString("userName"));
                                contactModel.setContactName(modelObject.optString("userDisplayName"));
                                contactModel.setContactMailId(modelObject.optString("userEmail"));
                                contactModel.setContactUniqueId(modelObject.optLong("userId"));
                                contactModel.setPrivacy(modelObject.optInt("privacy"));
                                contactModel.setTimeStamp(modelObject.optString("timestamp"));
                                contactModel.setContactStatus(modelObject.optString("userStatus"));
                                contactModel.setStatus(1);

                                contactModels.add(contactModel);
                            }
                            response.setContactModels(contactModels);
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
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(mBaseurl + "user/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime() + ".0000");
        hashMap.put("limit", "" + cloudRequest.getLimit());
        hashMap.put("key", cloudRequest.getKeyWord());

        httpMethod.setHeaderMap(hashMap);

        httpMethod.execute();
    }
}
