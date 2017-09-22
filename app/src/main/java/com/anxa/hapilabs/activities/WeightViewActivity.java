package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentCommentController;
import com.anxa.hapilabs.controllers.hapimoment.GetHapiMomentController;
import com.anxa.hapilabs.controllers.progress.GetWeightDataController;
import com.anxa.hapilabs.controllers.progress.WeightDataController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.hapilabs.R;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class WeightViewActivity extends HAPIActivity implements WeightDataListener, MealAddCommentListener {
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
    private TextView weightText;
    private TextView leanMassText;
    private TextView bodyFatText;
    private TextView boneMassText;
    private TextView bmiText;
    private TextView waterWeightText;

    private TextView submit_tv;
    private EditText comment_et;

    private boolean isHAPI4USelected = false;

    AddHapiMomentCommentController addHapiMomentCommentController;

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
        weightText  = (TextView)findViewById(R.id.weight_WeightText);
        bodyFatText  = (TextView)findViewById(R.id.weight_BodyFatText);
        leanMassText  = (TextView)findViewById(R.id.weight_LeanMassText);
        waterWeightText  = (TextView)findViewById(R.id.weight_WaterWeightText);
        boneMassText  = (TextView)findViewById(R.id.weight_BoneMassText);
        bmiText  = (TextView)findViewById(R.id.weight_BMIText);

        comment_et = (EditText) findViewById(R.id.comment_et);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        if (fromNotifCommunity) {
            community_header.setVisibility(View.VISIBLE);
            if (from3rdPartyCommunity) {
                //updateCommunityHeader();
            } else {
                //getWeight(String.valueOf(currentWeightView.activity_id));

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
    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }else if (v == submit_tv) {
            // check if the textview has something
            if (comment_et != null && comment_et.getText() != null && comment_et.getText().length() > 0) {
                String comment_message = comment_et.getText().toString();
                comment_et.setText("");
                createNewComment(comment_message);
            }
        }
    }


    public void communityCommentsSelected() {
        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.weight_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.weight_community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void communityCommentsSelected(View view) {
        isCommunityTabSelected = true;
        commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.weight_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.weight_community_comments_btn)).setTextColor(Color.BLACK);

        updateHAPI4ULayout();
    }
    private void refreshUI() {

        weightViewTopDate.setText(AppUtil.getEditWeightDateFormat(currentWeightView.start_datetime));
        weightViewTopTime.setText(AppUtil.getMealTime(currentWeightView.start_datetime));
        weightText.setText(String.format("%.2f", currentWeightView.currentWeightGrams / 1000) + "kg" );
        bodyFatText.setText(String.format("%.2f", currentWeightView.BodyFatRatio / 1000)+ "%");
        leanMassText.setText(String.format("%.2f", currentWeightView.LeanMassRatio / 1000)+ "%");
        waterWeightText.setText(String.format("%.2f", currentWeightView.BodyWaterRatio / 1000)+ "%");
        boneMassText.setText(String.format("%.2f", currentWeightView.BoneWeightGrams / 1000)+ "kg");
        bmiText.setText(String.format("%.2f", currentWeightView.BMI / 1000));

        if (isCommunityTabSelected) {
            // updateHAPI4ULayout();

            commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWeightView.comments));
        }
    }

    private void getWeight(String activityId) {
        if (getWeightDataController == null) {
            getWeightDataController = new GetWeightDataController(this, this);
        }
        getWeightDataController.getWeight( ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }
    private void updateHAPI4ULayout() {
        final int hapi4U_count = currentWeightView.hapi4Us.size();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                hapi4u_ll.setVisibility(View.VISIBLE);

                if (currentWeightView.hapi4Us.size() <= 0) {
                    hapi4u_numCount_tv.setVisibility(View.GONE);
                    hapi4u_user_iv.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.weight_hapi4u_smiley)).setVisibility(View.GONE);
                    ((RoundedImageView) findViewById(R.id.weight_hapi4u_num_image)).setVisibility(View.GONE);
                } else {
                    //check user liked his own post
                    for (HAPI4U hapi4U : currentWeightView.hapi4Us) {
                        if (hapi4U.user.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID())) {
                            isHAPI4USelected = true;
                        }
                    }
                    if (isHAPI4USelected) {
                        hapi4u_post_tv.setTextColor(ContextCompat.getColor(context, R.color.text_orangedark));
                        hapi4u_post_iv.setImageDrawable(getResources().getDrawable(R.drawable.meal_smiley_orange));
                    } else {
                        hapi4u_post_tv.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                        hapi4u_post_iv.setImageDrawable(getResources().getDrawable(R.drawable.hapi4u_blue));
                    }

                    hapi4u_numCount_tv.setVisibility(View.VISIBLE);
                    hapi4u_user_iv.setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.weight_hapi4u_smiley)).setVisibility(View.VISIBLE);
                    ((RoundedImageView) findViewById(R.id.weight_hapi4u_num_image)).setVisibility(View.VISIBLE);
                    hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                    //Get latest HAPI4u
                    HAPI4U hapi4U = currentWeightView.hapi4Us.get(currentWeightView.hapi4Us.size() - 1);

                    Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                    if (bmp == null) {
                        new WeightViewActivity.DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
                    } else
                        hapi4u_user_iv.setImageBitmap(bmp);
                }
            }
        });
    }
    private void createNewComment(String comment_message) {
        Comment comment = new Comment();

        if (isCommunityTabSelected) {
            comment.comment_type = 0;
            CommentUser user = new CommentUser();
            user.user_id = ApplicationEx.getInstance().userProfile.getRegID();
            user.firstname = ApplicationEx.getInstance().userProfile.getFirstname();
            comment.user = user;
        } else {
            comment.comment_type = 3;
        }

        comment.meal_id = String.valueOf(currentWeightView.activity_id);
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;

        currentWeightView.comments.add(comment);
        hideKeyboard();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentWeightView.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWeightView.comments));
        }

        nonBlockingUI(comment);
    }
    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(currentWeightView.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_ll.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.weight_coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.weight_community_comments_btn)).setTextColor(Color.LTGRAY);
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
    public void postHAPI4U(View view) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                postHAPI4UtoAPI();
            }
        });
    }
    public void nonBlockingUI(final Comment comment) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                submitComment(comment);
            }
        });
    }

    private void hideKeyboard() {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);
    }
    private void submitComment(Comment currentcomment) {
        System.out.println("submitComment: " + currentcomment);
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            if (isCommunityTabSelected) {
                addHapiMomentCommentController.uploadMealCommunityComment(String.valueOf(currentWeightView.activity_id), currentcomment, username);
            } else {
                addHapiMomentCommentController.uploadMealComment(String.valueOf(currentWeightView.activity_id), currentcomment, username);
            }
        }
    }
    private void postHAPI4UtoAPI() {
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            addHapiMomentCommentController.postHAPI4U(String.valueOf(currentWeightView.activity_id), null, username);
        }
    }

    @Override
    public void uploadMealCommentSuccess(String response) {

    }

    @Override
    public void uploadMealCommentFailedWithError(MessageObj response, String entryID) {
        updateHAPI4ULayout();
    }

    @Override
    public void uploadMealCommentRefresh(Comment comment) {

    }

    @Override
    public void uploadMealCommentDelete(Comment comment) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;
        String photoid = "";

        public DownloadImageTask(ImageView bmImage, int photoID) {
            this.bmImage = bmImage;
            this.photoid = Integer.toString(photoID);
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

            ImageManager.getInstance().addImage(photoid, result);

        }
    }
}
