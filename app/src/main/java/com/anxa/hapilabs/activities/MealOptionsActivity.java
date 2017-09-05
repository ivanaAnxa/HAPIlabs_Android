package com.anxa.hapilabs.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.AccountSettingsListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.accountsettings.PostAccountSettingsController;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 01/08/2016.
 */
public class MealOptionsActivity extends HAPIActivity implements AccountSettingsListener {

    private CheckBox mealRatingStar_cb;
    private CheckBox mealFoodGroup_cb;
    private TextView mealOptionsDate_tv;

    private final int MEAL_USER_RATING_FOOD_GROUP = 0;
    private final int MEAL_USER_RATING_STAR = 1;

    private int selectedRating;

    ProgressBar mealOptionsProgressBar;

    PostAccountSettingsController postAccountSettingsController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.meal_options);

        mealOptionsDate_tv = (TextView) findViewById(R.id.dateHeader);

        mealOptionsProgressBar = (ProgressBar) findViewById(R.id.meal_options_progressbar);
        mealOptionsProgressBar.setVisibility(View.INVISIBLE);

        if (getIntent().getStringExtra("dateHeader") != null) {
            mealOptionsDate_tv.setText(getIntent().getStringExtra("dateHeader"));
        }

        mealRatingStar_cb = (CheckBox) findViewById(R.id.meal_rating_cb);
        mealRatingStar_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    if (mealFoodGroup_cb.isChecked()) {
                        mealFoodGroup_cb.setChecked(false);
                    }
                }
                selectedRating = MEAL_USER_RATING_STAR;
                mealRatingStar_cb.setChecked(true);
            }
        });

        mealFoodGroup_cb = (CheckBox) findViewById(R.id.meal_foodgroup_cb);
        mealFoodGroup_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    if (mealRatingStar_cb.isChecked()) {
                        mealRatingStar_cb.setChecked(false);
                    }
                }
                selectedRating = MEAL_USER_RATING_FOOD_GROUP;
                mealFoodGroup_cb.setChecked(true);
            }
        });

        mealRatingStar_cb.setChecked(ApplicationEx.getInstance().userRatingSetting);
        mealFoodGroup_cb.setChecked(!ApplicationEx.getInstance().userRatingSetting);

    }


    public void saveMealOptions(View view) {

        mealOptionsProgressBar.setVisibility(View.VISIBLE);

        //save to data
        ApplicationEx.getInstance().userRatingSetting = (selectedRating == MEAL_USER_RATING_STAR);

        //save to API
        saveToAPI();
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }

    /**
     * private methods
     **/
    private void saveToAPI() {
        final String username = ApplicationEx.getInstance().userProfile.getRegID();
        final int settingsValue = ApplicationEx.getInstance().userRatingSetting ? 1:0;

        System.out.println("save account settings to API");

        if (postAccountSettingsController == null) {
            postAccountSettingsController = new PostAccountSettingsController(this, this, this);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postAccountSettingsController.postAccountSettings(settingsValue, username);
            }
        });
    }

    /**AccountSettingsListener**/
    @Override
    public void accountSettingsSuccess(String response) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mealOptionsProgressBar.setVisibility(View.GONE);

                //finish the activity
                finish();            }
        });

    }

    @Override
    public void accountSettingsFailedWithError(MessageObj response) {

        final String responseStr = response.getMessage_string();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mealOptionsProgressBar.setVisibility(View.GONE);

                Toast.makeText(MealOptionsActivity.this, responseStr, Toast.LENGTH_SHORT);
            }
        });
    }
}
