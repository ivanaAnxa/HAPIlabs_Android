package com.anxa.hapilabs.controllers.water;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetWaterListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetWaterResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Water;

/**
 * Created by aprilanxa on 20/07/2017.
 */

public class GetWaterImplementer {

    private JsonGetWaterResponseHandler jsonResponseHandler;

    private Context context;
    private GetWaterListener listener;

    public GetWaterImplementer(Context context, String userId, String refId, GetWaterListener listener) {
        this.context = context;
        jsonResponseHandler = new JsonGetWaterResponseHandler(mainHandler);
        getWater(userId, refId, jsonResponseHandler);
        this.listener = listener;
    }

    //where data = xml string format post data
    private void getWater(String userID, String refId, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.GET_HAPI_WATER);
        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userID);
        connection.addParam("id", refId);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_HAPI_WATER) + userID + refId));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }

    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    Water waterObj = null;

                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null) {
                        waterObj = (Water) jsonResponseHandler.getResponseObj();

                        if (waterObj != null) {
                            ApplicationEx.getInstance().currentWater = waterObj;
                        }

                    }

                    listener.getWaterSuccess(waterObj);

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.getWaterFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
