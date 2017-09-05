package com.anxa.hapilabs.controllers.selectcoach;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetCoachListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonCoachListResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;

public class CoachSelectionImplementer {


    JsonCoachListResponseHandler jsonResponseHandler;

    //Handler responseHandler;
    protected ProgressChangeListener progresslistener;

    GetCoachListener listener;

    Context context;

    public CoachSelectionImplementer(Context context, String username, /*Handler responseHandler,*/ProgressChangeListener progresslistener, GetCoachListener listener) {
        this.context = context;
        this.listener = listener;
        this.progresslistener = progresslistener;

        jsonResponseHandler = new JsonCoachListResponseHandler(coachHandler);

        getCoach(username, jsonResponseHandler);

    }

    public void getCoach(String username, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.COACH_LIST);

        String userId = ApplicationEx.getInstance().userProfile.getRegID();

        if (userId == null) {
            if (username!=null){
                url = url.replace("%@", username);
            }else{

            }

        } else {
            url = url.replace("%@", userId);
        }


        Connection connection = new Connection(responseHandler);

        //connection.addParam("username", username);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.COACH_LIST) + ApplicationEx.getInstance().userProfile.getRegID()));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.GET, url, null);

    }


    final Handler coachHandler = new Handler() {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() instanceof List<?>) {

                        List<Coach> coaches = (List<Coach>) jsonResponseHandler.getResponseObj();

                        //update language
                        if (Locale.getDefault().getLanguage() != null)
                            ApplicationEx.language = Locale.getDefault().getLanguage();

                        listener.getCoachSuccess("SUCCESS", coaches);

                    } else if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj) {
                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                        mesObj.setType(MESSAGE_TYPE.FAILED);
                        listener.getCoachFailedWithError(mesObj);

                    }


                    //STEP 3:


                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);
                    listener.getCoachFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
