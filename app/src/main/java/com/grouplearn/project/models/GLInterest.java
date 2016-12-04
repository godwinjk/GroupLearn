package com.grouplearn.project.models;

/**
 * Created by WiSilica on 04-12-2016 20:48 for GroupLearn application.
 */

public class GLInterest extends BaseModel {
    long interestId;
    String interestName;
    long userId;

    public long getInterestId() {
        return interestId;
    }

    public void setInterestId(long interestId) {
        this.interestId = interestId;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
