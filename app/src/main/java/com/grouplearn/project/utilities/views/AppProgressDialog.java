package com.grouplearn.project.utilities.views;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Godwin Joseph on 24-05-2016 09:54 for Group Learn application.
 */
public class AppProgressDialog extends ProgressDialog {
    private static AppProgressDialog mProgressDialog;
    private static ProgressDialog mDialog;
    private static Context mContext;

    private AppProgressDialog(Context context) {
        super(context);
    }

    private AppProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public static AppProgressDialog getProgressDialog(Context context) {

        mProgressDialog = initializeAlertDialog(context);
        return mProgressDialog;
    }

    private static AppProgressDialog initializeAlertDialog(Context context) {
        dismissDialog();

        mContext = context;
        mProgressDialog = new AppProgressDialog(context);

        return mProgressDialog;
    }

    public static void dismissDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog.cancel();
        }
        mProgressDialog = null;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        dismissDialog();
    }

    @Override
    public void cancel() {
        super.cancel();
        dismissDialog();
    }
}
