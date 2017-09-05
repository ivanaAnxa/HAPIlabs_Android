package com.anxa.hapilabs.controllers.registration;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetCoachProgramListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 18/08/2016.
 */
public class CoachProgramController {

    Context context;
    CoachProgramImplementer coachProgramImplementer;

    protected ProgressChangeListener progressChangeListener;

    GetCoachProgramListener listener;

    public CoachProgramController(Context context, ProgressChangeListener progressChangeListener, GetCoachProgramListener listener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.listener =listener;
    }

    public void getPaymentOptions(String coachId){
        coachProgramImplementer = new CoachProgramImplementer(context, coachId, progressChangeListener, listener);

    }
}
