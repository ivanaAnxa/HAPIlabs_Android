package com.anxa.hapilabs.common.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class C2DMRegistrationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.w("C2DM", "Registration Receiver called");

        if ("com.google.android.c2dm.intent.REGISTRATION".equals(action)) {
            Log.w("C2DM", "Received registration ID");
            final String registrationId = intent
                    .getStringExtra("registration_id");
            String error = intent.getStringExtra("error");

            Log.d("C2DM", "dmControl: registrationId = " + registrationId
                    + ", error = " + error);
            
            // TODO Send this to my application server
        }
    }
}

