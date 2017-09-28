package com.anxa.hapilabs.activities.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import com.anxa.hapilabs.models.Weight;
import com.hapilabs.R;
import com.anxa.hapilabs.activities.CoachSelectionActivity;
import com.anxa.hapilabs.activities.LoginPageActivity;
import com.anxa.hapilabs.activities.MealAddActivity;
import com.anxa.hapilabs.activities.MealViewActivity;
import com.anxa.hapilabs.activities.NotificationActivity;
import com.anxa.hapilabs.activities.UpgradeActivity;
import com.anxa.hapilabs.common.connection.ConnectionManager;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.DateChangeListener;
import com.anxa.hapilabs.common.connection.listener.SyncListener;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.storage.CommentDAO;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.PhotoDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.mymeals.GetSyncImplementer;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.Meal.STATE;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CustomDialog;
import com.anxa.hapilabs.ui.DatePagerLayout;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;

@SuppressLint("NewApi")
public class MealsFragments extends Fragment implements OnClickListener,
        DateChangeListener, SyncListener, BitmapDownloadListener {

    private Context context;
    OnClickListener listener;
    View instance;

    ListView listview;

    private GetSyncImplementer implementer;

    private List<Meal> selectedDailyList = new ArrayList<>();
    private List<HapiMoment> selectedDailyHAPImomenList = new ArrayList<>();
    private List<Workout> selectedDailyWorkoutList = new ArrayList<>();
    private List<Weight> selectedDailyWeightList  = new ArrayList<>();
    private MealListFragment mealList;
    private int selectedDayIndex = 0;

    // hast table f date ranges
    // example: {"1","5_28","2","5_29","3","5_30","4","5_1"};
    private Hashtable<String, String> dateTable = new Hashtable<>();
    private Hashtable<String, List<Meal>> weeklyMeal = new Hashtable<>();
    private Hashtable<String, List<HapiMoment>> weeklyHapiMoments = new Hashtable<>();
    private Hashtable<String, List<Workout>> weeklyWorkout = new Hashtable<>();
    private Hashtable<String, List<Weight>> weeklyWeight = new Hashtable<>();

    private int currentDate_day = 0;

    private Hashtable<String, Integer> mealCountPerWeek;
    private Hashtable<String, Integer> commentCountPerWeek;

    private Date toDate = AppUtil.getCurrentDateinDate();
    private Date toDateUI = AppUtil.getCurrentDateinDate();
    private Date fromDate;
    private Date fromDateUI;
    private DatePagerLayout dateC;
    private int dayOfWeek;

    private ProgressBar progress;
    private CustomDialog dialog = null;

    public static boolean toStopWork = false;
    private Thread bitmapThread;

    RelativeLayout hapimomentListContainer;

    @Override
    public void onResume() {
        super.onResume();
        toStopWork = false;
        refresh();
    }

    @Override
    public void onPause() {
//        toStopWork = true;
        ConnectionManager.getInstance().clearQueue();
        super.onPause();
    }

    @Override
    public void onStop() {
//        toStopWork = true;
        super.onStop();
    }

    public MealsFragments() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.context.getResources().getString(R.string.broadcast_sync));
        filter.addAction(this.context.getString(R.string.meallist_photo_download_update));
        filter.addAction(this.context.getString(R.string.meallist_photo_download));
        filter.addAction(this.context.getString(R.string.meallist_getmeal_week));
        filter.addAction(this.context.getString(R.string.meallist_deletemeal_refresh));
        filter.addAction(this.context.getString(R.string.meallist_deletehapimoment_refresh));
        filter.addAction(this.context.getString(R.string.meallist_addhapimoment_refresh));
        filter.addAction(this.context.getString(R.string.workoutListUpdate));
        filter.addAction(this.context.getString(R.string.workoutListDelete));
        filter.addAction(this.context.getString(R.string.meallist_get_new_week));

        getActivity().getApplicationContext().registerReceiver(the_receiver, filter);

        toDate = new Date();

        if (ApplicationEx.getInstance().isCalendarSelected != null) {
            toDate = ApplicationEx.getInstance().currentSelectedDate;
        }

        ApplicationEx.getInstance().currentSelectedDate = toDate;
        ApplicationEx.getInstance().selectedCalendarDay = AppUtil.getWeekIndexOfDate(toDate);

        //get last 7 records
        dayOfWeek = 6;
        // get from date by adding today and index
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);

        currentDate_day = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_YEAR, -6);
        fromDate = c.getTime();

        c.add(Calendar.DAY_OF_YEAR, 3);
        fromDateUI = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 6);
        toDateUI = c.getTime();

        Calendar cToDate = Calendar.getInstance();
        cToDate.setTime(toDate);

        //onCreateView - get 4 only
        for (int i = 0; i <= dayOfWeek; i++) {
            dateTable.put("" + (i), (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH));
            c.add(Calendar.DAY_OF_YEAR, -1);
        }

        ApplicationEx.getInstance().toDateCurrent = toDate;
        // meals fragment from:
        ApplicationEx.getInstance().fromDateCurrent = fromDate;

        View rootView = inflater.inflate(R.layout.fragment_mymeals, container, false);
        // init listview and adapter
        mealList = (MealListFragment) rootView.findViewById(R.id.meallist);
        // init progres
        progress = (ProgressBar) rootView.findViewById(R.id.progressBar);

        updateProgress();
        // get Initial Date today

        updateWeeklyMeal();
        updateTodaysMeal();

        dateC = (DatePagerLayout) rootView.findViewById(R.id.meallist_header);

        dateC.setDatePager();

        updateDateHeaders(dateC);

        dateC.updateProgressText(false);

        mealList.initDate(selectedDailyList, context, this);
        hapimomentListContainer = ((RelativeLayout) mealList.findViewById(R.id.hapimomentListContainer));

        return rootView;
    }

    private void updateDateHeaders(DatePagerLayout dateC) {

        dateC.setDateListener(this);
        dateC.initDate(ApplicationEx.getInstance().currentSelectedDate, mealCountPerWeek, commentCountPerWeek, dateTable, dayOfWeek, selectedDayIndex);
    }

    private void updateTodaysMeal() {

        selectedDailyList = weeklyMeal.get(AppUtil.getMonthonDate(ApplicationEx.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationEx.getInstance().currentSelectedDate));
        selectedDailyHAPImomenList = weeklyHapiMoments.get(AppUtil.getMonthonDate(ApplicationEx.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationEx.getInstance().currentSelectedDate));

        sort(selectedDailyHAPImomenList);
        selectedDailyWorkoutList = weeklyWorkout.get(AppUtil.getMonthonDate(ApplicationEx.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationEx.getInstance().currentSelectedDate));
        selectedDailyWeightList = weeklyWeight.get(AppUtil.getMonthonDate(ApplicationEx.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationEx.getInstance().currentSelectedDate));
    }

    public void sort(final List<HapiMoment> hapiObj) {
        if (hapiObj != null && hapiObj.size() > 0) {
            Collections.sort(hapiObj, new Comparator<HapiMoment>() {
                public int compare(HapiMoment h1, HapiMoment h2) {
                    return ((HapiMoment) h1).mood_datetime.compareTo(((HapiMoment) h2).mood_datetime);
                }
            });
        }
    }

    private void reInitDate() {
        // get the index of today's date
        toDate = ApplicationEx.getInstance().toDateCurrent;

        dayOfWeek = 6;
        // get from date by adding today and index
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);

        currentDate_day = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DAY_OF_YEAR, -6);
        fromDate = c.getTime();

        Calendar cToDate = Calendar.getInstance();
        cToDate.setTime(toDate);

        c.add(Calendar.DAY_OF_YEAR, 3);
        fromDateUI = c.getTime();
        c.add(Calendar.DAY_OF_YEAR, 6);
        toDateUI = c.getTime();

        for (int i = 0; i <= dayOfWeek; i++) {
            dateTable.put("" + (i), (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH));
            c.add(Calendar.DAY_OF_YEAR, -1);
        }

        toDate = ApplicationEx.getInstance().toDateSyncCurrent;
        fromDate = ApplicationEx.getInstance().fromDateCurrent;
    }

    private void getSync() {
        String userId = ApplicationEx.getInstance().userProfile.getRegID();
        if (userId == null || userId.length() < 1) {
            logout();
        } else {
            implementer = new GetSyncImplementer(this.context, userId, AppUtil.getDateOnlyinUTC(toDate), AppUtil.getDateOnlyinUTC(fromDate), this);
        }
    }

    private void updateWeeklyMeal() {
        weeklyMeal = AppUtil.getMealsByDateRange(toDateUI, fromDateUI,
                ApplicationEx.getInstance().tempList, dayOfWeek + 1);
        weeklyWorkout = AppUtil.getWorkoutByDateRange(toDateUI, fromDateUI,
                ApplicationEx.getInstance().workoutList, dayOfWeek + 1);
        weeklyWeight = AppUtil.getWeightByDateRange(toDateUI, fromDateUI,
                ApplicationEx.getInstance().tempWeightList, dayOfWeek + 1);
        mealCountPerWeek = AppUtil.getMealCountPerWeek(weeklyMeal);

        commentCountPerWeek = AppUtil.getCommentCountPerWeek(weeklyMeal);
        weeklyHapiMoments = AppUtil.getHapimomentsByDateRange(toDateUI, fromDateUI,
                ApplicationEx.getInstance().tempHapimomentList, dayOfWeek + 1);
        weeklyWeight= AppUtil.getWeightByDateRange(toDateUI, fromDateUI,
                ApplicationEx.getInstance().tempWeightList, dayOfWeek + 1);
//        weeklyHapiMoments = AppUtil.getHapimomentsByDateRange(toDate, fromDate,
//                ApplicationEx.getInstance().tempHapimomentList, dayOfWeek + 1);
    }


    private void displayInfo(MEAL_TYPE mealtype) {

        String message = AppUtil.getMealTip(context, ApplicationEx.getInstance().selectedCalendarDay, mealtype);
        String title = AppUtil.getMealTitle(context, mealtype);

        dialog = new CustomDialog(context, null, null, null, true, message,
                title, this);

        dialog.show();
    }


    @Override
    public void onClick(View v) {

        if (dialog != null && dialog.isShowing()) {
            // yes
            // no
            // others
            // close

            if (v instanceof TextView && ((TextView) v).getText().toString().equals(getString(R.string.btn_now))) {//now
                //show Coach selection page
                callCoach();
                dialog.dismiss();
                return;
            }
            dialog.dismiss(); //could be a pop up or coach button later
        } else if (v.getId() == R.id.meal_uploadfailed_refresh) {
            // find the meal id and resubmit
            String mealID = (String) v.getTag();

            Meal meal = ApplicationEx.getInstance().tempList.get(mealID);
            if (meal != null) {
                meal.state = STATE.ADD_ONGOING;
                meal.meal_status = "RESUME_ONGOING";

                ApplicationEx.getInstance().tempList.remove(mealID);
                ApplicationEx.getInstance().tempList.remove(mealID);
                ApplicationEx.getInstance().tempList.put(mealID, meal);

                Intent broadint = new Intent();
                broadint.putExtra("MEAL_ID", mealID);
                broadint.setAction(context.getResources().getString(R.string.meallist_addmeal_refresh));
                getActivity().sendBroadcast(broadint);

                // broad cast a meal ui change to remove the failed display
                Intent broad = new Intent();
                broad.setAction(context.getResources().getString(R.string.meallist_getmeal_week));
                getActivity().sendBroadcast(broad);
            }

        } else if (v.getId() == R.id.meal_uploadfailed_delete) {
            // find the meal id and submit
            String mealID = (String) v.getTag();
            //delete real time && and in DB
            Meal meal = ApplicationEx.getInstance().tempList.get(mealID);
            if (meal != null) {

                try {
                    //delete from DB:
                    MealDAO daomeal = new MealDAO(context, null);
                    DaoImplementer dao = new DaoImplementer(daomeal, context);
                    dao.deleteMeal(meal);

                    ApplicationEx.getInstance().tempList.remove(mealID);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // broadcast a meal change
                Intent broadint = new Intent();
                broadint.setAction(context.getResources().getString(R.string.meallist_getmeal_week));
                context.sendBroadcast(broadint);

                updateTodaysMeal();
                refresh();
            }

        } else if (v.getId() == R.id.mealinfo) {
            //
            if (v.getTag() != null && v.getTag() instanceof MEAL_TYPE) {
                displayInfo((MEAL_TYPE) v.getTag());
            }
        } else if (v.getTag() != null) {
            if (v.getTag() instanceof Meal) {
                viewMeal((Meal) v.getTag());
            } else {

                System.out.println("onClick getTag: " + v.getTag());
                MEAL_TYPE mealtype = MEAL_TYPE
                        .getMealType((Integer) v.getTag());
                addNewMeal(mealtype);
            }
        } else if (v.getId() == R.id.header_left || v.getId() == R.id.badge_notif) {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.header_right) {
            Intent intent = new Intent(getActivity(), UpgradeActivity.class);
            startActivity(intent);
        }
        else {
            viewMeal((Meal) v.getTag());
        }
    }

    private void addNewMeal(MEAL_TYPE mealType) {
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), MealAddActivity.class);
        // set date basd on the current selected headerDate
        mainIntent.putExtra("MEAL_TYPE", mealType.ordinal() + 1);
        mainIntent.putExtra("MEAL_STATE", Meal.MEALSTATE_ADD);
        getActivity().startActivityForResult(mainIntent, ApplicationEx.REQUESTCODE_MEALADD);
    }

	/* Called when the second activity's finished */

    private void viewMeal(Meal meal) {
        ApplicationEx.getInstance().currentMealView = meal;
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), MealViewActivity.class);
        this.getActivity().startActivityForResult(mainIntent, ApplicationEx.REQUESTCODE_MEALVIEW);
    }

    @Override
    public void dateChange(Date date, int weekIndex) {
        // TODO Auto-generated method stub

        // date change get new currentdate
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        selectedDayIndex = weekIndex; //set selected date based on whats is passed by the DateUI //as of july07

        int currentDate_day = c.get(Calendar.DAY_OF_MONTH);
        int currentDate_month = c.get(Calendar.MONTH) + 1;

        selectedDailyList = weeklyMeal.get(currentDate_month + "_" + currentDate_day);
        selectedDailyHAPImomenList = weeklyHapiMoments.get(currentDate_month + "_" + currentDate_day);
        mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);

        /*Workout*/
        selectedDailyWorkoutList = weeklyWorkout.get(currentDate_month + "_" + currentDate_day);
        selectedDailyWeightList = weeklyWeight.get(currentDate_month + "_" + currentDate_day);
        mealList.updateExerciseData(selectedDailyWorkoutList);
        mealList.updateWeightData(selectedDailyWeightList);
    }

    private void updateProgress() {
        progress.setVisibility(View.GONE);
    }

    //call this when switching tabs to refresh the meals ui
    private void refresh() {
        if (mealList != null && selectedDailyList != null) {
            mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
            mealList.updateExerciseData(selectedDailyWorkoutList);
            mealList.updateWeightData(selectedDailyWeightList);
        } else if (selectedDailyWorkoutList != null) {
//            mealList.updateExerciseData(selectedDailyWorkoutList);
//            mealList.updateExerciseData(new ArrayList<Workout>(selectedDailyWorkoutList));
        } else {
            logout();
        }
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == context.getResources().getString(R.string.broadcast_sync)) {// update timeline only if this
                // is the content on Show
                // update Weekly Meal
                System.out.println("broadcast: broadcast_sync");

                updateProgress();
                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(
                        ApplicationEx.getInstance().currentSelectedDate,
                        mealCountPerWeek,
                        commentCountPerWeek,
                        dateTable,
                        dayOfWeek,
                        AppUtil.getWeekIndexOfDate(ApplicationEx.getInstance().currentSelectedDate));
                dateC.updateProgressText(true);
                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                if (selectedDailyWorkoutList != null) {
                    mealList.updateExerciseData(selectedDailyWorkoutList);
                }
                if (selectedDailyWeightList != null) {
                    mealList.updateWeightData(selectedDailyWeightList);
                }


            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_photo_download_update)) {// update timeline only
                // if this is the
                // content on Show
                System.out.println("broadcast: meallist_photo_download_update");

                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);

            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_photo_download)) {// update timeline only
                // if this is the
                // content on Show
                System.out.println("broadcast: meallist_photo_download");

                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
//                mealList.updateExerciseData(selectedDailyWorkoutList);

            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_deletemeal_refresh)) {// update timeline only if
                // this is the content
                System.out.println("broadcast: meallist_deletemeal_refresh");

                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(
                        ApplicationEx.getInstance().currentSelectedDate,
                        mealCountPerWeek,
                        commentCountPerWeek,
                        dateTable,
                        dayOfWeek,
                        AppUtil.getWeekIndexOfDate(ApplicationEx.getInstance().currentSelectedDate));

                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);
            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_deletehapimoment_refresh)
                    || intent.getAction() == context.getResources().getString(R.string.meallist_addhapimoment_refresh)) {// update timeline only if
                // this is the content
                System.out.println("broadcast: meallist_deletehapimoment_refresh");
                updateWeeklyMeal();
                updateTodaysMeal();
                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);

            } else if (intent.getAction() == context.getResources().getString(R.string.workoutListUpdate)) { //update workout list
                System.out.println("broadcast: workoutListUpdate");
                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(
                        ApplicationEx.getInstance().currentSelectedDate,
                        mealCountPerWeek,
                        commentCountPerWeek,
                        dateTable,
                        dayOfWeek,
                        AppUtil.getWeekIndexOfDate(ApplicationEx.getInstance().currentSelectedDate));
                mealList.updateData(selectedDailyList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);
            } else if (intent.getAction() == context.getResources().getString(R.string.workoutListDelete)) { //update workout list
                System.out.println("broadcast: workoutListDelete");
                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(
                        ApplicationEx.getInstance().currentSelectedDate,
                        mealCountPerWeek,
                        commentCountPerWeek,
                        dateTable,
                        dayOfWeek,
                        AppUtil.getWeekIndexOfDate(ApplicationEx.getInstance().currentSelectedDate));
                mealList.updateData(selectedDailyList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_getmeal_week)) {// update timeline only if
                // this is the content
                System.out.println("broadcast: meallist_getmeal_week");
                reInitDate();
                getSync();
                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(ApplicationEx.getInstance().currentSelectedDate, mealCountPerWeek, commentCountPerWeek, dateTable, dayOfWeek);
                dateC.updateProgressText(false);

                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);

            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_get_new_week)) {// update week meal count only, not get_sync
                // this is the content
                System.out.println("broadcast: meallist_get_new_week");
                reInitDate();
                updateWeeklyMeal();
                updateTodaysMeal();
                dateC.initDate(ApplicationEx.getInstance().currentSelectedDate, mealCountPerWeek, commentCountPerWeek, dateTable, dayOfWeek);
                mealList.updateData(selectedDailyList, selectedDailyHAPImomenList);
                mealList.updateExerciseData(selectedDailyWorkoutList);
                mealList.updateWeightData(selectedDailyWeightList);
            }
        }
    };

    private void callCoach() {
        // call My listview
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), CoachSelectionActivity.class);
        getActivity().startActivity(mainIntent);
    }


    public void getSyncSuccess(String response) {
        Intent broadint = new Intent();

        broadint.setAction(context.getResources().getString(R.string.broadcast_sync));
        //remove progressbar for the first sync

        ApplicationEx.getInstance().isLoginSync = false;
        context.sendBroadcast(broadint);
        startImageDownload(ApplicationEx.getInstance().tempList);
    }


    public void getSyncFailedWithError(MessageObj response) {
        logout();
    }


    private void startImageDownload(final Hashtable<String, Meal> mealList) {
        System.gc();

        System.out.println("MealsFragments: startImageDownload");

        bitmapThread = new Thread() {
            @Override
            public void run() {
                try {
                    Enumeration<Meal> mealEnum = mealList.elements();

                    while (mealEnum.hasMoreElements()) {

                        Meal meal = mealEnum.nextElement();
                        List<Photo> photos = meal.photos;

                        if (photos != null && photos.size() > 0) {

                            //download photo
                            for (int j = 0; j < photos.size(); j++) {

                                Photo photo = photos.get(j);

                                if (photo.image == null && photo.photo_id != null && photo.photo_url_large != null) {
                                    //download
                                    final String photoid = photo.photo_id;
                                    final String mealid = meal.meal_id;
                                    final String url = photo.photo_url_large;

//                                    System.out.println("MealsFragments: download photo for loop");

//                                    ((Activity) context).runOnUiThread(new Runnable() {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            while (!toStopWork) {
                                                System.out.println("!toStopWork");
                                                download(photoid, url, mealid);
                                            }
                                        }
                                    });
                                }

                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        bitmapThread.start();
    }

    private void download(String photoId, String url, String mealId) {

        System.out.println("MealsFragments: download");
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(this.context,
                url,
                photoId,
                mealId, this);
    }


    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub

        Intent broadInt = new Intent();
        broadInt.setAction(context.getResources().getString(R.string.meallist_photo_download));
        broadInt.putExtra("MEAL_ID", mealId);
        broadInt.putExtra("PHOTO_ID", photoId);

        context.sendBroadcast(broadInt);
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    private void logout() {

        ApplicationEx.getInstance().setIsLogin(context, false);
        //clear the saved login credentials
        ApplicationEx.getInstance().clearLoginCredentials();
        ApplicationEx.getInstance().currentSelectedDate = AppUtil.getCurrentDateinDate();
        ApplicationEx.getInstance().currentWeightView = null;
        ApplicationEx.getInstance().currentStepsView = null;
        ApplicationEx.getInstance().tempWeightList = null;
        clearTables();

        Intent mainIntent;

        mainIntent = new Intent(this.getActivity(), LoginPageActivity.class);

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //restart sessions
        try {
            if (ApplicationEx.getInstance().anxaMatsController == null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(context, ApplicationEx.getInstance().userProfile.getRegID());
            }
            ApplicationEx.getInstance().anxaMatsController.closeSession(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            // Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }

        this.getActivity().startActivity(mainIntent);
        this.getActivity().finish();
    }

    private void clearTables() {
        //sequence is important

        PhotoDAO photoDAO = new PhotoDAO(context, null);
        photoDAO.clearTable();

        CoachDAO coachDAO = new CoachDAO(context, null);
        coachDAO.clearTable();

        UserProfileDAO userProfileDAO = new UserProfileDAO(context, null);
        userProfileDAO.clearTable();

        CommentDAO commentDAO = new CommentDAO(context, null);
        commentDAO.clearTable();

        MessageDAO messageDAO = new MessageDAO(context, null);
        messageDAO.clearTable();

        MealDAO mealDAO = new MealDAO(context, null);
        mealDAO.clearTable();

        NotificationDAO notificationDAO = new NotificationDAO(context, null);
        notificationDAO.clearTable();

        WorkoutDAO workoutDAO = new WorkoutDAO(context, null);
        workoutDAO.clearTable();
    }

    public void goToPreviousWeek(View view) {

        dateC.goToPreviousWeek();
    }


    public void goToNextWeek(View view) {
        dateC.goToNextWeek();
    }
}
