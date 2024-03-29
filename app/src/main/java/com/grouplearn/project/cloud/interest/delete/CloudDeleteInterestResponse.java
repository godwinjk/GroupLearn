package com.grouplearn.project.cloud.interest.delete;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.bean.GLInterest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudDeleteInterestResponse extends CloudConnectResponse {
    ArrayList<GLInterest> interests = new ArrayList<>();

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    }
}
