package com.anxa.hapilabs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetSurveyQuestionListener;
import com.anxa.hapilabs.common.connection.listener.PostSurveyAnswerListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.UpdateProfileListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.surveyquestions.PostSurveyAnswerController;
import com.anxa.hapilabs.controllers.updateprofile.UpdateProfileController;
import com.anxa.hapilabs.controllers.surveyquestions.GetSurveyQuestionsController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.SurveyOption;
import com.anxa.hapilabs.models.UserProfile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by aprilanxa on 4/8/2016.
 */
public class RegistrationOptinActivity extends HAPIActivity implements OnClickListener, GetSurveyQuestionListener, UpdateProfileListener, ProgressChangeListener, PostSurveyAnswerListener {

    CheckBox healthProf_checkboxYes, healthProf_checkboxNo, sendTips_checkboxYes, sendTips_checkboxNo;
    TextView selectSource_tv, selectSource_arrow, selectProfession_tv, selectProfession_arrow;
    RelativeLayout professionalLayout;
    ProgressBar progressBar;

    GetSurveyQuestionsController questionsController;
    List<SurveyOption> optionsList;
    String[] sourceOptionsArray;
    String[] professionOptionsArray;

    AlertDialog genericDialog;
    AlertDialog.Builder builder;
    UserProfile userProfile;

    UpdateProfileController controller;
    PostSurveyAnswerController postSurveyAnswerController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reg_optin);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setCancelable(false);

        //connect to api to get the options from the server
        getSurveyQuestions();

        healthProf_checkboxYes = (CheckBox) findViewById(R.id.healthProf_checkBoxYes);
        healthProf_checkboxNo = (CheckBox) findViewById(R.id.healthProf_checkBoxNo);
        sendTips_checkboxYes = (CheckBox) findViewById(R.id.tips_checkBoxYes);
        sendTips_checkboxNo = (CheckBox) findViewById(R.id.tips_checkBoxNo);
        selectSource_tv = (TextView) findViewById(R.id.optin_select_tv);
        selectSource_arrow = (TextView) findViewById(R.id.optin_sourcearrow_tv);
        selectProfession_tv = (TextView) findViewById(R.id.optin_prof_tv);
        selectProfession_arrow = (TextView) findViewById(R.id.optin_profarrow_tv);

        professionalLayout = (RelativeLayout) findViewById(R.id.profession_layout);
        professionalLayout.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar_optin);
        progressBar.setVisibility(View.VISIBLE);

        updateFooterButton(getResources().getString(R.string.btn_continue), this);

        professionOptionsArray = getResources().getStringArray(R.array.profession_array);
        //default source option
        sourceOptionsArray = getResources().getStringArray(R.array.source_array);

        userProfile = ApplicationEx.getInstance().userProfile;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerbutton) {
            if (isPageValid()) {
                submitOptinPage();
            } else {
                displayToastMessage(getResources().getString(R.string.ALERTMESSAGE_FORM_EMPTY_TITLE));
            }
        }
    }

    private boolean isPageValid() {
        if ((healthProf_checkboxNo.isChecked() || healthProf_checkboxYes.isChecked()) &&
                (selectSource_arrow.getText().toString().length() > 1) &&
                (sendTips_checkboxNo.isChecked() || sendTips_checkboxYes.isChecked())) {
            if (healthProf_checkboxYes.isChecked()) {
                return (selectProfession_arrow.getText().toString().length() > 1);
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void submitOptinPage() {

        String answerSur = (String) (selectSource_arrow).getText().toString();
        int answerIndex = Arrays.asList(sourceOptionsArray).indexOf(answerSur);

        userProfile.setSurvey_answer(optionsList.get(answerIndex).option_text);
        if (selectProfession_arrow.getText().toString().length() <= 1) {
            userProfile.setProfession("");
        } else {
            userProfile.setProfession(selectProfession_arrow.getText().toString());
        }
        userProfile.setReceive_newsletter(sendTips_checkboxYes.isChecked());

        userProfile.setHas_answered_optin(1);

        SharedPreferences sharedPreferences = getSharedPreferences("com.hapilabs", Context.MODE_PRIVATE);
        userProfile.setMotivation_level(sharedPreferences.getString("motivation_level", "5"));
        userProfile.setTime_to_spend(sharedPreferences.getString("time_to_spend", "30"));

        Log.i("answersource:" + userProfile.getSurvey_answer(), "time: " + userProfile.getTime_to_spend());

        //submit survey answer first
        submitSurveyAnswer();
//        updateProfile(userProfile);
    }

    private void submitSurveyAnswer() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        if (postSurveyAnswerController == null) {
            postSurveyAnswerController = new PostSurveyAnswerController(this, this, this);
        }

//        startProgress();
        postSurveyAnswerController.postSurveyAnswer(userProfile.getSurvey_answer());
    }

    private void updateProfile(UserProfile userProfile) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        if (controller == null) {
            controller = new UpdateProfileController(this, this, this);
        }
//        startProgress();
        controller.updateProfile(userProfile);

    }

    public void showOptinSources(View view) {
        builder.setItems(sourceOptionsArray,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        selectSource_arrow.setText(sourceOptionsArray[item]);
                    }
                });
        genericDialog = builder.create();
        genericDialog.show();
    }

    public void setCheckBoxValue(View view) {
        switch (view.getId()) {
            case R.id.healthProf_checkBoxYes:
                if (healthProf_checkboxYes.isChecked()) {
                    healthProf_checkboxNo.setChecked(false);
                    professionalLayout.setVisibility(View.VISIBLE);
                } else {
                    professionalLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.healthProf_checkBoxNo:
                healthProf_checkboxYes.setChecked(false);
                professionalLayout.setVisibility(View.GONE);
                break;
            case R.id.tips_checkBoxYes:
                sendTips_checkboxNo.setChecked(false);
                break;
            case R.id.tips_checkBoxNo:
                sendTips_checkboxYes.setChecked(false);
                break;
            default:
        }
    }

    public void showProfessionOptions(View view) {
        builder.setItems(professionOptionsArray,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        selectProfession_arrow.setText(professionOptionsArray[item]);
                    }
                });
        genericDialog = builder.create();
        genericDialog.show();
    }

    private void getSurveyQuestions() {
        if (questionsController == null) {
            questionsController = new GetSurveyQuestionsController(this, this, this);
        }

//        startProgress();
        questionsController.getQuestions(ApplicationEx.getInstance().userProfile.getRegID());
    }

    @Override
    public void getQuestionSuccess(String response, final List<SurveyOption> options) {
//        stopProgress();
        if (response != null) {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    optionsList = options;
                    updateSourcesList();
                }
            });
        }
    }

    private void updateSourcesList() {
        //for sources, to change array
        Collections.sort(optionsList, new Comparator<SurveyOption>() {
            public int compare(SurveyOption o1, SurveyOption o2) {
                return o1.option_index.compareTo(o2.option_index);
            }
        });

        List<String> strlist = new ArrayList<String>();

        for (int i = 0; i < optionsList.size(); i++) {
            strlist.add(optionsList.get(i).option_displaytext);
        }

        sourceOptionsArray = new String[strlist.size()];
        sourceOptionsArray = strlist.toArray(sourceOptionsArray);
    }

    @Override
    public void getQuestionFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        final String message = response.getMessage_string();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                displayToastMessage(message);
//                stopProgress();
            }
        });
    }

    @Override
    public void updateProfileSuccess(String response) {
        //move on to homepage
//        displayToastMessage("update profile success");
//        stopProgress();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        goToWelcomePage();
    }

    @Override
    public void updateProfileFailedWithError(MessageObj response) {
//        displayToastMessage("error: " + response.toString());
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void postSurveyAnswerSuccess(String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        updateProfile(userProfile);
    }

    public void postSurveyAnswerFailedWithError(MessageObj response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    private void goToLoginPage() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("ISFROMPAYMENT", true);
        this.startActivity(mainIntent);
    }

    private void goToWelcomePage() {
        Intent mainIntent = new Intent(this, WelcomeScreenActivity.class);
        this.startActivity(mainIntent);
    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub
        startProgressinParent();
    }

    @Override
    public void stopProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                stopProgressinParent();

            }
        });
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }
}
