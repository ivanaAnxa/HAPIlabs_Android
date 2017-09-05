package com.anxa.hapilabs.controllers.exercise;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.AccountSettingsListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.RegisterDeviceListener;
import com.anxa.hapilabs.controllers.accountsettings.PostAccountSettingsImplementer;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public class RegisterDeviceController {

    Context context;

    protected ProgressChangeListener progressChangeListener;
    RegisterDeviceImplementer registerDeviceImplementer;
    RegisterDeviceListener registerDeviceListener;

    public RegisterDeviceController(Context context,
                                         ProgressChangeListener progressChangeListener,
                                         RegisterDeviceListener registerDeviceListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.registerDeviceListener = registerDeviceListener;
    }

    public void registerDevice(String userID, String UUID, String appName, String appVersion) {

        registerDeviceImplementer = new RegisterDeviceImplementer(context, userID, UUID, appName, appVersion, registerDeviceListener);

    }
}

