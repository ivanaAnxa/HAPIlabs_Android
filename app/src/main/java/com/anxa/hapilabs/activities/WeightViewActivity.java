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

import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.hapimoment.GetHapiMomentController;
import com.anxa.hapilabs.controllers.progress.GetWeightDataController;
import com.anxa.hapilabs.controllers.progress.WeightDataController;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.hapilabs.R;

import java.util.List;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class WeightViewActivity extends HAPIActivity implements WeightDataListener
{
    final Context context = this;

    GetWeightDataController getWeightDataController;

    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;
    private Boolean isCommunityTabSelected = false;

    private Weight currentWeightView;

    private CommentListLayout commentlist;
    private RelativeLayout communityComments_rl;
    private RelativeLayout coachComments_rl;
    private RelativeLayout community_header;
    private TextView hapi4u_post_tv;
    private TextView hapi4u_numCount_tv;
    private ImageView hapi4u_post_iv;
    private RoundedImageView hapi4u_user_iv;
    private RelativeLayout hapi4u_ll;
    private TextView weightViewTopDate;
    private TextView weightViewTopTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.weight_view);

        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        if (fromNotif) {
            updateHeader(1, getResources().getString(R.string.WEIGHT_BUTTON), this);
        } else {
            updateHeader(0, getResources().getString(R.string.WEIGHT_BUTTON), this);
        }
        community_header = (RelativeLayout) findViewById(R.id.weight_community_header);
        commentlist = (CommentListLayout) findViewById(R.id.weight_commentlist);
        hapi4u_ll = (RelativeLayout) findViewById(R.id.weight_hapi4u_ll);
        communityComments_rl = (RelativeLayout) findViewById(R.id.weight_community_comments_container);
        coachComments_rl = (RelativeLayout) findViewById(R.id.weight_coach_comments_container);
        hapi4u_numCount_tv = (TextView) hapi4u_ll.findViewById(R.id.weight_hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_ll.findViewById(R.id.weight_hapi4u_avatar);
        hapi4u_post_iv = (ImageView) hapi4u_ll.findViewById(R.id.weight_hapi4u_iv);
        hapi4u_post_tv = (TextView) hapi4u_ll.findViewById(R.id.weight_hapi4u_tv);
        hapi4u_ll.setVisibility(View.GONE);
        currentWeightView = ApplicationEx.getInstance().currentWeightView;
        weightViewTopDate  = (TextView)findViewById(R.id.weightViewTop_Date);
        weightViewTopTime  = (TextView)findViewById(R.id.weightViewTop_Time);
        if (fromNotifCommunity) {
            community_header.setVisibility(View.VISIBLE);
            if (from3rdPartyCommunity) {
                //updateCommunityHeader();
            } else {
                getWeight(String.valueOf(currentWeightView.activity_id));

                community_header.setVisibility(View.GONE);
            }
            communityCommentsSelected();

        } else {
            community_header.setVisibility(View.GONE);
            getWeight(String.valueOf(currentWeightView.activity_id));


        }

        if (currentWeightView != null) {
            refreshUI();
        }
    }

    private void refreshUI() {

        weightViewTopDate.setText(AppUtil.getEditWeightDateFormat(currentWeightView.start_datetime));
        weightViewTopTime.setText(AppUtil.getMealTime(currentWeightView.start_datetime));
        if (isCommunityTabSelected) {
           // updateHAPI4ULayout();

            commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWeightView.comments));
        }
    }

    public void communityCommentsSelected() {
        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.hapimoment_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.hapimoment_community_comments_btn)).setTextColor(Color.BLACK);
    }


    private void getWeight(String activityId) {
        if (getWeightDataController == null) {
            getWeightDataController = new GetWeightDataController(this, this);
        }
        getWeightDataController.getWeight( ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }


    @Override
    public void postWeightDataSuccess(String response) {

    }

    @Override
    public void postWeightDataFailedWithError(MessageObj response) {

    }

    @Override
    public void getWeightDataSuccess(Weight response) {

    }

    @Override
    public void getWeightDataFailedWithError(MessageObj response) {

    }
}
