package com.anxa.hapilabs.controllers.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.PostStepsGoogleFitListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostStepsGoogleFitHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostWorkoutResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public class PostStepsGoogleFitImplementer {

    private JsonPostStepsGoogleFitHandler jsonResponseHandler;

    private Context context;
    private PostStepsGoogleFitListener listener;
    public Workout workout;
    private String data;


    public PostStepsGoogleFitImplementer(Context context, String userId, ArrayList<Workout> workoutObjArray, PostStepsGoogleFitListener listener) {
        this.context = context;
        jsonResponseHandler = new JsonPostStepsGoogleFitHandler(mainHandler);
        this.listener = listener;

        data = JsonRequestWriter.getInstance().createStepsArrayJson(workoutObjArray);

        postStepsData(userId, data, jsonResponseHandler);
    }


    //where data = xml string format post data
    private void postStepsData(String userID, String data, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.POST_ACTIVITY_STEPS);

        Connection connection = new Connection(responseHandler);

        connection.addParam("userId", userID);
        connection.addParam("deviceCode", AppUtil.getDeviceId(context));
        connection.addParam("deviceType", "53");
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_ACTIVITY_STEPS) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, data);
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
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null) {
                    }

                    listener.postStepsGoogleFitSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage());

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.postStepsGoogleFitFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };

}
