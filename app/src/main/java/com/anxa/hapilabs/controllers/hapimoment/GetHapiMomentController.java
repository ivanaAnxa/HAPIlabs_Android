package com.anxa.hapilabs.controllers.hapimoment;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetHapiMomentListener;

/**
 * Created by aprilanxa on 14/09/2016.
 */
public class GetHapiMomentController {
    Context context;

    GetHapiMomentImplementer getHapiMomentImplementer;
    GetHapiMomentListener getHapiMomentListener;

    public GetHapiMomentController(Context context,
                                   GetHapiMomentListener getHapiMomentListener) {
        this.context = context;
        this.getHapiMomentListener = getHapiMomentListener;
    }


    public void getHapiMoment(String refId, String username) {

        getHapiMomentImplementer = new GetHapiMomentImplementer(context, username, refId, getHapiMomentListener);

    }
}
