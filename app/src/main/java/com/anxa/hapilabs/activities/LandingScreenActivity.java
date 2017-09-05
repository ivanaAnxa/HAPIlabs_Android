package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetCoachListener;
import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.connection.listener.GetNotificationListener;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.notification.local.NotificationManager;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.controllers.messages.MessageController;
import com.anxa.hapilabs.controllers.notifications.NotificationController;
import com.anxa.hapilabs.controllers.selectcoach.CoachSelectionController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
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

import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by aprilanxa on 4/5/2016.
 */
public class LandingScreenActivity extends HAPIActivity implements OnClickListener, GetMessagesListener, GetNotificationListener, GetCoachListener,
        LoginListener, ProgressChangeListener{

    Button getStartedButton;
    Button whatsInTheAppButton;
    TextView loginLink;

    String fbEmail;
    String fbPassword;

    private CustomDialog dialog;

    MessageController msgController;
    NotificationController notifController;
    CoachSelectionController coachController;
    LoginController loginController;

    private Boolean fromPayment = false;

    List<Coach> coachArray = new ArrayList<Coach>();


    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        loginController = new LoginController(this, this, this);

        callbackManager = CallbackManager.Factory.create();
        fromPayment = getIntent().getBooleanExtra("ISFROMPAYMENT", false);


        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reg_update);
//        setContentView(R.layout.landing_screen);

        getStartedButton = (Button)findViewById(R.id.landing_getstarted_email);
//        whatsInTheAppButton = (Button)findViewById(R.id.landing_whatsintheapp);
//        loginLink = (TextView)findViewById(R.id.landing_loginlink);

        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("user_birthday","email"));
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

        if (ApplicationEx.getInstance().isLogin(this)) {
            //autologin
            UserProfileDAO userdao = new UserProfileDAO(this, null);
            ApplicationEx.getInstance().userProfile = userdao.getUserProfile();
            clearTables(); //except userprofile dao

            if (ApplicationEx.getInstance().userProfile!= null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this, ApplicationEx.getInstance().userProfile.getRegID());
                ApplicationEx.getInstance().anxaMatsController.isLogin(true);

                //call once after login then everytime the user goes to tab
                getMessages();

                //call once after login then every time the user call Coach selection(free user)
                getCoaches();
                callSync();
                callNotif();
            }
        }else{
            System.out.println("LandingScreenActivity !isLogin");

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Boolean isFromLogin = false;
                isFromLogin = extras.getBoolean("fromLogin");
                if (!isFromLogin) {
                    showTourPage();
                }
            }else{
                showTourPage();
            }
        }
    }

    private void clearTables() {
        MessageDAO messageDAO = new MessageDAO(this, null);
        messageDAO.clearTable();
        messageDAO.clearTable();

        NotificationDAO notificationDAO = new NotificationDAO(this, null);
        notificationDAO.clearTable();
        notificationDAO.clearTable();

    }

    @Override
    public void onClick(View v) {
        if (v == getStartedButton) {
            Log.i("getStarted", "button");
        }else if(v == whatsInTheAppButton){
            Log.i("whatsInTheAppButton", "button");
        }else if(v == loginLink){
            Log.i("loginLink", "button");
        }
    }

    public void callRegister(View view){
        Intent mainIntent = new Intent(this, MainObjectiveActivity.class);
        this.startActivity(mainIntent);
    }

    public void callLogin(View view){
        Intent mainIntent = new Intent(this, LoginPageActivity.class);
        mainIntent.putExtra("COACHID", "1");
        this.startActivity(mainIntent);
    }

    public void callFBLogin(View view){

    }

    private void showTourPage() {
        Intent mainIntent;
        mainIntent = new Intent(this, TourPageActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(mainIntent);
    }

    public void showWhatsInTheApp(View view){
        Intent mainIntent;
        mainIntent = new Intent(this, RegistrationTourPageActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(mainIntent);
    }


    private void getMessages() {

        if (msgController == null) {
            msgController = new MessageController(this, this);
        }

//        msgController.getMessages(ApplicationEx.getInstance().userProfile.getRegID());
        Date fromDate = AppUtil.getCurrentDateinDate();
        msgController.getLatest(String.valueOf(AppUtil.dateToUnixTimestamp(fromDate)), "10");
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


    public void startImageDownload(final List<Coach> coaches) {
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

    @Override
    public void getNotificationSuccess(String response) {
        // TODO Auto-generated method stub
        // getMessage API SUCCESSFULLY RECEIVED. if your not in the NOtificationFragment there is no need to refresh the screen
        //update timestamp
        setLastTimeStamp(this, ApplicationEx.getInstance().fromDateNotificationSync);
    }

    @Override
    public void getNotificationFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }
    private void download(String photoId, String url, String mealId) {

        new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    private void dowloadpics() {

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

    //todo need to remove tis method and move elsewhere:
    private void setLastTimeStamp(Context context, long lasttimestamp) {
        final SharedPreferences prefs = ApplicationEx.getInstance().getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(ApplicationEx.PROPERTY_SYNCNOTIF_TODATE, lasttimestamp);
        editor.commit();
    }

    private void callSync() {
        if (ApplicationEx.getInstance().userProfile != null && ApplicationEx.getInstance().userProfile.getPic_url_large() != null && ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null)
            dowloadpics();

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

    private void callNotif() {
        new NotificationManager(this);
    }

    public void postMessagesSuccess(String response) {
    }

    public void postMessagesError(MessageObj response){
    }

    public void markNotifAsReadSuccess(String response){
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

    private void getPublicProfile(AccessToken accessToken){

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
                                fbEmail = object.getString("email");
                                fbPassword = object.optString("email");
                                loginController.startLogin(object.getString("email"), object.optString("email"), true);

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

    @Override
    public void loginSuccess(String response) {

        ApplicationEx.getInstance().saveLoginCredentials(this, fbEmail, fbPassword);

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

                ApplicationEx.getInstance().setIsLogin(LandingScreenActivity.this, true);

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

    private void showLoginDialog(String message, String title) {

        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, message, title, this);
        dialog.show();
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

    @Override
    public void loginServices(String username, String password, String data,
                              Handler responseHandler) {
        // TODO Auto-generated method stub
    }
}
