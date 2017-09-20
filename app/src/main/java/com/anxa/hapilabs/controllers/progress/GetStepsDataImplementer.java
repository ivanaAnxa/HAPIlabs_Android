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
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class GetStepsDataImplementer {
    Handler responseHandler;

    ProgressChangeListener progressChangeListener;
    StepsDataListener stepsDataListener;
    JsonStepsDataResponseHandler jsonStepsDataResponseHandler;
    Context context;

    public GetStepsDataImplementer(Context context, String username, String activity_id,
                                StepsDataListener listener) {

        this.context = context;
        this.stepsDataListener = listener;


            getSteps(username, activity_id, jsonStepsDataResponseHandler);

    }

    private void getSteps(String username, String activity_id,  Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.GET_HAPICOACH_STEPS);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_HAPICOACH_STEPS) + username + activity_id));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }

    final Handler addWeightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    Steps currentStepsView = null;
                    if (jsonStepsDataResponseHandler != null) {
                        currentStepsView = (Steps) jsonStepsDataResponseHandler.getResponseObj();

                        if (currentStepsView!=null) {
                            ApplicationEx.getInstance().currentStepsView = currentStepsView;
                        }
                    }

                    stepsDataListener.getStepsDataSuccess(currentStepsView);
                    break;
                case JsonDefaultResponseHandler.ERROR:

                    stepsDataListener.getStepsDataFailedWithError(new MessageObj());
                    break;
            }//end Switch
        }
    };
}
