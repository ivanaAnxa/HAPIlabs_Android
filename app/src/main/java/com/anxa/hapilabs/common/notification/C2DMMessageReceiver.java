package com.anxa.hapilabs.common.notification;

import java.util.Calendar;
import java.util.Date;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

public class C2DMMessageReceiver extends BroadcastReceiver {


    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Log.i("NOTIFY", "C2DMMessageReceiver");

        if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {

            final String payload = intent.getStringExtra("payload");

            final int badge = Integer.parseInt(intent.getStringExtra("badge"));
            ApplicationEx.getInstance().unreadNotifications = badge;

            final String sound = intent.getStringExtra("sound");

            final int refID = Integer.parseInt(intent.getStringExtra("refId"));

            final String refType = intent.getStringExtra("refType");
            Log.w("C2DM", "Received message: " + payload  + refID);


            // display it on the notification Bar
            String label = context.getResources().getString(R.string.app_name);
            String message = payload;

            NotificationManager mNM;
            mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Set the icon, scrolling text and timestamp
            Notification noti = new Notification(ApplicationEx.DEFAULT_RES_ICONS, label, System.currentTimeMillis());
            CharSequence contentTitle = label;
            CharSequence contentText = message;
            RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.custom_notification_layout);
            contentView.setImageViewResource(R.id.image, R.drawable.icon);
            contentView.setTextViewText(R.id.text, contentText);
            contentView.setTextViewText(R.id.title, contentTitle);
            contentView.setTextViewText(R.id.time, getTimeStampEntryDate(noti.when));
            noti.contentView = contentView;

            // Uses the default lighting scheme
            noti.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;//|Notification.DEFAULT_SOUND;

            // Will show lights and make the notification disappear when the presses it
            noti.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
            noti.number = 10;
            Intent intentPen = new Intent();


//            if (refType != null) {
//                //find this meal
//                intentPen.setClass(context, com.hapilabs.activities.MainActivity.class); // once message is clicked user will be directed to this page
//                intentPen.putExtra("TAB", 2);// 1 meals 2//messages
//                intentPen.putExtra("FROM_NOTIF_INT", 1); //from notif
//            } else {
                intentPen.setClass(context, com.anxa.hapilabs.activities.NotificationActivity.class); // once message is clicked user will be directed to this page
                intentPen.putExtra("fromPush", "fromPush");
//            }


            intentPen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intentPen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // The PendingIntent to launch our activity if the user selects this notification
            ApplicationEx.getInstance().pushGCMIntent.add(PendingIntent.getActivity(context, ApplicationEx.getInstance().pushGCMIntentIndex, intentPen, 0));//(context, 0, intentPen, 0);

            // Set the info for the views that show in the notification panel.
            try {

                noti.contentView = contentView;

                // Use new API
                Notification.Builder builder = new Notification.Builder(context)
                        .setContentIntent(ApplicationEx.getInstance().pushGCMIntent.get(ApplicationEx.getInstance().pushGCMIntentIndex))
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle(label)
                        .setAutoCancel(true)
                        .setContentText(message);

                NotificationManager notificationManager =
                        (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                if (Build.VERSION.SDK_INT < 16) {
                    noti = builder.getNotification();
                } else {
                    noti = builder.build();
                }
                // noti.contentIntent  =pendingIntent;

            } catch (Exception e) {
            }


            try {
                mNM.notify(ApplicationEx.getInstance().pushGCMIntentIndex, noti);
            } catch (Exception e) {
            }
            ApplicationEx.getInstance().pushGCMIntentIndex++; //need a place where to reset this

        }

    }

    public String getTimeStampEntryDate(long longdate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(longdate));
        String temp = ((c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY)) + ":" +
                ((c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE));

        return temp;

    }
}