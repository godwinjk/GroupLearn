package com.grouplearn.project.cloud.message;

import android.content.Context;
import android.text.TextUtils;

import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.message.messageGet.CloudGetMessageRequest;
import com.grouplearn.project.cloud.message.messageGet.CloudGetMessageResponse;
import com.grouplearn.project.cloud.message.messageSet.CloudSetMessageRequest;
import com.grouplearn.project.cloud.message.messageSet.CloudSetMessageResponse;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.utilities.AesHelper;
import com.grouplearn.project.utilities.ChatUtilities;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Godwin Joseph on 03-10-2016 10:21 for Group Learn application.
 */
public class CloudMessageManager extends BaseManager implements CloudMessageManagerInterface {
    private static final String TAG = "CloudMessageManager";

    String mBaseurl;

    public CloudMessageManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        mBaseurl = CloudConstants.getBaseUrl();
    }

    @Override
    public void getAllMessages(final CloudGetMessageRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetMessageResponse response = new CloudGetMessageResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (dataObject != null) {
                            int messageCount = dataObject.optInt("messageCount", 0);
                            response.setMessageCount(messageCount);
                            JSONArray messageArray = dataObject.optJSONArray("messageDetails");

                            ArrayList<GLMessage> messageModels = new ArrayList<>();
                            for (int i = 0; messageArray != null && messageCount > 0 && i < messageArray.length(); i++) {
                                JSONObject modelObject = messageArray.optJSONObject(i);
                                GLMessage messageModel = new GLMessage();

                                String encryptedMessage = modelObject.optString("message");
                                long groupCloudId = modelObject.optLong("groupId");
                                messageModel.setReceiverId(groupCloudId);

                                String origMessage = AesHelper.decrypt(groupCloudId, encryptedMessage);
                                messageModel.setMessageBody(origMessage);

                                messageModel.setMessageType(modelObject.optInt("messageType"));
                                messageModel.setReceiverId(modelObject.optLong("groupId"));
                                messageModel.setSenderName(modelObject.optString("senderName"));
                                messageModel.setMessageId(modelObject.optLong("messageId"));
                                messageModel.setSenderId(modelObject.optLong("sender_id"));
                                messageModel.setCloudFilePath(modelObject.optString("fileUrl"));
                                String url = modelObject.optString("fileUrl");
                                if (!TextUtils.isEmpty(url)) {
                                    try {
                                        url = URLDecoder.decode(url, "UTF-8");
                                        url = CloudConstants.getProfileBaseUrl() + url;
                                        messageModel.setCloudFilePath(url);
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                }
                                messageModel.setReadStatus(ChatUtilities.NOT_READ);
                                messageModel.setTimeStamp(modelObject.optString("timestamp"));

                                messageModels.add(messageModel);
                            }
                            response.setMessageModels(messageModels);
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
        httpMethod.setUrl(mBaseurl + "message/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit() + "&type=" + cloudRequest.getType() + "&groupId=" + cloudRequest.getGroupId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);

        httpMethod.execute();
    }

    @Override
    public void setAllMessages(final CloudSetMessageRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudSetMessageResponse response = new CloudSetMessageResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudSetMessageResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
//                                int messageCount = dataObject.optInt("messageCount", 0);
//                                response.setMessageCount(messageCount);
//                                JSONArray messageArray = dataArray.optJSONArray("messageDetails");

                                ArrayList<GLMessage> messageModels = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GLMessage messageModel = new GLMessage();

                                    String encryptedMessage = modelObject.optString("message");
                                    long groupCloudId = modelObject.optLong("groupId");
                                    messageModel.setReceiverId(groupCloudId);

                                    String origMessage = AesHelper.decrypt(groupCloudId, encryptedMessage);
                                    messageModel.setMessageBody(origMessage);

                                    messageModel.setMessageType(modelObject.optInt("messageType"));
                                    messageModel.setReceiverId(modelObject.optLong("groupId"));
                                    messageModel.setSenderName(modelObject.optString("senderName"));
                                    messageModel.setTempId(modelObject.optLong("tempId"));
                                    messageModel.setMessageId(modelObject.optLong("messageId"));
                                    messageModel.setCloudFilePath(modelObject.optString("fileUrl"));

                                    String url = modelObject.optString("fileUrl");
                                    if (!TextUtils.isEmpty(url)) {
                                        try {
                                            url = URLDecoder.decode(url, "UTF-8");
                                            url = CloudConstants.getProfileBaseUrl() + url;
                                            messageModel.setCloudFilePath(url);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    messageModel.setReadStatus(ChatUtilities.NOT_READ);
                                    messageModel.setTimeStamp(modelObject.optString("timestamp"));

                                    messageModels.add(messageModel);
                                }
                                response.setMessageModels(messageModels);
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
        httpMethod.setUrl(mBaseurl + "message");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GLMessage model : cloudRequest.getMessageModels()) {
            JSONObject modelObject = new JSONObject();
            try {
                long groupCloudId = model.getReceiverId();
                modelObject.put("groupId", groupCloudId);

                modelObject.put("messageType", model.getMessageType());

                String origMessage = model.getMessageBody();
                String encryptedMessage = AesHelper.encrypt(groupCloudId, origMessage);

                modelObject.put("message", encryptedMessage);
                modelObject.put("senderName", model.getSenderName());
                modelObject.put("tempId", model.getTempId());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }
}
