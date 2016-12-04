package com.grouplearn.project.cloud.interest.add;

import com.grouplearn.project.cloud.CloudConnectRequest;
import com.grouplearn.project.models.GLInterest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudAddInterestRequest extends CloudConnectRequest {
    ArrayList<GLInterest> interests = new ArrayList<>();

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    } public void setInterests(GLInterest interests) {
        this.interests.add(interests);
    }
    @Override
    public int validate() {
        return 0;
    }
}
