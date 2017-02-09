package com.grouplearn.project.cloud.appManagement.gcmRegistration;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.grouplearn.project.cloud.CloudConnectResponse;
import com.grouplearn.project.cloud.CloudConstants;
import com.grouplearn.project.cloud.CloudError;
import com.grouplearn.project.cloud.CloudResponseCallback;
import com.grouplearn.project.utilities.Log;
import com.grouplearn.project.utilities.errorManagement.ErrorHandler;

/**
 * Created by Godwin Joseph on 13-05-2016 15:57 for Group Learn application.
 */
public class GcmRegistration extends AsyncTask<Void, Void, String> {
    private final CloudResponseCallback mCallBack;
    Context mContext;
    String TAG = "GetGcmRegistrationIdTask";
    /**
     * GCM object
     **/


    public GcmRegistration(Context ctx, CloudResponseCallback cloudResponseCallback) {
        this.mCallBack = cloudResponseCallback;
        this.mContext = ctx;
    }


    @Override
    protected String doInBackground(Void... params) {
        String regId = "";
//        try {
//            if (mGcm == null) {
//                mGcm = GoogleCloudMessaging.getInstance(mContext);
//            }
//            regId = mGcm.register(AppConstants.GOOGLE_PROJECT_ID);
//        } catch (IOException ex) {
//            Log.e(TAG, "Error: " + ex.getLocalizedMessage());
//        } catch (Exception ex) {
//            Log.e(TAG, "Error: " + ex.getLocalizedMessage());
//        }
//        Log.d(TAG, "AsyncTask completed: " + regId);
        return regId;
    }

    @Override
    protected void onPostExecute(String regId) {
        Log.d(TAG, "GCM RegIdbrfore Use: " + regId);
        if (TextUtils.isEmpty(regId))
            mCallBack.onFailure(null, new CloudError(ErrorHandler.GCM_REG_FAILED, ErrorHandler.ErrorMessage.GCM_REG_FAILED));
        else {
            CloudConnectResponse response = new CloudGcmRegistrationResponse();
            response.setResponseStatus(mContext,CloudConstants.SUCCESS_CODE);
            response.setResponseMessage(CloudConstants.SUCCESS_MESSAGE);
            response.setTimeStamp(System.currentTimeMillis());
            ((CloudGcmRegistrationResponse) response).setGcmToken(regId);
            mCallBack.onSuccess(null, response);
        }
    }
}
