package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetCoachProgramListener;
import com.anxa.hapilabs.common.connection.listener.PostProductOrderListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.controllers.accountsettings.PostProductOrderController;
import com.anxa.hapilabs.controllers.registration.CoachProgramController;
import com.anxa.hapilabs.models.CoachProgram;
import com.anxa.hapilabs.models.MessageObj;

import java.util.List;

/**
 * Created by aprilanxa on 18/08/2016.
 */
public class PaymentOptionsActivity extends HAPIActivity implements ProgressChangeListener, GetCoachProgramListener, View.OnClickListener,
        PostProductOrderListener{

    List<CoachProgram> coachPrograms;
    CoachProgramController coachProgramController;
    PostProductOrderController postProductOrderController;
    String coachId;

    //UI
    RelativeLayout plans_rl;

    LinearLayout plan_ll;
    LinearLayout plan1_ll;
    LinearLayout plan2_ll;
    LinearLayout plan3_ll;

    TextView plan1_title_tv;
    TextView plan2_title_tv;
    TextView plan3_title_tv;

    TextView plan1_teaser_tv;
    TextView plan2_teaser_tv;
    TextView plan3_teaser_tv;

//    WebView planWebView;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        coachId = getIntent().getStringExtra("ID");

        setContentView(R.layout.chooseyourplan);

        plans_rl = (RelativeLayout) findViewById(R.id.paymentoptions_rl);

        plan_ll = (LinearLayout) findViewById(R.id.plan_ll);
        plan_ll.setVisibility(View.INVISIBLE);

        plan1_ll = (LinearLayout) findViewById(R.id.plan1_ll);
        plan2_ll = (LinearLayout) findViewById(R.id.plan2_ll);
        plan3_ll = (LinearLayout) findViewById(R.id.plan3_ll);

        plan1_title_tv = (TextView) findViewById(R.id.plan1_title_tv);
        plan2_title_tv = (TextView) findViewById(R.id.plan2_title_tv);
        plan3_title_tv = (TextView) findViewById(R.id.plan3_title_tv);

        plan1_teaser_tv = (TextView) findViewById(R.id.plan1_teaser_tv);
        plan2_teaser_tv = (TextView) findViewById(R.id.plan2_teaser_tv);
        plan3_teaser_tv = (TextView) findViewById(R.id.plan3_teaser_tv);

        progressBar = (ProgressBar) findViewById(R.id.progressBar_payment);
        progressBar.setVisibility(View.VISIBLE);

        updateHeader(4, getResources().getString(R.string.PAYMENT_OPTIONS_TITLE), this);

        if (coachProgramController == null)
            coachProgramController = new CoachProgramController(this, this, this);

        coachProgramController.getPaymentOptions(coachId);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }
    }

    /**Get Coach Program Listener**/
    public void getCoachProgramSuccess(String response, List<CoachProgram> coachPrograms) {

        this.coachPrograms = coachPrograms;
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

                updateUI();
            }
        });

    }

    public void getCoachProgramFailedWithError(MessageObj response) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**Post Product Order Listener**/
    public void postProductOrderSuccess(String response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        System.out.println("postProductOrderSuccess");
        proceedToPremiumPage();
    }
    public void postProductOrderFailedWithError(MessageObj response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });
        System.out.println("postProductOrderFailed");
    }

    /**Public Button Listener**/
    public void proceedToPremium(View v){
        System.out.println("proceedToPremium");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        postProductOrder();

    }

    public void proceedToWebKit(View v){
        if (v==plan2_ll){
            System.out.println("proceedToWebkit - plan2");
        }else if (v==plan3_ll){
            System.out.println("proceedToWebkit - plan3");
        }
    }

    /**
     * Private Methods
     **/
    private void updateUI() {

        progressBar.setVisibility(View.GONE);

        if (coachPrograms != null && coachPrograms.size() > 0) {

            plan_ll.setVisibility(View.VISIBLE);

            if (coachPrograms.size() >= 3) {
                plan1_ll.setVisibility(View.VISIBLE);
                plan2_ll.setVisibility(View.VISIBLE);
                plan3_ll.setVisibility(View.VISIBLE);

                plan1_title_tv.setText(coachPrograms.get(0).coachprogram_title);
                plan1_teaser_tv.setText(coachPrograms.get(0).teaser);

                plan2_title_tv.setText(coachPrograms.get(1).coachprogram_title);
                plan2_teaser_tv.setText(coachPrograms.get(1).teaser);

                plan3_title_tv.setText(coachPrograms.get(2).coachprogram_title);
                plan3_teaser_tv.setText(coachPrograms.get(2).teaser);

            }else if (coachPrograms.size() == 2) {
                plan1_ll.setVisibility(View.VISIBLE);
                plan2_ll.setVisibility(View.VISIBLE);
                plan3_ll.setVisibility(View.GONE);

                plan1_title_tv.setText(coachPrograms.get(0).coachprogram_title);
                plan1_teaser_tv.setText(coachPrograms.get(0).teaser);

                plan2_title_tv.setText(coachPrograms.get(1).coachprogram_title);
                plan2_teaser_tv.setText(coachPrograms.get(1).teaser);

            } else if (coachPrograms.size() == 1) {
                plan1_ll.setVisibility(View.VISIBLE);
                plan2_ll.setVisibility(View.GONE);
                plan3_ll.setVisibility(View.GONE);

                plan1_title_tv.setText(coachPrograms.get(0).coachprogram_title);
                plan1_teaser_tv.setText(coachPrograms.get(0).teaser);
            }
        }
    }

    private void postProductOrder(){
        System.out.println("postProductOrder: " + coachPrograms.get(0).coachprogram_id);

        if (postProductOrderController == null) {
            postProductOrderController = new PostProductOrderController(this, this, this);
        }
        startProgress();

        postProductOrderController.postProductOrder(Integer.toString(coachPrograms.get(0).coachprogram_id));
    }

    private void proceedToPremiumPage(){
        Intent mainIntent = new Intent(this, PremiumAccessActivity.class);
        mainIntent.putExtra("coachProgramID", Integer.toString(coachPrograms.get(0).coachprogram_id));
        mainIntent.putExtra("coachID", coachId);
        this.startActivity(mainIntent);
    }
}
