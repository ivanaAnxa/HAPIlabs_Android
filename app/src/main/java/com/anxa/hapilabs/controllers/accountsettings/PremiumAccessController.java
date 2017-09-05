package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PremiumAccessListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 22/08/2016.
 */
public class PremiumAccessController {
    Context context;

    protected ProgressChangeListener progressChangeListener;
    PremiumAccessImplementer premiumAccessImplementer;
    PremiumAccessListener premiumAccessListener;

    public PremiumAccessController(Context context,
                                      ProgressChangeListener progressChangeListener,
                                   PremiumAccessListener premiumAccessListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.premiumAccessListener = premiumAccessListener;
    }


    public void sendAccessCode(String coachProgramId, String phoneNumber) {
        premiumAccessImplementer = new PremiumAccessImplementer(context, coachProgramId, phoneNumber, "send_accesscode", progressChangeListener, premiumAccessListener);
    }

    public void validateAccessCode(String accessCode) {
        premiumAccessImplementer = new PremiumAccessImplementer(context, accessCode, "", "validate_accesscode", progressChangeListener, premiumAccessListener);
    }
}
