package com.anxa.hapilabs.activities;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.controllers.progress.GetStepsDataController;
import com.anxa.hapilabs.controllers.progress.GetWeightDataController;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Weight;
import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.GetMealListener;
import com.anxa.hapilabs.common.connection.listener.GetMealUpdateListener;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.connection.listener.GetTimelineActivityListener;
import com.anxa.hapilabs.common.connection.listener.GetWaterListener;
import com.anxa.hapilabs.common.connection.listener.GetWorkoutListener;
import com.anxa.hapilabs.common.connection.listener.PremiumAccessListener;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.exercise.GetWorkoutController;
import com.anxa.hapilabs.controllers.hapimoment.GetHapiMomentController;
import com.anxa.hapilabs.controllers.mymeals.GetMealController;
import com.anxa.hapilabs.controllers.notifications.NotificationController;
import com.anxa.hapilabs.controllers.timelineactivity.GetTimelineActivityController;
import com.anxa.hapilabs.controllers.water.GetWaterController;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_STATE;
import com.anxa.hapilabs.models.Notification.NOTIFICATION_TYPE;
import com.anxa.hapilabs.models.TimelineActivity;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CustomListView;
import com.anxa.hapilabs.ui.adapters.NotificationListAdapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class NotificationActivity extends HAPIActivity implements GetNotificationListener, OnClickListener, OnMenuItemClickListener, GetHapiMomentListener, GetWorkoutListener, GetMealListener,
        GetTimelineActivityListener, GetWaterListener, WeightDataListener, StepsDataListener {

    private NotificationDAO notifDAO;
    private NotificationListAdapter adapter;
    private NotificationController notifController;
    private GetHapiMomentController getHapiMomentController;
    private GetWeightDataController getWeightDataController;
    private GetStepsDataController getStepsDataController;
    private GetWorkoutController getWorkoutController;
    private List<Notification> notificationList;

    private PopupMenu popupMenu;
    private final static int ONE = 1;
    private final static int TWO = 2;
    private final static int THREE = 3;

    private String fromPush = "";

    private String entryType;

    private CustomListView notifListView;
    private boolean isFrom3rdParty = false;
    private RelativeLayout dummyView;
    private int workout_id;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notificationsview);

        updateHeader(12, getString(R.string.NOTIFICATIONS), this);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            fromPush = extras.getString("fromPush");
        }

        dummyView = (RelativeLayout) findViewById(R.id.dummy_view);
        dummyView.setVisibility(View.GONE);
        //if not logged in
        if (!ApplicationEx.getInstance().isLogin(this) || ApplicationEx.getInstance().userProfile.getRegID() == null) {
            Intent mainIntent;
            mainIntent = new Intent(NotificationActivity.this, LandingScreenActivity.class);

            if (fromPush != null && fromPush.equalsIgnoreCase("fromPush")) {
                ApplicationEx.getInstance().setFromPush(this, true);
                mainIntent.putExtra("fromPush", "fromPush");
            }
            this.startActivity(mainIntent);
        }

        notifListView = (CustomListView) findViewById(R.id.listview);
        notifDAO = new NotificationDAO(this, null);
        notificationList = new ArrayList<>(ApplicationEx.getInstance().notificationList.values());
        if (notificationList.size() > 0)
            sort(notificationList);

        if (adapter == null) {
            adapter = new NotificationListAdapter(this, notificationList, this);
        }
        notifListView.setAdapter(adapter);

        notifListView.setClickable(true);

        popupMenu = new PopupMenu(this, findViewById(R.id.header_right));
        popupMenu.getMenu().add(Menu.NONE, ONE, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_MARK_ALL_AS_READ));
        popupMenu.getMenu().add(Menu.NONE, TWO, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CLEAR_ALL));
        popupMenu.getMenu().add(Menu.NONE, THREE, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CANCEL));
        popupMenu.setOnMenuItemClickListener(this);
    }

    @Override
    public void onResume() {
        notifListView.setClickable(true);
        dummyView.setVisibility(View.GONE);

        isFrom3rdParty = false;
        //fetch new notification if any
        super.onResume();
        //make sure new notification
        startUpdate();
    }

    /**
     * OnClick Method
     **/
    @Override
    public void onClick(View v) {
        if (v == v.getRootView().findViewById(R.id.header_left)) {
            onBackPressed();

        } else if (v == v.getRootView().findViewById(R.id.header_right)) {
            popupMenu.show();
        } else {

            notifListView.setClickable(false);

            int notifId = (Integer) v.getTag(R.id.notif_id);
            //update notif
            Notification item = getNotificationByID(notifId);

            assert item != null;
            item.notificationState = NOTIFICATION_STATE.READ;

            updateNotifAsRead(item, Integer.toString(notifId));

            //go to click action
            if (item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MESSAGE_COACH) {
                dummyView.setVisibility(View.VISIBLE);

                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                mainIntent.putExtra("TAB", 2);// 1 meals 2//messages
                mainIntent.putExtra("FROM_NOTIF_INT", 1); //from notif
                startActivity(mainIntent);

            } else if (item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_EXERCISE) {
                dummyView.setVisibility(View.VISIBLE);

                System.out.println("ONCLICK NOTIF...NOTIFICATION_CHECKED_EXERCISE: " + item.ref_id);
                boolean notFound = true;

                for (Workout workout : ApplicationEx.getInstance().workoutList.values()) {
                    System.out.println("ONCLICK NOTIF...workoutId: " + workout.activity_id);

                    if (workout.activity_id.compareTo(item.ref_id) == 0) {

                        System.out.println("ONCLICK NOTIF FOUND ...workoutId: " + workout.activity_id);

                        notFound = false;
                        ApplicationEx.getInstance().currentWorkoutView = workout;

                        Intent mainIntent = new Intent(getBaseContext(), ExerciseViewActivity.class);
                        mainIntent.putExtra("FROM_NOTIF", true);
                        startActivity(mainIntent);

                        return;

                    } else {
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                        System.out.println("ONCLICK NOTIF... NOT FOUND");
                    }
                }
                if (notFound) {
                    getWorkoutDetails(item.ref_id);
                }


            } else if (item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_MOMENT) {
                dummyView.setVisibility(View.VISIBLE);

                System.out.println("ONCLICK NOTIF...NOTIFICATION_CHECKED_MOMENT: " + item.ref_id);

                boolean notFound = true;

                for (HapiMoment hapiMoment : ApplicationEx.getInstance().hapiMomentList) {
                    System.out.println("ONCLICK NOTIF...hapimoment id: " + hapiMoment.mood_id);

                    if (hapiMoment.mood_id == Integer.parseInt(item.ref_id)) {

                        notFound = false;
                        ApplicationEx.getInstance().currentHapiMoment = hapiMoment;
                        Intent mainIntent = new Intent(getBaseContext(), HapiMomentViewActivity.class);
                        mainIntent.putExtra("FROM_NOTIF", true);
                        startActivity(mainIntent);
                        return;

                    } else {
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                        System.out.println("ONCLICK NOTIF... NOT FOUND");
                    }
                }
                //hapimoment not in the database
                if (notFound) {
                    getHAPIMoment(item.ref_id);
                }

            } else if (item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_CHECKED_WATER) {
                dummyView.setVisibility(View.VISIBLE);

                System.out.println("ONCLICK NOTIF...NOTIFICATION_CHECKED_WATER: " + item.ref_id);

                boolean notFound = true;

                for (Water water : ApplicationEx.getInstance().waterList) {
                    System.out.println("ONCLICK NOTIF...water  id: " + water.water_id);
                    if (water.water_id.equalsIgnoreCase(item.ref_id)) {
                        notFound = false;

                        ApplicationEx.getInstance().currentWater = water;

                        Intent mainIntent = new Intent(getBaseContext(), WaterViewActivity.class);
                        mainIntent.putExtra("FROM_NOTIF", true);
                        mainIntent.putExtra("WATER_STATUS", "view");
                        startActivity(mainIntent);
                        return;

                    } else {
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                        System.out.println("ONCLICK NOTIF... NOT FOUND");
                    }
                }

                if (notFound) {
                    getHAPIWater(item.ref_id);
                }

            } else if (item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_COMMENT_COMMUNITY || item.notificationType == NOTIFICATION_TYPE.NOTIFICATION_MEAL_HAPI4U_COMMUNITY) {
                dummyView.setVisibility(View.VISIBLE);

                //check if comment from other post
                GetTimelineActivityController getTimelineActivityController = new GetTimelineActivityController(this, this, this);
                if (item.ref_type.equalsIgnoreCase("MealId")) {
                    entryType = "meal";
                } else if (item.ref_type.equalsIgnoreCase("WaterId")) {
                    entryType = "water";
                } else if (item.ref_type.equalsIgnoreCase("ActivityId")) {
                    if(item.coachMessage.toLowerCase().contains("weight")){
                        entryType = "weight";
                    }else if(item.coachMessage.toLowerCase().contains("steps")){
                        entryType = "steps";
                    }
                    else {
                        entryType = "exercise";
                    }
                } else {
                    entryType = "hapimoment";
                }
                if (item.is_community) {
                    //launch separate page
                    getTimelineActivityController.getTimelineActivity(ApplicationEx.getInstance().userProfile.getRegID(), item.ref_id, entryType);

                } else {
                    //launch your meal
                    boolean notFound = true;
                    isFrom3rdParty = true;

                    if (item.ref_type.equalsIgnoreCase("MealId")) {
                        for (Meal meal : ApplicationEx.getInstance().tempList.values()) {
                            if (meal.meal_id.compareTo(item.ref_id) == 0) {
                                notFound = false;
                                ApplicationEx.getInstance().currentMealView = meal;
                                Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);
                                mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", true);
                                mainIntent.putExtra("FROM_NOTIF", true);
                                startActivity(mainIntent);
                                return;

                            } else {
                                Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                                System.out.println("ONCLICK NOTIF... NOT FOUND");
                            }
                        }//end for

                        if (notFound) {
                            getMeal(item.ref_id);
                        }
                    } else if (item.ref_type.equalsIgnoreCase("MoodId")) {
                        notFound = true;

                        for (HapiMoment hapiMoment : ApplicationEx.getInstance().hapiMomentList) {
                            System.out.println("ONCLICK NOTIF...hapimoment id: " + hapiMoment.mood_id);

                            if (hapiMoment.mood_id == Integer.parseInt(item.ref_id)) {

                                notFound = false;
                                ApplicationEx.getInstance().currentHapiMoment = hapiMoment;
                                Intent mainIntent = new Intent(getBaseContext(), HapiMomentViewActivity.class);
                                mainIntent.putExtra("FROM_NOTIF", true);
                                mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", true);

                                if (item.is_community) {
                                    mainIntent.putExtra("FROM_NOTIF_COMMUNITY", true);
                                }

                                startActivity(mainIntent);
                                return;

                            } else {
                                Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                                System.out.println("ONCLICK NOTIF... NOT FOUND");
                            }
                        }
                        //hapimoment not in the database
                        if (notFound) {
                            getHAPIMoment(item.ref_id);
                        }
                    } else if (item.ref_type.equalsIgnoreCase("WaterId")) {
                        notFound = true;

                        for (Water water : ApplicationEx.getInstance().waterList) {
                            System.out.println("ONCLICK NOTIF...water id: " + water.water_id);

                            if (water.water_id == item.ref_id) {

                                notFound = false;
                                ApplicationEx.getInstance().currentWater = water;
                                Intent mainIntent = new Intent(getBaseContext(), WaterViewActivity.class);
                                mainIntent.putExtra("FROM_NOTIF", true);
                                mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", true);

                                if (item.is_community) {
                                    mainIntent.putExtra("FROM_NOTIF_COMMUNITY", true);
                                }

                                startActivity(mainIntent);
                                return;

                            } else {
                                Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                                System.out.println("ONCLICK NOTIF... NOT FOUND");
                            }
                        }
                        //hapimoment not in the database
                        if (notFound) {
                            getHAPIWater(item.ref_id);
                        }
                    }else if (item.ref_type.equalsIgnoreCase("ActivityId") && item.coachMessage.toLowerCase().contains("weight")) {

                        getWeight(item.ref_id);

                    }else if (item.ref_type.equalsIgnoreCase("ActivityId") && item.coachMessage.toLowerCase().contains("steps")) {

                        getSteps(item.ref_id);

                    }
                }


            } else {// find the reference meal and go there

                dummyView.setVisibility(View.VISIBLE);

                boolean notFound = true;

                if (item.ref_type.equalsIgnoreCase("MoodId")) {
                    notFound = true;

                    for (HapiMoment hapiMoment : ApplicationEx.getInstance().hapiMomentList) {
                        System.out.println("ONCLICK NOTIF...hapimoment id: " + hapiMoment.mood_id);

                        if (hapiMoment.mood_id == Integer.parseInt(item.ref_id)) {
                            notFound = false;
                            ApplicationEx.getInstance().currentHapiMoment = hapiMoment;
                            Intent mainIntent = new Intent(getBaseContext(), HapiMomentViewActivity.class);
                            mainIntent.putExtra("FROM_NOTIF", true);
                            if (item.is_community) {
                                mainIntent.putExtra("FROM_NOTIF_COMMUNITY", true);
                            }

                            startActivity(mainIntent);
                            return;

                        } else {
                            Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                            System.out.println("ONCLICK NOTIF... NOT FOUND");
                        }
                    }
                    //hapimoment not in the database
                    if (notFound) {
                        getHAPIMoment(item.ref_id);
                    }
                } else if (item.ref_type.equalsIgnoreCase("WaterId")) {
                    notFound = true;

                    for (Water water : ApplicationEx.getInstance().waterList) {
                        System.out.println("ONCLICK NOTIF...water id: " + water.water_id);

                        if (water.water_id == item.ref_id) {
                            notFound = false;
                            ApplicationEx.getInstance().currentWater = water;
                            Intent mainIntent = new Intent(getBaseContext(), WaterViewActivity.class);
                            mainIntent.putExtra("FROM_NOTIF", true);
                            if (item.is_community) {
                                mainIntent.putExtra("FROM_NOTIF_COMMUNITY", true);
                            }

                            startActivity(mainIntent);
                            return;

                        } else {
                            Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                            System.out.println("ONCLICK NOTIF... NOT FOUND");
                        }
                    }
                    //hapimoment not in the database
                    if (notFound) {
                        getHAPIWater(item.ref_id);
                    }
                } else if (item.ref_type.equalsIgnoreCase("ActivityId")) {
                    notFound = true;

                    for (Workout workout : ApplicationEx.getInstance().workoutList.values()) {
                        System.out.println("ONCLICK NOTIF...workout id: " + workout.activity_id);

                        if (workout.activity_id == item.ref_id) {

                            notFound = false;
                            ApplicationEx.getInstance().currentWorkoutView = workout;
                            Intent mainIntent = new Intent(getBaseContext(), ExerciseViewActivity.class);
                            mainIntent.putExtra("FROM_NOTIF", true);
                            if (item.is_community) {
                                mainIntent.putExtra("FROM_NOTIF_COMMUNITY", true);
                            }
                            startActivity(mainIntent);
                            return;

                        } else {
                            Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                            System.out.println("ONCLICK NOTIF... NOT FOUND");
                        }
                    }
                    //hapimoment not in the database
                    if (notFound) {
                        workout_id = Integer.parseInt(item.ref_id);
                        getWorkoutDetails(item.ref_id);
                    }
                } else {
                    for (Meal meal : ApplicationEx.getInstance().tempList.values()) {
                        if (meal.meal_id.compareTo(item.ref_id) == 0) {

                            notFound = false;

                            ApplicationEx.getInstance().currentMealView = meal;
                            Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);
                            mainIntent.putExtra("FROM_NOTIF", true);

                            startActivity(mainIntent);
                            return;

                        } else {
                            Toast.makeText(this, "not found", Toast.LENGTH_SHORT);
                            System.out.println("ONCLICK NOTIF... NOT FOUND");
                        }
                    }
                    if (notFound) {
                        getMeal(item.ref_id);
                    }
                }
            }//end if
        }
    }

    /**
     * Listener Methods
     **/

    @Override
    public void getNotificationSuccess(String response) {
        // TODO Auto-generated method stub
        System.out.println("Notif Activity getNotificationSuccess");

        notificationList = new ArrayList<>(ApplicationEx.getInstance().notificationList.values());

        //noinspection SynchronizeOnNonFinalField
        synchronized (notificationList) {
            // sort
            if (notificationList != null && notificationList.size() > 0) {
                sort(notificationList);
                this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        adapter.updateItems(notificationList);
                    }
                });
            }
        }
        //update timestamp
        setLastTimeStamp(this, ApplicationEx.getInstance().fromDateNotificationSync);
    }


    @Override
    public void getNotificationFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
    }

    public void markNotifAsReadSuccess(String response) {
        System.out.println("markNotifAsReadSuccess" + response);

    }

    public void markNotifAsReadFailedWithError(MessageObj response) {
        System.out.println("markNotifAsReadFailedWithError" + response);

    }

    public void markAllNotifAsReadSuccess(String response) {
        System.out.println("markAllNotifAsReadSuccess" + response);

    }

    public void markAllNotifAsReadFailedWithError(MessageObj response) {
        System.out.println("markAllNotifAsReadFailedWithError" + response);

    }

    public void clearAllNotifSuccess(String response) {
        notifDAO.clearTable();

        ApplicationEx.getInstance().notificationList.clear();
        System.out.println("clearAllNotifSuccess" + response);

    }

    public void clearAllNotifFailedWithError(MessageObj response) {
        System.out.println("clearAllNotifFailedWithError" + response);

        getNotifications();

    }

    public void getHapiMomentSuccess(HapiMoment hapiMoment) {
        ApplicationEx.getInstance().currentHapiMoment = hapiMoment;
        proceedToHAPIMomentPage(false, false);
    }

    public void getHapiMomentFailedWithError(MessageObj response) {
        System.out.println("getHapiMomentFailedWithErrror");
    }

    public void getWaterSuccess(Water water) {
        ApplicationEx.getInstance().currentWater = water;
//        proceedToWaterPage();
        proceedToWaterPage(false, false);
    }

    public void getWaterFailedWithError(MessageObj response) {
        System.out.println("getWaterFailedWithError");
    }

    public void getWorkoutSuccess(Workout workout) {
        System.out.println("getWorkoutSuccess: " + workout);
        ApplicationEx.getInstance().currentWorkoutView = workout;
        proceedToExercisePage();
    }

    public void getWorkoutFailedWithError(MessageObj response) {
        System.out.println("getWorkoutFailedWithError");
    }

    public void getMealSuccess(String response) {
        proceedToMealPage(false, false);
    }

    public void getMealFailedWithError(MessageObj response) {
//        proceedToMealPage();
    }

    /**
     * PopUp Menu Callback
     **/
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case ONE:
                markAllAsRead();
                break;
            case TWO:
                clearAllNotifications();
                break;
            case THREE:
                break;
        }
        return false;
    }

    /**
     * Private Methods
     **/
    private void markAllAsRead() {
        //update UI first, then call the API
        updateNotificationsList();

        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.markAllNotifAsRead(ApplicationEx.getInstance().userProfile.getRegID(), "");
    }


    private void clearAllNotifications() {
        //update UI first, then call the API
        clearAllNotificationsInUI();

        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.clearAllNotifications(ApplicationEx.getInstance().userProfile.getRegID(), "");
    }


    private void getNotifications() {
        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.getNotifications(ApplicationEx.getInstance().userProfile.getRegID()/*,-1*/);
    }

    private void markNotificationsRead(String notifId) {

        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.markNotifAsRead(ApplicationEx.getInstance().userProfile.getRegID(), notifId);
    }

    private void setLastTimeStamp(Context context, long lastTimestamp) {

        final SharedPreferences prefs = ApplicationEx.getInstance().getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(ApplicationEx.PROPERTY_SYNCNOTIF_TODATE, lastTimestamp);
        editor.apply();
    }

    private void sort(final List<Notification> items) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (items != null && items.size() > 0) {
                    Collections.sort(items, new Comparator<Notification>() {
                        public int compare(Notification notif1, Notification notif2) {
                            return notif2.timestamp.compareTo(notif1.timestamp);
                        }
                    });
                }
            }
        });
    }

    private void startUpdate() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getNotifications();
            }
        });
    }

    private void updateNotifAsRead(final Notification item, String notifId) {

        System.out.println("updateNotifAsRead: " + item + "**" + notifId);
        //update in API
        markNotificationsRead(notifId);

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //update in sql
                notifDAO.update(item);
            }
        });
    }

    private Notification getNotificationByID(int notifid) {
        for (Notification notif : notificationList) {
            if (notif.notificationID == notifid) {
                return notif;
            }
        }
        return null;
    }

    private void updateNotificationsList() {

        notificationList = new ArrayList<>(ApplicationEx.getInstance().notificationList.values());
        if (notificationList.size() > 0) {
            sort(notificationList);

            //mark all as read
            for (int i = 0; i < notificationList.size(); i++) {
                notificationList.get(i).notificationState = NOTIFICATION_STATE.READ;
            }

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.updateItems(notificationList);
                }
            });
        }
    }

    private void clearAllNotificationsInUI() {
        notificationList = new ArrayList<>();
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.updateItems(notificationList);
            }
        });
    }

    private void getHAPIMoment(String moodId) {
        if (getHapiMomentController == null) {
            getHapiMomentController = new GetHapiMomentController(this, this);
        }
        getHapiMomentController.getHapiMoment(moodId, ApplicationEx.getInstance().userProfile.getRegID());
    }
    private void getWeight(String activityId) {
        if (getWeightDataController == null) {
            getWeightDataController = new GetWeightDataController(this, this);
        }
        getWeightDataController.getWeight(ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }
    private void getSteps(String activityId) {
        if (getStepsDataController == null) {
            getStepsDataController = new GetStepsDataController(this, this);
        }
        getStepsDataController.getSteps(ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }
    private void getMeal(String mealId) {
        GetMealController mealController = new GetMealController(this, this);
        mealController.getMealDetails(ApplicationEx.getInstance().userProfile.getRegID(), mealId);
    }

    private void getHAPIWater(String waterId) {
        GetWaterController getWaterController = new GetWaterController(this, this);
        getWaterController.getWater(waterId, ApplicationEx.getInstance().userProfile.getRegID());
    }

    private void getWorkoutDetails(String workoutid) {
        if (getWorkoutController == null) {
            getWorkoutController = new GetWorkoutController(this, this);
        }
        getWorkoutController.getWorkout(ApplicationEx.getInstance().userProfile.getRegID(), workoutid);
    }

    private void proceedToHAPIMomentPage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), HapiMomentViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);

        startActivity(mainIntent);
    }
    private void proceedToWeightViewPage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), WeightViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);

        startActivity(mainIntent);
    }
    private void proceedToStepsViewPage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), StepsViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);

        startActivity(mainIntent);
    }
    private void proceedToWaterPage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), WaterViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);
        mainIntent.putExtra("WATER_STATUS", "view");
        startActivity(mainIntent);
    }

    private void proceedToExercisePage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), ExerciseViewActivity.class);
        mainIntent.putExtra("EXERCISE_ACTIVITY_ID", ApplicationEx.getInstance().currentWorkoutView.activity_id);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);
        startActivity(mainIntent);
    }

    private void proceedToExercisePage() {
        Intent mainIntent = new Intent(getBaseContext(), ExerciseViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        startActivity(mainIntent);
    }

    private void proceedToMealPage(boolean fromCommunity, boolean from3rdParty) {
        Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);
        mainIntent.putExtra("FROM_NOTIF", true);
        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", fromCommunity);
        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", from3rdParty);
        startActivity(mainIntent);
    }

    public void getTimelineActivitySuccess(TimelineActivity response) {

        if (entryType.equalsIgnoreCase("meal")) {
            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            proceedToMealPage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
        } else if (entryType.equalsIgnoreCase("water")) {

            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            ApplicationEx.getInstance().currentWater = response.water;
            proceedToWaterPage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));

        } else if (entryType.equalsIgnoreCase("hapimoment")) {
            ApplicationEx.getInstance().currentHapiMoment = response.hapiMoment;
            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            proceedToHAPIMomentPage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
        } else if (entryType.equalsIgnoreCase("exercise")) {
            ApplicationEx.getInstance().currentWorkoutView = response.workout;
            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            proceedToExercisePage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
        }else if (entryType.equalsIgnoreCase("weight")) {
            ApplicationEx.getInstance().currentWeightView = response.weight;
            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            proceedToWeightViewPage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
        }else if (entryType.equalsIgnoreCase("steps")) {
            ApplicationEx.getInstance().currentStepsView = response.steps;
            ApplicationEx.getInstance().currentCommunityUser = response.commentUser;
            proceedToStepsViewPage(true, !response.commentUser.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
        }

    }

    public void getTimelineActivityFailedWithError(MessageObj response) {
    }

    @Override
    public void postWeightDataSuccess(String response) {

    }

    @Override
    public void postWeightDataFailedWithError(MessageObj response) {

    }

    @Override
    public void getWeightDataSuccess(Weight response) {
        ApplicationEx.getInstance().currentWeightView = response;
        //proceedToWeightViewPage(true, !response.c.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID()));
    }

    @Override
    public void getWeightDataFailedWithError(MessageObj response) {
        System.out.println("getWeightDataFailedWithError");
    }

    @Override
    public void postStepsDataSuccess(String response) {

    }

    @Override
    public void postStepsDataFailedWithError(MessageObj response) {

    }

    @Override
    public void getStepsDataSuccess(Steps response) {
        ApplicationEx.getInstance().currentStepsView = response;
        proceedToStepsViewPage(true, false);
    }

    @Override
    public void getStepsDataFailedWithError(MessageObj response) {
        System.out.println("getStepsDataFailedWithError");
    }
}