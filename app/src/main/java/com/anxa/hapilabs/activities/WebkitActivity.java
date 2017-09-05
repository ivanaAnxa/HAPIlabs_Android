package com.anxa.hapilabs.activities;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Coach;

import android.content.pm.ActivityInfo;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WebkitActivity extends HAPIActivity implements OnClickListener

{

    private static final String RENEW_OVERRIDE = "renew";
    Context context;
    OnClickListener listener;
    WebView webView;
    String url;
    String title;
    ProgressBar progressbar;

    //openFile For image upload
    private static final int FILECHOOSER_RESULTCODE = 2888;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        url = getIntent().getStringExtra("URL");
        title = getIntent().getStringExtra("TITLE");

        setContentView(R.layout.fragment_myweight);
        webView = (WebView) findViewById(R.id.webview);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        setWebViewSettings();
        if((url.startsWith(WebServices.URL.qcDtsURLString) ||  url.startsWith(WebServices.URL.liveDtsURLString)  ||  url.startsWith(WebServices.URL.liveFRDtsURLString)) && ApplicationEx.getInstance().userProfile != null)
        {

                Map<String, String> extraHeaders = new HashMap<String, String>();
                String dataToPass = ApplicationEx.getInstance().userProfile.getEmail() + ":" + ApplicationEx.getInstance().userProfile.getPassword();
                byte[] data = new byte[0];
                try {
                    data = dataToPass.getBytes("UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String base64 = "Basic " + Base64.encodeToString(data, Base64.URL_SAFE|Base64.NO_WRAP);
                extraHeaders.put("Authorization",base64);

                webView.loadUrl(url,extraHeaders);


        }else{
            webView.loadUrl(url);
        }

        updateHeader(4, title, this);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(webView.getUrl() != null){
            if(webView.getUrl().endsWith("subscription/premium")){
                finish();
            }
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressbar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            String urlParsed = url.toLowerCase();

            Log.i("urlParsed", urlParsed);

            if (urlParsed.endsWith("reminders")) {

                setReminder();
                return true;
            } else if (urlParsed.endsWith("subscription/premium")) {
                Log.i("coming soon!", urlParsed);
                showPremiumPage();
            }

            if (urlParsed.startsWith("mailto:")) {
                MailTo mt = MailTo.parse(url);
                Intent i = newEmailIntent(WebkitActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                startActivity(i);
                view.reload();
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings() {
        // workaround so that the default browser doesn't take over
        webView.setWebViewClient(new MyWebViewClient());


        //webView.setWebChromeClient(new WebChromeClient());

        webView.setWebChromeClient(new WebChromeClient() {

            // openFileChooser for Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {

                // Update message
                mUploadMessage = uploadMsg;

                try {

                    // Create AndroidExampleFolder at sdcard

                    File imageStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES)
                            , "AndroidExampleFolder");

                    if (!imageStorageDir.exists()) {
                        // Create AndroidExampleFolder at sdcard
                        imageStorageDir.mkdirs();
                    }

                    // Create camera captured image file path and name
                    File file = new File(
                            imageStorageDir + File.separator + "IMG_"
                                    + String.valueOf(System.currentTimeMillis())
                                    + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file);

                    // Camera capture image intent
                    final Intent captureIntent = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    // Create file chooser intent
                    Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

                    // Set camera intent to file chooser
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                            , new Parcelable[]{captureIntent});

                    // On select image call onActivityResult method of activity
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "Exception:" + e,
                            Toast.LENGTH_LONG).show();
                }

            }

            // openFileChooser for Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            //openFileChooser for other Android versions
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType,
                                        String capture) {

                openFileChooser(uploadMsg, acceptType);
            }


            // The webPage has 2 filechoosers and will send a
            // console message informing what action to perform,
            // taking a photo or updating the file

            public boolean onConsoleMessage(ConsoleMessage cm) {

                onConsoleMessage(cm.message(), cm.lineNumber(), cm.sourceId());
                return true;
            }

            public void onConsoleMessage(String message, int lineNumber, String sourceID) {
                //Log.d("androidruntime", "Show console messages, Used for debugging: " + message);

            }
        });   // End setWebChromeClient


        webView.setVerticalScrollbarOverlay(true);
        webView.setFocusableInTouchMode(true);
        webView.setFocusable(true);
        webView.setBackgroundColor(0);
        webView.requestFocus(View.FOCUS_DOWN);

        WebSettings webSettings = webView.getSettings();
        // webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSaveFormData(true);
        webSettings.setSavePassword(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        webSettings.setGeolocationEnabled(true);
        webSettings.setLoadWithOverviewMode(true);

        //setDefaultUserAgent
        String defaultagent = webView.getSettings().getUserAgentString();
        if (defaultagent == null)
            defaultagent = com.anxa.hapilabs.common.util.AppUtil.getDefaultUserAgent();
        webView.getSettings().setUserAgentString(ApplicationEx.getInstance().customAgent + " " + defaultagent);


    }
//		
//		@SuppressLint("SetJavaScriptEnabled")
//		private void setWebViewSettings(){
//			   // workaround so that the default browser doesn't take over
//		       webView.setWebViewClient(new MyWebViewClient());
//		       webView.setWebChromeClient(new WebChromeClient());
//		       
//		       webView.setVerticalScrollbarOverlay(true);
//		       webView.setFocusableInTouchMode(true);
//		       webView.setFocusable(true);
//		       webView.setBackgroundColor(0);
//		       webView.requestFocus(View.FOCUS_DOWN);
//		      
//		       WebSettings webSettings = webView.getSettings();
//		       webSettings.setUseWideViewPort(true);
//		       webSettings.setDomStorageEnabled(true);
//		       webSettings.setSaveFormData(true);
//		       webSettings.setSavePassword(false);
//		      webSettings.setJavaScriptEnabled(true);
//		       webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//		       webSettings.setLoadsImagesAutomatically(true);
//		       webSettings.setBlockNetworkImage(false);
//		       webSettings.setGeolocationEnabled(true);
//		       webSettings.setLoadWithOverviewMode(true);
//		       
//
//			
//		}
//		
    // Return here when file selected from camera or from SDcard

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {

        if (requestCode == FILECHOOSER_RESULTCODE) {

            if (null == this.mUploadMessage) {
                return;
            }
            Uri result = null;
            try {
                if (resultCode != RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = intent == null ? mCapturedImageURI : intent.getData();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "activity :" + e,
                        Toast.LENGTH_LONG).show();
            }

            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }else{
            finishActivity(0);
        }
    }


    //reminders:
    public void setReminder() {
        Intent mainIntent;
        mainIntent = new Intent(this, MealReminderActivity.class);
        startActivity(mainIntent);
    }

    //reminders:
    public void showPremiumPage() {

        Coach userProfileCoach = ApplicationEx.getInstance().userProfile.getCoach();

        if (userProfileCoach == null){
            Intent mainIntent;
            mainIntent = new Intent(this, CoachSelectionActivity.class);
            startActivity(mainIntent);
        }else{
            Intent mainIntent;
            mainIntent = new Intent(this, PaymentQuestionActivity.class);
            mainIntent.putExtra("ID", userProfileCoach.coach_id);
            mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_PAYMENT); //webstate = payment

            this.startActivity(mainIntent);
        }

    }

    //mailTO:
    public static Intent newEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }
    }


}