package com.anxa.hapilabs.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

import com.hapilabs.R;

/**
 * Created by aprilanxa on 03/11/2016.
 */

public class AboutActivity extends HAPIActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.about);

        updateHeader(17, getResources().getString(R.string.PROFILE_HELP_ABOUT), this);

    }

    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }
    }
}
