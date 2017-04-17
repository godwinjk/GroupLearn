package com.grouplearn.project.cloud.interest.get;

import com.grouplearn.project.bean.GLInterest;
import com.grouplearn.project.cloud.CloudConnectResponse;

import java.util.ArrayList;

/**
 * Created by WiSilica on 04-12-2016 21:06 for GroupLearn application.
 */

public class CloudGetInterestResponse extends CloudConnectResponse {
    int interestCount;
    int skillsCount;
    ArrayList<GLInterest> interests = new ArrayList<>();
    ArrayList<GLInterest> skills = new ArrayList<>();

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

    public int getSkillsCount() {
        return skillsCount;
    }

    public void setSkillsCount(int skillsCount) {
        this.skillsCount = skillsCount;
    }

    public ArrayList<GLInterest> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<GLInterest> skills) {
        this.skills = skills;
    }
}
