package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.PremiumAccessListener;
import com.anxa.hapilabs.controllers.accountsettings.PremiumAccessController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.ui.CustomDialog;

/**
 * Created by aprilanxa on 23/08/2016.
 */
public class ValidateAccessCodeActivity extends HAPIActivity implements PremiumAccessListener {


    String coachId;
    String telNum;
    String coachProgramId;

    EditText validateCode_et;
    TextView validateCode_telNum;

    PremiumAccessController premiumAccessController;

    ProgressBar progressBar;
    CustomDialog dialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.validateaccesscode);

        coachId = getIntent().getStringExtra("coachID");
        telNum = getIntent().getStringExtra("telNum");
        coachProgramId = getIntent().getStringExtra("coachProgramId");

        System.out.print("Validate coachId: " + coachId);

        updateHeader(10, getResources().getString(R.string.PREMIUM_ACCESS_TITLE), this);

        validateCode_et = (EditText) findViewById(R.id.validatecode_et);
        validateCode_telNum = (TextView) findViewById(R.id.validate_telnum_tv);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_validateCode);
        progressBar.setVisibility(View.GONE);

        if (telNum!=null){
            validateCode_telNum.setText(telNum);
        }else{
            validateCode_telNum.setText("");
        }
    }

    /**Premium Access Listener**/
    public void sendAccessCodeSuccess(String response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    public void sendAccessCodeFailedWithError(MessageObj response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void validateAccessCodeSuccess(String response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        proceedToWelcomePage();

    }
    public void validateAccessCodeFailedWithError(MessageObj response){
        final String message = response.getMessage_string();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                showCustomDialog(message);
            }
        });
    }

    /**Button OnClick**/
    public void validateAccessCode(View view){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        if (premiumAccessController == null)
            premiumAccessController = new PremiumAccessController(this, this, this);

        premiumAccessController.validateAccessCode(validateCode_et.getText().toString());
    }

    public void resendAccessCode(View view){
        showCustomDialog();
    }

    @Override
    public void onClick(View v) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            if (v.getId() == R.id.YesButton) {
                resendAccessCodeToAPI();
            } else if (v.getId() == R.id.NoButton) {
                dialog.dismiss();
            }else if(v.getId() == R.id.OtherButton){
                dialog.dismiss();
            }
        }
    }

    /**Private Methods**/
    private void proceedToWelcomePage(){
        Intent mainIntent = new Intent(this, WelcomeBravoActivity.class);
        mainIntent.putExtra("coachID", coachId);
        this.startActivity(mainIntent);
    }

    private void resendAccessCodeToAPI(){
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

    private void showCustomDialog() {
        String confirmationMessage = getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_DES) + "\n" + telNum;
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_ok), getResources().getString(R.string.btn_cancel), false, confirmationMessage, getResources().getString(R.string.PREMIUM_TEL_NO_CONFIRMATION_TITLE), this);
        dialog.show();
    }

    private void showCustomDialog(String content) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, content, getResources().getString(R.string.ERROR), this);
        dialog.show();
    }
}