package com.anxa.hapilabs.controllers.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsGraphListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonStepsGraphResponseHandler;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public class StepsGraphImplementer {

    Handler responseHandler;

    ProgressChangeListener progressChangeListener;
    StepsGraphListener stepsGraphListener;
    JsonStepsGraphResponseHandler jsonStepsGraphResponseHandler;
    Context context;


    public StepsGraphImplementer(Context context, String username, ProgressChangeListener progressChangeListener,
                                      StepsGraphListener listener) {

        this.context = context;
        this.stepsGraphListener = listener;
        this.progressChangeListener = progressChangeListener;

        jsonStepsGraphResponseHandler = new JsonStepsGraphResponseHandler(getStepsHandler);
        getSteps(username, "", jsonStepsGraphResponseHandler);
    }

    //where data = xml string format post data
    private void getSteps(String username, String data, Handler responseHandler) {

        System.out.print("getSteps: " + username);

        String url = WebServices.getURL(WebServices.SERVICES.GET_STEPS_GRAPH_NATIVE);
        //fromDate: january 2015
        String fromDate = "1420070400";
        long unixTime = System.currentTimeMillis() / 1000L;
        String toDate = String.valueOf(unixTime);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("To", toDate);
        connection.addParam("From", fromDate);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_STEPS_GRAPH_NATIVE) + username + fromDate + toDate));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        System.out.print("getSteps GET" + url);

        connection.create(Connection.GET, url, "");
    }

    final Handler getStepsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonStepsGraphResponseHandler != null) {
                    }

                    stepsGraphListener.getStepsGraphSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    stepsGraphListener.getStepsGraphFailedWithError(new MessageObj());

                    break;
            }//end Switch
        }
    };
}
