package com.anxa.hapilabs.controllers.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonWeightDataResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;

/**
 * Created by aprilanxa on 13/10/2016.
 */

public class WeightDataImplementer {
    Handler responseHandler;

    ProgressChangeListener progressChangeListener;
    WeightDataListener weightDataListener;
    JsonWeightDataResponseHandler jsonWeightDataResponseHandler;
    Context context;


    public WeightDataImplementer(Context context, String username, String command, String activity_id, String weight, String date, ProgressChangeListener progressChangeListener,
                                 WeightDataListener listener) {

        this.context = context;
        this.weightDataListener = listener;
        this.progressChangeListener = progressChangeListener;

        //toDate = present
        String data = JsonRequestWriter.getInstance().createWeightDataJson(weight, date);

        System.out.println("WeightDataImplementer data: " + data);
        jsonWeightDataResponseHandler = new JsonWeightDataResponseHandler(addWeightHandler);
        if (command.equalsIgnoreCase("post")) {
            postWeight(username, data, jsonWeightDataResponseHandler);
        } else if (command.equalsIgnoreCase("update")) {
            updateWeight(username, activity_id, data, jsonWeightDataResponseHandler);
        } else if (command.equalsIgnoreCase("delete")) {
            deleteWeight(username, activity_id, data, jsonWeightDataResponseHandler);
        }
    }

    //where data = xml string format post data
    private void postWeight(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.POST_WEIGHT_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_WEIGHT_DATA) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    private void updateWeight(String username, String activity_id, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.UPDATE_WEIGHT_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.UPDATE_WEIGHT_DATA) + username + activity_id));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    private void deleteWeight(String username, String activity_id, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.DELETE_WEIGHT_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.DELETE_WEIGHT_DATA) + username + activity_id));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, "");
    }

    final Handler addWeightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonWeightDataResponseHandler != null) {
                    }

                    weightDataListener.postWeightDataSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:

                    break;
            }//end Switch
        }
    };
}