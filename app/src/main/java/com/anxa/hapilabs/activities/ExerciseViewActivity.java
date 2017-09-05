package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetWorkoutListener;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.PostWorkoutListener;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.exercise.GetWorkoutController;
import com.anxa.hapilabs.controllers.exercise.PostWorkoutController;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentCommentController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.CustomDialog;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * Created by ivanaanxa on 9/1/2016.
 */
public class ExerciseViewActivity extends HAPIActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, GetWorkoutListener, PostWorkoutListener, MealAddCommentListener {

    private TextView tv_exerciseType, tv_exerciseTime, tv_exerciseDescription, tv_exerciseStep,
            tv_exerciseDistance, tv_exerciseDuration, coachChecked;
    private ImageView iv_exerciseType;
    private LinearLayout isCheckedll;
    private PopupMenu popupMenu;
    private CustomDialog dialog;
    private String workoutid;
    private Workout myCurrentWorkout;
    private ProgressBar exerciseViewProgressBar;

    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;
    private Boolean isCommunityTabSelected = false;
    private boolean isHAPI4USelected = false;


    private GetWorkoutController getWorkoutController;
    private PostWorkoutController postWorkoutController;

    private final static int MENU_EDIT_EXERCISE = 11;
    private final static int MENU_SHARE_FACEBOOK = 12;
    private final static int MENU_SHARE_TWITTER = 13;
    private final static int MENU_DELETE_EXERCISE = 15;
    private final static int MENU_CANCEL = 16;

    private final static int TWITTER_REQUEST_CODE = 111;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    private TextView hapi4u_post_tv;
    private TextView hapi4u_numCount_tv;
    private ImageView hapi4u_post_iv;
    private RoundedImageView hapi4u_user_iv;
    private RelativeLayout hapi4u_ll;
    private LinearLayout save_button_ll;
    private LinearLayout exercise_approvedIconContainer;

    private CommentListLayout commentlist;
    private RelativeLayout communityComments_rl;
    private RelativeLayout coachComments_rl;
    private RelativeLayout community_header;

    private TextView submit_tv;
    private EditText comment_et;

    final Context context = this;

    AddHapiMomentCommentController addHapiMomentCommentController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.exercise_view);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        initUI();
        updateHeader(16, getResources().getString(R.string.EXERCISE_HEADER), this);
        createPopUpMenu();

        isCommunityTabSelected = false;

        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        //comments
        commentlist = (CommentListLayout) findViewById(R.id.exercise_commentlist);
        save_button_ll = (LinearLayout) findViewById(R.id.save_button_ll);

        community_header = (RelativeLayout) findViewById(R.id.exercise_community_header);

        hapi4u_ll = (RelativeLayout) findViewById(R.id.exercise_hapi4u_ll);
        communityComments_rl = (RelativeLayout) findViewById(R.id.exercise_community_comments_container);
        coachComments_rl = (RelativeLayout) findViewById(R.id.exercise_coach_comments_container);
        hapi4u_numCount_tv = (TextView) hapi4u_ll.findViewById(R.id.exercise_hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_ll.findViewById(R.id.exercise_hapi4u_avatar);
        hapi4u_post_iv = (ImageView) hapi4u_ll.findViewById(R.id.exercise_hapi4u_iv);
        hapi4u_post_tv = (TextView) hapi4u_ll.findViewById(R.id.exercise_hapi4u_tv);
        hapi4u_ll.setVisibility(View.GONE);

        exercise_approvedIconContainer = (LinearLayout) findViewById(R.id.exerciseView_checked);
        communityComments_rl.setSelected(false);
        coachComments_rl.setSelected(true);

        exerciseViewProgressBar = (ProgressBar)findViewById(R.id.exercise_progressBar);

        comment_et = (EditText) findViewById(R.id.comment_et);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        myCurrentWorkout = ApplicationEx.getInstance().currentWorkoutView;

        if (fromNotif) {
            updateHeader(1, getResources().getString(R.string.EXERCISE_HEADER), this);
            if (fromNotifCommunity) {
                isCommunityTabSelected = true;
                if (from3rdPartyCommunity) {
                    updateCommunityHeader();

                    if (myCurrentWorkout != null)
                        updateUI();
                } else {
                    if (myCurrentWorkout != null) {
                        updateUI();
                    }else{
                        exerciseViewProgressBar.setVisibility(View.VISIBLE);
                        getWorkoutDetails();
                    }
                    community_header.setVisibility(View.GONE);
                }
                communityCommentsSelected();
            } else {
                updateUI();
            }
        } else {
            getWorkoutDetailsFromAPI();
        }
        initFacebookSharing();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initUI() {
        tv_exerciseType = (TextView) findViewById(R.id.exerciseViewTop_Type);
        tv_exerciseTime = (TextView) findViewById(R.id.exerciseViewTop_Time);
        tv_exerciseDescription = (TextView) findViewById(R.id.exerciseView_Description);
        tv_exerciseStep = (TextView) findViewById(R.id.exerciseView_StepTakenText);
        tv_exerciseDistance = (TextView) findViewById(R.id.exerciseView_DistanceText);
        tv_exerciseDuration = (TextView) findViewById(R.id.exerciseView_DurationText);
        iv_exerciseType = (ImageView) findViewById(R.id.exerciseView_TypeIcon);
        isCheckedll = (LinearLayout) findViewById(R.id.exerciseView_checked);
        coachChecked = (TextView) findViewById(R.id.checked);
        coachChecked.setText(getString(R.string.COACH_CHECKED));
    }

    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            popupMenu.show();
        } else if (v.getId() == R.id.YesButton || v.getId() == R.id.NoButton) {
            if (dialog != null)
                dialog.dismiss();
            if (v.getId() == R.id.YesButton)
                deleteExercise();
        } else if (v.getId() == R.id.OtherButton) {
            dialog.dismiss();
        } else if (v == submit_tv) {
            // check if the textview has something
            if (comment_et != null && comment_et.getText() != null && comment_et.getText().length() > 0) {
                String comment_message = comment_et.getText().toString();
                comment_et.setText("");
                createNewComment(comment_message);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TWITTER_REQUEST_CODE) {
            int TWITTER_SUCCESFUL_POST = 0;
            if (resultCode == TWITTER_SUCCESFUL_POST) {
                showCustomDialog(getResources().getString(R.string.TWITTER_SUCCESSFUL_CONTENT_ACTIVITY), getResources().getString(R.string.TWITTER_SUCCESSFUL_TITLE_ACTIVITY));
            } else {
                showCustomDialog(getResources().getString(R.string.TWITTER_FAILED_CONTENT), getResources().getString(R.string.TWITTER_FAILED_TITLE));
            }
        }
    }

    private void createPopUpMenu() {
        popupMenu = new PopupMenu(this, findViewById(R.id.header_right_tv));
        popupMenu.getMenu().add(Menu.NONE, MENU_EDIT_EXERCISE, Menu.NONE, getResources().getString(R.string.MENU_EDIT_EXERCISE));
        popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_FACEBOOK, Menu.NONE, getResources().getString(R.string.MENU_SHARE_FACEBOOK));
        popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_TWITTER, Menu.NONE, getResources().getString(R.string.MENU_SHARE_TWITTER));
        popupMenu.getMenu().add(Menu.NONE, MENU_DELETE_EXERCISE, Menu.NONE, getResources().getString(R.string.MENU_DELETE_EXERCISE));
        popupMenu.getMenu().add(Menu.NONE, MENU_CANCEL, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CANCEL));
        popupMenu.setOnMenuItemClickListener(this);
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT_EXERCISE:
                editExercise();
                break;
            case MENU_SHARE_FACEBOOK:
                shareViaFacebook();
                break;
            case MENU_SHARE_TWITTER:
                shareViaTwitter();
                break;
            case MENU_DELETE_EXERCISE:
                showCustomDialog();
                break;
            case MENU_CANCEL:
                break;
        }
        return false;
    }

    private void showCustomDialog() {
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), false, getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL), null, this);
        dialog.show();
    }

    private void showCustomDialog(String message, String title) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, message, title, this);
        dialog.show();
    }

    private void getWorkoutDetails() {
        if (getWorkoutController == null) {
            getWorkoutController = new GetWorkoutController(this.getApplicationContext(), this);
        }
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            workoutid = extra.getString("EXERCISE_ACTIVITY_ID");
            WorkoutDAO dao = new WorkoutDAO(this.getBaseContext(), null);
            myCurrentWorkout = dao.getWorkoutByActivityID(workoutid);

            if (myCurrentWorkout.activity_id != null) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI();
                    }
                });
            }
        }

    }

    private void getWorkoutDetailsFromAPI() {
        if (getWorkoutController == null) {
            getWorkoutController = new GetWorkoutController(this.getApplicationContext(), this);
        }
        getWorkoutController.getWorkout(ApplicationEx.getInstance().userProfile.getRegID(), myCurrentWorkout.activity_id);
    }


    @Override
    public void getWorkoutSuccess(final Workout response) {
        myCurrentWorkout = response;

        System.out.println("getWorkoutSucces: " + response.comments);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });
    }

    @Override
    public void getWorkoutFailedWithError(MessageObj response) {
    }


    public void postWorkoutSuccess(String response, Workout workoutObj) {

        if (myCurrentWorkout.command == "deleted") {
            System.out.println("Deleted entry Success!!!");
            int index = -1;
            for (Workout w : ApplicationEx.getInstance().workouts) {
                if (w.activity_id == myCurrentWorkout.activity_id)
                    index = ApplicationEx.getInstance().workouts.indexOf(w);
            }
            if (index > -1) {
                ApplicationEx.getInstance().workouts.remove(index);
                ApplicationEx.getInstance().workoutList.remove(String.valueOf(myCurrentWorkout.activity_id));
            }
        }

        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.workoutListDelete));
        this.sendBroadcast(broadint);

        finish();
    }

    public void postWorkoutFailedWithError(MessageObj response, Workout workoutObj) {
    }


    /**
     * Private Methods
     **/
    private void run(final String userID, final String workoutid) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateWorkOut(userID, workoutid);
            }
        });
    }

    private void updateWorkOut(String userID, String workoutId) {
        if (getWorkoutController == null) {
            getWorkoutController = new GetWorkoutController(this.getApplicationContext(), this);
        }
        getWorkoutController.getWorkout(ApplicationEx.getInstance().userProfile.getRegID(), workoutId);
    }

    private void updateUI() {
        if (myCurrentWorkout.isChecked) {
            exercise_approvedIconContainer.setVisibility(View.VISIBLE);
        } else {
            exercise_approvedIconContainer.setVisibility(View.GONE);
        }
        tv_exerciseType.setText(AppUtil.getExerciseValue(this, myCurrentWorkout.exercise_type));
        tv_exerciseTime.setText(AppUtil.getExerciseTime(myCurrentWorkout.exercise_datetime));
        tv_exerciseDescription.setText(myCurrentWorkout.workout_desc);
        tv_exerciseStep.setText(String.valueOf(myCurrentWorkout.steps));
        tv_exerciseDistance.setText(String.valueOf(myCurrentWorkout.distance));

        int hours = myCurrentWorkout.duration / 60; //since both are ints, you get an int
        int minutes = myCurrentWorkout.duration % 60;

        tv_exerciseDuration.setText(String.format("%d:%02d:00", hours, minutes));

        switch (myCurrentWorkout.exercise_type) {
            case RUNNING:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_running);
                break;
            case CYCLING:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_biking);
                break;
            case WALKING:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_walking);
                break;
            case SWIMMING:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_swimming);
                break;
            case WORKOUT:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_workout);
                break;
            default:
                iv_exerciseType.setImageResource(R.drawable.exercise_inner_other);
        }

        if (isCommunityTabSelected) {
            updateHAPI4ULayout();
            commentlist.updateData(AppUtil.getCommunityComments(myCurrentWorkout.comments));
        } else {
            hapi4u_ll.setVisibility(View.GONE);
            commentlist.updateData(AppUtil.getCoachComments(myCurrentWorkout.comments));
        }

        exerciseViewProgressBar.setVisibility(View.GONE);
    }

    private void deleteExercise() {
        myCurrentWorkout.command = "deleted";
        postWorkoutController = new PostWorkoutController(this.getApplicationContext(), this);

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                postWorkoutController.postWorkout(ApplicationEx.getInstance().userProfile.getRegID(), myCurrentWorkout.command, myCurrentWorkout);
            }
        });
    }

    private void editExercise() {
        Intent i = new Intent(this, ExerciseActivity.class);
        i.putExtra("EXERCISE_ACTIVITY_ID", myCurrentWorkout.activity_id);
        this.startActivityForResult(i, 1);
    }

    //    Sharing
    private void initFacebookSharing() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                showCustomDialog(getResources().getString(R.string.FACEBOOK_SUCCESSFUL_CONTENT_ACTIVITY), getResources().getString(R.string.FACEBOOK_SUCCESSFUL_TITLE_ACTIVITY));
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                showCustomDialog(getResources().getString(R.string.FACEBOOK_FAILED_CONTENT), getResources().getString(R.string.FACEBOOK_FAILED_TITLE));
            }
        });
    }

    private void shareViaFacebook() {

        String fbContentURL = "";

        if (ApplicationEx.language.equals("fr")) {
            fbContentURL = "http://www.savoirmanger.fr/my-meals/activity/share/";
        } else {
            fbContentURL = "http://www.hapicoach.com/my-meals/activity/share/";
        }

        fbContentURL = fbContentURL + UrlSafeBase64(myCurrentWorkout.activity_id) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

        if (myCurrentWorkout != null) {

            String fbTitle = AppUtil.getExerciseValue(this, myCurrentWorkout.exercise_type) + " " + getResources().getString(R.string.FACEBOOK_TITLE) + " " + AppUtil.getExerciseTime(myCurrentWorkout.exercise_datetime);

            String fbDescription;

            if (myCurrentWorkout.workout_desc.length() > 0) {
                fbDescription = myCurrentWorkout.workout_desc + ". " + getResources().getString(R.string.FACEBOOK_DESCRIPTION_EXERCISE);
            } else {
                fbDescription = getResources().getString(R.string.FACEBOOK_EXERCISE_CONTENT);
            }

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle(fbTitle)
                        .setContentDescription(
                                fbDescription)
                        .setContentUrl(Uri.parse(fbContentURL))
                        .build();

                shareDialog.show(linkContent);
            }
        }
    }

    private String UrlSafeBase64(String string) {

        String output = "";
        int value = Integer.parseInt(string);

        byte[] byteArray = intToByteArray(value);
        output = new String(Base64.encode(byteArray, Base64.DEFAULT));
        output = output.replace("/", "_");
        output = output.replace("+", "-");
        output = output.replace("=", "");

        return output;
    }

    public static byte[] intToByteArray(int a) {
        byte[] ret = new byte[4];
        ret[0] = (byte) (a & 0xFF);
        ret[1] = (byte) ((a >> 8) & 0xFF);
        ret[2] = (byte) ((a >> 16) & 0xFF);
        ret[3] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    private void shareViaTwitter() {

        if (myCurrentWorkout != null) {
            String twitterContentURL;

            if (ApplicationEx.language.equals("fr")) {
                twitterContentURL = "http://www.savoirmanger.fr/my-meals/activity/share/";
            } else {
                twitterContentURL = "http://www.hapicoach.com/my-meals/activity/share/";
            }

            twitterContentURL = twitterContentURL + UrlSafeBase64(myCurrentWorkout.activity_id) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

            // Create intent using ACTION_VIEW and a normal Twitter url:
            String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                    urlEncode(getResources().getString(R.string.FACEBOOK_EXERCISE_CONTENT)),
                    removeWhiteSpaces(urlEncode(twitterContentURL)));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            // Narrow down to official Twitter app, if available:
            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
            startActivityForResult(intent, TWITTER_REQUEST_CODE);
        }
    }

    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf(TAG, "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }

    private String removeWhiteSpaces(String string) {
        String output = "";
        output = string.replace("%0A", "");
        return output;
    }

    private void updateCommunityHeader() {

        community_header.setVisibility(View.VISIBLE);
        (findViewById(R.id.header_right_tv)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.exercise_community_header)).setVisibility(View.VISIBLE);
        try {
            String user3rdName = ApplicationEx.getInstance().currentCommunityUser.firstname + " " + ApplicationEx.getInstance().currentCommunityUser.lastname;
            ((TextView) findViewById(R.id.exercise_community_name)).setText(user3rdName);
        } catch (NullPointerException e) {
            ((TextView) findViewById(R.id.exercise_community_name)).setText("");
        }

        ImageView community_avatar = (ImageView) findViewById(R.id.exercise_community_avatar);
        //update useravatar
        Bitmap bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().currentCommunityUser.user_id);
        if (bmp == null) {
            new DownloadImageTask(community_avatar, Integer.parseInt(ApplicationEx.getInstance().currentCommunityUser.user_id)).execute(ApplicationEx.getInstance().currentCommunityUser.picture_url_large);
        } else
            community_avatar.setImageBitmap(bmp);

        coachComments_rl.setVisibility(View.GONE);
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

    public void communityCommentsSelected() {
        isCommunityTabSelected = true;
        commentlist.updateData(AppUtil.getCommunityComments(myCurrentWorkout.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.exercise_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.exercise_community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void communityCommentsSelected(View view) {
        isCommunityTabSelected = true;
        commentlist.updateData(AppUtil.getCommunityComments(myCurrentWorkout.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.exercise_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.exercise_community_comments_btn)).setTextColor(Color.BLACK);

        updateHAPI4ULayout();
    }

    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(myCurrentWorkout.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_ll.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.exercise_coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.exercise_community_comments_btn)).setTextColor(Color.LTGRAY);
    }

    private void updateHAPI4ULayout() {
        final int hapi4U_count = myCurrentWorkout.hapi4Us.size();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                hapi4u_ll.setVisibility(View.VISIBLE);

                if (myCurrentWorkout.hapi4Us.size() <= 0) {
                    hapi4u_numCount_tv.setVisibility(View.GONE);
                    hapi4u_user_iv.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.exercise_hapi4u_smiley)).setVisibility(View.GONE);
                    ((RoundedImageView) findViewById(R.id.exercise_hapi4u_num_image)).setVisibility(View.GONE);
                } else {
                    //check user liked his own post
                    for (HAPI4U hapi4U : myCurrentWorkout.hapi4Us) {
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
                    ((ImageView) findViewById(R.id.exercise_hapi4u_smiley)).setVisibility(View.VISIBLE);
                    ((RoundedImageView) findViewById(R.id.exercise_hapi4u_num_image)).setVisibility(View.VISIBLE);
                    hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                    //Get latest HAPI4u
                    HAPI4U hapi4U = myCurrentWorkout.hapi4Us.get(myCurrentWorkout.hapi4Us.size() - 1);

                    Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                    if (bmp == null) {
                        new DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
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

        comment.meal_id = String.valueOf(myCurrentWorkout.activity_id);
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;

        myCurrentWorkout.comments.add(comment);
        hideKeyboard();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(myCurrentWorkout.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(myCurrentWorkout.comments));
        }

        nonBlockingUI(comment);
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

    public void submitComment(Comment currentcomment) {
        System.out.println("submitComment: " + currentcomment);
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            if (isCommunityTabSelected) {
                addHapiMomentCommentController.uploadMealCommunityComment(String.valueOf(myCurrentWorkout.activity_id), currentcomment, username);
            } else {
                addHapiMomentCommentController.uploadMealComment(String.valueOf(myCurrentWorkout.activity_id), currentcomment, username);
            }
        }
    }

    @Override
    public void uploadMealCommentSuccess(String response) {
        // TODO Auto-generated method stub
        //update UI
//        updateMealUI();
        if (myCurrentWorkout.activity_id != null) {
            runThis(ApplicationEx.getInstance().userProfile.getRegID(), String.valueOf(myCurrentWorkout.activity_id));
        }
    }

    public void runThis(final String userID, final String waterId) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getWorkoutDetailsFromAPI();
            }
        });
    }


    @Override
    public void uploadMealCommentFailedWithError(MessageObj response,
                                                 String entryID) {
        // TODO Auto-generated method stub
        updateHAPI4ULayout();
    }

    @Override
    public void uploadMealCommentRefresh(Comment comment) {
        // TODO Auto-generated method stub
        //find this comment
        if (myCurrentWorkout != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = myCurrentWorkout.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    commentFail.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;
                    myCurrentWorkout.comments.remove(i);
                    myCurrentWorkout.comments.add(i, comment);
                    i = comments.size() + 1;
                }
            }
        }

        //submit to server
        submitComment(comment);

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(myCurrentWorkout.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(myCurrentWorkout.comments));
        }
    }

    @Override
    public void uploadMealCommentDelete(Comment comment) {
        // TODO Auto-generated method stub
        if (myCurrentWorkout != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = myCurrentWorkout.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    myCurrentWorkout.comments.remove(i);
                    i = comments.size() + 1;
                }
            }
        }

        updateHAPI4ULayout();
        commentlist.updateData(AppUtil.getAllComments(myCurrentWorkout.comments));
    }

    public void postHAPI4U(View view) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                postHAPI4UtoAPI();
            }
        });
    }

    private void postHAPI4UtoAPI() {
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            addHapiMomentCommentController.postHAPI4U(String.valueOf(myCurrentWorkout.activity_id), null, username);
        }
    }

}
