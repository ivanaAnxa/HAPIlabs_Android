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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.addmeal.AddISHapiCommentController;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentCommentController;
import com.anxa.hapilabs.controllers.progress.GetStepsDataController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.hapilabs.R;

import java.io.InputStream;
import java.io.SyncFailedException;
import java.util.Date;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class StepsViewActivity extends HAPIActivity implements StepsDataListener, View.OnClickListener
{
    final Context context = this;

    GetStepsDataController getStepsDataController;

    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;
    private Boolean isCommunityTabSelected = false;


    private TextView
            tv_stepViewTop_Title, tv_stepViewTop_Time,
            tv_stepDeviceType,
            tv_stepTaken, tv_stepDuration, tv_stepCalories;

    private TextView submit_tv;
    private EditText comment_et;

    private ProgressBar stepViewProgressBar;

    private Steps currentStepsView;

    private CommentListLayout commentlist;
    private RelativeLayout communityComments_rl;
    private RelativeLayout coachComments_rl;

    private RelativeLayout hapi4u_rl;
    private TextView hapi4u_numCount_tv;
    private RoundedImageView hapi4u_user_iv;
    private TextView hapi4u_post_tv;
    private ImageView hapi4u_post_iv;

    private boolean isHAPI4USelected = false;
    AddHapiMomentCommentController addHapiMomentCommentController;
    private AddISHapiCommentController isHapiController;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.steps_view); //or exercise

        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        initUI();

        updateHeader(17, getResources().getString(R.string.STEPS_BUTTON), this);

        /*if (fromNotif) {
            //updateHeader(1, getResources().getString(R.string.WEIGHT_BUTTON), this);
        } else {
            //updateHeader(0, getResources().getString(R.string.WEIGHT_BUTTON), this);
        }*/

        currentStepsView = ApplicationEx.getInstance().currentStepsView;
        if (fromNotifCommunity) {
           // community_header.setVisibility(View.VISIBLE);
            if (from3rdPartyCommunity) {
                //updateCommunityHeader();
            } else {
                getSteps(String.valueOf(currentStepsView.activity_id));

                //community_header.setVisibility(View.GONE);
            }
            communityCommentsSelected();

        } else {
           // community_header.setVisibility(View.GONE);
            getSteps(String.valueOf(currentStepsView.activity_id));


        }

        if (currentStepsView != null) {
            refreshUI();
        }
    }


    private void initUI () {
        tv_stepDeviceType = (TextView) findViewById(R.id.step_deviceText);
        tv_stepViewTop_Title = (TextView) findViewById(R.id.stepViewTop_Type);
        tv_stepViewTop_Time = (TextView) findViewById(R.id.stepViewTop_Time);
        tv_stepTaken = (TextView) findViewById(R.id.stepView_StepTakenText);
        tv_stepCalories = (TextView) findViewById(R.id.stepView_CaloriesText);
        tv_stepDuration = (TextView) findViewById(R.id.stepView_DurationText);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);
        comment_et = (EditText) findViewById(R.id.comment_et);

        commentlist = (CommentListLayout) findViewById(R.id.step_commentlist);
        communityComments_rl = (RelativeLayout) findViewById(R.id.step_community_comments_container);
        coachComments_rl = (RelativeLayout) findViewById(R.id.step_coach_comments_container);

        hapi4u_rl = (RelativeLayout) findViewById(R.id.step_hapi4u_ll);
        hapi4u_numCount_tv = (TextView) hapi4u_rl.findViewById(R.id.step_hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_rl.findViewById(R.id.step_hapi4u_avatar);
        hapi4u_post_tv = (TextView) hapi4u_rl.findViewById(R.id.step_hapi4u_tv);
        hapi4u_post_iv = (ImageView) hapi4u_rl.findViewById(R.id.step_hapi4u_iv);

        stepViewProgressBar = (ProgressBar)findViewById(R.id.step_progressBar);


    }


    private void refreshUI() {
        tv_stepDeviceType.setText(currentStepsView.device_name);
        tv_stepViewTop_Title.setText(AppUtil.getEditWeightDateFormat(currentStepsView.start_datetime));
        tv_stepViewTop_Time.setText(AppUtil.getExerciseTime(currentStepsView.start_datetime));
        tv_stepTaken.setText(String.format(currentStepsView.steps_count));
        tv_stepCalories.setText(String.valueOf(currentStepsView.steps_calories));

        int hours = (int) (currentStepsView.steps_duration/60);
        int minutes = (int) (currentStepsView.steps_duration%60);
        tv_stepDuration.setText(String.format("%d:%02d:00", hours, minutes));

        stepViewProgressBar.setVisibility(View.GONE);
    }




    private void getSteps(String activityId) {
        if (getStepsDataController == null) {
            getStepsDataController = new GetStepsDataController(this, this);
        }
        getStepsDataController.getSteps( ApplicationEx.getInstance().userProfile.getRegID(), activityId);
    }

    public void onClick(View v){
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        } else if (v == submit_tv) {
            // check if the textview has something
            if (comment_et != null && comment_et.getText() != null && comment_et.getText().length() > 0) {
                String comment_message = comment_et.getText().toString();
                comment_et.setText("");

                createNewComment(comment_message);
            }
        } else if (v.getId() == R.id.chat_status) {
            if (v.getTag() != null && v.getTag() instanceof Comment) {
                Comment comment = (Comment) v.getTag();
                isCommentHapi(comment);
            }
        }
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

        comment.meal_id = String.valueOf(currentStepsView.activity_id);
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;

        currentStepsView.comments.add(comment);
        hideKeyboard();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentStepsView.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentStepsView.comments));
        }

        nonBlockingUI(comment);
    }
    private void hideKeyboard() {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);
    }
    public void nonBlockingUI(final Comment comment) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                submitComment(comment);
            }
        });
    }
    private void submitComment(Comment currentcomment) {
        System.out.println("submitComment: " + currentcomment);
        if (addHapiMomentCommentController == null) {
            //addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            if (isCommunityTabSelected) {
                addHapiMomentCommentController.uploadMealCommunityComment(String.valueOf(currentStepsView.activity_id), currentcomment, username);
            } else {
                addHapiMomentCommentController.uploadMealComment(String.valueOf(currentStepsView.activity_id), currentcomment, username);
            }
        }
    }

    private void isCommentHapi(Comment comment) {
        if (isHapiController == null) {
            //isHapiController = new AddISHapiCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null)
            isHapiController.uploadMealiSHapiComment(currentStepsView.activity_id, comment, username);
    }

    public void communityCommentsSelected() {
        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentStepsView.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_rl.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.step_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.step_community_comments_btn)).setTextColor(Color.BLACK);

        updateHAPI4ULayout();
    }

    public void communityCommentsSelected(View view) {
        isCommunityTabSelected = true;
        commentlist.updateData(AppUtil.getCommunityComments(currentStepsView.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_rl.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.step_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.step_community_comments_btn)).setTextColor(Color.BLACK);

        updateHAPI4ULayout();
    }

    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(currentStepsView.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_rl.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.step_coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.step_community_comments_btn)).setTextColor(Color.LTGRAY);
    }

    private void updateHAPI4ULayout() {
        final int hapi4U_count = currentStepsView.hapi4Us.size();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                hapi4u_rl.setVisibility(View.VISIBLE);

                if (currentStepsView.hapi4Us.size() <= 0) {
                    hapi4u_numCount_tv.setVisibility(View.GONE);
                    hapi4u_user_iv.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.step_hapi4u_smiley)).setVisibility(View.GONE);
                    ((RoundedImageView) findViewById(R.id.step_hapi4u_num_image)).setVisibility(View.GONE);
                } else {
                    //check user liked his own post
                    for (HAPI4U hapi4U : currentStepsView.hapi4Us) {
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
                    ((ImageView) findViewById(R.id.step_hapi4u_smiley)).setVisibility(View.VISIBLE);
                    ((RoundedImageView) findViewById(R.id.step_hapi4u_num_image)).setVisibility(View.VISIBLE);
                    hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                    //Get latest HAPI4u
                    HAPI4U hapi4U = currentStepsView.hapi4Us.get(currentStepsView.hapi4Us.size() - 1);

                    Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                    if (bmp == null) {
                        new DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
                    } else
                        hapi4u_user_iv.setImageBitmap(bmp);
                }
            }
        });
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
