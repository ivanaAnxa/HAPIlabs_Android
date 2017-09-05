package com.anxa.hapilabs.controllers.water;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.AddWaterListener;
import com.anxa.hapilabs.models.Water;

/**
 * Created by aprilanxa on 25/07/2016.
 */
public class AddWaterController {

    Context context;

    protected ProgressChangeListener progressChangeListener;
    AddWaterImplementer addWaterImplementer;
    AddWaterListener addWaterListener;

    public AddWaterController(Context context,
                              ProgressChangeListener progressChangeListener,
                              AddWaterListener addWaterListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.addWaterListener = addWaterListener;
    }


    public void uploadWater(Water water, String username) {

        addWaterImplementer = new AddWaterImplementer(context, username, water, progressChangeListener, addWaterListener);

    }
}
