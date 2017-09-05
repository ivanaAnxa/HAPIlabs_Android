package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hapilabs.R;



/**
 * Created by aprilanxa on 4/21/2016.
 */
public class WelcomeScreenActivity extends HAPIActivity{

    Button login_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.welcome_screen);

        login_btn = (Button)findViewById(R.id.footerbutton);
        updateFooterButton(getResources().getString(R.string.WELCOME2_FOOTER_LOG), this);

        login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == login_btn) {
            goToLoginPage();
        }
    }

    private void goToLoginPage(){
        Intent mainIntent;
        mainIntent = new Intent(this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mainIntent.putExtra("ISFROMPAYMENT", true);
        this.startActivity(mainIntent);
    }
}
