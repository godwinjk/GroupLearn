package com.grouplearn.project.app.uiManagement;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.grouplearn.project.app.uiManagement.interactor.SignOutInteractor;
import com.grouplearn.project.app.uiManagement.interfaces.SignOutListener;

/**
 * Created by WiSilica on 21-01-2017 16:43.
 *
 * @Author : Godwin Joseph Kurinjikattu
 */

public class SignoutService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public ComponentName startService(Intent service) {

        SignOutInteractor interactor = new SignOutInteractor(this);
        interactor.doForceSignOut(new SignOutListener() {
            @Override
            public void onSignOutSuccessful() {
                Intent i = new Intent(SignoutService.this, SplashScreenActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SignoutService.this.startActivity(i);
                stopSelf();
            }

            @Override
            public void onSignOutFailed() {

            }

            @Override
            public void onSignOutCanceled() {

            }
        });
        return null;
    }

}
