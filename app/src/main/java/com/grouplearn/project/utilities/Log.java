package com.grouplearn.project.utilities;

/**
 * Created by Godwin Joseph on 13-05-2016 15:17 for Group Learn application.
 */
public class Log {
    public static boolean IS_DEBUG_ENABLED = true;

    public static void i(String TAG, String msg) {
        if (IS_DEBUG_ENABLED)
            android.util.Log.i(TAG, msg);
    }

    public static void v(String TAG, String msg) {
        if (IS_DEBUG_ENABLED)
            android.util.Log.v(TAG, msg);
    }

    public static void w(String TAG, String msg) {
        if (IS_DEBUG_ENABLED)
            android.util.Log.w(TAG, msg);
    }

    public static void d(String TAG, String msg) {
        if (IS_DEBUG_ENABLED)
            android.util.Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        if (IS_DEBUG_ENABLED)
            android.util.Log.e(TAG, msg);
    }
}
