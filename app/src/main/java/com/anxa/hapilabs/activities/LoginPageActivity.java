package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetCoachListener;
import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.notification.local.NotificationManager;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.controllers.messages.MessageController;
import com.anxa.hapilabs.controllers.notifications.NotificationController;
import com.anxa.hapilabs.controllers.selectcoach.CoachSelectionController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CustomDialog;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginPageActivity extends HAPIActivity implements ProgressChangeListener, LoginListener,
        GetNotificationListener, GetCoachListener, BitmapDownloadListener, GetMessagesListener {

    private Button login_btn;
    private Button signup_btn;
    private TextView forgot_tv;
    private EditText login_username_et;
    private EditText login_password_et;

    private LoginController controller;

    private Boolean fromPayment = false;

    private MessageController msgController;
    private NotificationController notifController;
    private CoachSelectionController coachController;

    private CustomDialog dialog;

    private List<Coach> coachArray = new ArrayList<Coach>();
    private CoachDAO coachDAO;
    private DaoImplementer implDao;

    CallbackManager callbackManager;
    LoginButton loginButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // NOTE: check if there is an existing login in the userProfile
        // if user exit do not login, start with sync
        // otherwise show login page

        coachDAO = new CoachDAO(this, null);
        implDao = new DaoImplementer(coachDAO, this);

        if (ApplicationEx.getInstance().isLogin(this)) {
            UserProfileDAO userdao = new UserProfileDAO(this, null);
            ApplicationEx.getInstance().userProfile = userdao.getUserProfile();
            clearTables(); //except userprofile dao

            if (ApplicationEx.getInstance().userProfile == null) {
                onCreateInit();
                callNotif();
            } else {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this, ApplicationEx.getInstance().userProfile.getRegID());
                ApplicationEx.getInstance().anxaMatsController.isLogin(true);

                //call once after login then everytime the user goes to tab
                getMessages();

                //call once after login then every time the user call Coach selection(free user)
                getCoaches();
                callSync();
                callNotif();
            }
        } else {

            //logout fb
            LoginManager.getInstance().logOut();

            onCreateInit();
            callNotif();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void onCreateInit() {
        fromPayment = getIntent().getBooleanExtra("ISFROMPAYMENT", false);

        controller = new LoginController(this, this, this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        if (fromPayment && ApplicationEx.getInstance().userProfile != null) {

            System.out.println("onCreateInit fromPayment");

            String username = ApplicationEx.getInstance().userProfile.getUsername();

            String password = ApplicationEx.getInstance().userProfile.getPasswordPlain();

            controller.startLogin(username, password, false);
            ApplicationEx.getInstance().fromFBConnect = false;


        } else {

            FacebookSdk.sdkInitialize(this.getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            // App code
                            System.out.println("loginManager onSuccess");
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            // App code
                            System.out.println("loginManager onError" + exception.toString());

                        }
                    });

            setContentView(R.layout.login);

            signup_btn = (Button) findViewById(R.id.footerbutton);

            updateFooterButton(getResources().getString(R.string.NAVIGATION_SIGNUP), this);

            login_username_et = (EditText) findViewById(R.id.login_username_et);
            login_password_et = (EditText) findViewById(R.id.login_password_et);

            login_btn = (Button) findViewById(R.id.login_loginbtn);

            forgot_tv = (TextView) findViewById(R.id.login_forgotpassword);

            login_btn.setOnClickListener(this);

            forgot_tv.setOnClickListener(this);

            if (ApplicationEx.getInstance().getSavedUserName(this)!=null || ApplicationEx.getInstance().getSavedUserName(this).length() > 1){
                login_username_et.setText(ApplicationEx.getInstance().getSavedUserName(this));
            }

            loginButton = (LoginButton)findViewById(R.id.fb_login_button);
            loginButton.setReadPermissions(Arrays.asList("email"));

            // Other app specific specialization

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    getPublicProfile(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    // App code
                    System.out.println("onCancel: " );

                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
//            if(ApplicationEx.getInstance().userProfile != null) {
//                login_username_et.setText(ApplicationEx.getInstance().userProfile.getUsername());
//            }

            initProgress();
        }


        //clean initial data
        ApplicationEx.getInstance().mealsToAdd = new Hashtable<String, Meal>();
        ApplicationEx.getInstance().tempList = new Hashtable<String, Meal>();
        ApplicationEx.getInstance().userProfile = new UserProfile();
        ApplicationEx.getInstance().tempHapimomentList = new Hashtable<String, HapiMoment>();
        ApplicationEx.getInstance().workoutList = new Hashtable<String, Workout>();
        ApplicationEx.getInstance().messageList = new Hashtable<String, Object>();
        ApplicationEx.getInstance().notificationList = new Hashtable<String, Notification>();


    }

    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {

        if (view == signup_btn) {

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            callRegister();

        } else if (view == login_btn) {

            try {
                setHideSoftKeyboard(login_username_et);
            } catch (Exception e) {
            }

            try {
                setHideSoftKeyboard(login_password_et);
            } catch (Exception e) {
            }

            if (validateLogin()) {
                startProgress();
                controller.startLogin(login_username_et.getText().toString(), login_password_et.getText().toString(), false);
                ApplicationEx.getInstance().fromFBConnect = false;
            }

        } else if (view == forgot_tv) {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.OtherButton) {
            dialog.dismiss();
        }
    }

    @Override
    public void startProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                startProgressinParent();
            }
        });
    }

    @Override
    public void stopProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                stopProgressinParent();

            }
        });
    }

    private void callRegister() {

        ApplicationEx.getInstance().setIsLogin(this, false);
        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, LandingScreenActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("fromLogin", true);
        this.startActivity(mainIntent);

    }

    private void clearTables() {
        MessageDAO messageDAO = new MessageDAO(this, null);
        messageDAO.clearTable();
        messageDAO.clearTable();

        NotificationDAO notificationDAO = new NotificationDAO(this, null);
        notificationDAO.clearTable();
        notificationDAO.clearTable();

    }

    private void callNotif() {
        new NotificationManager(this);
    }

    private void callSync() {
        if (ApplicationEx.getInstance().userProfile != null && ApplicationEx.getInstance().userProfile.getPic_url_large() != null && ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null)
            downloadpics();

        //remove messages, coaches here should only be called once or whem user is in the message tab.
        //notifications
        getNotifications();

        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("ISFROMPAYMENT", false);
        this.startActivity(mainIntent);

    }

    private void download(String photoId, String url, String mealId) {
        new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    private void downloadpics() {
        //download profile pic
        String url = ApplicationEx.getInstance().userProfile.getPic_url_large();
        url = url.replace("https", "http");
        download(ApplicationEx.getInstance().userProfile.getRegID(), url, "0");
        //download coach pic
        if (ApplicationEx.getInstance().userProfile.getCoach() != null) {
            Coach _coach = ApplicationEx.getInstance().userProfile.getCoach();
            download(_coach.coach_id, _coach.avatar_url, "0");
        }
    }

    private void callWelcome(String id) {
        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, PaymentQuestionActivity.class);
        mainIntent.putExtra("ID", id);
        mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_WELCOME); //webstate 1 = welcome
        this.startActivity(mainIntent);
    }

    private void getMessages() {
        if (msgController == null) {
            msgController = new MessageController(this, this);
        }

        Date fromDate = AppUtil.getCurrentDateinDate();
        msgController.getPrevious(String.valueOf(AppUtil.dateToUnixTimestamp(fromDate)), "10");
    }

    private void getNotifications() {
        if (notifController == null) {
            notifController = new NotificationController(this, this);
        }
        notifController.getNotifications(ApplicationEx.getInstance().userProfile.getRegID());
    }

    private void getCoaches() {
        if (coachController == null) {
            coachController = new CoachSelectionController(this, this, this);
        }
        coachController.getCoach(ApplicationEx.getInstance().userProfile.getRegID());
    }

    @Override
    public void loginSuccess(String response) {

        ApplicationEx.getInstance().saveLoginCredentials(this, login_username_et.getText().toString(), login_password_et.getText().toString());

        stopProgress();
        //check if your need to call the welcome page.

        //logout fb
        LoginManager.getInstance().logOut();

        //TODO: call get Sync
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //register for push();

                try {
                    checkGCM(ApplicationEx.getInstance().userProfile.getRegID());
                } catch (Exception e) {
                }

                downloadpics();

                ApplicationEx.getInstance().setIsLogin(LoginPageActivity.this, true);

                //check if you need to launch the welcome page
                if (ApplicationEx.getInstance().hasWelcome && fromPayment) {
                    ApplicationEx.getInstance().hasWelcome = true;
                    callWelcome(ApplicationEx.getInstance().userProfile.getRegID());

                } else {
                    if (ApplicationEx.getInstance().anxaMatsController != null)
                        ApplicationEx.getInstance().anxaMatsController.isLogin(true);
                    //call once after login then everytime the user goes to tab
                    getMessages();

                    //call once after login then every time the user call Coach selection(free user)
                    callSync();
                    callNotif();
                }
            }
        });
    }

    @Override
    public void loginFailedWithError(MessageObj response) {

        //logout fb
        LoginManager.getInstance().logOut();
        String message = response.getMessage_string();

        if (message == null) {
            message = getResources().getString(R.string.ALERTMESSAGE_LOGIN_EMPTY);
        } else if (message.contains("offline")) {
            message = getResources().getString(R.string.ALERTMESSAGE_OFFLINE);
        }

        final String messageDialog = message;
        //else if message is offline: display offline alert message
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //
                showLoginDialog(messageDialog, getResources().getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
            }
        });

        stopProgress();
    }

    @Override
    public void getCoachSuccess(String response, List<Coach> coaches) {

        //select coach list based on current language; for en show only en coaches for fr show only fr coaches
        List<Coach> coachbyLanguage = new ArrayList<Coach>();

        for (int i = 0; i < coaches.size(); i++) {

            Coach coach = coaches.get(i);


            if (ApplicationEx.language.equals("fr")) { //get french only coaches
                if (coach.coach_profile_fr != null && coach.coach_profile_fr.length() > 0) {
                    coachbyLanguage.add(coach);
                }

            } else {//ge english as default
                if (coach.coach_profile_en != null && coach.coach_profile_en.length() > 0) {
                    coachbyLanguage.add(coach);
                }

            }
            coachArray.add(coach);
        }

        startImageDownload(coachbyLanguage);

    }


    private void startImageDownload(final List<Coach> coaches) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {


                    for (int j = 0; j < coaches.size(); j++) {
                        final int coachindex = j;

                        runOnUiThread(new Runnable() {


                            Coach coach = (Coach) coaches.get(coachindex);

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                download(coach.coach_id, coach.avatar_url, coach.coach_id);

                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
        Bitmap bmp = ImageManager.getInstance().findImage(photoId);

        if (photoId.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID())) {
            return;
        }

        for (int i = 0; i < coachArray.size(); i++) {
            Coach coach = coachArray.get(i);
            if (photoId.equalsIgnoreCase(coach.coach_id)) {

                coach.coach_photo = new Photo();
                coach.coach_photo.image = bmp;
                coachArray.set(i, coach);

                implDao.add(coach);
            }
        }
    }


    @Override
    public void loginServices(String username, String password, String data,
                              Handler responseHandler) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getNotificationSuccess(String response) {
        // TODO Auto-generated method stub
        // getMessage API SUCCESSFULLY RECEIVED. if your not in the NOtificationFragment there is no need to refresh the screen

        //update timestamp
        setLastTimeStamp(this, ApplicationEx.getInstance().fromDateNotificationSync);
    }

    //todo need to remove tis method and move elsewhere:
    private void setLastTimeStamp(Context context, long lasttimestamp) {

        final SharedPreferences prefs = ApplicationEx.getInstance().getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(ApplicationEx.PROPERTY_SYNCNOTIF_TODATE, lasttimestamp);
        editor.commit();
    }


    @Override
    public void getNotificationFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    public void markNotifAsReadSuccess(String response){}
    public void markNotifAsReadFailedWithError(MessageObj response){}

    public void markAllNotifAsReadSuccess(String response) {
    }

    public void markAllNotifAsReadFailedWithError(MessageObj response) {
    }

    public void clearAllNotifSuccess(String response) {
    }

    public void clearAllNotifFailedWithError(MessageObj response) {
    }


    @Override
    public void getMessagesSuccess(String response) {
        // TODO Auto-generated method stub
        // getMessage API SUCCESSFULLY RECEIVED. if your not in the MessageFragment there is no need to refresh the screen


    }

    @Override
    public void getMessagesFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        // getMessage API FAILED RECEIVED. if your not in the MessageFragment there is no need to refresh the screen

    }

    @Override
    public void getCoachFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }


    private void showLoginDialog(String message, String title) {

        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, message, title, this);
        dialog.show();
    }

    public void postMessagesSuccess(String response) {
    }

    public void postMessagesError(MessageObj response) {
    }

    private Boolean validateLogin() {
        if (login_username_et.getText() == null || login_username_et.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_EMPTY));
            return false;
        } else if (!isEmail(login_username_et.getText().toString())) {
            displayToastMessage(getString(R.string.SIGNUP_EMAIL_ERROR));
            return false;
        } else if (login_password_et.getText() == null || login_password_et.getText().length() <= 0) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_LOGIN_FAILED));
            return false;
        } else {
            return true;
        }
    }

    private boolean isEmail(String email) {
        Pattern pattern1 = Pattern
                .compile("^([a-zA-Z0-9_.+-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

        Matcher matcher1 = pattern1.matcher(email);

        return matcher1.matches();
    }

    private void getPublicProfile(AccessToken accessToken){

        System.out.println("getPublicProfile: ");

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            if (object != null) {

                                startProgress();
                                controller.startLogin(object.getString("email"), object.optString("email"), true);
                                ApplicationEx.getInstance().fromFBConnect = true;
//                                controller.startLogin(object.getString("email"), AppUtil.shaHashed(object.optString("email")));
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
