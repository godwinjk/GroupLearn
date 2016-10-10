package com.grouplearn.project.utilities.views;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import com.grouplearn.project.R;

/**
 * Created by Godwin Joseph on 19-05-2016 19:21 for Group Learn application.
 */
public class AppAlertDialog extends AlertDialog.Builder {
    private static AppAlertDialog mAlertDialog;
    private static AlertDialog mDialog;
    private static Context mContext;

    private AppAlertDialog(Context context) {
        super(context);
    }

    private AppAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static AppAlertDialog getAlertDialog(Context context) {

        mAlertDialog = initializeAlertDialog(context);
        return mAlertDialog;
    }

    private static AppAlertDialog initializeAlertDialog(Context context) {
        dismissDialog();

        mContext = context;
        mAlertDialog = new AppAlertDialog(context);

        return mAlertDialog;
    }

    public static void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog.cancel();
        }
        mDialog = null;
    }

    @Override
    public AlertDialog create() {
        mDialog = super.create();
        return mDialog;
    }

    @Override
    public AlertDialog show() {
        return super.show();
    }

    public void showWarningAlert(String message) {
        mAlertDialog.setIcon(R.drawable.warning);
        mAlertDialog.setTitle("Warning");
        mAlertDialog.setMessage(message);
        mAlertDialog.create().show();
    }
}
