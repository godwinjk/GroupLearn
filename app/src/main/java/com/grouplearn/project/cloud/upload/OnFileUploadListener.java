package com.grouplearn.project.cloud.upload;

import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.errorManagement.AppError;

/**
 * Created by Godwin on 07-05-2017 17:09 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public interface OnFileUploadListener {
    void onUploadSuccess(GLMessage message);

    void onUploadInProgress(GLMessage message, int progress);

    void onUploadFailed(GLMessage message, AppError error);
}
