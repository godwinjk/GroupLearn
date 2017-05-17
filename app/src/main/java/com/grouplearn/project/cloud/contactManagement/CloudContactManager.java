package com.grouplearn.project.cloud.contactManagement;

import android.content.Context;

import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.contactManagement.accept.CloudContactAcceptRequest;
import com.grouplearn.project.cloud.contactManagement.get.CloudContactGetRequest;
import com.grouplearn.project.cloud.contactManagement.get.CloudContactGetResponse;
import com.grouplearn.project.cloud.contactManagement.getRequest.CloudGetContactRequest;
import com.grouplearn.project.cloud.contactManagement.getRequest.CloudGetContactResponse;
import com.grouplearn.project.cloud.contactManagement.request.CloudContactRequest;
import com.grouplearn.project.cloud.contactManagement.request.CloudContactResponse;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
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

    public CloudContactManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        this.mBaseurl = CloudConstants.getBaseUrl();
    }

    @Override
    public void requestContact(final CloudContactRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudContactResponse response = new CloudContactResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        getUpdatedResponse(statusObject, response);
                        JSONArray dataArray = jsonObject.optJSONArray(DATA);

                        if (dataArray != null) {
                            ArrayList<GLContact> contactModels = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contactModel = new GLContact();
                                contactModel.setContactName(modelObject.optString("userDisplayName"));
                                contactModel.setContactMailId(modelObject.optString("userEmail"));
                                contactModel.setContactUserId(modelObject.optLong("userId"));
                                contactModel.setPrivacy(modelObject.optInt("privacy"));
                                contactModel.setTimeStamp(modelObject.optString("timestamp"));
                                contactModel.setContactStatus(modelObject.optString("userStatus"));

                                contactModel.setStatus(modelObject.optInt("status"));
                                contactModel.setMessage(modelObject.optString("message"));
                                contactModel.setStatus(1);

                                contactModels.add(contactModel);
                            }
                            response.setContacts(contactModels);
                            responseCallback.onSuccess(cloudRequest, response);

                        } else {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));

                        }
                    } else {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));

                    }
                } else {
                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                responseCallback.onFailure(cloudRequest, cloudError);
            }
        };

        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(mBaseurl + "contact-request");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        JSONArray entity = new JSONArray();
        for (int i = 0; i < cloudRequest.getContacts().size(); i++) {
            GLContact contact = cloudRequest.getContacts().get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("userId", contact.getContactUserId());
                object.put("userEmail", contact.getContactMailId());
                object.put("userDisplayName", contact.getContactName());
                entity.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setHeaderMap(hashMap);
        httpMethod.setEntityString(entity.toString());

        httpMethod.execute();
    }

    @Override
    public void acceptContactRequest(final CloudContactAcceptRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudContactResponse response = new CloudContactResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        getUpdatedResponse(statusObject, response);
                        JSONArray dataArray = jsonObject.optJSONArray(DATA);

                        if (dataArray != null) {
                            ArrayList<GLContact> contactModels = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contactModel = new GLContact();
                                contactModel.setContactUserId(modelObject.optLong("userId"));

                                contactModel.setStatus(modelObject.optInt("status"));
                                contactModel.setMessage(modelObject.optString("message"));
                                contactModel.setStatus(1);

                                contactModels.add(contactModel);
                            }
                            response.setContacts(contactModels);
                            responseCallback.onSuccess(cloudRequest, response);

                        } else {
                            responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));

                        }
                    } else {
                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));

                    }
                } else {
                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                responseCallback.onFailure(cloudRequest, cloudError);
            }
        };

        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(mBaseurl + "contact-request/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        JSONArray entity = new JSONArray();
        for (int i = 0; i < cloudRequest.getContacts().size(); i++) {
            GLContact contact = cloudRequest.getContacts().get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("userId", contact.getContactUserId());
                object.put("action", contact.getAction());
                entity.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setHeaderMap(hashMap);
        httpMethod.setEntityString(entity.toString());

        httpMethod.execute();
    }

    @Override
    public void getContactRequests(final CloudGetContactRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetContactResponse response = new CloudGetContactResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudGetContactResponse) getUpdatedResponse(statusObject, response);
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
                            int contactRequestCount = dataObject.optInt("contactRequestCount", 0);
                            response.setContactCount(contactRequestCount);
                            ArrayList<GLContact> contacts = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact groupModel = new GLContact();
                                groupModel.setContactUserId(modelObject.optLong("contactId"));
                                groupModel.setContactUserId(modelObject.optLong("userId"));
                                groupModel.setContactStatus(modelObject.optString("userStatus"));
                                groupModel.setIconUrl(modelObject.optString("userIconUrl"));
                                groupModel.setContactMailId(modelObject.optString("userEmail"));
                                groupModel.setContactName(modelObject.optString("userDisplayName"));

                                contacts.add(groupModel);
                            }
                            response.setContacts(contacts);
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
                } else{
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
        httpMethod.setUrl(mBaseurl + "contact/1?start" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void getContacts(final CloudContactGetRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudContactGetResponse response = new CloudContactGetResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudContactGetResponse) getUpdatedResponse(statusObject, response);
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
                            int contactRequestCount = dataObject.optInt("contactCount", 0);
                            response.setContactCount(contactRequestCount);
                            ArrayList<GLContact> contacts = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contact = new GLContact();
//                                contact.setContactUserId(modelObject.optLong("contactId"));
                                contact.setContactUserId(modelObject.optLong("userId"));
                                contact.setContactStatus(modelObject.optString("userStatus"));
                                contact.setIconUrl(modelObject.optString("userIconUrl"));
                                contact.setContactMailId(modelObject.optString("userEmail"));
                                contact.setContactName(modelObject.optString("userDisplayName"));
//fetching interests;
                                JSONArray interestArray = modelObject.optJSONArray("interests");
                                if (interestArray != null) {
                                    ArrayList<GLInterest> interests = new ArrayList<>();
                                    for (int j = 0; interestArray != null && i < interestArray.length(); i++) {
                                        JSONObject interestObject = interestArray.optJSONObject(j);
                                        GLInterest interest = new GLInterest();
                                        interest.setInterestType(GLInterest.INTEREST);
                                        interest.setInterestId(interestObject.optInt("interestId"));
                                        interest.setInterestName(interestObject.optString("interest"));
                                        interest.setUserId(interestObject.optLong("userId"));
                                        interest.setTimeStamp(interestObject.optString("timestamp"));

                                        interests.add(interest);
                                    }
                                    contact.setInterests(interests);
                                }
                                //fetching skills;
                                JSONArray skillArray = modelObject.optJSONArray("skills");
                                if (skillArray != null) {
                                    ArrayList<GLInterest> skills = new ArrayList<>();
                                    for (int j = 0; skillArray != null && i < skillArray.length(); i++) {
                                        JSONObject interestObject = skillArray.optJSONObject(j);
                                        GLInterest interest = new GLInterest();
                                        interest.setInterestType(GLInterest.INTEREST);
                                        interest.setInterestId(interestObject.optInt("interestId"));
                                        interest.setInterestName(interestObject.optString("interest"));
                                        interest.setUserId(interestObject.optLong("userId"));
                                        interest.setTimeStamp(interestObject.optString("timestamp"));

                                        skills.add(interest);
                                    }
                                    contact.setSkills(skills);
                                }

                                contacts.add(contact);
                            }
                            response.setContacts(contacts);
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
        httpMethod.setUrl(mBaseurl + "contact/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit() + "&userId=" + cloudRequest.getUserId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }

    @Override
    public void getUsers(final CloudContactGetRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudContactGetResponse response = new CloudContactGetResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudContactGetResponse) getUpdatedResponse(statusObject, response);
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
                            int contactRequestCount = dataObject.optInt("contactCount", 0);
                            response.setContactCount(contactRequestCount);
                            ArrayList<GLContact> contacts = new ArrayList<>();
                            for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                JSONObject modelObject = dataArray.optJSONObject(i);
                                GLContact contact = new GLContact();
//                                contact.setContactUserId(modelObject.optLong("contactId"));
                                contact.setContactUserId(modelObject.optLong("userId"));
                                contact.setContactStatus(modelObject.optString("userStatus"));
                                contact.setIconUrl(modelObject.optString("userIconUrl"));
                                contact.setContactMailId(modelObject.optString("userEmail"));
                                contact.setContactName(modelObject.optString("userDisplayName"));
//fetching interests;
                                JSONArray interestArray = modelObject.optJSONArray("interests");
                                if (interestArray != null) {
                                    ArrayList<GLInterest> interests = new ArrayList<>();
                                    for (int j = 0; interestArray != null && i < interestArray.length(); i++) {
                                        JSONObject interestObject = interestArray.optJSONObject(j);
                                        GLInterest interest = new GLInterest();
                                        interest.setInterestType(GLInterest.INTEREST);
                                        interest.setInterestId(interestObject.optInt("interestId"));
                                        interest.setInterestName(interestObject.optString("interest"));
                                        interest.setUserId(interestObject.optLong("userId"));
                                        interest.setTimeStamp(interestObject.optString("timestamp"));

                                        interests.add(interest);
                                    }
                                    contact.setInterests(interests);
                                }
                                //fetching skills;
                                JSONArray skillArray = modelObject.optJSONArray("skills");
                                if (skillArray != null) {
                                    ArrayList<GLInterest> skills = new ArrayList<>();
                                    for (int j = 0; skillArray != null && i < skillArray.length(); i++) {
                                        JSONObject interestObject = skillArray.optJSONObject(j);
                                        GLInterest interest = new GLInterest();
                                        interest.setInterestType(GLInterest.INTEREST);
                                        interest.setInterestId(interestObject.optInt("interestId"));
                                        interest.setInterestName(interestObject.optString("interest"));
                                        interest.setUserId(interestObject.optLong("userId"));
                                        interest.setTimeStamp(interestObject.optString("timestamp"));

                                        skills.add(interest);
                                    }
                                    contact.setSkills(skills);
                                }

                                contacts.add(contact);
                            }
                            response.setContacts(contacts);
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
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());
        hashMap.put("key", cloudRequest.getKey());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }
}
