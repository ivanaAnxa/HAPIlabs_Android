package com.anxa.hapilabs.controllers.progress;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetWeightDataResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonStepsDataResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonWeightDataResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Weight;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class GetWeightDataImplementer {
    Handler responseHandler;


    WeightDataListener stepsDataListener;
    JsonGetWeightDataResponseHandler jsonWeightDataResponseHandler;
    Context context;

    public GetWeightDataImplementer(Context context, String username, String activity_id,
                                   WeightDataListener listener) {

        this.context = context;
        this.stepsDataListener = listener;
        jsonWeightDataResponseHandler = new JsonGetWeightDataResponseHandler(weightHandler);

        getWeight(username, activity_id, jsonWeightDataResponseHandler);

    }

    private void getWeight(String username, String activity_id,  Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.GET_HAPICOACH_WEIGHT);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("id", activity_id);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_HAPICOACH_WEIGHT) + username + activity_id));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }

    final Handler weightHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    Weight currentWeightView = null;
                    if (jsonWeightDataResponseHandler != null) {
                        currentWeightView = (Weight) jsonWeightDataResponseHandler.getResponseObj();

                        if (currentWeightView!=null) {
                            ApplicationEx.getInstance().currentWeightView = currentWeightView;
                        }
                    }

                    stepsDataListener.getWeightDataSuccess(currentWeightView);
                    break;
                case JsonDefaultResponseHandler.ERROR:

                    stepsDataListener.getWeightDataFailedWithError(new MessageObj());

                    break;
            }//end Switch
        }
    };
}
