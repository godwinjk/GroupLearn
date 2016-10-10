package com.grouplearn.project.cloud.userManagement;

import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.cloud.userManagement.signIn.CloudSignInRequest;
import com.grouplearn.project.cloud.userManagement.signOut.CloudSignOutRequest;
import com.grouplearn.project.cloud.userManagement.signUp.CloudSignUpRequest;
import com.grouplearn.project.cloud.userManagement.status.CloudStatusRequest;

/**
 * Created by Godwin Joseph on 13-05-2016 13:06 for Group Learn application.
 */
public interface CloudUserManagerInterface {
    public void signIn(final CloudSignInRequest cloudRequest, final CloudResponseCallback cloudResponseCallback);

    public void singUp(final CloudSignUpRequest cloudRequest, final CloudResponseCallback cloudResponseCallback);

    public void signOut(final CloudSignOutRequest cloudRequest, final CloudResponseCallback cloudResponseCallback);

    public void getStatus(final CloudStatusRequest cloudRequest, final CloudResponseCallback cloudResponseCallback);

    public void setStatus(final CloudStatusRequest cloudRequest, final CloudResponseCallback cloudResponseCallback);
}
