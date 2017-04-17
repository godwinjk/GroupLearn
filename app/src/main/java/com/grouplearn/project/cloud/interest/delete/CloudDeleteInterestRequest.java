package com.grouplearn.project.cloud.interest.delete;

import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.cloud.CloudConnectRequest;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudDeleteInterestRequest extends CloudConnectRequest {
    ArrayList<GLInterest> interests = new ArrayList<>();
    ArrayList<GLInterest> skills = new ArrayList<>();

    public ArrayList<GLInterest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<GLInterest> interests) {
        this.interests = interests;
    }

    public void setInterests(GLInterest interest) {
        this.interests.add(interest);
    }

    public ArrayList<GLInterest> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<GLInterest> skills) {
        this.skills = skills;
    }

    public void setSkills(GLInterest skills) {
        this.skills.add(skills);
    }

    @Override
    public int validate() {
        return 0;
    }
}
