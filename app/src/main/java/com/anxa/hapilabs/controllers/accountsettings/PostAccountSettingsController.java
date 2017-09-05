package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.AccountSettingsListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 03/08/2016.
 */
public class PostAccountSettingsController {

    Context context;

    protected ProgressChangeListener progressChangeListener;
    PostAccountSettingsImplementer accountSettingsImplementer;
    AccountSettingsListener accountSettingsListener;

    public PostAccountSettingsController(Context context,
                                         ProgressChangeListener progressChangeListener,
                                         AccountSettingsListener accountSettingsListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.accountSettingsListener = accountSettingsListener;
    }


    public void postAccountSettings(int settingsValue, String username) {

        accountSettingsImplementer = new PostAccountSettingsImplementer(context, username, settingsValue, progressChangeListener, accountSettingsListener);

    }
}
