package com.grouplearn.project.cloud.interest;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.interest.add.CloudAddInterestRequest;
import com.grouplearn.project.cloud.interest.delete.CloudDeleteInterestRequest;
import com.grouplearn.project.cloud.interest.get.CloudGetInterestRequest;

/**
 * Created by WiSilica on 04-12-2016 21:12 for GroupLearn application.
 */

public interface CloudInterestManagerInterface {
    public void addInterest(CloudAddInterestRequest request, CloudResponseCallback callback);

    public void editInterest(CloudAddInterestRequest request, CloudResponseCallback callback);

    public void deleteInterest(CloudDeleteInterestRequest request, CloudResponseCallback callback);

    public void getInterest(CloudGetInterestRequest request, CloudResponseCallback callback);
}
