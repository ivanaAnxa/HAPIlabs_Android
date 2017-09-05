package com.anxa.hapilabs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.fragments.HeaderFragments;
import com.anxa.hapilabs.activities.fragments.MeFragments;
import com.anxa.hapilabs.activities.fragments.MealsFragments;
import com.anxa.hapilabs.activities.fragments.MessageFragments;
import com.anxa.hapilabs.activities.fragments.ProgressFragments;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.BitmapUploadListener;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.MealGraphListener;
import com.anxa.hapilabs.common.connection.listener.PostStepsGoogleFitListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.RegisterDeviceListener;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.storage.CommentDAO;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.PhotoDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.addmeal.AddMealController;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.exercise.PostStepsGoogleFitController;
import com.anxa.hapilabs.controllers.exercise.RegisterDeviceController;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.images.GetImageUploadController;
import com.anxa.hapilabs.controllers.mymeals.MyListMainController;
import com.anxa.hapilabs.controllers.notifications.NotificationController;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.UserProfile.MEMBER_TYPE;
import com.anxa.hapilabs.models.Workout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class MainActivity extends FragmentActivity implements OnClickListener, MealAddListener, ProgressChangeListener, BitmapUploadListener,
        MainActivityCallBack, BitmapDownloadListener, MealGraphListener, GetNotificationListener, SeekBar.OnSeekBarChangeListener,
        OnDataPointListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RegisterDeviceListener, PostStepsGoogleFitListener {

    Fragment currentFragment;
    Fragment messageFragment;
    Fragment mealFragment;
    Fragment progressFragment;
    Fragment meFragment;
    HeaderFragments headerView;
    RelativeLayout headerView_progress;
    SeekBar progressSeekBar;
    SeekBar progressSeekBar_clickable;

    NotificationController notifController;
    AddMealController addmealController;
    MyListMainController synccontroller;
    RegisterDeviceController registerDeviceController;
    PostStepsGoogleFitController postStepsGoogleFitController;

    ImageButton footer1;
    ImageButton footer2;
    ImageButton footer3;
    ImageButton footer4;

    TextView weight_tv;
    TextView steps_tv;
    TextView meals_tv;

    Boolean fromPayment = false;
    AlertDialog.Builder firstLaunchAlert;

    int fragmentIndex;
    int notifCount = 0;
    int tabIndex = 1;

    TextView header_tv;
    ImageView calendarIcon;
    ImageView summaryIcon;

    static final String TAG = "HAPIcoach";
    final int RESULTCODE_CRMDONE = 80;
    Meal currentMeal;
    Boolean fromNotif = false;
    int FONT_SIZE = 11;

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    ArrayList<Workout> workoutObjArray;
    private static long activityTime = 0;

    //date MM DD yyyy - activityTime
    public Hashtable<String, Long> activityTimeTable = new Hashtable<String, Long>();
    public Hashtable<String, Float> caloriesTimeTable = new Hashtable<String, Float>();
    public Hashtable<String, Integer> stepsTimeTable = new Hashtable<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        System.out.println("MainActivity onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.meallist_addmeal_refresh));
        filter.addAction(this.getResources().getString(R.string.meallist_addmeal));
        filter.addAction(this.getResources().getString(R.string.googlefit_request));
        filter.addAction(this.getResources().getString(R.string.googlefit_webview));
        this.getApplicationContext().registerReceiver(the_receiver, filter);

        Bundle extras = getIntent().getExtras();

        progressSeekBar = (SeekBar) findViewById(R.id.main_progress_seekbar);
        progressSeekBar.setOnSeekBarChangeListener(this);

        progressSeekBar_clickable = (SeekBar) findViewById(R.id.main_progress_seekbar_clickable);
        progressSeekBar_clickable.setOnSeekBarChangeListener(this);

        weight_tv = (TextView) findViewById(R.id.weight_label);
        steps_tv = (TextView) findViewById(R.id.steps_label);
        meals_tv = (TextView) findViewById(R.id.meals_label);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.hapilabs",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (extras != null) {
            Integer tabIntentExtra = extras.getInt("TAB");
            if (tabIntentExtra != null) {
                tabIndex = tabIntentExtra;
            }
            Integer tabIntentExtraNotif = extras.getInt("FROM_NOTIF_INT");

            fromNotif = tabIntentExtraNotif != null && tabIntentExtraNotif == 1;
        }

        fromPayment = getIntent().getBooleanExtra("ISFROMPAYMENT", false);
        //add alert when logged in after registration.
        if (fromPayment) {
            showFirstConnectionMessage();
        }

        if (!ApplicationEx.getInstance().isGoogleFitAllowed(this)) {
            showStepsFirstLaunchDialog();
        } else {
            mApiClient.connect();

        }

        createAllTables(this);

        ApplicationEx.getInstance().getWindowDimension(this);

        footer1 = (ImageButton) findViewById(R.id.footer1);
        footer1.setOnClickListener(this);

        footer2 = (ImageButton) findViewById(R.id.footer2);
        footer2.setOnClickListener(this);

        footer3 = (ImageButton) findViewById(R.id.footer3);
        footer3.setOnClickListener(this);

        footer4 = (ImageButton) findViewById(R.id.footer4);
        footer4.setOnClickListener(this);

        calendarIcon = (ImageView) findViewById(R.id.header_right);
        calendarIcon.setFocusableInTouchMode(false);

        summaryIcon = (ImageView) findViewById(R.id.header_right_progress);
        summaryIcon.setFocusableInTouchMode(false);
        summaryIcon.setOnClickListener(this);

        header_tv = (TextView) findViewById(R.id.header_title);

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            if (tabIndex == 2) {//message
                updateHeader(2, this);
                updateFooter(2);
                currentFragment = new MessageFragments();

            } else if (tabIndex == 3) {//progress
                updateHeader(3, this);
                updateFooter(3);
                currentFragment = new ProgressFragments();

            } else if (tabIndex == 4) {//me
                updateHeader(4, this);
                updateFooter(4);
                currentFragment = new MeFragments();

            } else {//default
                currentFragment = new MealsFragments();
                updateHeader(1, this);
                updateFooter(1);
            }

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            currentFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, currentFragment).commit();
        }

        if (!fromNotif) {
            synccontroller = new MyListMainController(this);
        }

        //update language
        try {
            if (Locale.getDefault().getLanguage() != null) {
                ApplicationEx.language = Locale.getDefault().getLanguage();
                if (!ApplicationEx.language.equals("fr")) {
                    ApplicationEx.language = "en"; //if its anything but french then default it to english
                }
            } else
                ApplicationEx.language = "en";
        } catch (Exception e) {
        }

        String ver = getResources().getString(R.string.app_version);
        // save userAgent
        ApplicationEx.getInstance().customAgent = TAG + " v/" + ver;
        ApplicationEx.getInstance().selectedTabIndex = tabIndex;

    }

    @Override
    public void onResume() {
        super.onResume();

        System.out.println("MainActivity onResume");

        if (ApplicationEx.getInstance().userProfile.getRegID() == null) {
            logout();
        }

        //progress
        if (ApplicationEx.getInstance().selectedTabIndex == 3) {
            updateHeader(3, this);
        } else if (currentFragment instanceof MealsFragments) {
            updateHeader(1, this);
            Intent broadint = new Intent();
            broadint.setAction(getString(R.string.workoutListUpdate));
            sendBroadcast(broadint);

        } else if (currentFragment instanceof MeFragments) {
            updateHeader(4, this);
        } else if (currentFragment instanceof MessageFragments) {
            updateHeader(2, this);
        } else if (currentFragment instanceof ProgressFragments) {
            updateHeader(3, this);
        } else {
            updateHeader(1, this);
        }

        if (ApplicationEx.getInstance().isFromPush(this)) {
            ApplicationEx.getInstance().setFromPush(this, false);
            launchActivity(NotificationActivity.class);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mApiClient != null) {
            Fitness.SensorsApi.remove(mApiClient, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.isSuccess()) {
                                mApiClient.disconnect();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void onConnected(Bundle bundle) {

        System.out.println("MainActivity onConnected");
        activityTimeTable = new Hashtable<>();
        caloriesTimeTable = new Hashtable<>();
        stepsTimeTable = new Hashtable<>();

        registerDeviceToAPI();

        new ViewWeekStepCountTask().execute();
    }

    private void registerDeviceToAPI() {

        String id = AppUtil.getDeviceId(this);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();

        if (registerDeviceController == null) {
            registerDeviceController = new RegisterDeviceController(this, this, this);
        }
        registerDeviceController.registerDevice(userID, id, getString(R.string.app_name), Integer.toString(HAPIActivity.getAppVersion(this)));
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("GoogleFit", "onConnectionFailed");

        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(MainActivity.this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                    if (field.getName().equalsIgnoreCase("Steps")) {
//                        workoutObj.steps = value.asInt();
//                        ApplicationEx.getInstance().workoutList.remove("1234567");
//                        ApplicationEx.getInstance().workoutList.put("1234567", workoutObj);
//
//                        Intent broadint = new Intent();
//                        broadint.setAction(getString(R.string.workoutListUpdate));
//                        sendBroadcast(broadint);
                    }
                }
            });
        }
    }


    public void createAllTables(Context context) {
        // TODO Auto-generated method stub

        //sequence is important
        PhotoDAO photoDAO = new PhotoDAO(context, null);
        photoDAO.createTable();

        CoachDAO coachDAO = new CoachDAO(context, null);
        coachDAO.createTable();

        UserProfileDAO userProfileDAO = new UserProfileDAO(context, null);
        userProfileDAO.createTable();

        CommentDAO commentDAO = new CommentDAO(context, null);
        commentDAO.createTable();

        MessageDAO messageDAO = new MessageDAO(context, null);
        messageDAO.createTable();

        MealDAO mealDAO = new MealDAO(context, null);
        mealDAO.createTable();

        NotificationDAO notificationDAO = new NotificationDAO(context, null);
        notificationDAO.createTable();

        WorkoutDAO workoutDAO = new WorkoutDAO(context, null);
        workoutDAO.createTable();
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() == context.getResources().getString(R.string.meallist_addmeal_refresh)) {
                String mealid = intent.getStringExtra("MEAL_ID");
                String username = ApplicationEx.getInstance().userProfile.getRegID();
                if (username != null) {

                    //add instance if it has photo
                    Meal meal = ApplicationEx.getInstance().tempList.get(mealid);
                    if (meal != null) {
                        if (meal.photos != null && meal.photos.size() > 0) {
                            uploadPhoto(meal);
                        } else {
                            uploadMeal(meal, username);
                        }
                    }
                }
            } else if (intent.getAction() == context.getResources().getString(R.string.meallist_addmeal)) {
            } else if (intent.getAction() == context.getResources().getString(R.string.googlefit_request)) {
                System.out.println("GOOGLEFIT_REQUEST_AGAIN");
                showStepsFirstLaunchDialog();
            } else if (intent.getAction() == context.getResources().getString(R.string.googlefit_webview)) {
                System.out.println("GOOGLEFIT_WEBVIEW");
                showGoogleFitWebView();
            }
        }
    };


    private void commitFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    private void commitMealsFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, fragment);
//        transaction.addToBackStack(null);

        transaction.detach(fragment);
        transaction.attach(fragment);

        ApplicationEx.getInstance().currentSelectedDate = AppUtil.getCurrentDateinDate();

        //broadcast the update
        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadint);

        // Commit the transaction
        transaction.commit();
    }

    //probably use a listener hear
    private void updateFooter(int index) {
        switch (index) {
            case 1: //meals
                footer1.setSelected(true);
                footer2.setSelected(false);
                footer3.setSelected(false);
                footer4.setSelected(false);

                break;
            case 2: //coach
                footer2.setSelected(true);
                footer1.setSelected(false);
                footer3.setSelected(false);
                footer4.setSelected(false);

                break;
            case 3: //progress
                footer3.setSelected(true);
                footer1.setSelected(false);
                footer2.setSelected(false);
                footer4.setSelected(false);
                break;
            case 4: //result
                footer4.setSelected(true);
                footer1.setSelected(false);
                footer2.setSelected(false);
                footer3.setSelected(false);
                break;
        }
    }

    //probably use a listener hear
    public void updateHeader(int index, OnClickListener onClick) {
        fragmentIndex = index;
        OnClickListener listener = (onClick == null ? this : onClick);

        ApplicationEx.getInstance().selectedTabIndex = index;

        switch (index) {
            case 1: //meals
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                if (ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_PREMIUM || ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_NORMAL) {
                    headerView.setHeader(null, null, getString(R.string.MYMEALS_TITLE), R.drawable.nav_notifications_icon, -1, -1, listener);
                } else {
                    headerView.setHeader(null, null, getString(R.string.MYMEALS_TITLE), R.drawable.nav_notifications_icon, R.drawable.nav_upgrade_icon, -1, listener);
                }

                if (headerView_progress == null)
                    headerView_progress = (RelativeLayout) findViewById(R.id.fragment_headerprogress);
                headerView_progress.setVisibility(View.GONE);

                break;

            case 2: //message
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                if (ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_PREMIUM || ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_NORMAL) {
                    headerView.setHeader(null, null, getString(R.string.MYMESSAGE_TITLE), -1, -1, -1, listener);
                } else {
                    headerView.setHeader(null, null, getString(R.string.MYMESSAGE_TITLE), -1, R.drawable.nav_upgrade_icon, -1, listener);
                }

                if (headerView_progress == null)
                    headerView_progress = (RelativeLayout) findViewById(R.id.fragment_headerprogress);
                headerView_progress.setVisibility(View.GONE);
                break;
            case 3: //progress
                if (headerView_progress == null)
                    headerView_progress = (RelativeLayout) findViewById(R.id.fragment_headerprogress);
                headerView_progress.setVisibility(View.VISIBLE);
                break;
            case 4: //me
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                if (ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_PREMIUM || ApplicationEx.getInstance().userProfile.getMember_type() == MEMBER_TYPE.PAID_NORMAL) {
                    headerView.setHeader(null, null, getString(R.string.about_me), R.drawable.info, -1, -1, listener);
                } else {
                    headerView.setHeader(null, null, getString(R.string.about_me), R.drawable.info, R.drawable.nav_upgrade_icon, -1, listener);
                }
                if (headerView_progress == null)
                    headerView_progress = (RelativeLayout) findViewById(R.id.fragment_headerprogress);
                headerView_progress.setVisibility(View.GONE);

                break;
            case 5: //help & contact
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setHeader(getString(R.string.about_me), null, getString(R.string.MYHELP_TITLE), R.drawable.nav_arrow_back, -1, -1, listener);
                break;
            case 6: //My Coach
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setHeader(getString(R.string.about_me), null, getString(R.string.PROFILE_MY_COACH), R.drawable.nav_arrow_back, -1, -1, listener);
                if (headerView_progress == null)
                    headerView_progress = (RelativeLayout) findViewById(R.id.fragment_headerprogress);
                headerView_progress.setVisibility(View.GONE);
                break;
        }
    }

    private void loadView(final int index) {
        updateFooter(index);
        switch (index) {
            case 1:
                if (mealFragment == null) {
                    mealFragment = new MealsFragments();
                }
                updateHeader(index, (OnClickListener) mealFragment);
                commitMealsFragment(mealFragment);
//                commitFragment(mealFragment);
                break;

            case 2:
                if (messageFragment == null) {
                    messageFragment = new MessageFragments();
                }
                updateHeader(index, (OnClickListener) messageFragment);
                commitFragment(messageFragment);
                break;

            case 3:
                if (progressFragment == null) {
                    progressFragment = new ProgressFragments();
                }

                progressSeekBar.setProgress(0);
                progressSeekBar_clickable.setProgress(0);
                updateHeader(index, (OnClickListener) progressFragment);
                commitFragment(progressFragment);
                break;

            case 4:
                if (meFragment == null) {
                    meFragment = new MeFragments();
                }
                updateHeader(index, (OnClickListener) meFragment);
                commitFragment(meFragment);
                break;

            default:
                if (meFragment == null) {
                    meFragment = new MeFragments();
                }
                commitFragment(meFragment);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            fm.addOnBackStackChangedListener(new OnBackStackChangedListener() {

                @Override
                public void onBackStackChanged() {
                    // TODO Auto-generated method stub
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0)
                        finish();

                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //backheader
        if (v == footer1) {
            loadView(1);
        } else if (v == footer2) {
            loadView(2);
        } else if (v == footer3) {
            loadView(3);
        } else if (v == footer4) {
            loadView(4);
        } else if (v.getId() == R.id.header_right_progress) {
            loadWeeklySummaryWebKit();
        } else if (v == calendarIcon) {
            launchActivity(UpgradeActivity.class);
        } else if (fragmentIndex == 1) { //meals
            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv || v.getId() == R.id.badge_notif) {
                launchActivity(NotificationActivity.class);
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            }
        } else if (fragmentIndex == 2) { //message
            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            }

        } else if (fragmentIndex == 3) { //progress
            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            }

        } else if (fragmentIndex == 4) { //me
            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            }

        } else if (fragmentIndex == 5) { //help & contact
            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            }
        }
    }

    public void launchActivity(Class obj) {
        Intent intent = new Intent(this, obj);
        startActivity(intent);
    }

    private void loadWeeklySummaryWebKit() {
        Intent mainIntent;
        mainIntent = new Intent(this, WebkitActivity.class);
        mainIntent.putExtra("URL", WebServices.getURL(WebServices.SERVICES.URL_WEEKLY_SUMMARY));
        mainIntent.putExtra("TITLE", getString(R.string.MYWEEKLYSUMMARY_TITLE));

        startActivity(mainIntent);
    }


    synchronized void uploadMeal(final Meal meal, final String username) {
        if (addmealController == null) {
            addmealController = new AddMealController(this, this, null, this, Meal.MEALSTATE_ADD);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addmealController.uploadMeal(meal, username);
            }
        });
    }

    private void uploadPhoto(Meal meal) {

        GetImageUploadController getImageUploadController = new GetImageUploadController(this, this, this);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();
        currentMeal = meal;
        if (userID != null) {
            getImageUploadController.startImageUpload(userID, meal.meal_id);
        }
    }


    public void processPostMeal(Boolean hasphoto, String tempMealID) {
        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(tempMealID);
        currentMeal = meal;
        if (meal == null)
            return;
        if (hasphoto) {
            uploadPhoto(meal);
        } else {
            String userID = ApplicationEx.getInstance().userProfile.getRegID();
            if (userID != null) {
                uploadMeal(meal, userID);
            }
        }
    }

    @Override
    public void uploadMealSuccess(String response) {
        // TODO Auto-generated method stub
        Log.i("uploadmealSuccess: ", response);
    }

    @Override
    public void uploadMealFailedWithError(MessageObj response, String entryID) {
        // TODO Auto-generated method stub
        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(entryID);

        MealDAO daomeal = new MealDAO(this, null);
        meal.meal_status = "FAILED";
        daomeal.updateMeal(meal);

        //broadcast the update
        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_photo_download_update));
        sendBroadcast(broadint);
    }

    public void mealGraphSuccess(List<GraphMeal> response, int mealtotal) {
        // TODO Auto-generated method stub
        callGraph(mealtotal, response);
    }

    private void callGraph(int total, List<GraphMeal> response) {

        //display CRM PAGES
        Intent mainIntent = new Intent(this, CRMActivity.class);

        if (ApplicationEx.getInstance().userProfile != null) {

            if (ApplicationEx.getInstance().userProfile.getMember_type() != MEMBER_TYPE.FREE) {
                mainIntent.putExtra("ISPREMIUM_USER", true);
            }

            long[] dates = new long[response.size()];
            int[] postcount = new int[response.size()];

            for (int i = 0; i < response.size(); i++) {
                GraphMeal item = new GraphMeal();
                item.date = response.get(i).date;
                item.mealcount = response.get(i).mealcount;
            }

            Collections.sort(response, new Comparator<GraphMeal>() {
                public int compare(GraphMeal o1, GraphMeal o2) {
                    return o1.date.compareTo(o2.date);
                }
            });

            for (int i = 0; i < response.size(); i++) {
                dates[i] = response.get(i).date.getTime();
                postcount[i] = response.get(i).mealcount;
            }
            mainIntent.putExtra("DATES", dates);
            mainIntent.putExtra("POSTCOUNT", postcount);
        }
        mainIntent.putExtra("TOTAL_MEAL", total);
        startActivityForResult(mainIntent, RESULTCODE_CRMDONE);
    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub
    }

    @Override
    public void stopProgress() {
        // TODO Auto-generated method stub
    }

    @Override
    public void BitmapUploadSuccess(Boolean forUpload, String mealId) {
        // TODO Auto-generated method stub
        if (forUpload) {
            //find the meal in the meal list
            Meal meal = ApplicationEx.getInstance().mealsToAdd.get(mealId);
            String username = ApplicationEx.getInstance().userProfile.getRegID();

            if (username != null) {
                uploadMeal(meal, username);
            }
        }
    }

    @Override
    public void BitmapUploadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        //broadcast the update
        Log.i("BitmapUploadError", "" + response);
        MealDAO daomeal = new MealDAO(this, null);
        currentMeal.meal_status = "FAILED";
        daomeal.updateMeal(currentMeal);

        ApplicationEx.getInstance().tempList.remove(currentMeal.meal_id);
        ApplicationEx.getInstance().tempList.put(currentMeal.meal_id, currentMeal);

        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_photo_download_update));
        sendBroadcast(broadint);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ApplicationEx.REQUESTCODE_MEALADD) {
            if (resultCode == Activity.RESULT_OK) {
                //check if there is an add meal
                //check if there is a photo
                Boolean hasPhoto = data.getBooleanExtra("withphoto", false);
                Boolean isAdd = data.getBooleanExtra("isAdd", false);
                Boolean isLoadCoach = data.getBooleanExtra("LOADCOACH", false);

                String tempMealID = data.getStringExtra("tempmealid");
                //broadcast the update
                Intent broadint = new Intent();
                broadint.setAction(this.getResources().getString(R.string.meallist_photo_download_update));
                sendBroadcast(broadint);

                if (isLoadCoach) {
                    //load coach here
                    try {
                        String coachid = ApplicationEx.getInstance().userProfile.getCoach().coach_id;
                        Intent mainIntent;
                        mainIntent = new Intent(this, PaymentQuestionActivity.class);
                        mainIntent.putExtra("ID", coachid); //coachID
                        mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_PAYMENT); //webstate = payment
                        this.startActivity(mainIntent);
                    } catch (Exception e) {
                    }

                } else if (!isAdd) { // add meals has been processed at mealAddActivity only process non Addmeal
                    processPostMeal(hasPhoto, tempMealID);
                }
            }
        } else if (requestCode == ApplicationEx.REQUESTCODE_MEALVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                Boolean isDeleted = data.getBooleanExtra("IS_DELETED", false);
                //broadcast the update to fresh the page
                if (isDeleted) {
                    Intent broadint = new Intent();
                    broadint.setAction(this.getResources().getString(R.string.meallist_deletemeal_refresh));
                    sendBroadcast(broadint);
                }
            }
        } else if (requestCode == REQUEST_OAUTH) {
            Log.e("GoogleFit", "REQUEST_OAUTH");

            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mApiClient.isConnecting() && !mApiClient.isConnected()) {
                    mApiClient.connect();
                }
                registerDeviceToAPI();
                ApplicationEx.getInstance().setGoogleFitAllowed(this, true);
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("GoogleFit", "RESULT_CANCELED");
                ApplicationEx.getInstance().setGoogleFitAllowed(this, false);

                Intent broadint = new Intent();
                broadint.setAction(getString(R.string.workoutListUpdate));
                sendBroadcast(broadint);
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }

    }

    @Override
    public void download(String photoId, String url, String mealId) {

        new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
        //broadcast to needed pages

        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_getavatar));
        sendBroadcast(broadint);
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    private void showFirstConnectionMessage() {

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("");
        alertDialog.setMessage(this.getResources().getString(R.string.FIRST_CONNECTION_MESSAGE));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void logout() {
        ApplicationEx.getInstance().setIsLogin(this, false);
        //clear the saved login credentials
        ApplicationEx.getInstance().clearLoginCredentials();
        ApplicationEx.getInstance().currentSelectedDate = AppUtil.getCurrentDateinDate();

        Intent mainIntent = new Intent(this, LoginPageActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //restart sessions
        try {

            if (ApplicationEx.getInstance().anxaMatsController == null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this, ApplicationEx.getInstance().userProfile.getRegID());
            }

            ApplicationEx.getInstance().anxaMatsController.closeSession(this);

        } catch (Exception E) {

        }
        startActivity(mainIntent);
    }

    private void getNotifications() {
        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.getNotifications(ApplicationEx.getInstance().userProfile.getRegID()/*,-1*/);
    }

    @Override
    public void getNotificationSuccess(String response) {
        // TODO Auto-generated method stub
        List<Notification> notificationList = new ArrayList<>(ApplicationEx.getInstance().notificationList.values());

        if (notificationList != null && notificationList.size() > 0) {
            notifCount = notificationList.size();
        }

        // Create a new Fragment to be placed in the activity layout
        if (tabIndex == 1) {//default
            currentFragment = new MealsFragments();
            updateHeader(1, this);
        }
    }


    @Override
    public void getNotificationFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
    }

    public void markNotifAsReadSuccess(String response) {
//        System.out.println("markNotifAsReadSuccess" + response);

    }

    public void markNotifAsReadFailedWithError(MessageObj response) {

    }

    public void markAllNotifAsReadSuccess(String response) {
    }

    public void markAllNotifAsReadFailedWithError(MessageObj response) {
    }

    public void clearAllNotifSuccess(String response) {
    }

    public void clearAllNotifFailedWithError(MessageObj response) {
    }

    /**
     * SeekBar Listener
     **/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        //send to api
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (seekBar == progressSeekBar) {
            switch (progress) {
                case 0:
                    weight_tv.setVisibility(View.VISIBLE);
                    steps_tv.setVisibility(View.INVISIBLE);
                    meals_tv.setVisibility(View.INVISIBLE);
                    break;
                case 1:
                    weight_tv.setVisibility(View.INVISIBLE);
                    steps_tv.setVisibility(View.VISIBLE);
                    meals_tv.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    weight_tv.setVisibility(View.INVISIBLE);
                    steps_tv.setVisibility(View.INVISIBLE);
                    meals_tv.setVisibility(View.VISIBLE);
                    break;
                default:
                    weight_tv.setVisibility(View.VISIBLE);
                    steps_tv.setVisibility(View.INVISIBLE);
                    meals_tv.setVisibility(View.INVISIBLE);
                    break;
            }

            Intent broadint = new Intent();
            broadint.setAction(this.getResources().getString(R.string.progress_refresh));
            sendBroadcast(broadint);

        } else if (seekBar == progressSeekBar_clickable) {
            ApplicationEx.getInstance().selectedProgressMenu = progress;
            progressSeekBar.setProgress(progress);
        }
    }


    public static void stopWork() {
        ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
        Runnable longRunningTask = new Runnable() {
            @Override
            public void run() {

            }
        };

        // submit task to threadpool:
        Future longRunningTaskFuture = threadPoolExecutor.submit(longRunningTask);
        // At some point in the future, if you want to kill the task:
        longRunningTaskFuture.cancel(true);
    }

    public void registerDeviceSuccess(String response) {
        System.out.println("registerDeviceSuccess");

//        postStepsDataToAPI();

    }

    public void registerDeviceFailedWithError(MessageObj response) {
        System.out.println("registerDeviceFailedWithError");
    }

    private void postStepsDataToAPI() {

        Log.e("History", "postStepsDataToAPI ");


        if (postStepsGoogleFitController == null) {
            postStepsGoogleFitController = new PostStepsGoogleFitController(this, this);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postStepsGoogleFitController.postSteps(ApplicationEx.getInstance().userProfile.getRegID(), workoutObjArray);
            }
        });
    }

    public void postStepsGoogleFitSuccess(String response) {
        Intent broadint = new Intent();
        broadint.setAction(getString(R.string.workoutListUpdate));
        sendBroadcast(broadint);
    }

    public void postStepsGoogleFitFailedWithError(MessageObj response) {

    }

    private void showStepsFirstLaunchDialog() {

        final Dialog stepsDialog = new Dialog(this);
        stepsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        stepsDialog.setContentView(R.layout.stepsdialog);
        stepsDialog.setCancelable(true);

        //set up button
        Button button_later = (Button) stepsDialog.findViewById(R.id.steps_later_btn);
        button_later.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stepsDialog.dismiss();
            }
        });

        //set up button
        Button button_yes = (Button) stepsDialog.findViewById(R.id.steps_yes_btn);
        button_yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getGoogleAccount();
                stepsDialog.dismiss();
            }
        });
        //now that the dialog is set up, it's time to show it
        if (!stepsDialog.isShowing())
            stepsDialog.show();
    }

    public void getGoogleAccount() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    private void showGoogleFitWebView() {

        final Dialog settingsDialog = new Dialog(this);
        settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WebView wv = new WebView(this);
        wv.loadUrl(WebServices.getURL(WebServices.SERVICES.GOOGLE_FIT_SUPPORT));

        settingsDialog.setContentView(wv);

        if (!settingsDialog.isShowing())
            settingsDialog.show();

    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }

    private void displayLastWeeksData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        //endTime
        now = AppUtil.getEndOfTheDay(now);
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        Log.e("History", "Range End: " + now);
        Log.e("History", "Range End in Millis: " + endTime);

        //startTime
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date startDate = AppUtil.getStartOfTheDay(cal.getTime());
        cal.setTime(startDate);
        long startTime = cal.getTimeInMillis();

        Log.e("History", "Range Start: " + startDate);
        Log.e("History", "Range Start: " + startTime);

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }

        }

        Log.e("History", "Steps Array : " + stepsTimeTable);

        new ViewWeekCalorieCountTask().execute();

    }

    private void showDataSet(DataSet dataSet) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        int steps = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                if (field.getName().equalsIgnoreCase("steps")) {
                    steps = dp.getValue(field).asInt();
                }
            }

            try {
                String dateKey = AppUtil.getDateinStringKey(dateFormat.parse(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))));
                stepsTimeTable.put(dateKey, steps);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private class ViewWeekCalorieCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksCaloriesData();
            return null;
        }
    }

    private void displayLastWeeksCaloriesData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        now = AppUtil.getEndOfTheDay(now);
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        Log.e("History", "Range End: " + now);
        Log.e("History", "Range End in Millis: " + endTime);

        cal.add(Calendar.WEEK_OF_YEAR, -1);
        Date startDate = AppUtil.getStartOfTheDay(cal.getTime());
        cal.setTime(startDate);
        long startTime = cal.getTimeInMillis();

        Log.e("History", "Range Start: " + startDate);
        Log.e("History", "Range Start: " + startTime);

        //Check how many calories were recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showCalorieDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showCalorieDataSet(dataSet);
            }
        }

        Log.e("History", "Calories Array : " + caloriesTimeTable);

        new ViewWeekDistanceCountTask().execute();
    }

    private void showCalorieDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        float calories = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));

                if (field.getName().equalsIgnoreCase("calories")) {
                    calories = dp.getValue(field).asFloat();
                }

            }

            try {
                String dateKey = AppUtil.getDateinStringKey(dateFormat.parse(dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS))));
                caloriesTimeTable.put(dateKey, calories);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    private class ViewWeekDistanceCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksDistanceData();
            return null;
        }
    }

    private void displayLastWeeksDistanceData() {

        Calendar calNow = Calendar.getInstance();
        Date now = new Date();
        now = AppUtil.getEndOfTheDay(now);
        calNow.setTime(now);

        for (int i = 0; i <= 7; i++) {

            activityTime = 0;

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, -i);

            Date endDate = AppUtil.getEndOfTheDay(cal.getTime());
            cal.setTime(endDate);
            long endTime = cal.getTimeInMillis();

            Log.e("History", "Range End in Millis: " + endTime);

            Date startDate = AppUtil.getStartOfTheDay(cal.getTime());
            Log.e("History", "Range Start: " + startDate);
            cal.setTime(startDate);
            long startTime = cal.getTimeInMillis();
            Log.e("History", "Range Start: " + startTime);

            String dateKey = AppUtil.getDateinStringKey(startDate);

            DataReadResult dataReadResult = Fitness.HistoryApi.readData(mApiClient, queryFitnessActivityTimeData(startDate, endDate)).await(1, TimeUnit.MINUTES);

            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    Log.e("History", "activity buckets: " + bucket.getActivity());

                    long activeTime = bucket.getEndTime(TimeUnit.MINUTES) - bucket.getStartTime(TimeUnit.MINUTES);
                    if (!bucket.getActivity().equalsIgnoreCase("unknown") && !bucket.getActivity().equalsIgnoreCase("still") && !bucket.getActivity().equalsIgnoreCase("in_vehicle")) {
                        activityTime = activityTime + activeTime;
                    }
                    Log.e("History", "activity buckets: " + activityTime);
                }
            }
            //Used for non-aggregated data
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());

            }
            activityTimeTable.put(dateKey, activityTime);
        }

        Log.e("History", "Activity Array: " + activityTimeTable);

        buildStepsObjectArray();


    }

    public DataReadRequest queryFitnessActivityTimeData(Date startDate, Date endDate) {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        DataSource ACTIVITY_SEGMENT = new DataSource.Builder()
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_activity_segment")
                .setAppPackageName("com.google.android.gms")
                .build();

        // [END build_read_data_request]
        return new DataReadRequest.Builder()
                .aggregate(ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByActivitySegment(1, TimeUnit.SECONDS)
                .setTimeRange(startDate.getTime(), endDate.getTime(), TimeUnit.MILLISECONDS)
                .build();
    }

    private void buildStepsObjectArray(){

        Log.e("History", "buildStepsObjectArray ");


        workoutObjArray = new ArrayList<Workout>();

        try{
            Calendar calNow = Calendar.getInstance();
            Date now = new Date();
            now = AppUtil.getEndOfTheDay(now);
            calNow.setTime(now);

            for (int i = 0; i <= 7; i++) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, -i);
                Date startDate = AppUtil.getStartOfTheDay(cal.getTime());

                Date endDate = AppUtil.getEndOfTheDay(cal.getTime());
                String dateKey = AppUtil.getDateinStringKey(endDate);

                Workout workoutObj = new Workout();
                Log.e("History", "buildStepsObjectArray: " + dateKey);
                Log.e("History", "buildStepsObjectArray " + activityTimeTable.get(dateKey));
                workoutObj.exercise_datetime = endDate;
                workoutObj.exercise_state = Workout.EXERCISE_STATE.ADD_ONGOING;
                workoutObj.exercise_type = Workout.EXERCISE_TYPE.ACTIVITY_IOS;
                workoutObj.device_name = "Google Fit";
                workoutObj.activity_id = "1234567";
                if (stepsTimeTable.get(dateKey)!=null) {
                    workoutObj.steps = stepsTimeTable.get(dateKey);
                }else{
                    workoutObj.steps = 0;
                }
                if (caloriesTimeTable.get(dateKey)!=null) {
                    workoutObj.calories = caloriesTimeTable.get(dateKey).intValue();
                }else{
                    workoutObj.calories = 0;
                }
                if (activityTimeTable.get(dateKey)!=null) {
                    workoutObj.duration = activityTimeTable.get(dateKey).intValue();
                }else{
                    workoutObj.duration = 0;
                }


                workoutObjArray.add(workoutObj);
            }

            postStepsDataToAPI();

        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }
}
