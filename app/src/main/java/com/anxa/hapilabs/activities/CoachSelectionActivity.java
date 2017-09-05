package com.anxa.hapilabs.activities;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetCoachListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.selectcoach.CoachSelectionController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.ui.adapters.CoachViewAdapter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;


public class CoachSelectionActivity extends HAPIActivity implements ProgressChangeListener, GetCoachListener, OnClickListener, BitmapDownloadListener {
    Handler handler;
    Boolean isProgress = true;// default is true

    List<Coach> coaches = new ArrayList<Coach>();
    ListView listView;
    ProgressBar progressbar;

    String selectedCoachID;
    Coach selectedCoach;

    CoachSelectionController controller;
    Timer t;
    LinearLayout coachProgressContainer;
    CoachViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        t = new Timer();
        handler = new Handler();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.coachselection);

        coachProgressContainer = (LinearLayout) findViewById(R.id.progresscontainer);
        listView = (ListView) findViewById(R.id.list);

        progressbar = (ProgressBar) coachProgressContainer.findViewById(R.id.progress);
        updateProgressUI();

        updateHeader(2, getResources().getString(R.string.NAVIGATIONTITLE_SELECTCOACH), this);
        updateFooterButton(getString(R.string.btn_submit), this);

        //create adapter
        adapter = new CoachViewAdapter(this, coaches, this);
        listView.setAdapter(adapter);

        //update language
        try {

            if (Locale.getDefault().getLanguage() != null) {
                ApplicationEx.language = Locale.getDefault().getLanguage();

                if (!ApplicationEx.language.equals("fr")) {
                    ApplicationEx.language = "en"; //if its anything but french then default it to english
                }
            } else
                ApplicationEx.language = "en";

        } catch (Exception e) {}

        if (!ApplicationEx.language.equals("fr")) {
            adapter.setLanguage(true); //english
        } else
            adapter.setLanguage(false);
        doTimerTask();
    }

    private void setController() {
        controller = new CoachSelectionController(this, this, this);
        controller.getCoach(ApplicationEx.getInstance().userProfile.getRegID());

    }

    public void run() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    //start timer based task here; which is the getSync()
                    doTimerTask();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//end run

            // TODO Auto-generated method stub

        };
        try {
            thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        thread.start();
    }

    /*this is for automatic refresh every 3 mins*/
    public void doTimerTask() {
        handler.post(new Runnable() {
            public void run() {
                setController();
            }
        });
    }

    @Override
    public void startProgress() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                startProgressinParent();
            }
        });
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


    private void download(String photoId, String url, String mealId) {
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    public void startImageDownload(final List<Coach> coaches) {
        Thread thread = new Thread() {

            @Override
            public void run() {
                try {
                    for (int j = 0; j < coaches.size(); j++) {
                        final int coachindex = j;
                        runOnUiThread(new Runnable() {
                            Coach coach = (Coach) coaches.get(coachindex);

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                download(coach.coach_id, coach.avatar_url, coach.coach_id);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void updateProgressUI() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isProgress)
                    coachProgressContainer.setVisibility(View.VISIBLE);
                else
                    coachProgressContainer.setVisibility(View.GONE);
            }
        });
    }

    public void updateCoachSelectionList(final List<Coach> coaches) {
        this.coaches = coaches;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                adapter.update(coaches);

                adapter.notifyDataSetInvalidated();
            }
        });
    }

    @Override
    public void getCoachSuccess(String response, List<Coach> coaches) {
        // TODO Auto-generated method stub
        isProgress = false;

        //select coach list based on current language; for en show only en coaches for fr show only fr coaches
        List<Coach> coachbyLanguage = new ArrayList<Coach>();

        for (int i = 0; i < coaches.size(); i++) {

            Coach coach = coaches.get(i);

            if (ApplicationEx.language.equals("fr")) { //get french only coaches
                if (coach.coach_profile_fr != null && coach.coach_profile_fr.length() > 0) {
                    coachbyLanguage.add(coach);
                }
            } else {//ge english as default
                if (coach.coach_profile_en != null && coach.coach_profile_en.length() > 0) {
                    coachbyLanguage.add(coach);
                }
            }
        }

        //get first coach entry and use as default
        selectedCoach = ((Coach) coachbyLanguage.get(0));
        selectedCoachID = ((Coach) coachbyLanguage.get(0)).coach_id;
        updateCoachSelectionList(coachbyLanguage);
        startImageDownload(coachbyLanguage);
        updateProgressUI();
    }


    @Override
    public void getCoachFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        isProgress = false;
        updateProgressUI();
    }

    private void callPayment(String id) {

         ApplicationEx.getInstance().selectedCoach = selectedCoach;
        //call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, PaymentQuestionActivity.class);
        mainIntent.putExtra("ID", id);
        mainIntent.putExtra("WEB_STATE", PaymentQuestionActivity.STATE_PAYMENT); //webstate = payment

        this.startActivity(mainIntent);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.profilecontainer) {

            int pos = (Integer) v.getTag(R.id.coachselect_coachindex);

            selectedCoach = coaches.get(pos);
            selectedCoachID = (String) v.getTag(R.id.coachselect_coachid);

            if (adapter != null)
                adapter.onClick(v);

        } else if (v == footerButton) {
            //submit
            callPayment(selectedCoachID);

        }

    }


    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
        Bitmap bmp = ImageManager.getInstance().findImage(photoId);
        for (int i = 0; i < coaches.size(); i++) {
            Coach coach = coaches.get(i);
            if (photoId == coach.coach_id) {
                coach.coach_photo = new Photo();
                coach.coach_photo.image = bmp;
                coaches.set(i, coach);
                updateCoachSelectionList(coaches);

            }
        }

    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}


