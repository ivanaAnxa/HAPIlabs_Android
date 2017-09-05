package com.anxa.hapilabs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.fragments.HeaderFragments;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.BitmapUploadListener;
import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.PushRegListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.PushRegController;
import com.anxa.hapilabs.controllers.addmeal.AddMealController;
import com.anxa.hapilabs.controllers.addmeal.DeleteMealController;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.images.GetImageUploadController;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Photo.PHOTO_STATUS;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class HAPIActivity extends Activity implements OnClickListener, BitmapDownloadListener, PushRegListener, ProgressChangeListener, BitmapUploadListener, MealAddListener {

    static final String TAG = "HAPICoach";
    /*******
     * for PUSH
     *******/
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    HeaderFragments headerView;
    TextView header_tv;
    Button footerButton;
    ProgressBar progressbar;
    String SENDER_ID = "917695631036"; // project number for HAPICOACH App added
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    AddMealController addmealController;
    DeleteMealController deleteMealController;
    private String regid;

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
        /*0 = meal add
         *1 = meal view
         **2 = coach view*
         *3 = FUllscreen image */

    protected void initProgress() {
        progressbar = ((ProgressBar) findViewById(R.id.progressBar));

    }

    protected void updateHeader(int index, String headertitle, OnClickListener onclick) {
        updateHeader(index, headertitle, onclick, null, null);
    }

    protected void updateHeader(int index, String headertitle, OnClickListener onclick, String leftString, String rightString) {

        switch (index) {

            case 0: //add meals
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(getString(R.string.ALERTMESSAGE_MY_MEALS_TITLE), R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(getString(R.string.MEAL_OPTIONS_HEADER), -1, onclick); //text only center
//                headerView.setRightHeader(getString(R.string.btn_submit), -1, onclick); //text only center
                break;

            case 1: //view meals
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(getString(R.string.btn_options), -1, onclick); //text only center
//                headerView.setRightHeader(getString(R.string.btn_edit), -1, onclick); //text only center
                break;
            case 2: //coach
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center
                headerView.setLeftHeader(null, -1, onclick); //text only center

                break;
            case 3: //full screen image
                if (headerView == null)

                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center

                break;
            case 4: //webkits
                if (headerView == null)

                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center

                break;
            case 5: //calendar
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                headerView.setCenterHeader("", -1, onclick); //text only center
                headerView.setLeftHeader(headertitle, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(null, R.drawable.nav_calendar_icon, onclick); //calendar icon

                break;
            case 6: //your objective
                if (headerView == null) {
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                    headerView.setVisibility(View.VISIBLE);
                }
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center

                break;
            case 7: //your payment
            case 8: //welcome
            case 9: //questionaire
            case 10: //welcome 2
                if (headerView == null) {
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);

                    headerView.setVisibility(View.VISIBLE);
                }
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, -1, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center
                break;
            case 11: //edit meals

                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                if (leftString != null)
                    headerView.setLeftHeader(leftString, R.drawable.nav_arrow_back, onclick); //text only center
                else
                    headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center

                if (rightString != null)
                    headerView.setRightHeader(rightString, R.drawable.delete_btn, onclick); //text only center
                else
                    headerView.setRightHeader(null, R.drawable.delete_btn, onclick); //text only center

                break;
            case 12: //notifications
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, R.drawable.ic_clear_white_24dp, onclick); //text only center
                headerView.setRightHeader(null, R.drawable.ic_more_horiz_white_24dp, onclick); //no right header
//                headerView.setRightHeader(null, -1, onclick); //no right header

                break;
            case 13: //saving layout in add meal is visible
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, -1, onclick); //no left header
                headerView.setRightHeader(null, -1, onclick); //no right header
                break;
            case 14: //meal stats
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(leftString, R.drawable.nav_arrow_back, onclick); //no left header
                headerView.setRightHeader(null, -1, onclick); //no right header
                break;
            case 15: //hapimoment
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(null, -1, onclick); //text only center
//                headerView.setRightHeader(getString(R.string.btn_submit), -1, onclick); //text only center
                break;
            case 16: //view exercise
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick); //text only center
                headerView.setLeftHeader(getString(R.string.ALERTMESSAGE_MY_MEALS_TITLE), R.drawable.nav_arrow_back, onclick); //text only center
                headerView.setRightHeader(getString(R.string.MEAL_OPTIONS_HEADER), -1, onclick); //text only center
                break;
            case 17:
                if (headerView == null)
                    headerView = (HeaderFragments) findViewById(R.id.fragment_header);
                headerView.setCenterHeader(headertitle, -1, onclick);
                headerView.setLeftHeader(null, R.drawable.nav_arrow_back, onclick);
                headerView.setRightHeader(null, -1, onclick);
                break;

        }

    }

    protected void updateFooterButton(String headertitle, OnClickListener onclick) {
        if (footerButton == null)
            footerButton = (Button) findViewById(R.id.footerbutton);
        footerButton.setText(headertitle);
        footerButton.setOnClickListener(onclick);
    }

    protected void showMultimediaSelector() {
//    		Intent mainIntent = new Intent(this,Camera2Activity.class);
//    		overridePendingTransition(R.anim.pull_up_from_bottom, R.anim.pull_upout_from_bottom);
//    		mainIntent.putExtra("MEDIA_TYPE", imageCode);
//    		mainIntent.putExtra("ACTIVITY_TYPE", getIntent().getByteExtra("ACTIVITY_TYPE",ApplicationEx.ENRICH_MOMENT));
//    		startActivityForResult(mainIntent, MainFragmentActivity.REQUESTCODE_CALLCAMERA_ADDACTIVITY);
    }

    //to received the camera activity result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

    protected void showDateTimeSelector() {

    }

    protected void showDateSelector() {

    }

    protected void showTimeSelector() {

    }

    @Override
    public void onClick(View v) {
    }

    public void startProgressinParent() {
        // TODO Auto-generated method stub
        progressbar.setVisibility(View.VISIBLE);

    }

    public void stopProgressinParent() {
        // TODO Auto-generated method stub
        progressbar.setVisibility(View.GONE);

    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
//System.out.println("bitmapdownloadsuccess hapiactivity");
//        Bitmap bmp = ImageManager.getInstance().findImage(photoId);
//        if (bmp != null)
//            ApplicationEx.getInstance().userProfile.setUserProfilePhoto(bmp);

    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    public void displayToastMessage(final String message) {
        final Context context = this;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast m = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                m.show();

            }
        });
    }

    /**
     * create instance of GoogleCloud MEssage and Check if this is an existing
     * Reg ID
     * <p/>
     * If result is empty, the app needs to register.
     */
    public void checkGCM(String userIDFromLogin) {
        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {

            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(this);
            if (regid.isEmpty()) {
                registerInBackground();
            } else {
                // register to server/
                sendRegistrationIdToServer(regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(
                ApplicationEx.PROPERTY_REG_ID, "");

        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(
                ApplicationEx.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private String getUserId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String userID = prefs.getString(ApplicationEx.PROPERTY_USER_ID, "");
        if (userID.isEmpty()) {
            Log.i(TAG, "userId not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(
                ApplicationEx.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return userID;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences,
        // but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */

    private void registerInBackground() {
        final Context context = this;
        new AsyncTask<Object, Object, String>() {
            protected String doInBackground(Object... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }

                    regid = gcm.register(SENDER_ID);

                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your
                    // app.
                    // The request to your server should be authenticated if
                    // your app
                    // is using accounts.

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }

                return msg;
            }

            protected void onPostExecute(String msg) {
                sendRegistrationIdToServer(regid);

            }
        }.execute(null, null, null);
    }

    /**
     * Send the registration ID received from google to the server
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    public void sendRegistrationIdToServer(String deviceToken) {

        String ver = getResources().getString(R.string.app_version);
        String regID = ApplicationEx.getInstance().userProfile.getRegID();

        if (regID != null) {

            PushRegController pushController = new PushRegController(this, this);

            pushController.startPushReg(ApplicationEx.getInstance().SENDER_ID, deviceToken, ver, ApplicationEx.getInstance().GCM_ANXAPUNC_SHAREDKEY, ApplicationEx.getInstance().userProfile);

        }

    }

//	/**
//	 * Handler to received response for sending the message to the server
//	 * 
//	 */
//	final Handler regHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case Connection.REQUEST_START:
//				// show progress bar here
//				break;
//			case Connection.REQUEST_SUCCESS:
//				// TODO: store reg id so you dont have th request to google
//				// again
//				storeRegistrationId(regid);
//
//				break;
//			case Connection.REQUEST_ERROR:
//				// TODO:
//				// dismiss progress here
//				// get errro message obj
//				// display error messagereturnErrorMessage((String) msg.obj);
//				break;
//
//			}// end switch
//		}
//
//	};

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param regId registration ID
     */
    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getGCMPreferences(this);
        int appVersion = getAppVersion(this);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationEx.PROPERTY_REG_ID, regId);
        editor.putInt(ApplicationEx.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void storeUserId(Context context, String userID) {

        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ApplicationEx.PROPERTY_USER_ID, userID);
        editor.putInt(ApplicationEx.PROPERTY_APP_VERSION, appVersion);
        editor.commit();

    }

    @Override
    public void pushRegSuccess(String regid) {
        // TODO Auto-generated method stub
        storeRegistrationId(regid);


    }

    @Override
    public void pushRegFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    private void uploadPhoto(Meal meal) {

        GetImageUploadController getImageUploadController = new GetImageUploadController(this, this, this);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();
        if (userID != null) {
            getImageUploadController.startImageUpload(userID, meal.meal_id);
        }

    }


    public void processDeleteMeal(String tempMealID, MealAddListener meallistener) {
        //delete this meal on the main list

//		Intent broadint = new Intent();
//		broadint.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
//		sendBroadcast(broadint);
//		

        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(tempMealID);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();
        uploadMeal(meal, userID, Meal.MEALSTATE_DELETE, meallistener);

    }

    public void processUpdateMeal(Boolean hasphoto, String tempMealID, MealAddListener meallistener) {
        //broadcast the update

        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadint);

        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(tempMealID);

        if (meal == null)
            return;

        if (hasphoto) {
            //check if there are still any photos not uploaded

            for (int i = 0; i < meal.photos.size(); i++) {
                Photo photo = meal.photos.get(i);
                if (photo.state != PHOTO_STATUS.SYNC_UPLOADPHOTO) {
                    uploadPhoto(meal); //if atleats one photo is now yet sync
                    return;
                }
            }
        }


        String userID = ApplicationEx.getInstance().userProfile.getRegID();
        if (userID != null) {
            uploadMeal(meal, userID, Meal.MEALSTATE_EDIT, meallistener);
        }

    }

    synchronized void uploadMeal(final Meal meal, final String username, byte mealCommand, MealAddListener meallistener) {
        if (addmealController == null) {
            addmealController = new AddMealController(this, this, null, meallistener, mealCommand);
        }


        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                addmealController.uploadMeal(meal, username);

            }
        });
    }

    @Override
    public void uploadMealSuccess(String response) {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void uploadMealFailedWithError(MessageObj response, String entryID) {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void BitmapUploadSuccess(Boolean forUpload, String mealId) {
        // TODO Auto-generated method stub


        if (forUpload) {
            //find the meal in the meal list
            Meal meal = ApplicationEx.getInstance().mealsToAdd.get(mealId);

            String username = ApplicationEx.getInstance().userProfile.getRegID();

            if (username != null) {
                uploadMeal(meal, username, Meal.MEALSTATE_EDIT);
            }
        }

    }


    synchronized void uploadMeal(final Meal meal, final String username, byte mealCommand) {
        if (addmealController == null) {
            addmealController = new AddMealController(this, this, null, this, mealCommand);
        }


        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                addmealController.uploadMeal(meal, username);

            }
        });


    }

    @Override
    public void BitmapUploadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub


    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopProgress() {
        // TODO Auto-generated method stub

    }

    public void onDestroy() {
        try {
            if (ApplicationEx.getInstance().anxaMatsController == null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this, ApplicationEx.getInstance().userProfile.getRegID());
            }
            ApplicationEx.getInstance().anxaMatsController.isLogin(false);
            ApplicationEx.getInstance().anxaMatsController.closeSession(this);
        } catch (Exception E) {

        }

        super.onDestroy();
    }
//	public void onPause(){
//		try{
//			if (ApplicationEx.getInstance().anxaMatsController == null){
//				ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this,ApplicationEx.getInstance().userProfile.getRegID());
//			}
//			ApplicationEx.getInstance().anxaMatsController.closeSession(this);
//		}catch(Exception E){
//			
//		}
//	      super.onPause();
//	}

    public void onResume() {
        super.onResume();
        try {
            if (ApplicationEx.getInstance().anxaMatsController == null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(this, ApplicationEx.getInstance().userProfile.getRegID());
            }
            ApplicationEx.getInstance().anxaMatsController.isLogin(false);
            ApplicationEx.getInstance().anxaMatsController.startSession(this);
        } catch (Exception E) {
        }
        super.onPause();
        // do something
    }
}


