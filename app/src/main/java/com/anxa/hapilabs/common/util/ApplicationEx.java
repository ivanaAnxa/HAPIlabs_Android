package com.anxa.hapilabs.common.util;


import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.MainActivity;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;
import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.fabric.sdk.android.Fabric;


public class ApplicationEx extends Application {

    public static final int REQUESTCODE_MEALADD = 199;
    public static final int REQUESTCODE_MEALEDIT = 198;
    public static final int REQUESTCODE_MEALDELETE = 197;
    public static final int REQUESTCODE_MEALVIEW = 196;
    public static final int REQUESTCODE_WORKOUTADD = 299;
    public static final int REQUESTCODE_WORKOUTEDIT = 298;
    public static final int REQUESTCODE_WORKOUTDELETE = 297;
    public static final int REQUESTCODE_WORKOUTVIEW = 296;
    //push notification
    public static final int DEFAULT_RES_ICONS = R.drawable.icon;
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_SYNC_TODATE = "sync_todate";
    public static final String PROPERTY_SYNC_FROMDATE = "sync_fromdate";
    public static final String PROPERTY_APP_LOGIN = "isLogin";
    public static final String PROPERTY_APP_GOOGLE_FIT_ALLOWED = "isGoogleFitAllowed";
    public static final String PROPERTY_APP_FROM_PUSH = "isFromPush";
    public static final String PROPERTY_APP_FIRSTLAUNCH = "isFirstLaunch";
    private static final String PROPERTY_APP_LOGIN_USERNAME = "userName";
    private static final String PROPERTY_APP_LOGIN_PASSWORD = "password";

    public static final String PROPERTY_SYNCNOTIF_TODATE = "syncnotif_todate";
    public static final String PROPERTY_APP_LASTSESSION = "lastsession";
    public static final String PROPERTY_MEAL_REMINDER_BREAKFAST = "08:00";
    public static final String PROPERTY_MEAL_REMINDER_LUNCH = "13:00";
    public static final String PROPERTY_MEAL_REMINDER_DINNER = "20:00";
    public static final String PROPERTY_MEAL_REMINDER_SETTING_BREAKFAST = "ReminderSettingBF";
    public static final String PROPERTY_MEAL_REMINDER_SETTING_LUNCH = "ReminderSettingLunch";
    public static final String PROPERTY_MEAL_REMINDER_SETTING_DINNER = "ReminderSettingDinner";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PROPERTY_USER_ID = "user_id";
    public static String sharedKey = "An39 C046h 809ile";//hapifork
    public static String sharedUserId;
    //weblink url
//    public static String myProfile_url = WebServices.URL.qcDomainURLString + "webkit/v1/profile?userid=";
//    public static String myWeight_url = WebServices.URL.qcDomainURLString + "webkit/v1/weightgraph?userid=";
//    public static String mySubs_url = WebServices.URL.qcDomainURLString + "webkit/v1/subscription?userid=";
//    public static String myResults_url = WebServices.URL.qcDomainURLString + "webkit/v1/weightgraph?userid=";
//    public static String about_about_url = WebServices.URL.qcDomainURLString + "webkit/v1/help/about";
//    public static String about_terms_url = WebServices.URL.qcDomainURLString + "webkit/v1/help/termsofservice";
//    public static String about_privacy_url = WebServices.URL.qcDomainURLString + "webkit/v1/help/privacypolicy";
//    public static String about_contacts_url = WebServices.URL.qcDomainURLString + "webkit/v1/help/contactus";
    public static String language = Locale.getDefault().getLanguage();
    public static String username = "";
    private static ApplicationEx instance = null;
    public AnxaMatsJobControlller anxaMatsController;
    public String customAgent;
    public boolean isLoginSync = true;
    public boolean isFirstLaunch = true;
    public String defaultTimeFormat = "yyyy-MM-dd";
    public String defaultTimeFormat2 = "yyyy-MM-dd hh:mm:ss";
    public Date toDateCurrent;
    public Date fromDateCurrent;
    public Boolean isCalendarSelected;

    public Date currentSelectedDate = new Date();
    public Date toDateSyncCurrent;
    public Date fromDateSyncCurrent;
    public long fromDateNotificationSync;
    public boolean hasWelcome = false;
    public String SENDER_ID = "917695631036"; // project number for hapilabs test App added
    public String GCM_ANXAPUNC_SHAREDKEY = "0YeMPngXK8B3x1nQGJKR";
    public int screenWidth;
    public int screenHeight;
    public int maxWidthImage; //for list meals dynamic sizing
    public int maxheightImage;//for list meals dynamic sizing
    public int maxWidthCameraView; //for list camera dynamic sizing
    public int maxHeightCameraView;//for list camera dynamic sizing
    public int unreadNotifications = 0;
    public UserProfile userProfile;
    public BitmapFactory.Options options_Avatar = new BitmapFactory.Options();
    public BitmapFactory.Options options_Profile = new BitmapFactory.Options();
    public Meal currentMealView;
    public Workout currentWorkoutView;
    public HapiMoment currentHapiMoment;
    public Water currentWater;
    public CommentUser currentCommunityUser;
    public Weight latestWeight;
    public Steps latestSteps;
    public double currentTotalSteps;
    public double currentTotalCalories;
    public double currentKmTravelled;
    public int selectedProgressMenu;
    public int selectedCalendarDay;
    public Weight currentWeightView;
    public Steps currentStepsView;

    public boolean googleFitAllowed = false;

    //dummy content
    public Hashtable<String, Meal> mealsToAdd = new Hashtable<String, Meal>();
    public Hashtable<String, Meal> tempList = new Hashtable<String, Meal>();
    public Hashtable<String, HapiMoment> tempHapimomentList = new Hashtable<String, HapiMoment>();
    public Hashtable<String, Meal> allMealList = new Hashtable<String, Meal>();
    public Hashtable<String, Meal> allHealthyMealList = new Hashtable<String, Meal>();
    public Hashtable<String, Meal> allOKMealList = new Hashtable<String, Meal>();
    public Hashtable<String, Meal> allUnhealthyList = new Hashtable<String, Meal>();
    public List<Water> waterList = new ArrayList<>();
    public List<Workout> workouts = new ArrayList<>();
    public Hashtable<String, Workout> workoutList = new Hashtable<String, Workout>();
    public List<HapiMoment> hapiMomentList = new ArrayList<>();
    public List<Weight> weightList = new ArrayList<>();
    public List<Steps> stepsList = new ArrayList<>();
    public Weight currentWeight;
    public Steps currentSteps;
    public Hashtable<String, Object> messageList = new Hashtable<String, Object>();
    public Hashtable<String, Notification> notificationList = new Hashtable<String, Notification>();
    public int pushGCMIntentIndex = 0;
    public List<PendingIntent> pushGCMIntent = new ArrayList<PendingIntent>();
    public Coach selectedCoach;
    public String currentDateRangeDisplay;
    public Date currentDateRangeDisplay_date;
    public Date currentDateRangeDisplay_date2;
    public int selectedTabIndex;
    public List<String> fromDateGetSync = new ArrayList<>();

    public long registrationDate;
    public int totalMeals;

    public String currentFBIdUser;
    public boolean userRatingSetting = false;

    public AlarmManager alarmMgr = null;
    public PendingIntent alarmIntent[] = new PendingIntent[9];
    public boolean fromFBConnect;


    public ApplicationEx() {
        super();

        userProfile = new UserProfile();
        options_Avatar.inSampleSize = 2;
        options_Profile.inSampleSize = 2;
    }

    public static ApplicationEx getInstance() {
        return instance;
    }

    public void getWindowDimension(Context ctx) {


        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;


        float imagepercentW = 0.85f;
        float imagepercentH = 0.30f;


        maxWidthImage = (int) (Math.round(screenWidth * imagepercentW));
        maxheightImage = (int) (Math.round(screenHeight * imagepercentH));


        imagepercentW = 0.8f;
        imagepercentH = 0.3f;

        maxWidthCameraView = (int) (Math.round(screenWidth * imagepercentW));
        maxHeightCameraView = (int) (Math.round(screenHeight * imagepercentH));

        //createAllTables(this);

        toDateSyncCurrent = getToDate(this);
        fromDateSyncCurrent = fromDate(this);

    }

    public SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public void setSyncDate(Date fromDate, Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationEx.PROPERTY_SYNC_FROMDATE, AppUtil.getDateinUTC(fromDate));
        editor.commit();

    }

    public void setSyncDate(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationEx.PROPERTY_SYNC_FROMDATE, null);
        editor.commit();

    }

    public Date getToDate(Context context) {
        return AppUtil.getCurrentDateinDate();
    }

    public Date fromDate(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);

//        String tempStr = prefs.getString(ApplicationEx.PROPERTY_SYNC_FROMDATE, "");

//        if (tempStr == null || tempStr.isEmpty()) {
        int addDays = -7; //get the first 7 days
//            int addDays = -45; //get the first 45 days
        return AppUtil.getCurrentDate(addDays);
//        }
//        return AppUtil.stringToDateFormat(tempStr);
    }

    public void setIsLogin(Context context, Boolean isLogin) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ApplicationEx.PROPERTY_APP_LOGIN, isLogin);

        editor.commit();
    }

    public boolean isLogin(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean(ApplicationEx.PROPERTY_APP_LOGIN, false);
    }

    public void setFromPush(Context context, Boolean isFromPush) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ApplicationEx.PROPERTY_APP_FROM_PUSH, isFromPush);

        editor.commit();
    }

    public boolean isFromPush(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean(ApplicationEx.PROPERTY_APP_FROM_PUSH, false);
    }

    public void saveLoginCredentials(Context context, String username, String password){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_APP_LOGIN_PASSWORD, password);
        editor.putString(PROPERTY_APP_LOGIN_USERNAME, username);
        editor.commit();
    }

    public void clearLoginCredentials(){
        //just the password
        //called when user logged out
        final SharedPreferences prefs = getGCMPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_APP_LOGIN_PASSWORD, "");
        editor.commit();
    }


    public String getSavedUserName(Context context){
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString(PROPERTY_APP_LOGIN_USERNAME, "");
    }

    public void setIsFirstLaunch(Context context, Boolean isLogin) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ApplicationEx.PROPERTY_APP_FIRSTLAUNCH, isLogin);
        editor.commit();

    }

    public boolean isFirstLaunch(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);

        return prefs.getBoolean(ApplicationEx.PROPERTY_APP_FIRSTLAUNCH, false);

    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        instance = this;

        String ver = getResources().getString(R.string.app_version);
        customAgent = getResources().getString(R.string.app_name) + ver;
    }

    public void setAllMealsRated(int mealsRated)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("all_meals_rated",mealsRated);
        editor.commit();
    }

    public int getAllMealsRated()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("all_meals_rated", 0);
    }

    public void setAllHealthyMealsRated(int mealsRated)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("healthy_meals_rated",mealsRated);
        editor.commit();
    }

    public int getAllHealthyMealsRated()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("healthy_meals_rated", 0);
    }

    public void setAllJustOkMealsRated(int mealsRated)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("justok_meals_rated",mealsRated);
        editor.commit();
    }

    public int getAllJustOkMealsRated()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("justok_meals_rated", 0);
    }

    public void setAllUnhealthyMealsRated(int mealsRated)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("unhealthy_meals_rated",mealsRated);
        editor.commit();
    }

    public int getAllUnhealthyMealsRated()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("unhealthy_meals_rated", 0);
    }

    public void resetAllMealsCurrentPage()
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allMealsCurrentPage",1);
        editor.commit();
    }

    public void setAllMealsCurrentPage(int currentPage)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allMealsCurrentPage",currentPage+1);
        editor.commit();
    }

    public int getAllMealsCurrentPaged()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("allMealsCurrentPage", 0);
    }

    public void resetAllHealthyMealsCurrentPage()
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allHealthyMealsCurrentPage",1);
        editor.commit();
    }

    public void setAllHealthyMealsCurrentPage(int currentPage)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allHealthyMealsCurrentPage",currentPage+1);
        editor.commit();
    }

    public int getAllHealthyMealsCurrentPaged()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("allHealthyMealsCurrentPage", 0);
    }

    public void resetAllOkMealsCurrentPage()
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allJustOkMealsCurrentPage",1);
        editor.commit();
    }

    public void setAllOkMealsCurrentPage(int currentPage)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allJustOkMealsCurrentPage",currentPage+1);
        editor.commit();
    }

    public int getAllOkMealsCurrentPaged()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("allJustOkMealsCurrentPage", 0);
    }

    public void resetAllUnhealthyMealsCurrentPage()
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allUnhealthyMealsCurrentPage",1);
        editor.commit();
    }

    public void setAllUnhealthyMealsCurrentPage(int currentPage)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("allUnhealthyMealsCurrentPage",currentPage+1);
        editor.commit();
    }

    public int getAllUnhealthyMealsCurrentPaged()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getInt("allUnhealthyMealsCurrentPage", 0);
    }

    public void setNoMoreContentsToShowForMealRating(boolean value)
    {
        final SharedPreferences prefs = getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("noMoreContentsToShowForMealRating",value);
        editor.commit();
    }

    public boolean getNoMoreContentsToShowForMealRating()
    {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE).getBoolean("noMoreContentsToShowForMealRating",false);
    }

    public void setGoogleFitAllowed(Context context, Boolean isAllowed) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ApplicationEx.PROPERTY_APP_GOOGLE_FIT_ALLOWED, isAllowed);

        editor.commit();
    }

    public boolean isGoogleFitAllowed(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean(ApplicationEx.PROPERTY_APP_GOOGLE_FIT_ALLOWED, false);
    }
}