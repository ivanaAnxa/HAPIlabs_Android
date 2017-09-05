package com.anxa.hapilabs.controllers.hapimoment;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.AddHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonAddHapiMomentResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by angelaanxa on 8/9/2016.
 */
public class AddHapiMomentImplementer {
    Handler responseHandler;

    ProgressChangeListener progressChangeListener;

    AddHapiMomentListener addHapiMomentListener;
    JsonAddHapiMomentResponseHandler jsonAddHapiMomentResponseHandler;
    Context context;
    private HapiMoment hapiMoment;


    public AddHapiMomentImplementer(Context context, String username, HapiMoment hapiMoment,
                               ProgressChangeListener progressChangeListener,
                                    AddHapiMomentListener addHapiMomentListener) {

        this.context = context;
        this.addHapiMomentListener = addHapiMomentListener;
        this.progressChangeListener = progressChangeListener;
        this.hapiMoment = hapiMoment;

        String data = JsonRequestWriter.getInstance().createAddHapimomentJson(hapiMoment);

        System.out.println("data: " + data);

        jsonAddHapiMomentResponseHandler = new JsonAddHapiMomentResponseHandler(addHapiMomentHandler, "");
        addHapiMomentService(username, data, jsonAddHapiMomentResponseHandler);

    }

    //where data = xml string format post data
    private void addHapiMomentService(String username, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_HAPI_MOMENT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_HAPI_MOMENT) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler addHapiMomentHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonAddHapiMomentResponseHandler != null) {
//
                    }

                    addHapiMomentListener.addHapiMomentSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    MessageObj mesObj = (MessageObj)jsonAddHapiMomentResponseHandler.getResponseObj();
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    addHapiMomentListener.addHapiMomentFailedWithError(mesObj);
                    break;
            }//end Switch
        }
    };
}
