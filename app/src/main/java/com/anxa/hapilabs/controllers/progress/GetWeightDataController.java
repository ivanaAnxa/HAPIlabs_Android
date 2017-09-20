package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;

/**
 * Created by angelaanxa on 9/20/2017.
 */



public class GetWeightDataController {

    Context context;

    GetWeightDataImplementer getWeightDataImplementer;
    WeightDataListener listener;

    public GetWeightDataController(Context context, WeightDataListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getWeight(String userId, String activity_id) {
        getWeightDataImplementer = new GetWeightDataImplementer(context, userId, activity_id, listener);
    }
}