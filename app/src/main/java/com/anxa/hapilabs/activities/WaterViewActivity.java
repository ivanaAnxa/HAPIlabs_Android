package com.anxa.hapilabs.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.AddWaterListener;
import com.anxa.hapilabs.common.connection.listener.GetWaterListener;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentCommentController;
import com.anxa.hapilabs.controllers.water.AddWaterController;
import com.anxa.hapilabs.controllers.water.GetWaterController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.ui.CommentListLayout;
import com.anxa.hapilabs.ui.RoundedImageView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by aprilanxa on 20/09/2016.
 */
public class WaterViewActivity extends HAPIActivity implements AddWaterListener, SeekBar.OnSeekBarChangeListener, GetWaterListener,
        MealAddCommentListener {

    private int FONT_SIZE = 18;
    private String waterStatus;

    private Water currentWater;
    private SeekBar glassSeekBar;
    private SeekBar bubbleSeekBar;
    private ProgressBar waterProgressBar;
    private ProgressBar getWaterProgressBar;
    private TextView waterDate;
    private Water waterEntryToUpload;
    private AddWaterController addWaterController;

    private TextView hapi4u_post_tv;
    private TextView hapi4u_numCount_tv;
    private ImageView hapi4u_post_iv;
    private RoundedImageView hapi4u_user_iv;
    private RelativeLayout hapi4u_ll;
    private LinearLayout save_button_ll;
    private LinearLayout water_approvedIconContainer;

    private CommentListLayout commentlist;
    private RelativeLayout communityComments_rl;
    private RelativeLayout coachComments_rl;
    private RelativeLayout community_header;

    private TextView submit_tv;
    private EditText comment_et;

    private boolean isHAPI4USelected = false;
    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;
    private Boolean isCommunityTabSelected = false;

    AddHapiMomentCommentController addHapiMomentCommentController;

    final Context context = this;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.water_view);

        waterStatus = getIntent().getStringExtra("WATER_STATUS");
        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        updateHeader(17, getResources().getString(R.string.MEALTYPE_WATER), this);

        glassSeekBar = (SeekBar) findViewById(R.id.seekBar_water);
        glassSeekBar.setOnSeekBarChangeListener(this);
        bubbleSeekBar = (SeekBar) findViewById(R.id.seekBar_water_thumb);
        bubbleSeekBar.setOnSeekBarChangeListener(this);
        waterProgressBar = (ProgressBar) findViewById(R.id.waterProgressBar);
        getWaterProgressBar = (ProgressBar) findViewById(R.id.water_view_progressBar);

        waterDate = (TextView) findViewById(R.id.waterViewTitle);

        //comments
        commentlist = (CommentListLayout) findViewById(R.id.water_commentlist);
        save_button_ll = (LinearLayout) findViewById(R.id.save_button_ll);

        hapi4u_ll = (RelativeLayout) findViewById(R.id.water_hapi4u_ll);
        communityComments_rl = (RelativeLayout) findViewById(R.id.water_community_comments_container);
        coachComments_rl = (RelativeLayout) findViewById(R.id.water_coach_comments_container);
        hapi4u_numCount_tv = (TextView) hapi4u_ll.findViewById(R.id.water_hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_ll.findViewById(R.id.water_hapi4u_avatar);
        hapi4u_post_iv = (ImageView) hapi4u_ll.findViewById(R.id.water_hapi4u_iv);
        hapi4u_post_tv = (TextView) hapi4u_ll.findViewById(R.id.water_hapi4u_tv);
        hapi4u_ll.setVisibility(View.GONE);

        water_approvedIconContainer = (LinearLayout)findViewById(R.id.water_approvedIconContainer);

        communityComments_rl.setSelected(false);
        coachComments_rl.setSelected(true);

        comment_et = (EditText) findViewById(R.id.comment_et);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        if (waterStatus.equalsIgnoreCase("view")) {
            currentWater = ApplicationEx.getInstance().currentWater;
            community_header = (RelativeLayout) findViewById(R.id.water_community_header);

            if (fromNotifCommunity) {
                if (from3rdPartyCommunity){
                    updateCommunityHeader();

                    if (currentWater!=null)
                        refreshUI();
                }else{
                    getWaterProgressBar.setVisibility(View.VISIBLE);
                    getHAPIWater(currentWater.water_id);
                    community_header.setVisibility(View.GONE);
                }
                communityCommentsSelected();

            } else {
                getWaterProgressBar.setVisibility(View.VISIBLE);

                community_header.setVisibility(View.GONE);
                getHAPIWater(currentWater.water_id);
            }


        } else {
            getWaterProgressBar.setVisibility(View.GONE);
            hideCommentsLayout();

            waterDate.setText(AppUtil.getDateinString(ApplicationEx.getInstance().currentSelectedDate));
            glassSeekBar.setProgress(0);
            bubbleSeekBar.setProgress(0);
            updateWaterThumb(0);
        }

    }

    public void saveWaterEntry(View v) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waterProgressBar.setVisibility(View.VISIBLE);
            }
        });

        if (waterStatus.equalsIgnoreCase("add")) {
            addWaterToAPI();
        } else {
            updateWaterToAPI();
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

    public void addWaterSuccess(String response) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waterProgressBar.setVisibility(View.GONE);
                finish();
            }
        });
    }

    public void addWaterFailedWithError(MessageObj response) {
        final String responseStr = response.getMessage_string();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                waterProgressBar.setVisibility(View.GONE);
            }
        });
        Toast.makeText(this, responseStr, Toast.LENGTH_SHORT);

    }

    /**
     * SeekBar Listener
     **/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
        //send to api
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (seekBar == bubbleSeekBar) {
            glassSeekBar.setProgress(progress);
        } else if (seekBar == glassSeekBar) {
            bubbleSeekBar.setProgress(progress);
        }
        updateWaterThumb(progress);
    }

    /**
     * Private Methods
     **/

    private void hideCommentsLayout(){
        ((LinearLayout)findViewById(R.id.water_comments_layout)).setVisibility(View.GONE);
        ((LinearLayout)findViewById(R.id.water_commentfieldcontainer)).setVisibility(View.GONE);
        commentlist.setVisibility(View.GONE);
    }
    private void refreshUI() {

        getWaterProgressBar.setVisibility(View.GONE);

        int currentProgress = Integer.parseInt(currentWater.getNumber_glasses());
        glassSeekBar.setProgress(currentProgress);
        bubbleSeekBar.setProgress(currentProgress);

        updateWaterThumb(currentProgress);

        if (currentWater.isChecked){
            water_approvedIconContainer.setVisibility(View.VISIBLE);
        }else{
            water_approvedIconContainer.setVisibility(View.GONE);
        }

        waterDate.setText(AppUtil.getDateinString(currentWater.getWater_datetime()));

        if (isCommunityTabSelected) {
            updateHAPI4ULayout();
            commentlist.updateData(AppUtil.getCommunityComments(currentWater.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWater.comments));
        }
    }

    private void addWaterToAPI() {
        try {
            waterEntryToUpload = new Water();
            waterEntryToUpload.number_glasses = Integer.toString(glassSeekBar.getProgress());
            waterEntryToUpload.setWater_datetime(ApplicationEx.getInstance().currentSelectedDate);

            if (addWaterController == null) {
                addWaterController = new AddWaterController(this, this, this);
            }

            addWaterController.uploadWater(waterEntryToUpload, ApplicationEx.getInstance().userProfile.getRegID());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWaterToAPI() {
        waterEntryToUpload = currentWater;
        waterEntryToUpload.number_glasses = Integer.toString(glassSeekBar.getProgress());

        int index = -1;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (Water p : ApplicationEx.getInstance().waterList) {
            Date waterDate = p.getWater_datetime();
            if (fmt.format(currentWater.getWater_datetime()).equals(fmt.format(waterDate))) {
                index = ApplicationEx.getInstance().waterList.indexOf(p);
                break;
            }
        }
        if (index > -1) {
            ApplicationEx.getInstance().waterList.set(index, waterEntryToUpload);
        }

        if (addWaterController == null) {
            addWaterController = new AddWaterController(this, this, this);
        }
        addWaterController.uploadWater(waterEntryToUpload, ApplicationEx.getInstance().userProfile.getRegID());
    }

    private void updateWaterThumb(int waterProgress) {

        glassSeekBar.setProgress(waterProgress);

        Bitmap bitmap;

        if (waterProgress == 0) {
            glassSeekBar.setThumb(getResources().getDrawable(R.drawable.water_glass_gray));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_bubble_gray);
        } else {
            glassSeekBar.setThumb(getResources().getDrawable(R.drawable.water_glass_blue));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_bubble_orange);
        }

        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bmp);

        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);

        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, getResources().getDisplayMetrics());

        p.setTextSize(pixel);
        p.setColor(getResources().getColor(R.color.text_white));
        String text = Integer.toString(waterProgress);
        int width = (int) p.measureText(text);
        int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
        c.drawText(text, (bmp.getWidth() - width) / 2, yPos - 2, p);

        bubbleSeekBar.setThumb(new BitmapDrawable(getResources(), bmp));
    }

    private void updateCommunityHeader() {

        community_header.setVisibility(View.VISIBLE);
        (findViewById(R.id.header_right_tv)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.water_community_header)).setVisibility(View.VISIBLE);
        try {
            String user3rdName = ApplicationEx.getInstance().currentCommunityUser.firstname + " " + ApplicationEx.getInstance().currentCommunityUser.lastname;
            ((TextView) findViewById(R.id.water_community_name)).setText(user3rdName);
        } catch (NullPointerException e) {
            ((TextView) findViewById(R.id.water_community_name)).setText("");
        }

        ImageView community_avatar = (ImageView) findViewById(R.id.water_community_avatar);
        //update useravatar
        Bitmap bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().currentCommunityUser.user_id);
        if (bmp == null) {
            new DownloadImageTask(community_avatar, Integer.parseInt(ApplicationEx.getInstance().currentCommunityUser.user_id)).execute(ApplicationEx.getInstance().currentCommunityUser.picture_url_large);
        } else
            community_avatar.setImageBitmap(bmp);

        save_button_ll.setVisibility(View.GONE);
        bubbleSeekBar.setEnabled(false);
        glassSeekBar.setEnabled(false);

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
        commentlist.updateData(AppUtil.getCommunityComments(currentWater.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.water_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.water_community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void communityCommentsSelected(View view) {
        isCommunityTabSelected = true;
        commentlist.updateData(AppUtil.getCommunityComments(currentWater.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.water_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.water_community_comments_btn)).setTextColor(Color.BLACK);

        updateHAPI4ULayout();
    }

    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(currentWater.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_ll.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.water_coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.water_community_comments_btn)).setTextColor(Color.LTGRAY);
    }

    private void updateHAPI4ULayout() {
        final int hapi4U_count = currentWater.hapi4Us.size();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                hapi4u_ll.setVisibility(View.VISIBLE);

                if (currentWater.hapi4Us.size() <= 0) {
                    hapi4u_numCount_tv.setVisibility(View.GONE);
                    hapi4u_user_iv.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.water_hapi4u_smiley)).setVisibility(View.GONE);
                    ((RoundedImageView) findViewById(R.id.water_hapi4u_num_image)).setVisibility(View.GONE);
                } else {
                    //check user liked his own post
                    for (HAPI4U hapi4U : currentWater.hapi4Us) {
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
                    ((ImageView) findViewById(R.id.water_hapi4u_smiley)).setVisibility(View.VISIBLE);
                    ((RoundedImageView) findViewById(R.id.water_hapi4u_num_image)).setVisibility(View.VISIBLE);
                    hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                    //Get latest HAPI4u
                    HAPI4U hapi4U = currentWater.hapi4Us.get(currentWater.hapi4Us.size() - 1);

                    Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                    if (bmp == null) {
                        new DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
                    } else
                        hapi4u_user_iv.setImageBitmap(bmp);
                }
            }
        });
    }

    private void getHAPIWater(String waterId) {
        GetWaterController getWaterController = new GetWaterController(this, this);
        getWaterController.getWater(waterId, ApplicationEx.getInstance().userProfile.getRegID());
    }

    public void getWaterSuccess(Water water) {
        System.out.println("getWaterSuccess");
        ApplicationEx.getInstance().currentWater = water;
        currentWater = water;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWaterProgressBar.setVisibility(View.GONE);

                refreshUI();
            }
        });
    }

    public void getWaterFailedWithError(MessageObj response) {
        System.out.println("getWaterFailedWithError");
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

        comment.meal_id = String.valueOf(currentWater.water_id);
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;

        currentWater.comments.add(comment);
        hideKeyboard();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentWater.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWater.comments));
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
                addHapiMomentCommentController.uploadMealCommunityComment(String.valueOf(currentWater.water_id), currentcomment, username);
            } else {
                addHapiMomentCommentController.uploadMealComment(String.valueOf(currentWater.water_id), currentcomment, username);
            }
        }
    }

    @Override
    public void uploadMealCommentSuccess(String response) {
        // TODO Auto-generated method stub
        //update UI
//        updateMealUI();
        if (currentWater.water_id !=null) {
            run(ApplicationEx.getInstance().userProfile.getRegID(), String.valueOf(currentWater.water_id));
        }
    }

    public void run(final String userID, final String waterId) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getHAPIWater(waterId);
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
        if (currentWater != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentWater.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    commentFail.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;
                    currentWater.comments.remove(i);
                    currentWater.comments.add(i, comment);
                    i = comments.size() + 1;
                }
            }
        }

        //submit to server
        submitComment(comment);

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentWater.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentWater.comments));
        }
    }
    @Override
    public void uploadMealCommentDelete(Comment comment) {
        // TODO Auto-generated method stub
        if (currentWater != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentWater.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    currentWater.comments.remove(i);
                    i = comments.size() + 1;
                }
            }
        }

        updateHAPI4ULayout();
        commentlist.updateData(AppUtil.getAllComments(currentWater.comments));
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
            addHapiMomentCommentController.postHAPI4U(String.valueOf(currentWater.water_id), null, username);
        }
    }
}
