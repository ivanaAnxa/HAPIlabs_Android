package com.anxa.hapilabs.controllers.timelineactivity;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetTimelineActivityListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;

/**
 * Created by aprilanxa on 08/03/2017.
 */

public class GetTimelineActivityController {

    Context context;
    GetTimelineActivityImplementer getTimelineActivityImplementer;
    GetTimelineActivityListener listener;

    protected ProgressChangeListener progresslistener;

    public GetTimelineActivityController(Context context,ProgressChangeListener progresslistener, GetTimelineActivityListener listener) {
        this.context = context;
        this.progresslistener = progresslistener;
        this.listener =listener;
    }


    public void getTimelineActivity(String userID, String entryID, String entryType){
        getTimelineActivityImplementer = new GetTimelineActivityImplementer(context, userID, entryID, entryType, progresslistener, listener);
    }
}
