package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.PremiumAccessListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonSendAccessCodeResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 22/08/2016.
 */
public class PremiumAccessImplementer {

    ProgressChangeListener progressChangeListener;
    PremiumAccessListener premiumAccessListener;
    JsonSendAccessCodeResponseHandler jsonSendAccessCodeResponseHandler;
    Handler responseHandler;
    Context context;

    String phoneNum;
    String coachProgramId;

    public PremiumAccessImplementer(Context context, String coachProgramId, String phoneNumber,
                                    String commandId,
                                    ProgressChangeListener progressChangeListener,
                                    PremiumAccessListener listener) {

        String username = ApplicationEx.getInstance().userProfile.getRegID();

        this.phoneNum = phoneNumber;
        this.coachProgramId = coachProgramId;
        this.context = context;
        this.premiumAccessListener = listener;
        this.progressChangeListener = progressChangeListener;

        if (commandId.equalsIgnoreCase("send_accesscode")) {
            //for send access code
            String data = JsonRequestWriter.getInstance().createSendAccessCodeJson(phoneNumber);
            jsonSendAccessCodeResponseHandler = new JsonSendAccessCodeResponseHandler(sendAccessCodeHandler, "");
            sendAccessCode(username, data, coachProgramId, jsonSendAccessCodeResponseHandler);
        }else if (commandId.equalsIgnoreCase("validate_accesscode")){
            jsonSendAccessCodeResponseHandler = new JsonSendAccessCodeResponseHandler(validateAccessCodeHandler, "");
            validateAccessCode(username, coachProgramId, jsonSendAccessCodeResponseHandler);
        }

    }

    private void sendAccessCode(String username, String data, String coachProgramId, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.SEND_ACCESS_CODE);

        Connection connection = new Connection(responseHandler);
        connection.addParam("id", coachProgramId);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.SEND_ACCESS_CODE) + username + coachProgramId));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    private void validateAccessCode(String username, String coachProgramId, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.VALIDATE_ACCESS_CODE);

        Connection connection = new Connection(responseHandler);
        connection.addParam("id", coachProgramId);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.VALIDATE_ACCESS_CODE) + username + coachProgramId));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, "");
    }

    final Handler sendAccessCodeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    premiumAccessListener.sendAccessCodeSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = (MessageObj)jsonSendAccessCodeResponseHandler.getResponseObj();
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    premiumAccessListener.sendAccessCodeFailedWithError(mesObj);

                    break;
            }//end Switch
        }
    };

    final Handler validateAccessCodeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    premiumAccessListener.validateAccessCodeSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = (MessageObj)jsonSendAccessCodeResponseHandler.getResponseObj();
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    premiumAccessListener.validateAccessCodeFailedWithError(mesObj);

                    break;
            }//end Switch
        }
    };
}
