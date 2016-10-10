package com.grouplearn.project.utilities;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.grouplearn.project.R;
import com.grouplearn.project.utilities.views.AppAlertDialog;

/**
 * Created by Godwin Joseph on 05-10-2016 17:00 for GroupLearn application.
 */

public class GeneralAlert {


        private static final String TAG = "GeneralAlert";
        public static AlertDialog.Builder alert;

        public static AlertDialog alertDialog;


        /**
         * Showing lollipop needed
         *
         * @param _ctx
         */
        public static void showAlert(Context _ctx, String title, String msg) {

            try {

                if (alertDialog != null)
                    alertDialog.dismiss();

                alert = AppAlertDialog.getAlertDialog(_ctx)
                        .setTitle(title)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert);

                alertDialog = alert.create();

                alertDialog.show();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        /**
         * Showing alerts
         *
         * @param _ctx
         */
        public static void showAlert(Context _ctx, String msg) {
            try {
                if (alertDialog != null)
                    alertDialog.dismiss();
                alert = AppAlertDialog.getAlertDialog(_ctx)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                }).setIcon(android.R.drawable.ic_dialog_alert);

                alertDialog = alert.create();

                alertDialog.show();
            } catch (Exception e) {
                Log.e(TAG, "Exception occurred " + e.getLocalizedMessage());
            }
        }

}
