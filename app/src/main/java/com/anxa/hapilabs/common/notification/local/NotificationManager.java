package com.anxa.hapilabs.common.notification.local;

import java.util.ArrayList;
import java.util.Calendar;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.MealReminderActivity;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;


public class NotificationManager {

    public static final int XML_PARSER_SUCCESS = 1;
    public static final int XML_PARSER_START = 0;
    public static final int XML_PARSER_ERROR = -1;
    String label;
//	String label ="HAPICOACH";

    Context context;

    int type;
    boolean isOn;
    boolean[] settingsBoolean = new boolean[3];

    public NotificationManager(Context context) {
        this.context = context;
        setNotification();
    }

    public NotificationManager(Context context, boolean isOn, int type, boolean[] settingsBoolean) {
        this.context = context;
        this.settingsBoolean = settingsBoolean;
        this.isOn = isOn;
        this.type = type;

        setOnOFFSettings(type);
    }

    private void setOnOFFSettings(int type) {
        final SharedPreferences prefs = getPreferenceName(context);
        SharedPreferences.Editor editor = prefs.edit();

        if (type == 0) {
            if (isOn)
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_BREAKFAST, "TRUE");
            else
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_BREAKFAST, "FALSE");

        } else if (type == 1) {
            if (isOn)
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_LUNCH, "TRUE");
            else
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_LUNCH, "FALSE");
        } else if (type == 2) {
            if (isOn)
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_DINNER, "TRUE");
            else
                editor.putString(ApplicationEx.PROPERTY_MEAL_REMINDER_SETTING_DINNER, "FALSE");
        }

        editor.commit();
    }

    private SharedPreferences getPreferenceName(Context context) {
        return context.getSharedPreferences(MealReminderActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    private String getTimerReminder(Context context, int type) {
        final SharedPreferences prefs = getPreferenceName(context);
        String sharedTime = null;

        switch (type) {
            case 0:// breakfast
                try {
                    sharedTime = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_BREAKFAST, "08:00");
                } catch (Exception e2) {
                }
                if (sharedTime == null)
                    sharedTime = "08:00";
                break;
            case 1:// lunch
                try {
                    sharedTime = prefs.getString(
                            ApplicationEx.PROPERTY_MEAL_REMINDER_LUNCH, "13:00");
                } catch (Exception e3) {
                    // TODO: handle exception
                }
                if (sharedTime == null)
                    sharedTime = "13:00";
                break;
            default: // dinner
                try {
                    sharedTime = prefs.getString(ApplicationEx.PROPERTY_MEAL_REMINDER_DINNER, "20:00");
                } catch (Exception e4) {
                    // TODO: handle exception
                }
                if (sharedTime == null)
                    sharedTime = "20:00";
                break;
        }
        return sharedTime;
    }


    //*read direct as local xml string*//
    public boolean setNotification() {

        //parser xml
        label = context.getString(R.string.app_name);

        ArrayList<NotificationObj> notifications = new ArrayList<NotificationObj>();
        //get settings in profile other wise use as default
        NotificationObj breakfastnotif = new NotificationObj();
        breakfastnotif.alert_type = 1;//daily
        breakfastnotif.isRemind = true;//notif is on
        String notifString = context.getString(R.string.LOCAL_NOTIF_BREAKFAST);
        if (ApplicationEx.getInstance().userProfile.getFirstname() != null) {
            notifString = notifString.replace("%@", "" + ApplicationEx.getInstance().userProfile.getFirstname());
        } else {
            notifString = notifString.replace("%@", "");
        }

        Log.i("notif ", notifString);

        breakfastnotif.message = notifString;
        //get from preference
        breakfastnotif.startTime = getTimerReminder(context, 0);

        NotificationObj lunchnotif = new NotificationObj();
        lunchnotif.alert_type = 1;//daily
        lunchnotif.isRemind = true;//notif is on
        notifString = context.getString(R.string.LOCAL_NOTIF_LUNCH);
        if (ApplicationEx.getInstance().userProfile.getFirstname() != null) {
            notifString = notifString.replace("%@", "" + ApplicationEx.getInstance().userProfile.getFirstname());
        } else {
            notifString = notifString.replace("%@", "");
        }
        lunchnotif.message = notifString;
        lunchnotif.startTime = getTimerReminder(context, 1);

        NotificationObj dinnernotif = new NotificationObj();
        dinnernotif.alert_type = 1;//daily
        dinnernotif.isRemind = true;//notif is on
        notifString = context.getString(R.string.LOCAL_NOTIF_DINNER);
        if (ApplicationEx.getInstance().userProfile.getFirstname() != null) {
            notifString = notifString.replace("%@", "" + ApplicationEx.getInstance().userProfile.getFirstname());
        } else {
            notifString = notifString.replace("%@", "");
        }
        dinnernotif.message = notifString;
        dinnernotif.startTime = getTimerReminder(context, 2);

        notifications.add(breakfastnotif);
        notifications.add(lunchnotif);
        notifications.add(dinnernotif);

        scheduleService(context, true, notifications);
        Log.i("call notif", "scheduleService");

        return true;
    }

    private void scheduleService(Context context, boolean deleteExisingAlarmDb, ArrayList<NotificationObj> listAlert) {
        if (settingsBoolean == null)
            settingsBoolean = new boolean[3];

        if (ApplicationEx.getInstance().alarmMgr == null) {
            ApplicationEx.getInstance().alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        NotificationObj tempAlert;

        int hour = 0;
        int minutes = 0;
        int day = 0;
        int dayOfTheWeek = 0;
        int currentdayOfTheWeek = 0;

        Calendar startCalendar = Calendar.getInstance();
        PendingIntent sender;

        for (int i = 0; i < listAlert.size(); i++) {
            try {
                startCalendar.setTimeInMillis(System.currentTimeMillis());

                tempAlert = listAlert.get(i);

                Intent intent = new Intent(context, NotificationServiceReceiver.class);
                intent.putExtra("ALERT_LABEL", label);
                intent.putExtra("ALERT_MESSAGE", tempAlert.message);
                intent.putExtra("ALERT_COUNTER", i);
                intent.putExtra("ALERT_TYPE", tempAlert.alert_type);

                hour = Integer.parseInt(tempAlert.startTime.substring(0, 2).trim());
                minutes = Integer.parseInt(tempAlert.startTime.substring(3, 5).trim());

                Calendar currentTime = Calendar.getInstance();
                Calendar alarmTime = Calendar.getInstance();

                if (tempAlert.alert_type == 1) { //daily{
                    alarmTime.set(Calendar.HOUR_OF_DAY, hour);
                    alarmTime.set(Calendar.MINUTE, minutes);
                    alarmTime.set(Calendar.SECOND, 0);
                    alarmTime.set(Calendar.MILLISECOND, 0);
                    day = alarmTime.get(Calendar.DAY_OF_MONTH);

                    if (currentTime.compareTo(alarmTime) == 1) {
                        alarmTime.set(Calendar.DATE, day + 1);
                    }
                    day = alarmTime.get(Calendar.DAY_OF_MONTH);
                    sender = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (settingsBoolean[i])
                        ApplicationEx.getInstance().alarmMgr.cancel(sender);
                    else
                        ApplicationEx.getInstance().alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), 86400000/*daily*/, sender);

                } else {  //weekly
                    currentdayOfTheWeek = currentTime.get(Calendar.DAY_OF_WEEK); //sunday = 1 sat = 7
                    //DO THIS TO MATCH THE NUMBERING ON SERVER

                    if (currentdayOfTheWeek == 1)
                        currentdayOfTheWeek = 7;
                    else
                        currentdayOfTheWeek = currentdayOfTheWeek - 1;

                    dayOfTheWeek = tempAlert.daysofweek; //

                    sender = PendingIntent.getBroadcast(context, i, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if (currentdayOfTheWeek > dayOfTheWeek) {
                        alarmTime.set(Calendar.DATE, day + (7 - (currentdayOfTheWeek - dayOfTheWeek)));
                    }

                    if (currentdayOfTheWeek < dayOfTheWeek) {
                        alarmTime.set(Calendar.DATE, day + (dayOfTheWeek - currentdayOfTheWeek));
                    }

                    if (dayOfTheWeek == currentdayOfTheWeek) {
                        if (currentTime.compareTo(alarmTime) == 1) {
                            alarmTime.set(Calendar.DATE, day + 7);
                        }
                    }

                    if (settingsBoolean[i])
                        ApplicationEx.getInstance().alarmMgr.cancel(sender);
                    else
                        ApplicationEx.getInstance().alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), 604800000/*weekly*/, sender);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
