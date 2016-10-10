package com.grouplearn.project.cloud;

import org.json.JSONObject;

/**
 * Created by Godwin Joseph on 13-05-2016 15:30 for Group Learn application.
 */
public class BaseManager {
    public static final String RESPONSE = "Response";
    public static final String STATUS = "Status";
    public static final String STATUS_CODE = "statusCode";
    public static final String STATUS_MESSAGE = "statusMessage";
    public static final String DATA = "Data";

    public CloudConnectResponse getUpdatedResponse(JSONObject statusObject, CloudConnectResponse response) {
        int responseStatus = statusObject.optInt("statusCode");
        String responseMessage = statusObject.optString("statusMessage");
        response.setResponseMessage(responseMessage);
        response.setResponseStatus(responseStatus);
        return response;
    }

    protected long getLong(String timeStamp) {
        return Long.getLong(timeStamp);
    }
}
