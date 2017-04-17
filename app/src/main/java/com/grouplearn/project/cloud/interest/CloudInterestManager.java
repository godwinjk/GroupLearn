package com.grouplearn.project.cloud.interest;

import android.content.Context;

import com.grouplearn.project.bean.GLCourse;
import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.cloud.BaseManager;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.courseManagement.add.CloudAddCourseResponse;
import com.grouplearn.project.cloud.interest.add.CloudAddInterestRequest;
import com.grouplearn.project.cloud.interest.add.CloudAddInterestResponse;
import com.grouplearn.project.cloud.interest.delete.CloudDeleteInterestRequest;
import com.grouplearn.project.cloud.interest.get.CloudGetInterestRequest;
import com.grouplearn.project.cloud.interest.get.CloudGetInterestResponse;
import com.grouplearn.project.cloud.networkManagement.CloudAPICallback;
import com.grouplearn.project.cloud.networkManagement.CloudHttpMethod;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Godwin on 04-12-2016 21:12 for GroupLearn application 22:22 for GroupLearn.
 * @author : Godwin Joseph Kurinjikattu
 */

public class CloudInterestManager extends BaseManager implements CloudInterestManagerInterface {
    private static final String TAG = "CloudInterestManager";

    String mBaseUrl;

    public CloudInterestManager(Context mContext) {
        super(mContext);
        this.mContext = mContext;
        mBaseUrl = CloudConstants.getBaseUrl();
    }

    @Override
    public void addInterest(final CloudAddInterestRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudAddInterestResponse response = new CloudAddInterestResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudAddInterestResponse) getUpdatedResponse(statusObject, response);
                            JSONObject dataObject = jsonObject.optJSONObject(DATA);
                            if (dataObject != null) {
                                JSONArray interestArray = dataObject.optJSONArray("myInterests");

                                ArrayList<GLInterest> glInterests = new ArrayList<>();
                                for (int i = 0; interestArray != null && i < interestArray.length(); i++) {
                                    JSONObject modelObject = interestArray.optJSONObject(i);
                                    GLInterest interest = new GLInterest();

                                    interest.setInterestId(modelObject.optLong("interestId"));
                                    interest.setInterestName(modelObject.optString("interest"));
                                    interest.setStatus(modelObject.optInt("status"));
                                    interest.setInterestType(GLInterest.INTEREST);
                                    interest.setMessage(modelObject.optString("message"));

                                    glInterests.add(interest);
                                }
                                response.setInterests(glInterests);
                                JSONArray skillsArray = dataObject.optJSONArray("mySkills");
                                ArrayList<GLInterest> glSkills = new ArrayList<>();
                                for (int i = 0; skillsArray != null && i < skillsArray.length(); i++) {
                                    JSONObject modelObject = skillsArray.optJSONObject(i);
                                    GLInterest interest = new GLInterest();

                                    interest.setInterestId(modelObject.optLong("skillId"));
                                    interest.setInterestName(modelObject.optString("skill"));
                                    interest.setStatus(modelObject.optInt("status"));
                                    interest.setInterestType(GLInterest.SKILL);
                                    interest.setMessage(modelObject.optString("message"));
                                    glSkills.add(interest);
                                }
                                response.setSkills(glSkills);
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
        httpMethod.setUrl(mBaseUrl + "interest");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        JSONObject object = new JSONObject();
        JSONArray interestsArray = new JSONArray();
        for (GLInterest model : cloudRequest.getInterests()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("interest", model.getInterestName());
                modelObject.put("interestId", 0);
                interestsArray.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("myInterests", interestsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray skillsArray = new JSONArray();
        for (GLInterest model : cloudRequest.getSkills()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("skill", model.getInterestName());
                modelObject.put("skillId", 0);
                skillsArray.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("mySkills", skillsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity.put(object);
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void editInterest(final CloudAddInterestRequest cloudRequest, final CloudResponseCallback responseCallback) {
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

                                    glCourse.setCourseId(modelObject.optLong("interestId"));
                                    glCourse.setCourseName(modelObject.optString("interest"));

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
        httpMethod.setUrl(mBaseUrl + "interest");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);
        JSONArray entity = new JSONArray();
        for (GLInterest model : cloudRequest.getInterests()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("interest", model.getInterestName());
                modelObject.put("interestId", model.getInterestId());
                entity.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void deleteInterest(final CloudDeleteInterestRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudAddInterestResponse response = new CloudAddInterestResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        if (statusObject != null) {
                            response = (CloudAddInterestResponse) getUpdatedResponse(statusObject, response);
                            JSONObject dataObject = jsonObject.optJSONObject(DATA);
                            if (dataObject != null) {
                                JSONArray interestArray = dataObject.optJSONArray("myInterests");

                                ArrayList<GLInterest> glInterests = new ArrayList<>();
                                for (int i = 0; interestArray != null && i < interestArray.length(); i++) {
                                    JSONObject modelObject = interestArray.optJSONObject(i);
                                    GLInterest interest = new GLInterest();

                                    interest.setInterestId(modelObject.optLong("interestId"));
                                    interest.setInterestName(modelObject.optString("interest"));
                                    interest.setStatus(modelObject.optInt("status"));
                                    interest.setInterestType(GLInterest.INTEREST);
                                    interest.setMessage(modelObject.optString("message"));

                                    glInterests.add(interest);
                                }
                                response.setInterests(glInterests);
                                JSONArray skillsArray = dataObject.optJSONArray("mySkills");
                                ArrayList<GLInterest> glSkills = new ArrayList<>();
                                for (int i = 0; skillsArray != null && i < skillsArray.length(); i++) {
                                    JSONObject modelObject = skillsArray.optJSONObject(i);
                                    GLInterest interest = new GLInterest();

                                    interest.setInterestId(modelObject.optLong("skillId"));
                                    interest.setInterestName(modelObject.optString("skill"));
                                    interest.setStatus(modelObject.optInt("status"));
                                    interest.setInterestType(GLInterest.SKILL);
                                    interest.setMessage(modelObject.optString("message"));
                                    glSkills.add(interest);
                                }
                                response.setSkills(glInterests);
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
        httpMethod.setUrl(mBaseUrl + "interest/1");

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());

        httpMethod.setHeaderMap(hashMap);

        JSONArray entity = new JSONArray();
        JSONObject object = new JSONObject();
        JSONArray interestsArray = new JSONArray();
        for (GLInterest model : cloudRequest.getInterests()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("interest", model.getInterestName());
                modelObject.put("interestId", model.getInterestId());
                interestsArray.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("myInterests", interestsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray skillsArray = new JSONArray();
        for (GLInterest model : cloudRequest.getSkills()) {
            JSONObject modelObject = new JSONObject();
            try {
                modelObject.put("skill", model.getInterestName());
                modelObject.put("skillId", model.getInterestId());
                skillsArray.put(modelObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            object.put("mySkills", skillsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        entity.put(object);
        httpMethod.setEntityString(entity.toString());
        httpMethod.execute();
    }

    @Override
    public void getInterest(final CloudGetInterestRequest cloudRequest, final CloudResponseCallback responseCallback) {
        if (cloudRequest == null || responseCallback == null)
            throw new IllegalArgumentException(TAG + " : Request Or Response is Null");
        CloudAPICallback listener = new CloudAPICallback() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                CloudGetInterestResponse response = new CloudGetInterestResponse();
                if (jsonObject != null) {
                    jsonObject = jsonObject.optJSONObject(RESPONSE);
                    if (jsonObject != null) {
                        JSONObject statusObject = jsonObject.optJSONObject(STATUS);
                        JSONObject dataObject = jsonObject.optJSONObject(DATA);
                        if (statusObject != null) {
                            response = (CloudGetInterestResponse) getUpdatedResponse(statusObject, response);
                            if (dataObject != null) {
                                JSONObject interestObject = dataObject.optJSONObject("myInterests");

                                //Retrieving interests
                                int interestCount = interestObject.optInt("interestCount", 0);
                                JSONArray interestArray = interestObject.optJSONArray("interestDetails");
                                response.setInterestCount(interestCount);

                                ArrayList<GLInterest> glInterests = new ArrayList<>();
                                for (int i = 0; interestArray != null && interestCount > 0 && i < interestArray.length(); i++) {
                                    JSONObject modelObject = interestArray.optJSONObject(i);
                                    GLInterest glInterest = new GLInterest();

                                    glInterest.setInterestId(modelObject.optLong("interestId"));
                                    glInterest.setInterestName(modelObject.optString("interest"));
                                    glInterest.setUserId(modelObject.optLong("userId"));
                                    glInterest.setInterestType(GLInterest.INTEREST);
                                    glInterest.setTimeStamp(modelObject.optString("timestamp"));

                                    glInterests.add(glInterest);
                                }
                                response.setInterests(glInterests);
                                //retrieving skills
                                JSONObject skillObject = dataObject.optJSONObject("mySkills");
                                int skillsCount = skillObject.optInt("skillCount", 0);
                                JSONArray skillsArray = skillObject.optJSONArray("skillDetails");
                                response.setSkillsCount(skillsCount);

                                ArrayList<GLInterest> glSkills = new ArrayList<>();
                                for (int i = 0; skillsArray != null && skillsCount > 0 && i < skillsArray.length(); i++) {
                                    JSONObject modelObject = skillsArray.optJSONObject(i);
                                    GLInterest glInterest = new GLInterest();

                                    glInterest.setInterestId(modelObject.optLong("skillId"));
                                    glInterest.setInterestName(modelObject.optString("skill"));
                                    glInterest.setUserId(modelObject.optLong("userId"));
                                    glInterest.setInterestType(GLInterest.SKILL);
                                    glInterest.setTimeStamp(modelObject.optString("timestamp"));

                                    glSkills.add(glInterest);
                                }
                                response.setSkills(glSkills);
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
        httpMethod.setUrl(mBaseUrl + "interest/1?start=" + cloudRequest.getStartTime() + "&limit=" + cloudRequest.getLimit() + "&userId=" + cloudRequest.getUserId());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("token", cloudRequest.getToken());
        hashMap.put("start", "" + cloudRequest.getStartTime());
        hashMap.put("limit", "" + cloudRequest.getLimit());
        hashMap.put("key", "" + cloudRequest.getUserId());

        httpMethod.setHeaderMap(hashMap);
        httpMethod.execute();
    }
}
