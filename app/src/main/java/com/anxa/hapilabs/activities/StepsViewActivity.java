package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.hapimoment.GetHapiMomentController;
import com.anxa.hapilabs.controllers.progress.GetStepsDataController;
import com.anxa.hapilabs.controllers.progress.GetWeightDataController;
import com.anxa.hapilabs.controllers.progress.WeightDataController;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.hapilabs.R;

import java.util.List;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class StepsViewActivity extends HAPIActivity implements StepsDataListener
{
    final Context context = this;

    GetStepsDataController getStepsDataController;

    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;
    private Boolean isCommunityTabSelected = false;

    private Steps currentStepsView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.steps_view); //or exercise

        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        if (fromNotif) {
            //updateHeader(1, getResources().getString(R.string.WEIGHT_BUTTON), this);
        } else {
            //updateHeader(0, getResources().getString(R.string.WEIGHT_BUTTON), this);
        }

        currentStepsView = ApplicationEx.getInstance().currentStepsView;
        if (fromNotifCommunity) {
           // community_header.setVisibility(View.VISIBLE);
            if (from3rdPartyCommunity) {
                //updateCommunityHeader();
            } else {
                getSteps(String.valueOf(currentStepsView.activity_id));

                //community_header.setVisibility(View.GONE);
            }
           // communityCommentsSelected();

        } else {
           // community_header.setVisibility(View.GONE);
            getSteps(String.valueOf(currentStepsView.activity_id));


        }

        if (currentStepsView != null) {
            refreshUI();
        }
    }

    private void refreshUI() {


    }




    private void getSteps(String activityId) {
        if (getStepsDataController == null) {
            getStepsDataController = new GetStepsDataController(this, this);
        }
        getStepsDataController.getSteps( ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }




    @Override
    public void postStepsDataSuccess(String response) {

    }

    @Override
    public void postStepsDataFailedWithError(MessageObj response) {

    }

    @Override
    public void getStepsDataSuccess(Steps response) {

    }

    @Override
    public void getStepsDataFailedWithError(MessageObj response) {

    }
}
