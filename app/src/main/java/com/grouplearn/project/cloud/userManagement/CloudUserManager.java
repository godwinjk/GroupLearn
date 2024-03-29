package com.grouplearn.project.cloud.userManagement;

import android.content.Context;
import android.text.TextUtils;

import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.cloud.userManagement.changePin.CloudChangePinRequest;
import com.grouplearn.project.cloud.userManagement.changePin.CloudChangePinResponse;
import com.grouplearn.project.cloud.userManagement.forgotPasswordRequest.CloudForgotPasswordRequest;
import com.grouplearn.project.cloud.userManagement.forgotPasswordRequest.CloudForgotPasswordResponse;
import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInRequest;
import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInResponse;
import com.grouplearn.project.cloud.userManagement.signOut.CloudSignOutRequest;
import com.grouplearn.project.cloud.userManagement.signUp.CloudSignUpRequest;
import com.grouplearn.project.cloud.userManagement.signUp.CloudSignUpResponse;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusResponse;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileRequest;
import com.grouplearn.project.cloud.userManagement.upload.CloudUploadProfileResponse;
import com.grouplearn.project.utilities.AppUtility;
import com.grouplearn.project.utilities.Helper;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;

/**
 * Created by Godwin Joseph on 13-05-2016 13:06 for Group Learn application.
 */
public class CloudUserManager extends BaseManager implements CloudUserManagerInterface {
    private static final String TAG = "CloudUserManager";
    String baseUrl;

    public CloudUserManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        baseUrl = CloudConstants.getBaseUrl();
    }

    @Override
    public void signIn(final CloudSignInRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudSignInResponse response = new CloudSignInResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudSignInResponse) getUpdatedResponse(statusObject, response);
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {

                            String userName = dataObject.optString("userName");
                            String userDisplayName = dataObject.optString("userDisplayName");
                            String userEmail = dataObject.optString("userEmail");
                            String token = dataObject.optString("token");
                            String userStatus = dataObject.optString("userStatus");
                            int userPrivacy = dataObject.optInt("userPrivacy");
                            long userId = dataObject.optLong("userId");
                            String url = dataObject.optString("userIconUrl");
                            if (!TextUtils.isEmpty(url)) {
                                try {
                                    url = URLDecoder.decode(url, "UTF-8");
                                    url = CloudConstants.getProfileBaseUrl() + url;
                                    response.setIconUrl(url);
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                            response.setUserToken(token);
                            response.setUserDisplayName(userDisplayName);
                            response.setUserEmail(userEmail);
                            response.setUserName(userName);

                            response.setUserId(userId);
                            response.setUserPrivacy(userPrivacy);
                            response.setUserStatus(userStatus);

                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(baseUrl + "login");
//        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("appUniqueId", "" + cloudRequest.getAppUniqueId());
//        httpMethod.setHeaderMap(headerMap);
        JSONObject entity = new JSONObject();
        try {
            entity.put("userEmail", cloudRequest.getUserName());
            entity.put("userPassword", Helper.hashString(cloudRequest.getPassword()));
            entity.put("appId", cloudRequest.getAppUniqueId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void singUp(final CloudSignUpRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudSignUpResponse();
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
                            String userToken = dataObject.optString("userToken");

                            ((CloudSignUpResponse) response).setUserToken(userToken);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(baseUrl + "user");
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("appUniqueId", "" + cloudRequest.getAppUniqueId());
        httpMethod.setHeaderMap(headerMap);
        JSONObject entity = new JSONObject();
        try {
            entity.put("userName", cloudRequest.getUserName());
            entity.put("userPassword", Helper.hashString(cloudRequest.getPassword()));
            entity.put("userDisplayName", cloudRequest.getDisplayName());
            entity.put("userEmail", cloudRequest.getMailId());
            entity.put("appId", cloudRequest.getAppUniqueId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void signOut(final CloudSignOutRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudSignUpResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudSignUpResponse) getUpdatedResponse(statusObject, response);
                        } else {
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                        if (dataObject != null) {
                            String userToken = dataObject.optString("userToken");
                            ((CloudSignUpResponse) response).setUserToken(userToken);
                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.DELETE_METHOD);
        httpMethod.setUrl("");
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("token", "" + cloudRequest.getToken());
        httpMethod.setHeaderMap(headerMap);
        httpMethod.execute();
    }

    @Override
    public void getStatus(final CloudStatusRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudStatusResponse();
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
                            String userStatus = dataObject.optString("userStatus");
                            String userDisplayName = dataObject.optString("userDisplayName");
                            int privacy = dataObject.optInt("privacy");

                            ((CloudStatusResponse) response).setStatus(userStatus);
                            ((CloudStatusResponse) response).setDisplayName(userDisplayName);
                            ((CloudStatusResponse) response).setPrivacy(privacy);

                            if (responseCallback != null) {
                                responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.GET_METHOD);
        httpMethod.setUrl(baseUrl + "status");
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("token", "" + cloudRequest.getToken());

        httpMethod.setHeaderMap(headerMap);
        httpMethod.execute();
    }

    @Override
    public void setStatus(final CloudStatusRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudStatusResponse();
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
                            String userStatus = dataObject.optString("userStatus");
                            String userDisplayName = dataObject.optString("userDisplayName");
                            int privacy = dataObject.optInt("privacy");

                            ((CloudStatusResponse) response).setStatus(userStatus);
                            ((CloudStatusResponse) response).setDisplayName(userDisplayName);
                            ((CloudStatusResponse) response).setPrivacy(privacy);
                            if (responseCallback != null) {
                                responseCallback.onSuccess(cloudRequest, response);
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(baseUrl + "status");
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("token", "" + cloudRequest.getToken());
        JSONObject entity = new JSONObject();
        try {
            entity.put("userStatus", cloudRequest.getStatus());
            entity.put("privacy", cloudRequest.getPrivacyValue());
            entity.put("userDisplayName", cloudRequest.getUserDisplayName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setHeaderMap(headerMap);
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void forgotPassword(final CloudForgotPasswordRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudForgotPasswordResponse response = new CloudForgotPasswordResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);

                        if (statusObject != null) {
                            response = (CloudForgotPasswordResponse) getUpdatedResponse(statusObject, response);
                            if (response.getResponseStatus() == 20001) {
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(baseUrl + "forgot");
        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("token", "" + cloudRequest.getToken());
        JSONObject entity = new JSONObject();
        try {
            entity.put("userEmail", cloudRequest.getUserName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setHeaderMap(headerMap);
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void changePin(final CloudChangePinRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");

        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudChangePinResponse response = new CloudChangePinResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);

                        if (statusObject != null) {
                            response = (CloudChangePinResponse) getUpdatedResponse(statusObject, response);
                            if (response.getResponseStatus() == 20001) {
                                if (responseCallback != null) {
                                    responseCallback.onSuccess(cloudRequest, response);
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(statusObject.optInt("statusCode"), statusObject.optString("statusMessage")));
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

            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.PUT_METHOD);
        httpMethod.setUrl(baseUrl + "forgot/1");
        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("token", "" + cloudRequest.getToken());
        JSONObject entity = new JSONObject();
        try {
            entity.put("userEmail", cloudRequest.getUserName());
            entity.put("requestId", cloudRequest.getOtp());
            entity.put("password", AppUtility.hashString(cloudRequest.getPassword()));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setHeaderMap(headerMap);
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void uploadImage(final CloudUploadProfileRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudUploadProfileResponse response = new CloudUploadProfileResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudUploadProfileResponse) getUpdatedResponse(statusObject, response);
                            String url = statusObject.optString("url");
                            if (!TextUtils.isEmpty(url)) {
                                try {
                                    url = URLDecoder.decode(url, "UTF-8");
                                    url = CloudConstants.getProfileBaseUrl() + url;
                                    response.setIconUrl(url);
                                    if (responseCallback != null) {
                                        responseCallback.onSuccess(cloudRequest, response);
                                    }
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    if (responseCallback != null) {
                                        responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                                    }
                                }
                            } else {
                                if (responseCallback != null) {
                                    responseCallback.onFailure(cloudRequest, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
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
        httpMethod.setUrl(baseUrl + "profile-upload");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);

        JSONObject entity = new JSONObject();
        try {
//            entity.put("groupId", cloudRequest.getGroupId());
            entity.put("image", cloudRequest.getImageBase64());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }
}
