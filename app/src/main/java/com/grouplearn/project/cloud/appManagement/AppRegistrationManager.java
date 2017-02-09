package com.grouplearn.project.cloud.appManagement;

import android.content.Context;

import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationRequest;
import com.grouplearn.project.cloud.appManagement.appRegistration.CloudAppRegistrationResponse;
import com.grouplearn.project.cloud.appManagement.gcmRegistration.GcmRegistration;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Godwin Joseph on 13-05-2016 12:52 for Group Learn application.
 */
public class AppRegistrationManager extends BaseManager implements AppRegistrationInterface {
    private static final String TAG = "AppRegistrationManager";
    private String baseUrl;

    public AppRegistrationManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        baseUrl = CloudConstants.getBaseUrl();
    }

    @Override
    public void registerApp(final CloudAppRegistrationRequest cloudRequest, final CloudResponseCallback responseCallback) throws IllegalArgumentException {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudConnectResponse response = new CloudAppRegistrationResponse();
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
                            int appUniqueId = dataObject.optInt("appId", 0);
                            ((CloudAppRegistrationResponse) response).setAppUniqueId(appUniqueId);
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
                if (responseCallback != null) {
                    responseCallback.onFailure(cloudRequest, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(baseUrl + "app");

        JSONObject entity = new JSONObject();
        try {
            entity.put("modelInfo", cloudRequest.getPhoneModel());
            entity.put("osInfo", cloudRequest.getPhoneOsVersion());
            entity.put("gcmToken", cloudRequest.getGcmToken());
            entity.put("uniqueId", cloudRequest.getPhoneUniqueId());
            entity.put("appVersion", cloudRequest.getAppVersion());
            entity.put("imeiNo", cloudRequest.getImeiNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void registerGcm(CloudResponseCallback callback) {
        GcmRegistration gcmRegistration = new GcmRegistration(mContext, callback);
        gcmRegistration.execute();
    }
}
