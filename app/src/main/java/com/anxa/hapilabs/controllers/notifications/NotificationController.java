package com.anxa.hapilabs.controllers.notifications;

import java.util.Calendar;

import android.content.Context;
import android.content.SharedPreferences;

import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.util.ApplicationEx;

public class NotificationController {


    Context context;

    GetNotificationImplementer notifImplementer;
    MarkNotificationImplementer markNotifImplementer;

    GetNotificationListener listener;

    private String COMMAND_MARK_AS_READ = "markAsRead";
    private String COMMAND_MARK_ALL_AS_READ = "markAllAsRead";
    private String COMMAND_CLEAR_ALL = "clearAllNotif";


    public NotificationController(Context context, GetNotificationListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void getNotifications(String userId) {

        notifImplementer = new GetNotificationImplementer(context, userId, getLastTimeStampFromNow(context), listener);
    }

    public void markNotifAsRead(String userId, String notifId) {
        markNotifImplementer = new MarkNotificationImplementer(context, userId, COMMAND_MARK_AS_READ, notifId, listener);
    }

    public void markAllNotifAsRead(String userId, String notifId) {

        markNotifImplementer = new MarkNotificationImplementer(context, userId, notifId, COMMAND_MARK_ALL_AS_READ, listener);
    }

    public void clearAllNotifications(String userId, String notifId) {

        markNotifImplementer = new MarkNotificationImplementer(context, userId, notifId, COMMAND_CLEAR_ALL, listener);
    }


    public long getLastTimeStamp(Context context) {

        final SharedPreferences prefs = ApplicationEx.getInstance().getGCMPreferences(context);
        long milperDay = 86400;
        long datenow = (Calendar.getInstance().getTimeInMillis()) / 1000;

        long fromDate = (datenow - (milperDay * 30));
        //get default notification is today from 42 days
        return prefs.getLong(ApplicationEx.PROPERTY_SYNCNOTIF_TODATE, fromDate);

    }

    public long getLastTimeStampFromNow(Context context) {

        final SharedPreferences prefs = ApplicationEx.getInstance().getGCMPreferences(context);
        long milperDay = 86400;

        long datenow = (Calendar.getInstance().getTimeInMillis()) / 1000;

        long fromDate = (datenow - (milperDay * 42));
        //get default notification is today from 42 days
        return fromDate;

    }

}
