package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;

/**
 * Created by angelaanxa on 9/21/2017.
 */

public class GetStepsDataController {
    Context context;

    GetStepsDataImplementer getStepstDataImplementer;
    StepsDataListener listener;

    public GetStepsDataController(Context context, StepsDataListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getSteps(String userId, String activity_id) {
        getStepstDataImplementer = new GetStepsDataImplementer(context, userId, activity_id, listener);
    }
}
