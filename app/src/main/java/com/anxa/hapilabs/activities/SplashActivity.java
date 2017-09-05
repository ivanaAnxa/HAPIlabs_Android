package com.anxa.hapilabs.activities;


import com.hapilabs.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {
    protected int _splashTime = 3000; // time to display the splash screen in ms

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(1);
        setContentView(R.layout.splash);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent mainIntent;
//                mainIntent = new Intent(SplashActivity.this, LoginPageActivity.class);
//                mainIntent.putExtra("COACHID", "1");
//                SplashActivity.this.startActivity(mainIntent);
//                SplashActivity.this.finish();
//            }
//        }, _splashTime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent;
                mainIntent = new Intent(SplashActivity.this, LandingScreenActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, _splashTime);
    }


}


