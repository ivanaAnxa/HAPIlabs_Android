package com.anxa.hapilabs.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.PremiumAccessListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.accountsettings.PremiumAccessController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.ui.CustomDialog;

import java.util.Arrays;

/**
 * Created by aprilanxa on 19/08/2016.
 */
public class PremiumAccessActivity extends HAPIActivity implements OnClickListener, PremiumAccessListener{

    ProgressBar progressBar;

    TextView country_tv;
    TextView code_tv;
    EditText telNum_et;

    AlertDialog.Builder builder;
    AlertDialog genericDialog;
    CustomDialog dialog;

    String coachId;
    String coachProgramId;
    String userCountry;
    String telNum;

    String[] countryArray;
    String[] countryCodeArray;

    PremiumAccessController premiumAccessController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.premiumaccess);

        coachId = getIntent().getStringExtra("coachID");

        System.out.println("PremiumAccessActivity coachId: " + coachId);

        coachProgramId = getIntent().getStringExtra("coachProgramID");

        builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setCancelable(false);

        updateHeader(10, getResources().getString(R.string.PREMIUM_ACCESS_TITLE), this);

        country_tv = (TextView) findViewById(R.id.premium_country_tv);
        code_tv = (TextView) findViewById(R.id.premium_code_tv);
        telNum_et = (EditText) findViewById(R.id.premium_telNum_et);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_premium);
        progressBar.setVisibility(View.GONE);

        country_tv.setOnClickListener(this);
        code_tv.setOnClickListener(this);

        countryArray = getResources().getStringArray(R.array.country_array);
        countryCodeArray = getResources().getStringArray(R.array.country_code_array);

        if(ApplicationEx.getInstance().userProfile.getCountry() == null){
            userCountry = "";
        }else{
            userCountry = ApplicationEx.getInstance().userProfile.getCountry();
        }

        if(Arrays.asList(countryArray).indexOf(userCountry) >= 0){
            code_tv.setText(countryCodeArray[Arrays.asList(countryArray).indexOf(userCountry)]);
            country_tv.setText(userCountry);
        }else{
            code_tv.setText("");
        }

        System.out.println("user country: " +  userCountry);

    }

    @Override
    public void onClick(View v) {
        if (v == country_tv) {
            builder.setItems(R.array.country_array,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            country_tv.setText(countryArray[item]);
                            code_tv.setText(countryCodeArray[item]);
                        }
                    });

            genericDialog = builder.create();
            genericDialog.show();
        }else if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (v.getId() == R.id.YesButton) {
                requestAccessCodeToAPI();
            } else if (v.getId() == R.id.NoButton) {
                dialog.dismiss();
            } else if(v.getId() == R.id.OtherButton){
                dialog.dismiss();
            }
        }
    }

    /**Button**/
    public void sendAccessCode(View view){
        telNum = code_tv.getText().toString() + " " + telNum_et.getText().toString();

        showCustomDialog();
    }

    /**Premium Access Listener**/
    public void sendAccessCodeSuccess(String response){
        System.out.println("sendAccessCode Succesful");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        proceedToValidatePage();
    }
    public void sendAccessCodeFailedWithError(MessageObj response){
        System.out.println("sendAccessCode failed");
        final String message = response.getMessage_string();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                showCustomDialog(message);
            }
        });
    }

    public void validateAccessCodeSuccess(String response){}
    public void validateAccessCodeFailedWithError(MessageObj response){}

    /**Private Methods**/
    private void showCustomDialog() {
        String confirmationMessage = getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_DES) + "\n" + telNum;
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel), false, confirmationMessage, getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_TITLE), this);
        dialog.show();
    }

    private void showCustomDialog(String content) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, content, getResources().getString(R.string.ERROR), this);
        dialog.show();
    }

    private void requestAccessCodeToAPI(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        if (premiumAccessController == null)
            premiumAccessController = new PremiumAccessController(this, this, this);

        premiumAccessController.sendAccessCode(coachProgramId, telNum.replace(" ", ""));
    }

    private void proceedToValidatePage(){
        Intent mainIntent = new Intent(this, ValidateAccessCodeActivity.class);
        mainIntent.putExtra("coachProgramId", coachProgramId);
        mainIntent.putExtra("coachId", coachId);
        mainIntent.putExtra("telNum", telNum);
        this.startActivity(mainIntent);
    }
}