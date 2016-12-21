package com.grouplearn.project.app;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.grouplearn.project.app.uiManagement.contact.ContactReadTask;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;

import it.moondroid.chatbot.ChatBotApplication;

/**
 * Created by Godwin Joseph on 24-05-2016 14:06 for Group Learn application.
 */
public class MyApplication extends ChatBotApplication {
    static MyApplication context;
    private RequestQueue mRequestQueue;
    private final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        context = this;
        readContacts();

    }

    public static MyApplication getAppContext() {
        return context;
    }

    private void readContacts() {
        ContactDbHelper mDbHelper = new ContactDbHelper(this);
        if (mDbHelper.getContacts().size() <= 0) {
            new ContactReadTask(this).execute();
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
