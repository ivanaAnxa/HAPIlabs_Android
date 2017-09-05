package com.anxa.hapilabs.controllers.registration;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetCoachProgramListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonCoachProgramResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.CoachProgram;
import com.anxa.hapilabs.models.MessageObj;

import java.util.List;

/**
 * Created by aprilanxa on 18/08/2016.
 */
public class CoachProgramImplementer {
    JsonCoachProgramResponseHandler jsonResponseHandler;
    protected ProgressChangeListener progressChangeListener;
    GetCoachProgramListener getCoachProgramListener;

    Context context;

    public CoachProgramImplementer(Context context, String coachID, ProgressChangeListener progressChangeListener, GetCoachProgramListener getCoachProgramListener) {
        this.context = context;
        this.getCoachProgramListener = getCoachProgramListener;
        this.progressChangeListener = progressChangeListener;

        jsonResponseHandler = new JsonCoachProgramResponseHandler(coachProgramHandler);

        getPaymentOptions(coachID, jsonResponseHandler);
    }

    public void getPaymentOptions(String coachID, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.GET_COACH_PROGRAM);
        String userId = ApplicationEx.getInstance().userProfile.getRegID();

        Connection connection = new Connection(responseHandler);
        connection.addParam("id", coachID);
        connection.addParam("userid", userId);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_COACH_PROGRAM) + userId + coachID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.GET, url, null);
    }


    final Handler coachProgramHandler = new Handler() {
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

                        List<CoachProgram> coachPrograms = (List<CoachProgram>) jsonResponseHandler.getResponseObj();
                        getCoachProgramListener.getCoachProgramSuccess("SUCCESS", coachPrograms);

                    } else if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof MessageObj) {
                        //STEP 3: login failed
                        MessageObj mesObj = (MessageObj) jsonResponseHandler.getResponseObj();
                        mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                        getCoachProgramListener.getCoachProgramFailedWithError(mesObj);
                    }
                    //STEP 3:
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    getCoachProgramListener.getCoachProgramFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
