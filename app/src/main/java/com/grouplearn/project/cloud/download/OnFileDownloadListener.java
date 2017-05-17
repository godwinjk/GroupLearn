package com.grouplearn.project.cloud.download;

import com.grouplearn.project.bean.GLMessage;
import com.grouplearn.project.utilities.errorManagement.AppError;

/**
 * Created by Godwin on 11-05-2017 16:37 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */

public interface OnFileDownloadListener {
    void onDownloadSuccess(GLMessage message);

    void onDownloadInProgress(GLMessage message, int progress);

    void onDownloadFailed(GLMessage message, AppError error);
}
