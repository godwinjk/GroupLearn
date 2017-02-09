package com.grouplearn.project.utilities.views;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.grouplearn.project.utilities.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Utility class to display loaders for UI delay procedures.
 *
 * @author Godwin J kurinjikattu.
 * @since 09 Sep 2015.
 */
public class DisplayInfo {
    final static String TAG = "DisplayInfo";
    public static ProgressDialog pd_progress;
    public static Toast mToast;
    static TimerTask timerTask;
    static Timer timer;
    private static boolean isShowing = false;

    public static void showLoader(final Context ctx, final String msg) {
        isShowing = true;
        try {
            final Activity activity = (Activity) ctx;

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoader(ctx);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (pd_progress == null)
                                    pd_progress = getProgressDialog(activity, msg, true);

                                pd_progress.show();
                                isShowing = true;

                                setTimerToCancelProgress(ctx);

                            }


                        }, 10);
                    }
                });

            }
        } catch (WindowManager.BadTokenException e) {

        } catch (Exception e) {
        }
    }

    public static boolean isLoaderShowing() {
        boolean isShowing = false;
        if (pd_progress == null)
            isShowing = false;
        else
            isShowing = pd_progress.isShowing();
        return isShowing;
    }

    public static void showLoader(final Context ctx, final String msg, final boolean isCancelable) {
        isShowing = true;
        try {
            final Activity activity = (Activity) ctx;

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoader(ctx);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                pd_progress = getProgressDialog(activity, msg, isCancelable);

                                pd_progress.show();
                                isShowing = true;

                                setTimerToCancelProgress(ctx);

                            }


                        }, 10);
                    }
                });

            }
        } catch (WindowManager.BadTokenException e) {

        } catch (Exception e) {

        }

    }


    private static void setTimerToCancelProgress(final Context ctx) {

        if (timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;

            timerTask = null;
        }
        Log.i(TAG, "SHOW LOADER||SHOW LOADER||SHOW LOADER||" + isShowing + ":" + pd_progress.isShowing());

        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                dismissLoader(ctx);
                Log.w(TAG, "Progress dialog automatically stoped after 1 min.");
            }
        };

        timer.schedule(timerTask, 30 * 1000);


        pd_progress.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isShowing = false;
            }
        });
    }

    public static ProgressDialog getProgressDialog(Activity ctx, String msg, boolean isCancelable) {
        pd_progress = new ProgressDialog(ctx);
        pd_progress.setMessage(msg);
        pd_progress.setCancelable(isCancelable);

        return pd_progress;

    }


    public static void dismissLoader(Context ctx) {

        if (pd_progress != null)
            Log.d(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||111111111");

        try {
            Activity activity = (Activity) ctx;

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||2222222");

                        try {
                            Log.d(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||333333");

                            dismissLoader1();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

        } catch (WindowManager.BadTokenException e) {

        } catch (Exception e) {
            Log.i(TAG, ">>>>>>>>>>>>inside the catch block" + e);
        }

    }

    public static void dismissLoader1() throws Exception {

        try {

            Log.d(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||444444");

            if (pd_progress != null)
                Log.d(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||555555" + isShowing + ":" + pd_progress.isShowing());
            if (pd_progress != null) {

                pd_progress.cancel();
                pd_progress.dismiss();
                pd_progress = null;
                isShowing = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "DISMISS LOADER||DISMISS LOADER||DISMISS LOADER||66666" + e);
            throw e;
        }
    }

    public static void showToast(final Context ctx, final String msg) {
        if (ctx != null)
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissToast(msg);
                    mToast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
    }

    public static void showToast(final Context ctx, final int stringResId) {
        if (ctx != null)
            ((Activity) ctx).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissToast(ctx.getString(stringResId));
                    mToast = Toast.makeText(ctx, stringResId, Toast.LENGTH_LONG);
                    mToast.show();
                }
            });
    }

    public static void dismissToast(String msg) {
        if (mToast != null)
            mToast.cancel();
    }

    public static ProgressDialog getProgress() {
        return pd_progress;
    }
}
