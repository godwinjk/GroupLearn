package com.grouplearn.project.app.uiManagement.interactor;

import android.content.Context;
import android.content.DialogInterface;

import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.uiManagement.databaseHelper.DataBaseHelper;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;
import com.grouplearn.project.utilities.views.AppAlertDialog;
import com.grouplearn.project.utilities.views.DisplayInfo;

/**
 * Created by Godwin Joseph on 08-09-2016 14:39 for Group Learn application.
 */
public class SignOutInteractor {
    Context mContext;

    public SignOutInteractor(Context mContext) {
        this.mContext = mContext;
    }

    public void doSignOut(final SignOutListener listener) {
        AppAlertDialog dialog = AppAlertDialog.getAlertDialog(mContext);
        dialog.setTitle("Sign Out");
        dialog.setMessage("Sign out will clear all the data from device and sign out from cloud. Do you want to continue?");

        dialog.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearData(listener);
                DisplayInfo.showToast(mContext, "Sign out successfully.");

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.onSignOutCanceled();
            }
        }).create().show();
    }

    public void doForceSignOut(final SignOutListener listener) {
        clearData(listener);
    }

    private void clearData(SignOutListener listener) {
        try {
            MessageInteractor.getInstance().stopTimer();
            new DataBaseHelper(mContext).clearDatabase();
            new AppSharedPreference(mContext).clearPreference();
            listener.onSignOutSuccessful();
        } catch (Exception e) {
            listener.onSignOutFailed();
        }
    }
}
