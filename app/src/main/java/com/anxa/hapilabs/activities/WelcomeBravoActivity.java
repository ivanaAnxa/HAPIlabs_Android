package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.ui.CustomDialog;

/**
 * Created by aprilanxa on 23/08/2016.
 */
public class WelcomeBravoActivity extends HAPIActivity implements LoginListener {

    String coachId;
    ImageView coach_iv;
    Coach coach = null;
    CoachDAO coachDAO;
    ProgressBar progressBar;

    MainActivityCallBack MainListener;
    LoginController loginController;

    CustomDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.welcome_premium);

        coachId = getIntent().getStringExtra("coachID");
        coach_iv = (ImageView) findViewById(R.id.welcome_coach_iv);
        progressBar = (ProgressBar) findViewById(R.id.welcome_progressBar);
        progressBar.setVisibility(View.GONE);

        updateCoachImageView();
    }

    /**
     * Listener
     **/
    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub

        updateCoachImageView();
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    public void loginSuccess(String response){
        proceedToQuestionnaires();
    }

    public void loginFailedWithError(MessageObj response){

        final String message = response.getMessage_string();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                showCustomDialog(message);
            }
        });
    }
    public void loginServices(String username, String password, String data, Handler responseHandler){}


    /**
     * OnClick Methods
     **/
    public void proceedToNextStep(View view) {
        callLogin();
    }

    /**
     * Private Methods
     **/

    private Bitmap getAvatar(Coach coach) {
        Bitmap avatarBMP = null;
        if (coach != null) {
            avatarBMP = ImageManager.getInstance().findImage(coach.coach_id);

            if (avatarBMP == null) {
                //download the image first
                MainListener.download(coach.coach_id, coach.avatar_url, "0");
            }

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);

            return avatarBMP;
        }
        return avatarBMP;
    }

    private void proceedToQuestionnaires(){
        Intent mainIntent = new Intent(this, PaymentQuestionActivity.class);
        mainIntent.putExtra("ID", ApplicationEx.getInstance().userProfile.getRegID());
        mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_WELCOME);
        this.startActivity(mainIntent);
    }

    private void callLogin() {

        if (loginController == null)
            loginController = new LoginController(this, this, this);

        loginController.startLogin(ApplicationEx.getInstance().userProfile.getUsername(), ApplicationEx.getInstance().userProfile.getPasswordPlain(), ApplicationEx.getInstance().fromFBConnect);
    }

    private void showCustomDialog(String content) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, content, getResources().getString(R.string.ERROR), this);
        dialog.show();
    }

    private void updateCoachImageView(){
        try {
            coach = ApplicationEx.getInstance().selectedCoach;
            if (coach == null) {
                coach = coachDAO.getCoachsbyID(coachId);
                if (coach != null && coach.coach_id != null) {
                    if (coach.coach_photo != null && coach.coach_photo.image != null) {
                        coach_iv.setImageBitmap(coach.coach_photo.image);

                    } else {
                        coach_iv.setImageBitmap(getAvatar(coach));
                    }
                } else {
                    coach_iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                }
            } else {
                coachId = coach.coach_id;
                if (coach.coach_photo != null && coach.coach_photo.image != null) {
                    coach_iv.setImageBitmap(coach.coach_photo.image);
                } else {
                    coach_iv.setImageBitmap(getAvatar(coach));
                }
            }
        } catch (Exception e) {
            coach_iv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
        }
    }
}
