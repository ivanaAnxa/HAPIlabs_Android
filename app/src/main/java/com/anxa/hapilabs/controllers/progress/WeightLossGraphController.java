package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.WeightLossGraphListener;

/**
 * Created by aprilanxa on 11/10/2016.
 */

public class WeightLossGraphController {
    Context context;

    WeightLossGraphImplementer weightLossGraphImplementer;
    WeightLossGraphListener listener;
    ProgressChangeListener progressChangeListener;

    public WeightLossGraphController(Context context, WeightLossGraphListener listener, ProgressChangeListener progressChangeListener) {
        this.context = context;
        this.listener = listener;
        this.progressChangeListener = progressChangeListener;
    }

    public void getGraphData(String command, String userId) {
        weightLossGraphImplementer = new WeightLossGraphImplementer(context, userId, progressChangeListener, listener);
    }
}
