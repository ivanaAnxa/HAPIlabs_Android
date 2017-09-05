package com.anxa.hapilabs.controllers.accountsettings;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.AccountSettingsListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonAccountSettingsResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 03/08/2016.
 */
public class PostAccountSettingsImplementer {
    Handler responseHandler;

    ProgressChangeListener progressChangeListener;

    AccountSettingsListener accountSettingsListener;
    JsonAccountSettingsResponseHandler jsonAccountSettingsResponseHandler;
    Context context;

    private static String APIKey = "An2x A3ct h9p1m36";


    public PostAccountSettingsImplementer(Context context, String username, int settingsValue,
                                          ProgressChangeListener progressChangeListener,
                                          AccountSettingsListener accountSettingsListener) {

        this.context = context;
        this.accountSettingsListener = accountSettingsListener;
        this.progressChangeListener = progressChangeListener;

        String data = JsonRequestWriter.getInstance().createAccountSettingsJson(settingsValue);

        System.out.println("save account settings to API data: " + data);

        jsonAccountSettingsResponseHandler = new JsonAccountSettingsResponseHandler(postAcctSettingsHandler, "");
        postAccountSettings(username, data, jsonAccountSettingsResponseHandler);

    }

    //where data = xml string format post data
    private void postAccountSettings(String username, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_ACCOUNT_SETTINGS);
        System.out.println("save account settings to API url: " + url);


        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_ACCOUNT_SETTINGS) + username, APIKey));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler postAcctSettingsHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    //STEP 1: stop progress here

                    accountSettingsListener.accountSettingsSuccess("Successful");
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id("");
                    mesObj.setMessage_string("");
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);
                    accountSettingsListener.accountSettingsFailedWithError(mesObj);

                    break;
            }//end Switch
        }
    };

}
