package com.grouplearn.project.cloud.interest.add;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.bean.GLInterest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudAddInterestResponse extends CloudConnectResponse {
    ArrayList<GLInterest> interests = new ArrayList<>();
    ArrayList<GLInterest> skills = new ArrayList<>();

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    }

    public ArrayList<GLInterest> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<GLInterest> skills) {
        this.skills = skills;
    }
}
