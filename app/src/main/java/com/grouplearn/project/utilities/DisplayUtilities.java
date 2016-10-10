package com.grouplearn.project.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;

import com.grouplearn.project.R;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.AppProgressDialog;

/**
 * Created by Godwin Joseph on 24-05-2016 10:14 for Group Learn application.
 */
public class DisplayUtilities {
    private static final String TAG = "DisplayUtilities";

    public static AppProgressDialog showProgressDialog(Context mContext, String message) {
        try {
            dismissDialog();
            AppProgressDialog dialog = AppProgressDialog.getProgressDialog(mContext);
            dialog.setMessage(message);
            return dialog;
        } catch (ActivityNotFoundException ex) {
//            AppProgressDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (IllegalArgumentException ex) {
//            AppProgressDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (NullPointerException ex) {
//            AppProgressDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void dismissDialog() {
//        AppProgressDialog.dismissDialog();
    }

    public static AppAlertDialog showWarning(Context context, String title, String message) {
        try {
            dismissDialog();
            AppAlertDialog dialog = AppAlertDialog.getAlertDialog(context);
            dialog.setTitle(title);
            dialog.setMessage(message);
            dialog.setIcon(R.drawable.warning);
            return dialog;
        } catch (ActivityNotFoundException ex) {
            AppAlertDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (IllegalArgumentException ex) {
            AppAlertDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (NullPointerException ex) {
            AppAlertDialog.dismissDialog();
            Log.e(TAG, ex.getLocalizedMessage());
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
