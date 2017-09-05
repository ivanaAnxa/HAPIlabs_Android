package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;

/**
 * Created by aprilanxa on 13/10/2016.
 */

public class WeightDataController {

    Context context;

    WeightDataImplementer weightDataImplementer;
    WeightDataListener listener;
    ProgressChangeListener progressChangeListener;

    public WeightDataController(Context context, WeightDataListener listener, ProgressChangeListener progressChangeListener) {
        this.context = context;
        this.listener = listener;
        this.progressChangeListener = progressChangeListener;
    }

//    Context context, String username, String command, String activity_id, String weight, String date, ProgressChangeListener progressChangeListener,
//    WeightLossGraphListener listener
    public void postGraphData(String userId, String weight, String date) {
        weightDataImplementer = new WeightDataImplementer(context, userId, "post", "", weight, date, progressChangeListener, listener);
    }

    public void updateGraphData(String userId, String activity_id, String weight, String date) {
        weightDataImplementer = new WeightDataImplementer(context, userId, "update", activity_id, weight, date, progressChangeListener, listener);
    }

    public void deleteGraphData(String userId, String activity_id, String weight, String date) {
        weightDataImplementer = new WeightDataImplementer(context, userId, "delete", activity_id, weight, date, progressChangeListener, listener);
    }
}
