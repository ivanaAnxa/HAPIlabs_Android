package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.hapilabs.R;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.UserProfile;

import android.widget.Button;
import android.view.View;

/**
 * Created by aprilanxa on 4/5/2016.
 */
public class MainObjectiveActivity extends HAPIActivity {

    Button loseWeightBtn, eatHealthierBtn, getMoreFitBtn;
    UserProfile userProfile;

    int LOSE_WEIGHT_GOALS_INDEX = 1;
    int EAT_HEALTHIER_GOALS_INDEX = 7;
    int GET_MORE_FIT_GOALS_INDEX = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reg_main_objective);

        loseWeightBtn = (Button) findViewById(R.id.reg_goal_loseweight);
        eatHealthierBtn = (Button) findViewById(R.id.reg_goal_eathealthier);
        getMoreFitBtn = (Button) findViewById(R.id.reg_goal_getmorefit);

        //create new instance of UserProfile, to save the goals_index
        userProfile = new UserProfile();
    }

    @Override
    public void onResume() {
        super.onResume();

        //create new instance of UserProfile, to save the goals_index
        userProfile = new UserProfile();
    }


    public void setMainObjective(View view) {


        if (view == loseWeightBtn) {
            //set goal to lose weight, goals_index = 1
            userProfile.setGoals_index(LOSE_WEIGHT_GOALS_INDEX);
            goToStartingWeight();
        } else if (view == eatHealthierBtn) {
            //set goal to eat healthier, goals_index = 7
            userProfile.setGoals_index(EAT_HEALTHIER_GOALS_INDEX);
            goToMotivationScreen();
        } else if (view == getMoreFitBtn) {
            //set goal to get more Fit, goals_index = 8
            userProfile.setGoals_index(GET_MORE_FIT_GOALS_INDEX);
            goToMotivationScreen();
        }
    }

    private void goToStartingWeight() {
        ApplicationEx.getInstance().userProfile = userProfile;
        Intent mainIntent = new Intent(this, StartWeightActivity.class);
        this.startActivity(mainIntent);
    }

    private void goToMotivationScreen() {
        ApplicationEx.getInstance().userProfile = userProfile;
        Intent mainIntent = new Intent(this, MotivationActivity.class);
        this.startActivity(mainIntent);
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }

}
