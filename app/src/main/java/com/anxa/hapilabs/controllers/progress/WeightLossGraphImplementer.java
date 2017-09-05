package com.anxa.hapilabs.controllers.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.WeightLossGraphListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonWeightGraphResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;

/**
 * Created by aprilanxa on 11/10/2016.
 */

public class WeightLossGraphImplementer {

    Handler responseHandler;

    ProgressChangeListener progressChangeListener;
    WeightLossGraphListener weightLossGraphListener;
    JsonWeightGraphResponseHandler jsonWeightGraphResponseHandler;
    Context context;


    public WeightLossGraphImplementer(Context context, String username, ProgressChangeListener progressChangeListener,
                                      WeightLossGraphListener listener) {

        this.context = context;
        this.weightLossGraphListener = listener;
        this.progressChangeListener = progressChangeListener;

        //fromDate: january 2015
        String fromDate = "1420070400";
        long unixTime = System.currentTimeMillis() / 1000L;
        String toDate = String.valueOf(unixTime);

        //toDate = present
        String data = JsonRequestWriter.getInstance().createWeightGraphJson(username, fromDate, toDate);
        jsonWeightGraphResponseHandler = new JsonWeightGraphResponseHandler(getWeightHandler);
        getWeight(username, data, jsonWeightGraphResponseHandler);
    }

    //where data = xml string format post data
    private void getWeight(String username, String data, Handler responseHandler) {

        System.out.print("getWeight" + username);

        String url = WebServices.getURL(WebServices.SERVICES.GET_WEIGHT_GRAPH_NATIVE);
        //fromDate: january 2015
        String fromDate = "1420070400";
        long unixTime = System.currentTimeMillis() / 1000L;
        String toDate = String.valueOf(unixTime);

        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", username);
        connection.addParam("To", toDate);
        connection.addParam("From", fromDate);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_WEIGHT_GRAPH_NATIVE) + username + fromDate + toDate));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        System.out.print("getWeightGET" + url);

        connection.create(Connection.GET, url, "");
    }

    final Handler getWeightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonWeightGraphResponseHandler != null) {
                    }

                    weightLossGraphListener.getWeightGraphSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    weightLossGraphListener.getWeightGraphSuccess("Successful");

                    break;
            }//end Switch
        }
    };
}
