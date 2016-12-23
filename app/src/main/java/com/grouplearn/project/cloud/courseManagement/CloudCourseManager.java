package com.grouplearn.project.cloud.courseManagement;

import android.content.Context;
import android.text.TextUtils;

import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseRequest;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseResponse;
import com.grouplearn.project.cloud.courseManagement.delete.CloudDeleteCourseRequest;
import com.grouplearn.project.cloud.courseManagement.get.CloudGetCourseRequest;
import com.grouplearn.project.cloud.courseManagement.get.CloudGetCourseResponse;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by WiSilica on 03-12-2016 22:28 for GroupLearn application.
 */

public class CloudCourseManager extends BaseManager implements CloudCourseManagerInterface {
    private static final String TAG = "CloudCourseManager";
    Context mContext;
    String mBaseurl;

    public CloudCourseManager(Context mContext) {
        this.mContext = mContext;
        mBaseurl = CloudConstants.getBaseUrl();
    }

    @Override
    public void addCourses(final CloudAddCourseRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudAddCourseResponse response = new CloudAddCourseResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudAddCourseResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<GLCourse> glCourses = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GLCourse glCourse = new GLCourse();

                                    glCourse.setCourseId(modelObject.optLong("courseId"));
                                    glCourse.setCourseName(modelObject.optString("courseName"));
                                    glCourse.setCourseIconId(modelObject.optString("courseIconId"));
                                    glCourse.setCourseStatus(modelObject.optInt("courseStatus"));
                                    glCourse.setStatus(modelObject.optInt("status"));
                                    glCourse.setMessage(modelObject.optString("message"));

                                    glCourses.add(glCourse);
                                }
                                response.setGlCourses(glCourses);
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
        httpMethod.setUrl(mBaseurl + "course");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GLCourse model : cloudRequest.getGlCourses()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("courseName", model.getCourseName());
                modelObject.put("courseIconId", 10/*model.getGroupIconId()*/);
                modelObject.put("url", model.getUrl());
                modelObject.put("url", model.getUrl());
                modelObject.put("definition", model.getDefinition());
                modelObject.put("contact", model.getContactDetails());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void editCourses(final CloudAddCourseRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudAddCourseResponse response = new CloudAddCourseResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudAddCourseResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<GLCourse> glCourses = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GLCourse glCourse = new GLCourse();

                                    glCourse.setCourseId(modelObject.optLong("courseId"));
                                    glCourse.setCourseName(modelObject.optString("courseName"));
                                    glCourse.setCourseIconId(modelObject.optString("courseIconId"));
                                    glCourse.setCourseStatus(modelObject.optInt("courseStatus"));

                                    glCourses.add(glCourse);
                                }
                                response.setGlCourses(glCourses);
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
        httpMethod.setUrl(mBaseurl + "course/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GLCourse model : cloudRequest.getGlCourses()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("courseName", model.getCourseName());
                modelObject.put("courseIconId", 10/*model.getGroupIconId()*/);
                modelObject.put("url", model.getUrl());
                modelObject.put("courseId", model.getCourseId());
                modelObject.put("definition", model.getDefinition());
                modelObject.put("contact", model.getContactDetails());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void deleteCourses(final CloudDeleteCourseRequest request, final CloudResponseCallback callback) {
        if (request == null || callback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudAddCourseResponse response = new CloudAddCourseResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudAddCourseResponse) getUpdatedResponse(statusObject, response);
                            JSONArray dataArray = jsonObject.optJSONArray(DATA);
                            if (dataArray != null) {
                                ArrayList<GLCourse> glCourses = new ArrayList<>();
                                for (int i = 0; dataArray != null && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GLCourse glCourse = new GLCourse();

                                    glCourse.setCourseId(modelObject.optLong("courseId"));
                                    glCourse.setCourseName(modelObject.optString("courseName"));
                                    glCourse.setCourseIconId(modelObject.optString("courseIconId"));
                                    glCourse.setCourseStatus(modelObject.optInt("courseStatus"));
                                    glCourse.setStatus(modelObject.optInt("status"));
                                    glCourse.setMessage(modelObject.optString("message"));

                                    glCourses.add(glCourse);
                                }
                                response.setGlCourses(glCourses);
                                if (callback != null) {
                                    callback.onSuccess(request, response);
                                }
                            } else {
                                if (callback != null) {
                                    callback.onFailure(request, new CloudError(ErrorHandler.INVALID_DATA_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_DATA_FROM_CLOUD));
                                }
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure(request, new CloudError(ErrorHandler.INVALID_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.INVALID_RESPONSE_FROM_CLOUD));
                            }
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure(request, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                        }
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(request, new CloudError(ErrorHandler.EMPTY_RESPONSE_FROM_CLOUD, ErrorHandler.ErrorMessage.EMPTY_RESPONSE_FROM_CLOUD));
                    }
                }
            }

            @Override
            public void onFailure(CloudError cloudError) {
                if (callback != null) {
                    callback.onFailure(request, cloudError);
                }
            }
        };
        CloudHttpMethod httpMethod = new CloudHttpMethod(mContext, listener);
        httpMethod.setRequestType(CloudHttpMethod.POST_METHOD);
        httpMethod.setUrl(mBaseurl + "course-delete");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", request.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GLCourse model : request.getGlCourses()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("courseName", model.getCourseName());
                modelObject.put("courseIconId", 10/*model.getGroupIconId()*/);
                modelObject.put("url", model.getUrl());
                modelObject.put("courseId", model.getCourseId());

                modelObject.put("definition", model.getDefinition());
                modelObject.put("contact", model.getContactDetails());

                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void getCourses(final CloudGetCourseRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetCourseResponse response = new CloudGetCourseResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudGetCourseResponse) getUpdatedResponse(statusObject, response);
                            if (dataObject != null) {
                                int courseCount = dataObject.optInt("courseCount", 0);
                                JSONArray dataArray = dataObject.optJSONArray("courseDetails");
                                response.setCourseCount(courseCount);

                                ArrayList<GLCourse> glCourses = new ArrayList<>();
                                for (int i = 0; dataArray != null && courseCount > 0 && i < dataArray.length(); i++) {
                                    JSONObject modelObject = dataArray.optJSONObject(i);
                                    GLCourse glCourse = new GLCourse();

                                    glCourse.setCourseId(modelObject.optLong("courseId"));
                                    glCourse.setCourseName(modelObject.optString("courseName"));
                                    glCourse.setCourseUserId(modelObject.optLong("courseUserId"));
                                    glCourse.setDefinition(modelObject.optString("definition"));
                                    glCourse.setGroupId(modelObject.optLong("groupId"));
                                    glCourse.setGroupName(modelObject.optString("groupName"));
                                    glCourse.setCourseIconId(modelObject.optString("courseIconId"));
                                    glCourse.setContactDetails(modelObject.optString("contact"));
                                    glCourse.setUrl(modelObject.optString("url"));
//                                    glCourse.setIconUrl(modelObject.optString("groupIconUrl"));
                                    glCourse.setGroupIconId(modelObject.optString("groupIconId"));

                                    String url = modelObject.optString("groupIconUrl");
                                    if (!TextUtils.isEmpty(url)) {
                                        try {
                                            url = URLDecoder.decode(url, "UTF-8");
                                            url = CloudConstants.getProfileBaseUrl() + url;
                                            glCourse.setIconUrl(url);
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    glCourse.setTimeStamp(modelObject.optString("timestamp"));
                                    glCourses.add(glCourse);
                                }
                                response.setGlCourses(glCourses);
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
        httpMethod.setUrl(mBaseurl + "course/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit() + "&key=" + cloudRequest.getKey() + "&userId=" + cloudRequest.getUserId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());
        hashMap.put("key", "" + cloudRequest.getKey());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }
}
