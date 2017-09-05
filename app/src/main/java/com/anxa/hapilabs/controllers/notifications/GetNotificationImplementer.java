package com.anxa.hapilabs.controllers.notifications;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonNotificationResponseHandler;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
import com.anxa.hapilabs.models.Notification;

public class GetNotificationImplementer {
    private JsonNotificationResponseHandler jsonResponseHandler;

    private Context context;
    private GetNotificationListener listener;

    public GetNotificationImplementer(Context context, String userId, long fromDate, GetNotificationListener listener) {
        this.context = context;
        jsonResponseHandler = new JsonNotificationResponseHandler(mainHandler, "");
        syncServices(userId, fromDate, jsonResponseHandler);
        this.listener = listener;
    }

    //where data = xml string format post data
    private void syncServices(String userID, long fromDate, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.GET_NOTIFICATIONS);

        Connection connection = new Connection(responseHandler);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.GET_NOTIFICATIONS) + userID + fromDate));
        connection.addParam("userid", userID);
        connection.addParam("from", fromDate + "");
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
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

//                    System.out.println("GET NOTIF: completed");
                    int unreadCount = 0;

                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>) {

                        List<Notification> notificationsList = (List<Notification>) jsonResponseHandler.getResponseObj();

	                		/*NEW OCT 1:
	                		 * first: merge existing notifications with new items from sync:
	                		 * logic: if notif id already exist in DB, sumply update the state(read/unread)
	                		 * if notif id does not exist, add the notification to the list
	                		 * next: update DB for the final list
	                		*/
                        DaoImplementer implDao = new DaoImplementer(new NotificationDAO(context, null), context);

                        for (Notification notif : notificationsList) {
                            Notification n = ApplicationEx.getInstance().notificationList.get(notif.notificationID);

                            if (n != null) {
                                notif.notificationState = n.notificationState;
                            }

                            try {
                                implDao.deleteNotif(notif.notificationID);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (notif.notificationState== Notification.NOTIFICATION_STATE.UNREAD){
                                unreadCount++;
                            }
                            implDao.add(notif);

                            ApplicationEx.getInstance().notificationList.put(notif.notificationID + "", notif);
                        }
                    }

                    ApplicationEx.getInstance().unreadNotifications = unreadCount;

                    listener.getNotificationSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage());

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.getNotificationFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
