package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsGraphListener;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public class StepsGraphController {
    Context context;

    StepsGraphImplementer stepsGraphImplementer;
    StepsGraphListener listener;
    ProgressChangeListener progressChangeListener;

    public StepsGraphController(Context context, StepsGraphListener listener, ProgressChangeListener progressChangeListener) {
        this.context = context;
        this.listener = listener;
        this.progressChangeListener = progressChangeListener;
    }

    public void getStepsGraphData(String command, String userId) {
        stepsGraphImplementer = new StepsGraphImplementer(context, userId, progressChangeListener, listener);
    }
}
