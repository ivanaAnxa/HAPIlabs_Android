package com.anxa.hapilabs.controllers.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonStepsDataResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 28/10/2016.
 */

public class StepsDataImplementer {

    Handler responseHandler;

    ProgressChangeListener progressChangeListener;
    StepsDataListener stepsDataListener;
    JsonStepsDataResponseHandler jsonStepsDataResponseHandler;
    Context context;


    public StepsDataImplementer(Context context, String username, String command, String activity_id, String steps, String date, ProgressChangeListener progressChangeListener,
                                 StepsDataListener listener) {

        this.context = context;
        this.stepsDataListener = listener;
        this.progressChangeListener = progressChangeListener;

        //toDate = present
        String data = JsonRequestWriter.getInstance().createStepsDataJson(steps, date);

        System.out.println("StepsData: " + data);
        jsonStepsDataResponseHandler = new JsonStepsDataResponseHandler(addWeightHandler);
        if (command.equalsIgnoreCase("post")) {
            postSteps(username, data, jsonStepsDataResponseHandler);
        } else if (command.equalsIgnoreCase("update")) {
            updateSteps(username, activity_id, data, jsonStepsDataResponseHandler);
        } else if (command.equalsIgnoreCase("delete")) {
            deleteSteps(username, activity_id, data, jsonStepsDataResponseHandler);
        }
    }

    //where data = xml string format post data
    private void postSteps(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.POST_STEPS_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_STEPS_DATA) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    private void updateSteps(String username, String activity_id, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.UPDATE_STEPS_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.UPDATE_STEPS_DATA) + username + activity_id));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    private void deleteSteps(String username, String activity_id, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.DELETE_STEPS_DATA);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.DELETE_STEPS_DATA) + username + activity_id));
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
                    if (jsonStepsDataResponseHandler != null) {
                    }

                    stepsDataListener.postStepsDataSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:

                    stepsDataListener.postStepsDataFailedWithError(new MessageObj());
                    break;
            }//end Switch
        }
    };
}
