package com.anxa.hapilabs.controllers.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.RegisterDeviceListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonRegisterDeviceResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;

/**
 * Created by aprilanxa on 06/06/2017.
 */

public class RegisterDeviceImplementer {

    private JsonRegisterDeviceResponseHandler jsonResponseHandler;

    private Context context;
    private RegisterDeviceListener listener;
    private String data;

    public RegisterDeviceImplementer(Context context, String userId, String UUID, String appName, String appVersion,
                                     RegisterDeviceListener listener) {
        this.context = context;
        jsonResponseHandler = new JsonRegisterDeviceResponseHandler(mainHandler);
        this.listener = listener;

        data = JsonRequestWriter.getInstance().createRegisterDeviceJson(UUID, appName, appVersion);
        registerDevice(userId, data, jsonResponseHandler);
    }


    //where data = xml string format post data
    private void registerDevice(String userID, String data, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.REGISTER_DEVICE);

        Connection connection = new Connection(responseHandler);

        connection.addParam("userid", userID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.REGISTER_DEVICE) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, data);

        System.out.println("register device url: " + url);

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

                    listener.registerDeviceSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage());

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.registerDeviceFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };

}
