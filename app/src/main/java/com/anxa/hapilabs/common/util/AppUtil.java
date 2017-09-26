package com.anxa.hapilabs.common.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hapilabs.R;
import com.anxa.hapilabs.common.handlers.reader.JsonUtil;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.UserProfile.GENDER;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;

public class AppUtil {

    public static String getMealTip(Context context, int dayIndex, MEAL_TYPE mealtype) {
        if (dayIndex == 0) { //sunday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYDINNER);
            }
        } else if (dayIndex == 1) { //Monday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYDINNER);
            }
        } else if (dayIndex == 2) { //TUesday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYDINNER);
            }
        } else if (dayIndex == 3) { //Wen
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYDINNER);
            }
        } else if (dayIndex == 4) { //THurs
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYDINNER);
            }
        } else if (dayIndex == 5) { //Fri
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYDINNER);
            }
        } else if (dayIndex == 6) { //Sat
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYDINNER);
            }
        }

        return "";
    }

    public static Hashtable<String, Integer> getMealCountPerWeek(Hashtable<String, List<Meal>> weeklyMeal) {

        Hashtable<String, Integer> mealCount = new Hashtable<String, Integer>();

        for (Object dateKey : weeklyMeal.keySet().toArray()) {

            List<Meal> arr = weeklyMeal.get((String) dateKey);


            int count = 0;

            for (Meal meal : arr) {

                if (meal != null && meal.meal_id != null) {
                    count++;
                }
                if (count > 5) //max count of meal is just 5 meals
                    count = 5;
            }
            mealCount.put("" + dateKey, count);

        }

        return mealCount;
    }

    public static Hashtable<String, Integer> getCommentCountPerWeek(Hashtable<String, List<Meal>> weeklyMeal) {

        Hashtable<String, Integer> mealCount = new Hashtable<String, Integer>();

        for (Object dateKey : weeklyMeal.keySet().toArray()) {

            List<Meal> arr = weeklyMeal.get((String) dateKey);

            int count = 0;

            for (Meal meal : arr) {

                if (meal != null && meal.commentsCount > 0) {
                    for (Comment comment : meal.comments) {
                        try {
                            if (comment.coach.coach_id != null) {
                                count++;
                                break;
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }

                    }
//                    count++;
                }
                if (count > 5) //max count of meal is just 5 meals
                    count = 5;
            }
            mealCount.put("" + dateKey, count);

        }

        return mealCount;
    }

    public static String getRealPathFromURI(Context context, String contentStr) {

        Uri contentURI = Uri.parse(contentStr);

        Cursor cursor = context.getContentResolver().query(contentURI, null,
                null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
        return null;
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, true);
        bm.recycle();
        return resizedBitmap;
    }

    /*
     * scale image proportionally depending on ratio of the actual image to max
     * width and height
     */
    public static Bitmap scaleImage(Bitmap bitmap, int boundingx, int boundingy) {

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) boundingx) / width;
        float yScale = ((float) boundingy) / height;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        // Create a new bitmap and convert it to a format understood by the
        // ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        return scaledBitmap;

    }

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return asHex(sha1hash);
    }

    public static Date stringToDateFormat(String str) {

        return stringToDateFormat(str, ApplicationEx.getInstance().defaultTimeFormat);
    }

    public static Date stringToDateFormat2(String str) {

        return stringToDateFormat(str, ApplicationEx.getInstance().defaultTimeFormat2);
    }

    public static int getHour() {

        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(new Date());
        return calendar.get(Calendar.HOUR_OF_DAY);

    }

    public static int getMinute() {


        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(new Date());
        return calendar.get(Calendar.MINUTE);

    }

    public static int getHour(Date d) {


        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(d);
        return calendar.get(Calendar.HOUR_OF_DAY);

    }

    public static int getMinute(Date d) {


        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(d);
        return calendar.get(Calendar.MINUTE);

    }

    public static Date formatDate(TimePicker timepicker, int month, int day, int year) {

        String time = formatDate(timepicker);

        time = year + "-" + month + "-" + day + " " + time;

        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return date;

    }

    public static String formatDate(TimePicker timepicker) {
        String str;

        String time;
        if (timepicker.getCurrentHour() < 11) { // Am
            time = ((timepicker.getCurrentHour() < 10) ? ("0" + timepicker
                    .getCurrentHour()) : timepicker.getCurrentHour())
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "am";

        } else { // pm
            time = timepicker.getCurrentHour()
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "pm";

        }


        str = time;

        return str;
    }

    public static String formatDate(DatePicker datepicker) {
        String str;
        int month = datepicker.getMonth() + 1;

        String date = datepicker.getYear()
                + "/"
                + ((month < 10) ? ("0" + month) : month)
                + "/"
                + ((datepicker.getDayOfMonth() < 10) ? ("0" + datepicker
                .getDayOfMonth()) : datepicker.getDayOfMonth());

        str = date;

        return str;
    }

    public static String formatDate(DatePicker datepicker, TimePicker timepicker) {
        String str;
        int month = datepicker.getMonth() + 1;

        String date = datepicker.getYear()
                + "-"
                + ((month < 10) ? ("0" + month) : month)
                + "-"
                + ((datepicker.getDayOfMonth() < 10) ? ("0" + datepicker
                .getDayOfMonth()) : datepicker.getDayOfMonth());

        String time;
        if (timepicker.getCurrentHour() < 11) { // Am
            time = ((timepicker.getCurrentHour() < 10) ? ("0" + timepicker
                    .getCurrentHour()) : timepicker.getCurrentHour())
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "am";

        } else { // pm
            time = timepicker.getCurrentHour()
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "pm";

        }

        str = date + " " + time;

        return str;
    }

    public static String getMonthDay(Date date) {
        String localTime;
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf;
            if (ApplicationEx.language.equalsIgnoreCase("fr")) {
                sdf = new SimpleDateFormat("dd MMM");
            } else {
                sdf = new SimpleDateFormat("MMM dd");
            }
            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

//        Log.d("Month+Day: ", localTime);
        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getHeaderDate(Date date) {
        String localTime;
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf;
//            if (ApplicationEx.language.equalsIgnoreCase("fr")) {
//                sdf = new SimpleDateFormat("dd MMM");
//            } else {
//                sdf = new SimpleDateFormat("MMM dd");
//            }

            sdf = new SimpleDateFormat("dd MMMM yyyy");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

//        Log.d("Month+Day: ", localTime);
        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static long formatDateLong(DatePicker datepicker,
                                      TimePicker timepicker) {

        int year;
        int month;
        int day;
        int hour;
        int mins;

        year = datepicker.getYear();
        month = datepicker.getMonth() + 1;
        day = datepicker.getDayOfMonth();
        hour = timepicker.getCurrentHour();
        mins = timepicker.getCurrentMinute();

        Calendar calendar = new GregorianCalendar(year, month, day, hour, mins);

        long millis = calendar.getTimeInMillis();

        return millis;
    }

    public static int getDayDifference(Date startDate, Date endDate) {
        Calendar cstartDate = getDatePart(startDate);
        Calendar cendDate = getDatePart(endDate);
        int daysbetween = 0;
        while (cstartDate.before(cendDate)) {
            cstartDate.add(Calendar.DAY_OF_MONTH, 1);
            daysbetween++;

        }
        return daysbetween;
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);// set this to midnight

        cal.set(Calendar.MINUTE, 0);// set this to 0
        cal.set(Calendar.SECOND, 0);// set this to 0
        cal.set(Calendar.MILLISECOND, 0);// set this to 0

        return cal;
    }

    public static Date updateDate(Date date, int dayOffset) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int DayoftheMonth = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, DayoftheMonth + (dayOffset));
        return c.getTime();

    }

    public static long dateToLongFormat(String str, String dateformat) {
        long returnValue;

        Date d = stringToDateFormat(str, dateformat);

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + (offsetHrs));
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + (offsetMins));

        returnValue = c.getTimeInMillis() / 1000;

        return returnValue;

    }

    public static Date stringToDateFormat(String str, String dateformat) {
        if (str != null && str.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat,
                    Locale.getDefault());

            Date d;
            try {
                d = sdf.parse(str);

            } catch (ParseException e) {
                return null;
            }
            return d;
        } else {
            return null;
        }
    }

    public static boolean StringtoBooleanFormat(String str,
                                                boolean defaultBoolean) {
        if (str != null && str.length() > 0) {
            if (str.toLowerCase(Locale.US).equals("true")) {
                return true;
            } else if (str.toLowerCase(Locale.US).equals("false")) {
                return false;
            }
        }
        return defaultBoolean;
    }


    public static String getEatingHabits(Context context, int index) {

        switch (index) {
            case 0:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_1);
            case 1:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_2);
            case 2:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_3);
            case 3:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_4);
            case 4:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_5);
            default:
                return context.getResources().getString(R.string.EATINGHABITS_SELECTION_6);
        }

    }


    public static float StringtoFloatFormat(String str) {
        Float f = 0f;

        if (str != null && str.length() > 0) {
            f = Float.valueOf(str);
        }
        return f;
    }

    public static String GendertoString(Context context, GENDER gender) {

        if (gender == GENDER.FEMALE)
            return context.getResources().getString(R.string.PROFILE_GENDER_FEMALE);
        else
            return context.getResources().getString(R.string.PROFILE_GENDER_MALE);

    }

    public static GENDER StringtoGender(String str) {

        if (str != null && str.length() > 0) {
            if (str.toLowerCase(Locale.US).equals("female")) {
                return GENDER.FEMALE;
            } else if (str.toLowerCase(Locale.US).equals("male")) {
                return GENDER.MALE;
            }
        }
        return null;

    }

    public static String getDateinUTC(long d) {
        return getDateinUTC(new Date(d * 1000));
    }

    public static String getDateOnlyinUTC(Date d) {

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));


        String temp = c.get(Calendar.YEAR)
                + "-"
                + getMonthNum(c.get(Calendar.MONTH))
                + "-"
                + ((c.get(Calendar.DAY_OF_MONTH) < 10) ? ("0" + c
                .get(Calendar.DAY_OF_MONTH)) : c
                .get(Calendar.DAY_OF_MONTH));

        //**************

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
//        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(d); // I assume your timestamp is in seconds and you're converting to milliseconds?

//        Log.d("Month+Day: ", localTime);
        return localTime;
    }

    public static String getDateinString(Date dateRaw) {
        if (ApplicationEx.language.equals("fr")) {
            //format: 16 May 2016
            String strDateFormat = "d MMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            return sdf.format(dateRaw);
        } else {
            String strDateFormat = "MMM d, yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
            return sdf.format(dateRaw);
        }
    }

    public static String getDateinStringKey(Date dateRaw) {
        String strDateFormat = "MMM d yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(dateRaw);

    }

    public static String getMonthDateinString(Date dateRaw) {

        //format: May 16
        String strDateFormat = "MMM d";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);

        return sdf.format(dateRaw);
    }

    public static String getDateinUTC(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));


        String temp = c.get(Calendar.YEAR)
                + "-"
                + getMonthNum(c.get(Calendar.MONTH))
                + "-"
                + ((c.get(Calendar.DAY_OF_MONTH) < 10) ? ("0" + c
                .get(Calendar.DAY_OF_MONTH)) : c
                .get(Calendar.DAY_OF_MONTH))
                + " "
                + ((c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c
                .get(Calendar.HOUR_OF_DAY)) : c
                .get(Calendar.HOUR_OF_DAY))
                + ":"
                + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c
                .get(Calendar.MINUTE)) : c.get(Calendar.MINUTE))
                + ":"
                + ((c.get(Calendar.SECOND) < 10) ? ("0" + c
                .get(Calendar.SECOND)) : c.get(Calendar.SECOND));

        return temp;

    }

    public static String getDateinGMT(Date d) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return isoFormat.format(d);
    }


    @SuppressWarnings("unused")
    private static String getMonth(int month, boolean shortVersion) {
        String returnMonth = "January";
        switch (month) {
            case 0:
                returnMonth = "January";
                break;
            case 1:
                returnMonth = "February";
                break;
            case 2:
                returnMonth = "March";
                break;
            case 3:
                returnMonth = "April";
                break;
            case 4:
                returnMonth = "May";
                break;
            case 5:
                returnMonth = "June";
                break;
            case 6:
                returnMonth = "July";
                break;
            case 7:
                returnMonth = "August";
                break;
            case 8:
                returnMonth = "September";
                break;
            case 9:
                returnMonth = "October";
                break;
            case 10:
                returnMonth = "November";
                break;
            case 11:
                returnMonth = "December";
                break;

        }
        return (shortVersion ? returnMonth.substring(0, 3) : returnMonth);
    }

    private static String getMonthNum(int month) {
        switch (month) {
            case 0:
                return "01";
            case 1:
                return "02";
            case 2:
                return "03";
            case 3:
                return "04";
            case 4:
                return "05";
            case 5:
                return "06";
            case 6:
                return "07";
            case 7:
                return "08";
            case 8:
                return "09";
            case 9:
                return "10";
            case 10:
                return "11";
            case 11:
                return "12";

        }
        return "01";
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getCurrentDate(int addDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addDays);
        return (calendar.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(String format, int addDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addDays);
        DateFormat formatter = new SimpleDateFormat(format);
        String dateNow = formatter.format(calendar.getTime());
        return dateNow;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(String format) {
        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        DateFormat formatter = new SimpleDateFormat(format);

        String dateNow = formatter.format(calendar.getTime());

        return dateNow;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getOffset(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.add(Calendar.HOUR_OF_DAY, (offsetHrs));
        c.add(Calendar.MINUTE, (offsetMins));


        return c.getTime();

    }

    @SuppressLint("SimpleDateFormat")
    public static Date getOffsetOnSync(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;


        c.add(Calendar.HOUR_OF_DAY, (-offsetHrs));
        c.add(Calendar.MINUTE, (-offsetMins));


        return c.getTime();

    }


    @SuppressLint("SimpleDateFormat")
    public static Date getCurrentDateinDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public static float dpToPixel(int dp, Context context) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
        return px;
    }

    public static String getTimeOnly(long millisec, boolean is24) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        if (!is24) {

            String temp = ((c.get(Calendar.HOUR) < 10) ? ("0" + c
                    .get(Calendar.HOUR)) : c.get(Calendar.HOUR))
                    + ":"
                    + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c
                    .get(Calendar.MINUTE)) : c.get(Calendar.MINUTE))
                    + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");

            return temp;
        } else {
            String temp = ((c.get(Calendar.HOUR_OF_DAY) < 10) ? ("0" + c
                    .get(Calendar.HOUR_OF_DAY)) : c.get(Calendar.HOUR_OF_DAY))
                    + ":"
                    + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c
                    .get(Calendar.MINUTE)) : c.get(Calendar.MINUTE));

            return temp;
        }

    }

    public static String getTimeOnly24(long millisec) {
        return getTimeOnly(millisec, true);
    }

    public static String getTimeOnly24() {
        long millisec = new Date().getTime();
        return getTimeOnly(millisec, true);
    }

    public static String getTimeOnly12(long millisec) {
        return getTimeOnly(millisec, false);
    }

    public static String getTimeOnly12() {
        long millisec = new Date().getTime();
        return getTimeOnly(millisec, false);

    }

    public static Bitmap rotateImage(Context context, Uri uri, int rotate) {
        try {
            // Bitmap a =
            // MediaStore.Images.Media.getBitmap(context.getContentResolver(),
            // uri);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate, 0, 0);
            return Bitmap.createBitmap(
                    MediaStore.Images.Media.getBitmap(
                            context.getContentResolver(), uri), 0, 0, 4128,
                    4128, matrix, true);

        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static Bitmap rotateImage(Context context, Uri uri, int rotate,
                                     int w, int h, int scaleX, int scaleY) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inDither = false; // Disable Dithering mode
            opts.inPurgeable = true; // Tell to gc that whether it needs free
            // memory, the Bitmap can be cleared
            opts.inInputShareable = true; // Which kind of reference will be
            // used to recover the Bitmap data
            // after being clear, when it will
            // be used in the future
            opts.outHeight = h;
            opts.outWidth = w;
            opts.inSampleSize = calculateInSampleSize(opts, scaleX, scaleY);

            try {
                Bitmap btemp = BitmapFactory.decodeFile(uri.getPath(), opts);
                return Bitmap.createBitmap(btemp, 0, 0, btemp.getWidth(),
                        btemp.getHeight(), matrix, true);

            } catch (OutOfMemoryError oum) {
                opts.inSampleSize = 2 * 2;
                Bitmap btemp = BitmapFactory.decodeFile(uri.getPath(), opts);
                return Bitmap.createBitmap(btemp, 0, 0, btemp.getWidth(),
                        btemp.getHeight(), matrix, true);

            }

        } catch (Exception e) {
        }
        return null;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    public static String getDefaultUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("HAPI" + " " + R.string.app_version);
        result.append("Dalvik/");
        result.append(System.getProperty("java.vm.version")); // such as 1.1.0
        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        result.append(version.length() > 0 ? version : "1.0");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");
        return result.toString();
    }

    public static long dateToUnixTimestamp(Date dt) {
        long unixtime = dt.getTime() / 1000L;
        return unixtime;
    }

    public static Date toDate(Timestamp timestamp) {
        long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
        return new Date(milliseconds);
    }

    public static Date toDate(Long timestamplong) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamplong * 1000);
        return calendar.getTime();

    }

    public static String convertTimeWithTimeZome(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time * 1000);
        return (cal.get(Calendar.YEAR) + " " + (cal.get(Calendar.MONTH) + 1) + " "
                + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.get(Calendar.HOUR_OF_DAY) + ":"
                + cal.get(Calendar.MINUTE));

    }

    public static List<Meal> getMealsByDate(Date date, List<Meal> list) {
        return list;
    }

    public static String getMealTitle(Context context, MEAL_TYPE mealtype) {

        switch (mealtype) {
            case BREAKFAST:
                return context.getResources().getString(R.string.MEALTYPE_BREAKFAST);
            case AM_SNACK:
                return context.getResources().getString(R.string.MEALTYPE_MORNINGSNACK);
            case LUNCH:
                return context.getResources().getString(R.string.MEALTYPE_LUNCH);
            case PM_SNACK:
                return context.getResources().getString(R.string.MEALTYPE_AFTERNOONSNACK);
            default:
                return context.getResources().getString(R.string.MEALTYPE_DINNER);
        }
    }

    public static String getMealTitle(Context context, int mealtype) {

        switch (mealtype) {
            case 0:
                return context.getResources().getString(R.string.MEALTYPE_BREAKFAST);
            case 1:
                return context.getResources().getString(R.string.MEALTYPE_MORNINGSNACK);
            case 2:
                return context.getResources().getString(R.string.MEALTYPE_LUNCH);
            case 3:
                return context.getResources().getString(R.string.MEALTYPE_AFTERNOONSNACK);
            default:
                return context.getResources().getString(R.string.MEALTYPE_DINNER);
        }
    }

    public static String getHAPIMoodTitle(Context context, int moodValue) {

        if (moodValue == 1) {
            return context.getString(R.string.HAPIMOMENT_GREAT);
        }
        if (moodValue == 2) {
            return context.getString(R.string.HAPIMOMENT_GOOD);
        }
        if (moodValue == 3) {
            return context.getString(R.string.HAPIMOMENT_OKAY);
        }
        if (moodValue == 4) {
            return context.getString(R.string.HAPIMOMENT_NOTGOOD);
        }
        if (moodValue == 5) {
            return context.getString(R.string.HAPIMOMENT_BAD);
        }

        return "";

    }

    public static int getDefaultHapimomentPhoto() {
        return R.drawable.hapi_default_image;
    }

    public static String getMealTime(Date mealDate) {

        //get time only for the dte format is HH:DD
        Calendar c = Calendar.getInstance();
        c.setTime(mealDate);
        c.setTimeZone(TimeZone.getDefault());

        return AppUtil.getTimeOnly12(c.getTimeInMillis());

    }

    public static String getExerciseTime(Date exerciseDate) {
        Date localDate = new Date(exerciseDate.getTime() + TimeZone.getDefault().getRawOffset());

        if (ApplicationEx.language.equals("fr")) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(localDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            return sdf.format(localDate);
        }
    }

    public static String getExerciseTimeWith24Format(Date exerciseDate) {
        Date localDate = new Date(exerciseDate.getTime() + TimeZone.getDefault().getRawOffset());

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(localDate);
    }

    public static String getExerciseTimeFromPicker(int hourOfDay, int minute) {
        if (ApplicationEx.language.equals("fr")) {
            return pad(hourOfDay) + ":" + pad(minute);
        } else {
            if (hourOfDay > 12) {
                int hourInt = hourOfDay - 12;

                return pad(hourInt) + ":" + pad(minute) + " PM";
            } else {
                return pad(hourOfDay) + ":" + pad(minute) + " AM";
            }
        }
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    public static String getProfileMealTime(Date mealDate) {
        if (ApplicationEx.language.equals("fr")) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM 'à' HH:mm");
            return sdf.format(mealDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd 'at' hh:mm a");
            return sdf.format(mealDate);
        }
    }

    public static int getPhotoResource(MEAL_TYPE type) {
        //depending on type image dummy meal color changes
        switch (type) {
            case BREAKFAST:
                return R.drawable.meal_default_breakfast;
            case AM_SNACK:
                return R.drawable.meal_default_amsnack;
            case LUNCH:
                return R.drawable.meal_default_lunch;
            case PM_SNACK:
                return R.drawable.meal_default_pmsnack;
            default:
                return R.drawable.meal_default_dinner;
        }

    }

    public static Hashtable<String, List<Meal>> getMealsByDateRange(Date todate, Date fromdate, Hashtable<String, Meal> list, int dayInTheRange) {

        Hashtable<String, List<Meal>> weeklyMeal = new Hashtable<String, List<Meal>>();
        String key = "";
        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;

        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);

            if (last_day_reached) {
                month_day = (month_day + 1) % 12;
                last_day_reached = false;
            }

            if (date_day == max_day) {
                last_day_reached = true;
            }
            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;
            //create dummy data
            List<Meal> value = new ArrayList<Meal>();
            weeklyMeal.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        }//end for

        Enumeration<Meal> mealEnum = list.elements();

        while (mealEnum.hasMoreElements()) {
            Meal meal = mealEnum.nextElement();
            JsonUtil.log(meal, "GETRANGE");

            //get date_day of the meal
            c.setTime(meal.meal_creation_date);
            date_day = c.get(Calendar.DAY_OF_MONTH);
            month_day = c.get(Calendar.MONTH) + 1;

            List<Meal> dummy = weeklyMeal.get(month_day + "_" + date_day);

            if (dummy != null) {
                dummy.add(meal);
                weeklyMeal.remove(month_day + "_" + date_day);
                weeklyMeal.put(month_day + "_" + date_day, dummy);
            }
        }//end while
        //end for
        //insert dummy meals
        for (Object dateKey : weeklyMeal.keySet().toArray()) {
            List<Meal> arr = weeklyMeal.get("" + dateKey);
            int addNullCount = (arr.size());
            for (int ctr = 0; ctr < (5 - addNullCount); ctr++) {
                arr.add(null);
            }
            weeklyMeal.remove("" + dateKey);
            weeklyMeal.put("" + dateKey, arr);
        }
        return weeklyMeal;
    }

    public static Hashtable<String, List<HapiMoment>> getHapimomentsByDateRange(Date todate, Date fromdate, Hashtable<String, HapiMoment> list, int dayInTheRange) {
        Hashtable<String, List<HapiMoment>> weeklyHapiMoment = new Hashtable<String, List<HapiMoment>>();
        String key = "";

        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;
        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);
            if (last_day_reached) {
                month_day = month_day + 1;
                last_day_reached = false;
            }
            if (date_day == max_day) {
                last_day_reached = true;
            }
            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;

            //create dummy data
            List<HapiMoment> value = new ArrayList<HapiMoment>();
            weeklyHapiMoment.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        }//end for
        Enumeration<HapiMoment> hapiMomentEnumeration = list.elements();

        while (hapiMomentEnumeration.hasMoreElements()) {
            HapiMoment hapiMoment = hapiMomentEnumeration.nextElement();

            //get date_day of the meal
            c.setTime(hapiMoment.mood_datetime);

            date_day = c.get(Calendar.DAY_OF_MONTH);
            month_day = c.get(Calendar.MONTH) + 1;

            List<HapiMoment> dummy = weeklyHapiMoment.get(month_day + "_" + date_day);

            if (dummy != null) {
                dummy.add(hapiMoment);
                weeklyHapiMoment.remove(month_day + "_" + date_day);
                weeklyHapiMoment.put(month_day + "_" + date_day, dummy);
            }
        }//end while

        //end for
        return weeklyHapiMoment;

    }

    public static Hashtable<String, List<Workout>> getWorkoutByDateRange(Date todate, Date fromdate, Hashtable<String, Workout> list, int dayInTheRange) {

        Hashtable<String, List<Workout>> weeklyWorkout = new Hashtable<String, List<Workout>>();
        String key = "";

        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;

        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);

            if (last_day_reached) {
                month_day = month_day + 1;
                last_day_reached = false;
            }

            if (date_day == max_day) {
                last_day_reached = true;
            }

            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;

            //create dummy data
            List<Workout> value = new ArrayList<Workout>();
            weeklyWorkout.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        }//end for
        Enumeration<Workout> workoutEnum = list.elements();

        while (workoutEnum.hasMoreElements()) {
            Workout workoutObj = workoutEnum.nextElement();
            //get date_day of the workout
            c.setTime(workoutObj.exercise_datetime);
            date_day = c.get(Calendar.DAY_OF_MONTH);
            month_day = c.get(Calendar.MONTH) + 1;

            List<Workout> dummy = weeklyWorkout.get(month_day + "_" + date_day);
            if (dummy != null) {
                dummy.add(workoutObj);
                weeklyWorkout.remove(month_day + "_" + date_day);
                weeklyWorkout.put(month_day + "_" + date_day, dummy);
            }
        }//end while
        //end for
        return weeklyWorkout;
    }

    public static Hashtable<String, List<Weight>> getWeightByDateRange(Date todate, Date fromdate, Hashtable<String, Weight> list, int dayInTheRange) {

        Hashtable<String, List<Weight>> weeklyWeight = new Hashtable<String, List<Weight>>();
        String key = "";

        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;

        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);

            if (last_day_reached) {
                month_day = month_day + 1;
                last_day_reached = false;
            }

            if (date_day == max_day) {
                last_day_reached = true;
            }

            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;

            //create dummy data
            List<Weight> value = new ArrayList<Weight>();
            weeklyWeight.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);
        }//end for
        Enumeration<Weight> weightEnum = list.elements();

        while (weightEnum.hasMoreElements()) {
            Weight weightObj = weightEnum.nextElement();
            //get date_day of the workout
            c.setTime(weightObj.start_datetime);
            date_day = c.get(Calendar.DAY_OF_MONTH);
            month_day = c.get(Calendar.MONTH) + 1;

            List<Weight> dummy = weeklyWeight.get(month_day + "_" + date_day);
            if (dummy != null) {
                dummy.add(weightObj);
                weeklyWeight.remove(month_day + "_" + date_day);
                weeklyWeight.put(month_day + "_" + date_day, dummy);
            }
        }//end while
        //end for
        return weeklyWeight;
    }


    public static String getDayofWeek(Date date) {
        return new SimpleDateFormat("EE", Locale.getDefault()).format(date);
    }

    public static String getMonthonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return "" + (c.get(Calendar.MONTH) + 1);
    }

    public static String getDayonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return ("" + c.get(Calendar.DAY_OF_MONTH));
    }

    public static int getDayonDateInt(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return (c.get(Calendar.DAY_OF_MONTH));
    }

    public static int getWeekIndexOfDate(Date date) {

        //return 0 if date falls on Sunday
        //return 1 if date falls on Monday
        //return 2 if date falls on Tuesday
        //return 3 if date falls on Wednesday
        //return 4 if date falls on Thursday
        //return 5 if date falls on Friday
        //return 6 if date falls on Saturday

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK);
    }

    /*DO NOT EDIT THIS PART - Jen*/
    public static String shaHashed(String stringToHash) {

        try {
            byte[] result;

            final MessageDigest digest = MessageDigest.getInstance("SHA-1");
            result = digest.digest(stringToHash.getBytes("UTF-8"));

            // Another way to make HEX, my previous post was only the method like your solution
            StringBuilder sb = new StringBuilder();

            for (byte b : result) // This is your byte[] result..
            {
                sb.append(String.format("%02X", b));
            }

            return sb.toString().toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String createSignature(String input) {

        String signature = "";

        String hashInput = input + ApplicationEx.sharedKey;
        try {
            signature = AppUtil.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return signature;
    }


    //from 45kg to 45000
    public static String getWeightInGrams(String kg) {
        if (kg != null && kg.length() > 2) {

            return (kg.substring(0, kg.length() - 3) + "000");

        } else return "0";

    }

    //from 120cm to 120
    public static String getHeightIncm(String cm) {
        if (cm != null && cm.length() > 2) {

            return (cm.substring(0, cm.length() - 3));

        } else return "0";

    }


    //from 45000 to 45kg
    public static String getWeightInGrams(int metricValue) {
        String mv = String.valueOf(metricValue);
        if (mv != null) {
            return (mv.substring(0, mv.length() - 3) + " kg");

        } else return "44 kg";

    }

    //from 120 to 120 cm
    public static String getHeightIncm(int metricValue) {
        String mv = String.valueOf(metricValue);
        if (mv != null) {

            return (mv + " cm");

        } else return "120 cm";

    }


    public static boolean isEmail(String email) {

        Pattern pattern1 = Pattern
                .compile("^([a-zA-Z0-9_.-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

        Matcher matcher1 = pattern1.matcher(email);

        if (!matcher1.matches())
            return false;
        return true;
    }

    public static String getExerciseValue(Context context, Workout.EXERCISE_TYPE exerciseTypeIndex) {
        String exerciseString = "";

        if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ACTIVITY_IOS) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ACTIVITY);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.OTHER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_OTHER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.RUNNING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_RUN);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CYCLING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BIKE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MOUNTAIN_BIKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MOUNTAIN_BIKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WALKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WALK);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.HIKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_HIKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.DOWNHILL_SKIING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_DOWNHILL_SKIING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CROSSCOUNTRY_SKIING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CROSS_COUNTRY_SKIING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SNOWBOARDING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SNOWBOARDING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SKATING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SKATING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SWIMMING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SWIM);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WHEELCHAIR) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WHEELCHAIR);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ROWING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ROWING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ELLIPTICAL) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ELLIPTICAL);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.NO_EXERCISE) {
            exerciseString = "";
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.YOGA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_YOGA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.PILATES) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_PILATES);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CROSSFIT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CROSSFIT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SPINNING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SPINNING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ZUMBA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ZUMBA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BARRE) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BARRE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.GROUP_WORKOUT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_GROUP_WORKOUT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.DANCE) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_DANCE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BOOTCAMP) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BOOTCAMP);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BOXING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BOXING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MMA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MMA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MEDITATION) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MEDITATION);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STRENGTH_TRAINING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STRENGTH_TRAINING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CIRCUIT_TRAINING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CIRCUIT_TRAINING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CORE_STRENGTHENING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CORE_STRENGTHENING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ARC_TRAINER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ARC_TRAINER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STAIR_MASTER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STAIRMASTER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STEPWELL) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STEPWELL);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.NORDIC_WALKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_NORDIC_WALKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SPORTS) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SPORTS);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WORKOUT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WORKOUT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.AQUAGYM) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_AQUAGYM);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.AQUAGYM) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_GOLF);
        } else {
            exerciseString = "Google Fit";
        }

        return exerciseString;
    }

    public static String getWeightDateFormat(Date date) {

        String localTime = "";
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getEditWeightDateFormat(Date date) {

        String localTime = "";
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static List<Weight> get3MWeightList(boolean initDate) {
        List<Weight> weightGraphDataArrayList = ApplicationEx.getInstance().weightList;
        ArrayList<Weight> weightGraphDataArrayList_3m = new ArrayList<Weight>();

        Hashtable<Date, Weight> weightDate = new Hashtable<Date, Weight>();

        Collections.sort(weightGraphDataArrayList, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        ArrayList<Date> dateList = new ArrayList(3);

        if (initDate) {
            cal.setTime(new Date());
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        }

        //include current date;
        dateList.add(cal.getTime());
        Weight dummyWeight = new Weight();
        dummyWeight.start_datetime = cal.getTime();
        dummyWeight.currentWeightGrams = 0;
        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 3; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new Weight();
            dummyWeight.start_datetime = calValid.getTime();
            dummyWeight.currentWeightGrams = 0;
            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Weight weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(weight.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (weight.start_datetime.before(cal.getTime()) && weight.start_datetime.after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_3m.add(weightDate.get(date_list));
        }

        return weightGraphDataArrayList_3m;
    }

    public static List<Weight> get1MWeightList(boolean initDate, int dateIndex) {

        ArrayList<Weight> weightGraphDataArrayList_1m = new ArrayList<Weight>();
        List<Weight> weightGraphDataArrayList = ApplicationEx.getInstance().weightList;
        Hashtable<Date, Weight> weightDate = new Hashtable<Date, Weight>();
        ArrayList<Date> dateList;

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());
            dateList = new ArrayList<>(31);
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            calValid.set(Calendar.MONTH, calValid.get(Calendar.MONTH));
            calValid.set(Calendar.DAY_OF_MONTH, calValid.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateList = new ArrayList<>(cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        }
        //include current date;
        dateList.add(cal.getTime());

        Weight dummyWeight = new Weight();
        dummyWeight.start_datetime = cal.getTime();
        dummyWeight.currentWeightGrams = 0;

        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 31; i++) {
            calValid.add(Calendar.DAY_OF_MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new Weight();
            dummyWeight.start_datetime = calValid.getTime();
            dummyWeight.currentWeightGrams = 0;

            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Weight weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(weight.start_datetime);
            int dayIndex = calIndex.get(Calendar.DAY_OF_MONTH);

            if (weight.start_datetime.before(cal.getTime()) && weight.start_datetime.after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_1m.add(weightDate.get(date_list));
        }

        Collections.sort(weightGraphDataArrayList_1m, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        return weightGraphDataArrayList_1m;
    }

    /**
     * @return list of Weight data in a year
     * must be 12 items or less
     * return the latest weight recorderd in the month
     **/
    public static List<Weight> get1YWeightList(boolean initDate, int dateRangeIndex) {
        List<Weight> weightGraphDataArrayList = ApplicationEx.getInstance().weightList;
        Hashtable<Date, Weight> weightDate = new Hashtable<Date, Weight>();
        ArrayList<Date> dateList = dateList = new ArrayList<>(12);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());

        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR));

            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            calValid.set(Calendar.YEAR, calValid.get(Calendar.YEAR));
            calValid.set(Calendar.MONTH, Calendar.DECEMBER);
        }

        dateList.add(cal.getTime());

        //include current date;

        Weight dummyWeight = new Weight();
        dummyWeight.start_datetime = cal.getTime();
        dummyWeight.currentWeightGrams = 0;
        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 12; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new Weight();
            dummyWeight.start_datetime = calValid.getTime();
            dummyWeight.currentWeightGrams = 0;
            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Weight weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(weight.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (weight.start_datetime.before(cal.getTime()) && weight.start_datetime.after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        ArrayList<Weight> weightGraphDataArrayList_1y = new ArrayList<Weight>();
        for (Date date_list : dateList) {
            weightGraphDataArrayList_1y.add(weightDate.get(date_list));
        }

        return weightGraphDataArrayList_1y;
    }

    public static List<Weight> getAllWeightList() {
        List<Weight> weightGraphDataArrayList = ApplicationEx.getInstance().weightList;
        Weight oldestWeight = AppUtil.getOldestWeight();

        Collections.sort(weightGraphDataArrayList, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        ArrayList<Weight> weightGraphDataArrayList_all = new ArrayList<Weight>();

        Hashtable<Date, Weight> weightDate = new Hashtable<Date, Weight>();
        ArrayList<Date> dateList = new ArrayList<>();

        //newest date
        Calendar calLatest = Calendar.getInstance();
        calLatest.setTime(new Date());

        //oldest weight
        Calendar calValid = Calendar.getInstance();
        if (oldestWeight.start_datetime != null) {
            calValid.setTime(oldestWeight.start_datetime);
        } else {
            calValid.setTime(new Date());
        }

        //present year only
        if (calValid.get(Calendar.YEAR) == calLatest.get(Calendar.YEAR)) {
            return get1YWeightList(true, 0);
        }


        dateList.add(calLatest.getTime());

        Weight dummyWeight = new Weight();
        dummyWeight.start_datetime = calLatest.getTime();
        dummyWeight.currentWeightGrams = 0;
        weightDate.put(calLatest.getTime(), dummyWeight);

        int monthsDiff = getMonthsDifference(calValid.getTime(), calLatest.getTime());

        for (int i = 1; i < monthsDiff; i++) {
            calLatest.add(Calendar.MONTH, -1);
            dateList.add(calLatest.getTime());

            dummyWeight = new Weight();
            dummyWeight.start_datetime = calLatest.getTime();
            dummyWeight.currentWeightGrams = 0;
            weightDate.put(calLatest.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Weight weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(weight.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);
            int yearIndex = calIndex.get(Calendar.YEAR);

            if (weight.start_datetime.before(new Date()) && weight.start_datetime.after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH) && yearIndex == calIndex_date.get(Calendar.YEAR)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_all.add(weightDate.get(date_list));
        }
        return weightGraphDataArrayList_all;
    }

    public static Weight getOldestWeight() {
        List<Weight> weightGraphDataArrayList = ApplicationEx.getInstance().weightList;
        Weight oldestWeight = new Weight();

        if (weightGraphDataArrayList.size() > 0) {
            oldestWeight = weightGraphDataArrayList.get(0);
        }

        for (Weight weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(weight.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (weight.start_datetime.before(oldestWeight.start_datetime)) {
                oldestWeight = weight;
            }
        }

        return oldestWeight;
    }

    public static List<Weight> getWeightLogsList() {

        List<Weight> weightGraphDataArrayList = new ArrayList<Weight>();
        weightGraphDataArrayList = ApplicationEx.getInstance().weightList;

        Collections.sort(weightGraphDataArrayList, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        return weightGraphDataArrayList;
    }

    public static boolean isWeightDataHistory1Year() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        List<Weight> weightGraphDataArrayList = new ArrayList<Weight>();
        weightGraphDataArrayList = ApplicationEx.getInstance().weightList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = weightGraphDataArrayList.get(i).start_datetime;

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.MONTH);

            if (yearIndex == year - 1) {
                if (monthIndex <= month) {
                    return true;
                } else {
                    isTrue = false;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex == 1 && month == 12) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static boolean isWeightDataHistory3MonthsMore() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        int month3less = (month - 2) % 12;

        List<Weight> weightGraphDataArrayList = new ArrayList<Weight>();
        weightGraphDataArrayList = ApplicationEx.getInstance().weightList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = weightGraphDataArrayList.get(i).start_datetime;

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.YEAR);

            //01 2016
            //11 2015
            //9 2015

            if (yearIndex == year - 1) {
                if (monthIndex >= month3less) {
                    return true;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex >= month3less) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static boolean isWeightDataHistory3MonthsLess() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        int month3less = (month - 2) % 12;

        List<Weight> weightGraphDataArrayList = new ArrayList<Weight>();
        weightGraphDataArrayList = ApplicationEx.getInstance().weightList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = weightGraphDataArrayList.get(i).start_datetime;

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.YEAR);
            if (yearIndex == year - 1) {
                if (monthIndex < month3less) {
                    return true;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex < month3less) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String get1MDateRangeDisplay(boolean initDate, boolean previous, int index) {
        String stringDisplay = "";

        Calendar cal = Calendar.getInstance();

        if (ApplicationEx.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
        }

        if (previous) {
            cal.add(Calendar.MONTH, -1);
        } else {
            cal.add(Calendar.MONTH, 1);
        }

        if (initDate) {
            stringDisplay = new SimpleDateFormat("MMM dd").format(new Date());
            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + stringDisplay;
            ApplicationEx.getInstance().currentDateRangeDisplay_date = new Date();

        } else {
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime());
            ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
        }

        ApplicationEx.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    private static boolean fromInitDate = false;

    public static String get1WDateRangeDisplay(boolean initDate, boolean previous) {

        String stringDisplay = "";
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        if (ApplicationEx.getInstance().currentDateRangeDisplay_date == null || initDate) {
            fromInitDate = true;
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -6);

            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + new SimpleDateFormat("MMM dd").format(new Date());
            //store previous month
            ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationEx.getInstance().currentDateRangeDisplay_date2 = new Date();

        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            cal2.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);

            if (previous) {
                if (!fromInitDate) {
                    cal.add(Calendar.WEEK_OF_YEAR, -1);
                    cal2.add(Calendar.WEEK_OF_YEAR, -1);
                } else {
                    fromInitDate = false;
                }

            } else {
                cal.add(Calendar.WEEK_OF_YEAR, +1);
                cal2.add(Calendar.WEEK_OF_YEAR, +1);
            }

            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            stringDisplay = new SimpleDateFormat("MMM dd").format(cal2.getTime());
            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + stringDisplay;

            //store previous month
            ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationEx.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();
        }
        ApplicationEx.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    public static String get3MDateRangeDisplay(boolean initDate, boolean previous, int index) {
        String stringDisplay = "";
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        if (ApplicationEx.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -2);

            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + new SimpleDateFormat("MMM yyyy").format(new Date());
            //store previous month
            ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationEx.getInstance().currentDateRangeDisplay_date2 = new Date();

        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            cal2.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            if (previous) {
                cal.add(Calendar.MONTH, -3);
                cal2.add(Calendar.MONTH, -3);
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal2.getTime());
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;

                //store previous month
                ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
                ApplicationEx.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();

            } else {
                cal.add(Calendar.MONTH, +3);
                cal2.add(Calendar.MONTH, +3);
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal2.getTime());
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;

                //store previous month
                ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
                ApplicationEx.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();
            }
        }

        ApplicationEx.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    public static String get1YDateRangeDisplay(boolean initDate, boolean previous) {
        String stringDisplay = "";

        Calendar cal = Calendar.getInstance();

        if (ApplicationEx.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);

            if (previous) {
                cal.add(Calendar.YEAR, -1);
            } else {
                cal.add(Calendar.YEAR, 1);
            }
        }

        if (initDate) {
            cal.add(Calendar.MONTH, -11);
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(new Date());
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;
            ApplicationEx.getInstance().currentDateRangeDisplay_date = new Date();

        } else {
            stringDisplay = new SimpleDateFormat("yyyy").format(cal.getTime());
            ApplicationEx.getInstance().currentDateRangeDisplay_date = cal.getTime();
        }

        ApplicationEx.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    public static final int getMonthsDifference(Date date1, Date date2) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(date1);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(date2);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }

    public static List<Steps> get1MStepsList(boolean initDate, int dateIndex) {

        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        ArrayList<Steps> stepsGraphDataArrayList_1m = new ArrayList<Steps>();
        List<Steps> stepsGraphDataArrayList = ApplicationEx.getInstance().stepsList;
        Hashtable<Date, Steps> stepsDate = new Hashtable<Date, Steps>();
        ArrayList<Date> dateList;

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());
            dateList = new ArrayList<>(31);
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            calValid.set(Calendar.MONTH, calValid.get(Calendar.MONTH));
            calValid.set(Calendar.DAY_OF_MONTH, calValid.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateList = new ArrayList<>(cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        }
        //include current date;
        dateList.add(cal.getTime());

        Steps dummySteps = new Steps();
        dummySteps.start_datetime = cal.getTime();
        dummySteps.steps_count = "0";

        stepsDate.put(cal.getTime(), dummySteps);

        for (int i = 1; i < 31; i++) {
            calValid.add(Calendar.DAY_OF_MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new Steps();
            dummySteps.start_datetime = calValid.getTime();
            dummySteps.steps_count = "0";

            stepsDate.put(calValid.getTime(), dummySteps);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Steps steps : stepsGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(steps.start_datetime);
            int dayIndex = calIndex.get(Calendar.DAY_OF_MONTH);

            if (steps.start_datetime.before(cal.getTime()) && steps.start_datetime.after((Date) dateList.get(0))) {
                totalSteps = totalSteps + Double.parseDouble(steps.steps_count);
                totalCalories = totalCalories + steps.steps_calories;
                totalKmTravelled = totalKmTravelled + steps.steps_distance;

                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_MONTH)) {
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            stepsGraphDataArrayList_1m.add(stepsDate.get(date_list));
        }

        Collections.sort(stepsGraphDataArrayList_1m, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        ApplicationEx.getInstance().currentTotalSteps = totalSteps;
        ApplicationEx.getInstance().currentKmTravelled = totalKmTravelled / 1000;
        ApplicationEx.getInstance().currentTotalCalories = totalCalories;

        return stepsGraphDataArrayList_1m;
    }

    public static List<Steps> getStepsLogsList() {

        List<Steps> stepsGraphDataArrayList = ApplicationEx.getInstance().stepsList;

        Collections.sort(stepsGraphDataArrayList, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        return stepsGraphDataArrayList;
    }

    /**
     * @return list of Steps data in a year
     * must be 12 items or less
     * return the latest weight recorded in the month
     **/
    public static List<Steps> get1YStepsList(boolean initDate, int dateRangeIndex) {
        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        List<Steps> stepsGraphDataArrayList = ApplicationEx.getInstance().stepsList;
        Hashtable<Date, Steps> stepsDate = new Hashtable<Date, Steps>();
        ArrayList<Date> dateList = dateList = new ArrayList<>(12);
        Hashtable<Date, Float> stepsTotalPerMonth = new Hashtable<Date, Float>();

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR));

            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date);
            calValid.set(Calendar.YEAR, calValid.get(Calendar.YEAR));
            calValid.set(Calendar.MONTH, Calendar.DECEMBER);
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        dateList.add(cal.getTime());
        //include current date;
        Steps dummySteps = new Steps();
        dummySteps.start_datetime = cal.getTime();
        dummySteps.steps_count = "0";
        stepsDate.put(cal.getTime(), dummySteps);
        stepsTotalPerMonth.put(cal.getTime(), 0f);

        for (int i = 1; i < 12; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new Steps();
            dummySteps.start_datetime = calValid.getTime();
            dummySteps.steps_count = "0";
            stepsDate.put(calValid.getTime(), dummySteps);
            stepsTotalPerMonth.put(calValid.getTime(), 0f);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Steps steps : stepsGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(steps.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (steps.start_datetime.before(cal.getTime()) && steps.start_datetime.after((Date) dateList.get(0))) {
                totalSteps = totalSteps + Double.parseDouble(steps.steps_count);
                totalCalories = totalCalories + steps.steps_calories;
                totalKmTravelled = totalKmTravelled + steps.steps_distance;

                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        stepsTotalPerMonth.put(date_list, stepsTotalPerMonth.get(date_list) + Float.parseFloat(steps.steps_count));
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }
        ArrayList<Steps> stepsGraphDataArrayList_1y = new ArrayList<Steps>();
        for (Date date_list : dateList) {
            dummySteps = new Steps();
            dummySteps.start_datetime = date_list;
            dummySteps.steps_count = Float.toString(stepsTotalPerMonth.get(date_list));
            stepsGraphDataArrayList_1y.add(dummySteps);
        }

        ApplicationEx.getInstance().currentTotalSteps = totalSteps;
        ApplicationEx.getInstance().currentKmTravelled = totalKmTravelled / 1000;
        ApplicationEx.getInstance().currentTotalCalories = totalCalories;

        return stepsGraphDataArrayList_1y;
    }

    public static List<Steps> get3MStepsList(boolean initDate) {
        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        List<Steps> stepsGraphDataArrayList = ApplicationEx.getInstance().stepsList;
        ArrayList<Steps> stepsGraphDataArrayList_3m = new ArrayList<Steps>();

        Hashtable<Date, Steps> stepsDate = new Hashtable<Date, Steps>();
        Hashtable<Date, Float> stepsTotalPerMonth = new Hashtable<Date, Float>();
        ArrayList<Date> dateList = new ArrayList<>(3);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        Collections.sort(stepsGraphDataArrayList, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        if (initDate) {
            cal.setTime(new Date());
//            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            calValid.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        //include current date;
        dateList.add(cal.getTime());
        Steps dummySteps = new Steps();
        dummySteps.start_datetime = cal.getTime();
        dummySteps.steps_count = "0";
        stepsDate.put(cal.getTime(), dummySteps);
        stepsTotalPerMonth.put(cal.getTime(), 0f);

        for (int i = 1; i < 3; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new Steps();
            dummySteps.start_datetime = calValid.getTime();
            dummySteps.steps_count = "0";
            stepsDate.put(calValid.getTime(), dummySteps);
            stepsTotalPerMonth.put(calValid.getTime(), 0f);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Steps steps : stepsGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(steps.start_datetime);
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (steps.start_datetime.before(cal.getTime()) && steps.start_datetime.after((Date) dateList.get(0))) {
                totalSteps = totalSteps + Double.parseDouble(steps.steps_count);
                totalCalories = totalCalories + steps.steps_calories;
                totalKmTravelled = totalKmTravelled + steps.steps_distance;

                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);

                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        stepsTotalPerMonth.put(date_list, stepsTotalPerMonth.get(date_list) + Float.parseFloat(steps.steps_count));
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            dummySteps = new Steps();
            dummySteps.start_datetime = date_list;
            dummySteps.steps_count = Float.toString(stepsTotalPerMonth.get(date_list));

            stepsGraphDataArrayList_3m.add(dummySteps);
        }

        ApplicationEx.getInstance().currentTotalSteps = totalSteps;
        ApplicationEx.getInstance().currentKmTravelled = totalKmTravelled / 1000;
        ApplicationEx.getInstance().currentTotalCalories = totalCalories;

        return stepsGraphDataArrayList_3m;
    }

    public static List<Steps> get1WStepsList(boolean initDate) {
        List<Steps> stepsGraphDataArrayList = ApplicationEx.getInstance().stepsList;
        ArrayList<Steps> stepsGraphDataArrayList_1w = new ArrayList<>();

        Hashtable<Date, Steps> stepsDate = new Hashtable<>();
        ArrayList<Date> dateList = new ArrayList<>(7);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        Collections.sort(stepsGraphDataArrayList, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o1.start_datetime.compareTo(o2.start_datetime);
            }
        });

        if (initDate) {
            cal.setTime(new Date());
            calValid.setTime(cal.getTime());
        } else {
            cal.setTime(ApplicationEx.getInstance().currentDateRangeDisplay_date2);
            calValid.setTime(cal.getTime());
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        //include current date;
        dateList.add(cal.getTime());

        Steps dummySteps = new Steps();
        dummySteps.start_datetime = cal.getTime();
        dummySteps.steps_count = "0";
        stepsDate.put(cal.getTime(), dummySteps);

        for (int i = 1; i < 7; i++) {
            calValid.add(Calendar.DAY_OF_YEAR, -1);
            dateList.add(calValid.getTime());

            dummySteps = new Steps();
            dummySteps.start_datetime = calValid.getTime();
            dummySteps.steps_count = "0";
            stepsDate.put(calValid.getTime(), dummySteps);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (Steps steps : stepsGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(steps.start_datetime);
            int dayIndex = calIndex.get(Calendar.DAY_OF_YEAR);

            if (steps.start_datetime.before(cal.getTime()) && steps.end_datetime.after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_YEAR)) {
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            stepsGraphDataArrayList_1w.add(stepsDate.get(date_list));
        }
        return stepsGraphDataArrayList_1w;
    }

    public static double getTotalSteps(List<Steps> stepsList) {

        double stepstTotal = 0.0;

        for (Steps steps : stepsList) {
            stepstTotal = stepstTotal + Double.parseDouble(steps.steps_count);
        }
        return stepstTotal;
    }

    public static double getTotalKmTravelled(List<Steps> stepsList) {

        double kmTravelled = 0.0;

        for (Steps steps : stepsList) {
            kmTravelled = kmTravelled + steps.steps_distance;
        }
        return kmTravelled / 1000.0;
    }

    public static double getTotalCalories(List<Steps> stepsList) {

        double totalCalories = 0.0;

        for (Steps steps : stepsList) {
            totalCalories = totalCalories + steps.steps_calories;
        }
        return totalCalories;
    }

    public static double getHeighestSteps(List<Steps> stepsList) {
        double heighestSteps = 0.0;

        for (Steps steps : stepsList) {
            double steps_count = Double.parseDouble(steps.steps_count);

            heighestSteps = steps_count > heighestSteps ? steps_count : heighestSteps;
        }
        return heighestSteps;
    }

    public static double getLowestSteps(List<Steps> stepsList) {
        double lowestSteps = 0.0;

        //do not include zero
        if (stepsList.size() > 0) {
            //get lowest with value except 0
            for (Steps steps : stepsList) {
                double steps_count = Double.parseDouble(steps.steps_count);
                if (steps_count > 0) {
                    lowestSteps = steps_count;
                    break;
                }
            }

            for (Steps steps : stepsList) {
                double steps_count = Double.parseDouble(steps.steps_count);
                if (steps_count > 0) {
                    lowestSteps = steps_count < lowestSteps ? steps_count : lowestSteps;
                }
            }
        }
        return lowestSteps;
    }

    public static double getHeighestWeight(List<Weight> weightList) {
        double heighestWeight = 0.0;

        for (Weight weight : weightList) {
            heighestWeight = weight.currentWeightGrams > heighestWeight ? weight.currentWeightGrams : heighestWeight;
        }
        return heighestWeight;
    }

    public static double getLowestWeight(List<Weight> weightList) {
        double lowestWeight = 0.0;

        //do not include zero
        if (weightList.size() > 0) {
            //get lowest with value except 0
            for (Weight weight : weightList) {
                if (weight.currentWeightGrams > 0) {
                    lowestWeight = weight.currentWeightGrams;
                    break;
                }
            }

            for (Weight weight : weightList) {
                if (weight.currentWeightGrams > 0) {
                    lowestWeight = weight.currentWeightGrams < lowestWeight ? weight.currentWeightGrams : lowestWeight;
                }
            }
        }
        return lowestWeight;
    }

    public static String format(double number, int iteration) {
        char[] c = new char[]{'k', 'm', 'b', 't'};

        double d = ((long) number / 100) / 10.0;
        boolean isRound = (d * 10) % 10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000 ? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99) ? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : format(d, iteration + 1));
    }

    public static boolean isDateFromSync(Date selectedDate) {

        List<String> dateStringList = ApplicationEx.getInstance().fromDateGetSync;
        List<Date> dateList = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(dateStringList);
        dateStringList.clear();
        dateStringList.addAll(hashSet);

        for (String dateString : dateStringList) {
            //convert string to date
            Date newDate = new Date();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                newDate = df.parse(dateString);
                String newDateString = df.format(newDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            dateList.add(newDate);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        if (dateList.size() > 0 && dateList != null) {
            if (selectedDate.before(dateList.get(0))) {
                return true;
            }
        }
        return false;
    }

    public static String createSignature(String input, String sharedKey) {
        String signature = "";
        String hashInput = input + sharedKey;

        try {
            signature = AppUtil.SHA1(hashInput);
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return signature;
    }

    public static int getTotalMealsSinceRegDate(long regDate) {

        int dayDiff = getDayDifference(toDate(regDate), new Date());
        int totalMeals = dayDiff * 3;

        if (getHour() >= 9) {
            if (getHour() >= 14) {
                if (getHour() >= 21)
                    totalMeals = totalMeals + 1;

                totalMeals = totalMeals + 1;
            }
            totalMeals = totalMeals + 1;
        }

        return totalMeals;
    }

    public static int getTotalMealsToday(long regDate) {
        int dayDiff = getDayDifference(toDate(regDate), new Date());
        System.out.println("dayDiff: " + dayDiff);

        int totalMeals = (dayDiff + 1) * 3;

        return totalMeals;
    }

    public static String getTotalMealsDisplayString(Context context, long regDate) {
        String displayString = "";

//        Eng: 87 meals since your registration on January 1st, 2017
//        French: 87 repas depuis votre inscription le 1er janvier 2017

        context.getResources().getString(R.string.PROGRESS_MEALS_REGISTRATION);

        if (ApplicationEx.getInstance().language.equalsIgnoreCase("fr")) {
            displayString = getDateDisplayinProgressFR(regDate);
        } else {
            displayString = getDateDisplayinProgress(regDate);
        }

        displayString = getTotalMealsSinceRegDate(regDate) + " " + context.getResources().getString(R.string.PROGRESS_MEALS_REGISTRATION) + " " +
                displayString;

        return displayString;
    }

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    private static String getDateDisplayinProgress(long millisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        Date dt = new Date();
        dt.setTime(millisec * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d%@, yyyy");

        String temp = sdf.format(dt);

        temp = temp.replace("%@", getDayNumberSuffix(c.get(Calendar.DAY_OF_MONTH)));

        return temp;
    }

    private static String getDateDisplayinProgressFR(long millisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        Date dt = new Date();
        dt.setTime(millisec * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("%@ MMMM yyyy");

        String temp = sdf.format(dt);

        if (c.get(Calendar.DAY_OF_MONTH) == 1) {
            temp = temp.replace("%@", Calendar.DAY_OF_MONTH + "er");
        } else {
            temp = temp.replace("%@", Calendar.DAY_OF_MONTH + "e");
        }

        System.out.println("getDateDisplayinProgressFR: " + temp);

        return temp;
    }

    public static List<String> getDayArrayMeals() {

        List<String> dayArray = new ArrayList<>();
        String dayData;
        long currentDate = new Date().getTime();

        for (int i = 0; i < 5; i++) {
            dayData = new SimpleDateFormat("MMM dd").format(currentDate - i * 24 * 3600 * 1000l);
            dayArray.add(dayData);
        }

        Collections.reverse(dayArray);
        return dayArray;
    }

    public static int getEndOffsetMeal(Context context, String displayString) {
        return displayString.indexOf(context.getString(R.string.PROGRESS_MEALS_REGISTRATION));
    }

    public static String doubleToString(Double doubleValue) {
        NumberFormat nf = DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        return nf.format(doubleValue);
    }

    public static List<Object> getCommunityComments(List<Comment> allComments) {
        List<Object> communityComments = new ArrayList<Object>();

        for (Comment comment : allComments) {
            //community
            if (comment.comment_type == 0)
                communityComments.add(comment);
        }

        return communityComments;
    }

    public static List<Object> getAllComments(List<Comment> allComments) {
        List<Object> communityComments = new ArrayList<Object>();

        for (Comment comment : allComments) {
            //community
            communityComments.add(comment);
        }

        return communityComments;
    }

    public static List<Object> getCoachComments(List<Comment> allComments) {
        List<Object> coachComments = new ArrayList<Object>();

        for (Comment comment : allComments) {
            //community
            if (comment.comment_type != 0)
                coachComments.add(comment);
        }

        return coachComments;
    }

    public static String getDeviceId(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            return deviceId;
        } else {
            return android.os.Build.SERIAL;
        }
    }

    public static Date getStepsCurrentTime() {

        //get time only for the dte format is HH:DD
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.setTimeZone(TimeZone.getDefault());

        return c.getTime();

    }

    public static Date getStartOfTheDay(Date date) {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        cal.setTime(date); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    public static Date getEndOfTheDay(Date date) {

        Calendar cal = Calendar.getInstance(TimeZone.getDefault());

        cal.setTime(date); // compute start of the day for the timestamp
        cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));

        return cal.getTime();
    }

//    public static long stepsDateToTimeStamp(Date date){
////        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        sourceFormat.setTimeZone(TimeZone.getDefault());
////        Date parsed = sourceFormat.parse("2011-03-01 15:10:37"); // => Date is in UTC now
//
//        TimeZone tz = TimeZone.getTimeZone("UTC");
//        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        destFormat.setTimeZone(tz);
//
//        String result = destFormat.format(date);
//
//        AppUtil.dateToUnixTimestamp(datee)
//
//    e

}