package com.grouplearn.project.app;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;

/**
 * Created by Godwin on 24-05-2016 14:06 for Group Learn application 20:14 for GroupLearn.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
public class MyApplication extends MultiDexApplication {
    static MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        context = this;
        MultiDex.install(this);
    }

    public static MyApplication getAppContext() {
        return context;
    }

}
