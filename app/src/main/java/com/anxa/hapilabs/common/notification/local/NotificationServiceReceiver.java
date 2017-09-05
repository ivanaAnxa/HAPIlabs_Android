package com.anxa.hapilabs.common.notification.local;


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
import android.widget.Toast;


public class NotificationServiceReceiver extends BroadcastReceiver {

    Toast toast;

    @Override
    public void onReceive(Context context, Intent intent) {

//        String label = intent.getStringExtra("ALERT_LABEL");
        String label = context.getString(R.string.app_name);

        String message = intent.getStringExtra("ALERT_MESSAGE");

        System.out.println("label + message: " + label + "-" + message);


        NotificationManager mNM;
        mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Set the icon, scrolling text and timestamp
        Notification noti = new Notification(ApplicationEx.DEFAULT_RES_ICONS, label, System.currentTimeMillis());


        // Uses the default lighting scheme
        noti.defaults |= Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND;//|Notification.DEFAULT_SOUND;//
        // Will show lights and make the notification disappear when the presses it
        noti.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;

        Intent intentPen = new Intent();
        intentPen.setClass(context, com.anxa.hapilabs.activities.MainActivity.class); // once message is clicked user will be directed to this page
        intentPen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        // The PendingIntent to launch our activity if the user selects this notification
        ApplicationEx.getInstance().pushGCMIntent.add(PendingIntent.getActivity(context, ApplicationEx.getInstance().pushGCMIntentIndex, intentPen, 0));//(context, 0, intentPen, 0);

        // Set the info for the views that show in the notification panel.
        try {
            // Use new API
            Notification.Builder builder = new Notification.Builder(context)
                    .setContentIntent(ApplicationEx.getInstance().pushGCMIntent.get(ApplicationEx.getInstance().pushGCMIntentIndex))
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(label)
                    .setContentText(message);
            noti = builder.build();
//	      	   noti.setLatestEventInfo(context, label, message,ApplicationEx.getInstance().pushGCMIntent.get(ApplicationEx.getInstance().pushGCMIntentIndex) );
        } catch (Exception e) {
        }


        try {

            mNM.notify(ApplicationEx.getInstance().pushGCMIntentIndex, noti);
        } catch (Exception e) {
        }
        ApplicationEx.getInstance().pushGCMIntentIndex++; //need a place where to reset this
    }

    public String getTimeStampEntryDate(long longdate) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(longdate));
        String temp = ((c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c.get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY)) + ":" +
                ((c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE));


        return temp;


    }


}
    