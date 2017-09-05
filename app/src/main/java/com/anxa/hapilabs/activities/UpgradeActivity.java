package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Coach;

/**
 * Created by aprilanxa on 28/09/2016.
 */
public class UpgradeActivity extends HAPIActivity implements View.OnClickListener{

    Button upgradeBtn;
    Coach coach;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.upgrade_view);

        coach = ApplicationEx.getInstance().userProfile.getCoach();

        upgradeBtn = (Button) findViewById(R.id.upgdateBtn);
    }

    public void upgradeNow(View view){

        if (coach == null) {
            Intent mainIntent;
            mainIntent = new Intent(this, CoachSelectionActivity.class);
            startActivity(mainIntent);
        }else{
            callPayment(coach.coach_id);
        }
    }

    public void closePage(View view){
        onBackPressed();
    }

    private void callPayment(String id) {

        ApplicationEx.getInstance().selectedCoach = coach;
        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, PaymentQuestionActivity.class);
        mainIntent.putExtra("ID", id);
        mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_PAYMENT); //webstate = payment

        this.startActivity(mainIntent);
    }
}
