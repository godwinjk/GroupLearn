package com.grouplearn.project.app;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

import it.moondroid.chatbot.ChatBotApplication;

/**
 * Created by Godwin Joseph on 24-05-2016 14:06 for Group Learn application.
 */
public class MyApplication extends ChatBotApplication {
    static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }
}
