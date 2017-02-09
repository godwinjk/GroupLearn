package com.grouplearn.project.app.uiManagement;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;

/**
 * Created by WiSilica on 21-01-2017 16:43.
 *
 * @Author : Godwin Joseph Kurinjikattu
 */

public class SignoutService extends IntentService {
    public SignoutService() {
        super("SignoutService");
        registerReceiver(receiver, new IntentFilter("INVALID TOKEN"));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SignOutInteractor interactor = new SignOutInteractor(this);
        interactor.doForceSignOut(new SignOutListener() {
            @Override
            public void onSignOutSuccessful() {
                Intent i = new Intent(SignoutService.this, SplashScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SignoutService.this.startActivity(i);
            }

            @Override
            public void onSignOutFailed() {

            }

            @Override
            public void onSignOutCanceled() {

            }
        });
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("INVALID TOKEN")) {
                onHandleIntent(intent);
            }
        }
    };
}
