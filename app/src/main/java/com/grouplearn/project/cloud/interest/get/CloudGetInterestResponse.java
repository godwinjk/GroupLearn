package com.grouplearn.project.cloud.interest.get;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.models.GLInterest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudGetInterestResponse extends CloudConnectResponse {
    int interestCount;
    ArrayList<GLInterest> interests = new ArrayList<>();

    public int getInterestCount() {
        return interestCount;
    }

    public void setInterestCount(int interestCount) {
        this.interestCount = interestCount;
    }

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    }
}
