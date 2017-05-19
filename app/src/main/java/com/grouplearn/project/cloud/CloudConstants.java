package com.grouplearn.project.cloud;

import android.text.TextUtils;

import com.grouplearn.project.app.MyApplication;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;

/**
 * Created by Godwin Joseph on 13-05-2016 14:48 for Group Learn application.
 */
public class CloudConstants {
    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INTEGER_VALUE = -1;
    public static final int SUCCESS_CODE = 1000;
    public static final String SUCCESS_MESSAGE = "SUCCESS";

    //    private static final String BASE_URL = "http://192.168.43.239/grouplearn/public/";
    private static final String BASE_URL = "http://bodyretreatbeautyzone.com/grouplearn/public/";

    //    private static final String PROFILE_BASE_URL = "http://192.168.43.239/grouplearn";
    private static final String PROFILE_BASE_URL = "http://bodyretreatbeautyzone.com/grouplearn";


    //    private static final String FILE_BASE_URL = "http://192.168.43.239/grouplearn/public/group-file";
    private static final String FILE_BASE_URL = "http://bodyretreatbeautyzone.com/grouplearn/public/group-file";


    public static String getBaseUrl() {
        String ip = new AppSharedPreference(MyApplication.getAppContext()).getStringPrefValue(PreferenceConstants.CUSTOM_URL);
        if (TextUtils.isEmpty(ip))
            return BASE_URL;
        else {
            return ("http://" + ip + "/grouplearn/public/");
        }
    }

    public static String getProfileBaseUrl() {
        String ip = new AppSharedPreference(MyApplication.getAppContext()).getStringPrefValue(PreferenceConstants.CUSTOM_URL);
        if (TextUtils.isEmpty(ip))
            return PROFILE_BASE_URL;
        else {
            return ("http://" + ip + "/grouplearn");
        }
    }

    public static String getFileUploadBaseUrl() {
        String ip = new AppSharedPreference(MyApplication.getAppContext()).getStringPrefValue(PreferenceConstants.CUSTOM_URL);
        if (TextUtils.isEmpty(ip))
            return FILE_BASE_URL;
        else {
            return ("http://" + ip + "/grouplearn/public/group-file");
        }
    }
}
