package com.anxa.hapilabs.controllers.hapimoment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetHapiMomentListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetHapiMomentResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 14/09/2016.
 */
public class GetHapiMomentImplementer {

    private JsonGetHapiMomentResponseHandler jsonResponseHandler;

    private Context context;
    private GetHapiMomentListener listener;

    public GetHapiMomentImplementer(Context context, String userId, String refId, GetHapiMomentListener listener)
    {
        this.context = context;

        jsonResponseHandler = new JsonGetHapiMomentResponseHandler(mainHandler);

        getHapiMoment(userId, refId, jsonResponseHandler);

        this.listener = listener;
    }

    //where data = xml string format post data
    private void getHapiMoment(String userID, String refId, Handler responseHandler)
    {
        String url = WebServices.getURL(WebServices.SERVICES.GET_HAPI_MOMENT);
        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userID);
        connection.addParam("id", refId);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_HAPI_MOMENT) + userID + refId));
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
                    HapiMoment hapiMomentObj = null;

                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null)
                    {
                        hapiMomentObj = (HapiMoment) jsonResponseHandler.getResponseObj();

                        if (hapiMomentObj!=null) {
                            ApplicationEx.getInstance().currentHapiMoment = hapiMomentObj;
                        }

                    }

                    listener.getHapiMomentSuccess(hapiMomentObj);

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.getHapiMomentFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
