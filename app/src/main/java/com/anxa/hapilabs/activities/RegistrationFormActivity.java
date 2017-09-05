package com.anxa.hapilabs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.view.View.OnClickListener;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.RegistrationListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.controllers.registration.RegistrationController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.UserProfile.GENDER;
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

import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aprilanxa on 4/7/2016.
 */
public class RegistrationFormActivity extends HAPIActivity implements OnClickListener, RegistrationListener, LoginListener, ProgressChangeListener {

    final String TIMEZONE_DEFAULT = "Asia/Manila";
    final String GENDER_MALE = "1";
    final String GENDER_FEMALE = "0";
    RegistrationController controller;
    LoginController loginController;
    ImageButton manButton, womanButton;
    EditText firstNameText, lastNameText, emailText, passwordText;
    TextView countryTextView, manTextView, womanTextView;
    UserProfile userProfile = new UserProfile();
    GENDER gender;
    AlertDialog.Builder builder;
    AlertDialog genericDialog;

    Button submitButton;
//    ImageButton loginButton;
    LinearLayout progressLayout;

    LoginButton loginButton;
    CallbackManager callbackManager;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

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

        userProfile = ApplicationEx.getInstance().userProfile;

        //get user langauge based on default phone
        if (Locale.getDefault().getDisplayLanguage()!=null)
            ApplicationEx.language = Locale.getDefault().getLanguage();

        if (!ApplicationEx.language.equals("fr"))
            ApplicationEx.language = "en"; //if its anything but french then default it to english


        builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setCancelable(false);

        setContentView(R.layout.reg_profile_form);

        firstNameText = (EditText) findViewById(R.id.reg_firstName);
        lastNameText = (EditText) findViewById(R.id.reg_lastName);
        emailText = (EditText) findViewById(R.id.reg_email);
        passwordText = (EditText) findViewById(R.id.reg_password);

        countryTextView = (TextView) findViewById(R.id.reg_country);
        manTextView = (TextView) findViewById(R.id.reg_manTextView);
        womanTextView = (TextView) findViewById(R.id.reg_womanTextView);

        manButton = (ImageButton) findViewById(R.id.reg_manBtn);
        womanButton = (ImageButton) findViewById(R.id.reg_womanBtn);

        countryTextView.setOnClickListener(this);
        progressLayout = (LinearLayout) findViewById(R.id.progress);

        submitButton = (Button) findViewById(R.id.reg_submitBtn);
        submitButton.setEnabled(true);

        loginButton = (LoginButton)findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("user_birthday","email"));
        // Other app specific specialization

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                System.out.println("onSuccess: " + loginResult.getAccessToken());
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
                System.out.println("onError: " + exception.toString() );

            }
        });


    }

    public void selectGender(View view){
        if (view == manButton){
            gender = GENDER.MALE;
            userProfile.setGender(GENDER_MALE);
            manButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_man_active));
            womanButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_woman));
            manTextView.setTextColor(getResources().getColor(R.color.text_orange));
            womanTextView.setTextColor(getResources().getColor(R.color.text_darkgray));
        }else if (view == womanButton){
            gender = GENDER.FEMALE;
            userProfile.setGender(GENDER_FEMALE);
            manButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_man));
            womanButton.setImageDrawable(getResources().getDrawable(R.drawable.gender_woman_active));
            manTextView.setTextColor(getResources().getColor(R.color.text_darkgray));
            womanTextView.setTextColor(getResources().getColor(R.color.text_orange));
        }

    }
    private void submitRegistrationForm() {
        userProfile.setFirstname(firstNameText.getText().toString());
        userProfile.setLastname(lastNameText.getText().toString());
        userProfile.setEmail(emailText.getText().toString());
        userProfile.setUsername(emailText.getText().toString());
        userProfile.setPassword(passwordText.getText().toString());
        userProfile.setPasswordPlain(passwordText.getText().toString());
        userProfile.setCountry(countryTextView.getText().toString());

        userProfile.setLanguage(ApplicationEx.language);
        TimeZone tz = TimeZone.getDefault();

        if (tz.getID() != null)
            userProfile.setTimezone(tz.getID());
        else
            userProfile.setTimezone(TIMEZONE_DEFAULT);


        ApplicationEx.getInstance().userProfile = userProfile;
        ApplicationEx.getInstance().fromFBConnect = false;


        processRegistration(userProfile);
    }

    private void processRegistration(UserProfile profile) {

        if (controller == null) {
            controller = new RegistrationController(this, this, this);
        }
        startProgress();
        controller.register(profile);
    }

    public void validateRegistrationForm(View view){

        //dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);

        //disable submit button
        submitButton.setEnabled(false);


        try {
            setHideSoftKeyboard(passwordText);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(emailText);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(firstNameText);
        } catch (Exception e) {
        }

        try {
            setHideSoftKeyboard(lastNameText);
        } catch (Exception e) {
        }

        if (validateRegistrationForm()){
            startProgress();

            submitRegistrationForm();
        }else{
            //form not validated
            submitButton.setEnabled(true);
        }
    }

    public Boolean validateRegistrationForm(){
        if (firstNameText.getText() == null || firstNameText.getText().length() <= 0){
            displayToastMessage(getString(R.string.ALERTMESSAGE_FIRSTNAME_EMPTY));
            return false;
        }else if (lastNameText.getText() == null || lastNameText.getText().length() <= 0){
            displayToastMessage(getString(R.string.ALERTMESSAGE_LASTNAME_EMPTY));
            return false;
        }else if (!isEmail(emailText.getText().toString())) {
            displayToastMessage(getString(R.string.SIGNUP_EMAIL_ERROR));
            return false;
        }else if (passwordText.getText() == null || passwordText.getText().length() <= 0){
            displayToastMessage(getString(R.string.ALERTMESSAGE_PASSWORD_EMPTY));
            return false;
        }else if (countryTextView.getText() == null || countryTextView.getText().length() <= 0){
            displayToastMessage(getString(R.string.ALERTMESSAGE_COUNTRY_EMPTY));
            return false;
        }else if (gender == null) {
            displayToastMessage(getString(R.string.ALERTMESSAGE_GENDER_EMPTY));
            return false;
        }else{
            return true;
        }
    }

    public void facebookConnect(View view){
        displayToastMessage("facebook connect ongoing");
    }

    private boolean isEmail(String email) {
        Pattern pattern1 = Pattern
                .compile("^([a-zA-Z0-9_.+-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

        Matcher matcher1 = pattern1.matcher(email);

        if (!matcher1.matches())
            return false;
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.reg_country) {
            builder.setItems(R.array.country_array,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            String[] arr = getResources().getStringArray(
                                    R.array.country_array);
                            countryTextView.setText(arr[item]);
                            userProfile.setCountry(arr[item]);
                        }
                    });

            genericDialog = builder.create();
            genericDialog.show();
        }
    }

    @Override
    public void postRegistrationSuccess(String response) {
        stopProgress();

        //logout fb
        LoginManager.getInstance().logOut();

        if (response != null) {
//            Log.i("postRegistrationSuccess", response);
            // TODO: call get Sync
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    displayToastMessage("registration successful");
                    loginUser();
                }
            });
        }
    }

    @Override
    public void postRegistrationFailedWithError(MessageObj response) {
        stopProgress();

        //logout fb
        LoginManager.getInstance().logOut();

        // TODO Auto-generated method stub
        String message = response.getMessage_string();

        if (message == null || message.equals("")) {
            message = getResources().getString(R.string.REGISTRATION_FAILED);
        }
        displayToastMessage(message);
    }

    @Override
    public void loginSuccess(String response) {
        stopProgress();
        //check if your need to call the welcome page.
        //TODO: call get Sync
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                //register for push();
                try {
                    checkGCM(ApplicationEx.getInstance().userProfile.getRegID());
                    callOptinPage();

                } catch (Exception e) {

                }
                ApplicationEx.getInstance().setIsLogin(RegistrationFormActivity.this, true);
            }
        });
    }

    @Override
    public void loginFailedWithError(MessageObj response) {
        String message = response.getMessage_string();

        if (message == null) {
            message = getResources().getString(R.string.ALERTMESSAGE_LOGIN_EMPTY);
        }

        displayToastMessage(message);
        stopProgress();
    }

    @Override
    public void loginServices(String username, String password, String data,
                              Handler responseHandler) {
        // TODO Auto-generated method stub

    }

    public void goBackToPreviousPage(View view){
        finish();
    }

    private void loginUser(){
        //login user in the background, if successful go to the optin page
        if(loginController == null)
            loginController = new LoginController(this,this,this);
        loginController.startLogin(ApplicationEx.getInstance().userProfile.getUsername(), ApplicationEx.getInstance().userProfile.getPasswordPlain(), ApplicationEx.getInstance().fromFBConnect);

    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub
//        startProgress();
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void stopProgress() {

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                submitButton.setEnabled(true);

                progressLayout.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void callOptinPage() {
        Intent mainIntent;
        mainIntent = new Intent(this, RegistrationOptinActivity.class);
        finish();
        this.startActivity(mainIntent);
    }


    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                                userProfile.setFirstname(object.getString("first_name"));
                                userProfile.setLastname(object.getString("last_name"));
                                userProfile.setEmail(object.getString("email"));
                                userProfile.setUsername(object.getString("email"));
                                userProfile.setBday(object.optString("birthday"));
                                String email = object.optString("email");
                                System.out.println("object.email: " + email + "--" + AppUtil.shaHashed(email));
                                userProfile.setPassword(AppUtil.shaHashed(object.optString("email")));
                                userProfile.setPasswordPlain(AppUtil.shaHashed(object.optString("email")));

                                userProfile.setLanguage(ApplicationEx.language);

                                TimeZone tz = TimeZone.getDefault();

                                if (tz.getID() != null)
                                    userProfile.setTimezone(tz.getID());
                                else
                                    userProfile.setTimezone(TIMEZONE_DEFAULT);

                                if (object.optString("gender").equalsIgnoreCase("female")){
                                    userProfile.setGender(GENDER_FEMALE);
                                    selectGender(womanButton);
                                }else{
                                    userProfile.setGender(GENDER_MALE);
                                    selectGender(manButton);

                                }

                                firstNameText.setText(userProfile.getFirstname());
                                lastNameText.setText(userProfile.getLastname());
                                emailText.setText(userProfile.getEmail());
                                passwordText.setText(userProfile.getPassword());

                                ApplicationEx.getInstance().userProfile = userProfile;
                                ApplicationEx.getInstance().fromFBConnect = true;
                                processRegistration(userProfile);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }


                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,link,picture,gender,birthday,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
