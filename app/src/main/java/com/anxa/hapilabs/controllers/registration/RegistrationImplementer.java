package com.anxa.hapilabs.controllers.registration;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.RegistrationListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonRegistrationResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
import com.anxa.hapilabs.models.UserProfile;

import android.os.Message;

public class RegistrationImplementer {


    protected ProgressChangeListener progresslistener;

    JsonRegistrationResponseHandler jsonResponseHandler;
    RegistrationListener listener;
    Context context;

    public RegistrationImplementer(Context context,
                                   UserProfile userprofile,
                                   ProgressChangeListener progresslistener,
                                   RegistrationListener listener) {

        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;

        String data = JsonRequestWriter.getInstance().createRegistrationRequest(context, userprofile);
        System.out.println("data: " + data);


        jsonResponseHandler = new JsonRegistrationResponseHandler(registrationHandler);

        register(userprofile.getUsername(), data, jsonResponseHandler);

    }

    public void register(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.REGISTRATION);

        Connection connection = new Connection(responseHandler);

        connection.addParam("username", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.REGISTRATION) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, data);

    }


    final Handler registrationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() instanceof UserProfile) {


                        String password = ApplicationEx.getInstance().userProfile.getPassword();
                        String passwordplain = ApplicationEx.getInstance().userProfile.getPasswordPlain();

                        ApplicationEx.getInstance().userProfile = ((UserProfile) jsonResponseHandler.getResponseObj());
                        ApplicationEx.getInstance().userProfile.setPassword(password);
                        ApplicationEx.getInstance().userProfile.setPasswordPlain(passwordplain);


                        listener.postRegistrationSuccess("SUCCESS");

                    } else if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj) {

                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                        mesObj.setType(MESSAGE_TYPE.FAILED);
                        listener.postRegistrationFailedWithError(mesObj);
                    }
                    //STEP 3:

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    //STEP 3: login failed
                    MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.postRegistrationFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
