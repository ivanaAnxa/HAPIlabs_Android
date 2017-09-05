package com.anxa.hapilabs.controllers.notifications;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonMarkAsReadResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 08/07/2016.
 */
public class MarkNotificationImplementer {

    JsonMarkAsReadResponseHandler jsonResponseHandler;

    Context context;
    GetNotificationListener listener;
    String command;

    public MarkNotificationImplementer(Context context, String userId, String command, String notifId, GetNotificationListener listener) {
        this.context = context;
        this.listener = listener;
        this.command = command;

        jsonResponseHandler = new JsonMarkAsReadResponseHandler(mainHandler, "");

        if (command.equalsIgnoreCase("markAsRead")) {
            String data = JsonRequestWriter.getInstance().createMarkAsReadJson(notifId);
            markAsReadNotif(userId, data, jsonResponseHandler);
        } else if (command.equalsIgnoreCase("markAllAsRead")) {
            markAllAsReadNotif(userId, jsonResponseHandler);
        } else if (command.equalsIgnoreCase("clearAllNotif")) {
            clearAllNotif(userId, jsonResponseHandler);
        }

    }


    //where data = xml string format post data
    public void markAsReadNotif(String userID, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_MARK_AS_READ);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_MARK_AS_READ) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, data);
    }

    //where data = xml string format post data
    public void markAllAsReadNotif(String userID, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.POST_MARK_ALL_AS_READ);

        System.out.println("markAllAsReadNotif: " + url);
        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_MARK_ALL_AS_READ) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, "");
    }

    //where data = xml string format post data
    public void clearAllNotif(String userID, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.POST_CLEAR_ALL);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", userID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_CLEAR_ALL) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, "");
    }

    final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    if (command.equalsIgnoreCase("markAsRead")) {
                        listener.markNotifAsReadSuccess(jsonResponseHandler.getResultMessage());
                    }else if (command.equalsIgnoreCase("markAllAsRead")) {
                        listener.markAllNotifAsReadSuccess(jsonResponseHandler.getResultMessage());
                    } else if (command.equalsIgnoreCase("clearAllNotif")) {
                        listener.clearAllNotifSuccess(jsonResponseHandler.getResultMessage());
                    }
                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    if (command.equalsIgnoreCase("markAsRead")) {
                        listener.markNotifAsReadFailedWithError(mesObj);
                    }else if (command.equalsIgnoreCase("markAllAsRead")) {
                        listener.markAllNotifAsReadFailedWithError(mesObj);
                    } else if (command.equalsIgnoreCase("clearAllNotif")) {
                        listener.clearAllNotifFailedWithError(mesObj);
                    }

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
