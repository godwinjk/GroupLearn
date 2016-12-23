package com.grouplearn.project.app;

import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.grouplearn.project.app.databaseManagament.AppSharedPreference;
import com.grouplearn.project.app.databaseManagament.constants.PreferenceConstants;
import com.grouplearn.project.app.uiManagement.contact.ContactReadTask;
import com.grouplearn.project.app.uiManagement.databaseHelper.ContactDbHelper;
import com.grouplearn.project.app.uiManagement.interactor.ContactListInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.ContactViewInterface;
import com.grouplearn.project.bean.GLContact;
import com.grouplearn.project.utilities.errorManagement.AppError;

import java.util.ArrayList;

import it.moondroid.chatbot.ChatBotApplication;

/**
 * Created by Godwin Joseph on 24-05-2016 14:06 for Group Learn application.
 */
public class MyApplication extends ChatBotApplication {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 101;
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
        AppSharedPreference mPref = new AppSharedPreference(context);
        boolean isLogIn = mPref.getBooleanPrefValue(PreferenceConstants.IS_LOGIN);
        if (isLogIn) {
            ContactDbHelper mDbHelper = new ContactDbHelper(this);
            if (mDbHelper.getContacts().size() <= 0) {
                ContactReadTask readTask = new ContactReadTask(this);
                readTask.setContactViewInterface(new ContactViewInterface() {
                    @Override
                    public void onGetAllContacts(ArrayList<GLContact> contactModels) {

                    }

                    @Override
                    public void onGetAllContactsFromDb(ArrayList<GLContact> contactModels) {

                    }

                    @Override
                    public void onGetContactsFinished(ArrayList<GLContact> contactModels) {
                        if (contactModels != null && contactModels.size() > 0) {
                            ContactListInteractor interactor = new ContactListInteractor(context);
                            interactor.addAllContacts(contactModels, this);
                        }
                    }

                    @Override
                    public void onGetContactsFailed(AppError error) {

                    }
                });
                readTask.execute();
            }
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
