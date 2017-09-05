package com.anxa.hapilabs.controllers.hapimoment;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.AddHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.models.HapiMoment;

/**
 * Created by angelaanxa on 8/9/2016.
 */
public class AddHapiMomentController {

    Context context;

    protected ProgressChangeListener progressChangeListener;
    AddHapiMomentImplementer addHapiMomentImplementer;
    AddHapiMomentListener addHapiMomentListener;

    public AddHapiMomentController(Context context,
                              ProgressChangeListener progressChangeListener,
                                   AddHapiMomentListener addHapiMomentListener) {
        this.context = context;
        this.progressChangeListener = progressChangeListener;
        this.addHapiMomentListener = addHapiMomentListener;
    }


    public void uploadHapiMoment(HapiMoment hapiMoment, String username) {

        addHapiMomentImplementer = new AddHapiMomentImplementer(context, username, hapiMoment, progressChangeListener, addHapiMomentListener);

    }
}
