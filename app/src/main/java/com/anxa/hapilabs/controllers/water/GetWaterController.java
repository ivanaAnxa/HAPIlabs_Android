package com.anxa.hapilabs.controllers.water;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetWaterListener;

/**
 * Created by aprilanxa on 20/07/2017.
 */

public class GetWaterController {

    Context context;

    GetWaterImplementer getWaterImplementer;
    GetWaterListener getWaterListener;

    public GetWaterController(Context context,
                              GetWaterListener getWaterListener) {
        this.context = context;
        this.getWaterListener = getWaterListener;
    }


    public void getWater(String refId, String username) {
        getWaterImplementer = new GetWaterImplementer(context, username, refId, getWaterListener);
    }
}
