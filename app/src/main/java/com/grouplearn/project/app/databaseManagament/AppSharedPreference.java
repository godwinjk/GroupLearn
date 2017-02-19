package com.grouplearn.project.app.databaseManagament;

import android.content.Context;
import android.content.SharedPreferences;

import com.grouplearn.project.app.MyApplication;

/**
 * Created by Godwin Joseph on 07-05-2016 13:05 for Group Learn application.
 */
public class AppSharedPreference {
    private static final String SHARED_PREF_NAME = "GROUP_PREF";
    Context mContext;

    public AppSharedPreference(Context context) {
        this.mContext = context;
    }

    /**
     * Getting boolean key value from shared preference
     *
     * @param key
     * @return
     */
    public boolean getBooleanPrefValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

    /**
     * Getting boolean key value from shared preference
     *
     * @param key
     * @return
     */
    public boolean getBooleanPrefValue2(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getBoolean(key, true);
    }


    /**
     * Getting boolean key value from shared preference
     *
     * @param key
     * @return
     */
    public long getLongPrefValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getLong(key, -1);
    }

    /**
     * Setting a boolean key value to Shared preference
     *
     * @param key
     * @param value
     */
    public void setBooleanPrefValue(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();

    }

    /**
     * Getting float key value from shared preference
     *
     * @param key
     * @return
     */
    public float getFloatPrefValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getFloat(key, (float) 0.000000);
    }

    /**
     * Setting a boolean key value to Shared preference
     *
     * @param key
     * @param value
     */
    public void setFloatPrefValue(String key, float value) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putFloat(key, value);
        editor.commit();
    }


    /**
     * Setting a boolean key value to Shared preference
     *
     * @param key
     * @param value
     */
    public void setLongPrefValue(String key, long value) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * Getting boolean key value from shared preference
     *
     * @param key
     * @return
     */
    public String getStringPrefValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String value = pref.getString(key, null);
        return value;
    }

    /**
     * Setting a boolean key value to Shared preference
     *
     * @param key
     * @param value
     */
    public void setStringPrefValue(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Getting integer key value from shared preference
     *
     * @param key
     * @return
     */
    public int getIntegerPrefValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return pref.getInt(key, -1);
    }

    /**
     * Setting a integer key value to Shared preference
     *
     * @param key
     * @param value
     */
    public void setIntegerPrefValue(String key, int value) {
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(key, value);
        editor.commit();
    }

    public void clearPreference() {
        SharedPreferences pref = MyApplication.getAppContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.clear();
        editor.commit();
    }
}
