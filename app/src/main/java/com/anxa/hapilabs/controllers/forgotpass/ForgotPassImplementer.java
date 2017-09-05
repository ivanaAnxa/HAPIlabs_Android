package com.anxa.hapilabs.controllers.forgotpass;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.ForgotPasswordListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonForgotResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;

public class ForgotPassImplementer {


    JsonForgotResponseHandler jsonResponseHandler;

    //Handler responseHandler;
    protected ProgressChangeListener progresslistener;

    ForgotPasswordListener listener;

    Context context;

    public ForgotPassImplementer(Context context, String username, String password, /*Handler responseHandler,*/ProgressChangeListener progresslistener, ForgotPasswordListener listener) {
        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;

        jsonResponseHandler = new JsonForgotResponseHandler(forgotPassHandler);

        forgotpass(username, jsonResponseHandler);

    }

    //where data = xml string format post data
    public void forgotpass(String username, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.FORGOTPASSWORD);

        String data = JsonRequestWriter.getInstance().createForgotPasswordRequest(username);
        System.out.println("forgot data: " + data);

        Connection connection = new Connection(responseHandler);
        connection.addParam("username", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.FORGOTPASSWORD) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }


    final Handler forgotPassHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj) {
                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                        mesObj.setType(MESSAGE_TYPE.SUCCESS);
                        if (listener != null) {
                            listener.forgotPasswordSuccess("Success");
                        } else {
                        }
                    }


                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                    mesObj.setMessage_id(mesObj.getMessage_id());
                    mesObj.setMessage_string(mesObj.getMessage_string());
                    mesObj.setType(MESSAGE_TYPE.FAILED);
                    listener.forgotPasswordFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
