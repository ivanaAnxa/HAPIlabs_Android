package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsDataListener;

/**
 * Created by aprilanxa on 28/10/2016.
 */

public class StepsDataController {

    Context context;

    StepsDataImplementer stepsDataImplementer;
    StepsDataListener listener;
    ProgressChangeListener progressChangeListener;

    public StepsDataController(Context context, StepsDataListener listener, ProgressChangeListener progressChangeListener) {
        this.context = context;
        this.listener = listener;
        this.progressChangeListener = progressChangeListener;
    }

    public void postGraphData(String userId, String steps, String date) {
        stepsDataImplementer = new StepsDataImplementer(context, userId, "post", "", steps, date, progressChangeListener, listener);
    }

    public void updateGraphData(String userId, String activity_id, String steps, String date) {
        stepsDataImplementer = new StepsDataImplementer(context, userId, "update", activity_id, steps, date, progressChangeListener, listener);
    }

    public void deleteGraphData(String userId, String activity_id, String steps, String date) {
        stepsDataImplementer = new StepsDataImplementer(context, userId, "delete", activity_id, steps, date, progressChangeListener, listener);
    }
}
