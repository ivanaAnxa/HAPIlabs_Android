package com.anxa.hapilabs.activities;

import java.util.Locale;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class PaymentQuestionActivity extends HAPIActivity implements OnClickListener, ProgressChangeListener, LoginListener {

    Button footerButton;
    LinearLayout footerButtonLayout;
    WebView webView;

    String url;
    String WEB_ID = null;
    String coachId;
    String coachProgramID;

    LoginController controller;

    ProgressBar progressBar;

    private byte state;
    public static final byte STATE_PAYMENT = 50;
    public static final byte STATE_WELCOME = 51;
    public static final byte STATE_QUESTIONAIRE = 52;
    public static final byte STATE_WELCOME_2 = 53;

    private static final String LOGIN_OVERRIDE = "login";
    private static final String FIRTSMEAL_OVERRIDE = "logyourfirstmeal";
    private static final String PAYMENTSUCCESS_OVERRIDE = "success";
    private static final String PAYMENTFAILED_OVERRIDE = "failed";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.questionpayment);
        webView = (WebView) findViewById(R.id.webkit);

        footerButtonLayout = (LinearLayout) findViewById(R.id.footerbuttonlayout2);
        footerButtonLayout.setVisibility(View.GONE);
        footerButton = (Button) findViewById(R.id.footerbutton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_question);
        progressBar.setVisibility(View.VISIBLE);

        state = getIntent().getByteExtra("WEB_STATE", PaymentQuestionActivity.STATE_PAYMENT); //webstate 1 = welcome

        WEB_ID = getIntent().getStringExtra("ID");
        coachId = WEB_ID;

        updateView();

        initWebView();

        //TODO: get UserProfile ticket and set it as cookie
        //String cookie_str = ApplicationEx.getInstance().userProfile.getTicket();
        //setCookies(cookie_str);


    }

    private void updateView() {

        switch (state) {
            case STATE_PAYMENT:

                updateHeader(7, getResources().getString(R.string.PAYMENT_HEADER_TITLE), this);
                url = WebServices.getURL(SERVICES.GET_PAYMENTKIT);

                url = url.replace("%@", WEB_ID);

                url = url.replace("%s", ApplicationEx.getInstance().userProfile.getRegID());

                webView.loadUrl(url);

                break;
            case STATE_WELCOME:

                url = WebServices.getURL(SERVICES.GET_QUESTIONKIT);
                url = url.replace("%@", WEB_ID);
                webView.loadUrl(url);

                updateHeader(8, getResources().getString(R.string.WELCOME_HEADER_TITLE), this);
                updateFooterButton(getResources().getString(R.string.WELCOME_FOOTER_LETMEANSWER), this);

                break;
            case STATE_QUESTIONAIRE:
                updateHeader(9, getResources().getString(R.string.QUESTION_HEADER_TITLE), this);
                // updateFooterButton(getResources().getString(R.string.QUESTION_FOOTER_QOF), this);

                break;
            case STATE_WELCOME_2:
                updateHeader(10, getResources().getString(R.string.WELCOME_HEADER_TITLE), this);
                updateFooterButton(getResources().getString(R.string.WELCOME2_FOOTER_LOG), this);

                break;
        }
    }

    protected void updateFooterButton(String headertitle, OnClickListener onclick) {
        if (footerButton == null)
            footerButton = (Button) findViewById(R.id.footerbutton);
        footerButton.setText(headertitle);
        footerButton.setOnClickListener(onclick);
    }

    private void setCookies(String cookie_str) {
        CookieSyncManager syncManager = CookieSyncManager.createInstance(webView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(WebServices.DOMAIN_NAME, WebServices.COOKIE_NAME + "=" + cookie_str);
        syncManager.sync();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);

            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error){
            super.onReceivedError(view, request, error);

            progressBar.setVisibility(View.GONE);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            System.out.println("PaymentQActivity url: " + url);
            if (url.contains("freetrial")){
//                coachProgramID = url.substring(url.indexOf("coachProgramId=")+15, url.indexOf("&email="));
                String programSubString = url.substring(url.indexOf("coachProgramId=")+15);
                coachProgramID = programSubString.substring(0, programSubString.indexOf("&"));

                System.out.println("freetrial url coachProgramID: " + coachProgramID);
                proceedToPremiumPage();
                return true;
            }else if (url.toLowerCase(Locale.getDefault()).trim().contains(PAYMENTFAILED_OVERRIDE.toLowerCase().trim())) {
                displayToastMessage("PAYMENT FAILED");
                //reload payment webkit
                state = STATE_PAYMENT;
                updateView();
                return true;
            } else if (url.toLowerCase(Locale.getDefault()).trim().contains(PAYMENTSUCCESS_OVERRIDE.toLowerCase().trim())) {
                displayToastMessage("PAYMENT SUCCESS");
                state = STATE_WELCOME;
                callLogin();

                //call welcome page
                return true;
            } else if (url.toLowerCase().trim().contains(LOGIN_OVERRIDE.toLowerCase().trim())) {
                //user selected free account, redirect to login afterwards
                callLogin();
                return true;
            } else if (url.toLowerCase().trim().contains(FIRTSMEAL_OVERRIDE.toLowerCase().trim())) {
                //
                callSync();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private void callLogin() {

        if (controller == null)
            controller = new LoginController(this, this, this);

        controller.startLogin(ApplicationEx.getInstance().userProfile.getUsername(), ApplicationEx.getInstance().userProfile.getPasswordPlain(), ApplicationEx.getInstance().fromFBConnect);
    }

    private void initWebView() {

        // workaround so that the default browser doesn't take over
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVerticalScrollbarOverlay(true);
        webView.setFocusableInTouchMode(true);
        webView.setFocusable(true);
        webView.setBackgroundColor(0);
        webView.requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setPluginState(PluginState.ON);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        //setDefaultUserAgent
        String defaultagent = webView.getSettings().getUserAgentString();
        if (defaultagent == null)
            defaultagent = com.anxa.hapilabs.common.util.AppUtil.getDefaultUserAgent();
        webView.getSettings().setUserAgentString(ApplicationEx.getInstance().customAgent + " " + defaultagent);


    }

    @Override
    public void onClick(View v) {}

    private void download(String photoId, String url, String mealId) {
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(this, url, photoId, mealId, this);
    }

    private void callSync() {

        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, MainActivity.class);
        //added 01/03/2016
        mainIntent.putExtra("ISFROMPAYMENT", true);
        this.startActivity(mainIntent);
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

    @Override
    public void loginSuccess(String response) {
        try {
            //stopProgress();
        } catch (Exception e) {}

        //check if your need to call the welcome page.

        //TODO: call get Sync

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                dowloadpics();
                //check if you need to launch the welcome page
                if (!ApplicationEx.getInstance().hasWelcome) {

                    ApplicationEx.getInstance().hasWelcome = true;

                    callWelcome(ApplicationEx.getInstance().userProfile.getRegID());

                } else
                    callSync();
            }
        });

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
    public void loginFailedWithError(MessageObj response) {
        String message = response.getMessage_string();
        displayToastMessage(message);
        stopProgress();
    }

    @Override
    public void startProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //	startProgressinParent();
            }
        });
    }

    @Override
    public void stopProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //stopProgressinParent();

            }
        });
    }

    @Override
    public void loginServices(String username, String password,
                              String data, Handler responseHandler) {
        // TODO Auto-generated method stub

    }

    private void proceedToPremiumPage(){
        Intent mainIntent = new Intent(this, PremiumAccessActivity.class);
        mainIntent.putExtra("coachProgramID", coachProgramID);
        mainIntent.putExtra("coachID", coachId);
        this.startActivity(mainIntent);
    }
}