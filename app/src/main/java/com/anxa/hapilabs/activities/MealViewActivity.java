package com.anxa.hapilabs.activities;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetMealListener;
import com.anxa.hapilabs.common.connection.listener.GetMealUpdateListener;
import com.anxa.hapilabs.common.connection.listener.GetTimelineActivityListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.MealDeleteListener;
import com.anxa.hapilabs.common.connection.listener.MealISHapiCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.addmeal.AddISHapiCommentController;
import com.anxa.hapilabs.controllers.addmeal.AddMealCommentController;
import com.anxa.hapilabs.controllers.addmeal.DeleteMealController;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.mymeals.GetMealController;
import com.anxa.hapilabs.controllers.mymeals.GetMealUpdateController;
import com.anxa.hapilabs.controllers.timelineactivity.GetTimelineActivityController;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.FOODGROUP;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Comment.STATUS;
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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class MealViewActivity extends HAPIActivity implements OnKeyListener,
        MainActivityCallBack, BitmapDownloadListener,
        MealAddCommentListener, ProgressChangeListener, MealAddListener, MealISHapiCommentListener, GetMealUpdateListener,
        PopupMenu.OnMenuItemClickListener, MealDeleteListener, OnClickListener, GetMealListener {

    private TextView tv_title;
    private TextView tv_time;
    private TextView tv_desc;
    private TextView hapi4u_post_tv;
    private TextView hapi4u_numCount_tv;
    private ImageView iv_mainPhoto;
    private ImageView[] iv_thumbnails;
    private ImageView hapi4u_post_iv;
    private RoundedImageView hapi4u_user_iv;

    private int selectedPhotoIndex = 0;

    private CommentListLayout commentlist;
    private AddMealCommentController controller;
    private AddISHapiCommentController isHapiController;
    private Meal currentMeal = new Meal();

    private EditText comment_et;

    private LinearLayout coachRating_ll;
    private LinearLayout personalRating_ll;
    private RelativeLayout coachComments_rl;
    private RelativeLayout communityComments_rl;
    private RelativeLayout hapi4u_ll;
    private RelativeLayout community_header;

    private TextView submit_tv;
    private TextView coachRating_tv;
    private TextView personalRating_tv;
    private RatingBar coachRatingBar;
    private RatingBar personalRatingBar;
    private View ratingSeparator;

    private PopupMenu popupMenu;
    private CustomDialog dialog;

    private ProgressBar progressBar;

    private final static int MENU_EDIT_MEAL = 11;
    private final static int MENU_SHARE_FACEBOOK = 12;
    private final static int MENU_SHARE_TWITTER = 13;
    private final static int MENU_DELETE_MEAL = 15;
    private final static int MENU_CANCEL = 16;
    private final static int TWITTER_REQUEST_CODE = 111;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    private boolean isCommunityTabSelected = false;
    private boolean isHAPI4USelected = false;
    private boolean fromNotifCommunity;
    private boolean from3rdPartyCommunity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mymeals_view);

        currentMeal = ApplicationEx.getInstance().currentMealView;

        Boolean fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);

        community_header = (RelativeLayout)findViewById(R.id.meal_community_header);

        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        if (fromNotifCommunity){
            community_header.setVisibility(View.VISIBLE);
            updateCommunityHeader(ApplicationEx.getInstance().userProfile.getRegID(), currentMeal.meal_id);
        }else{
            ((RelativeLayout)findViewById(R.id.meal_community_header)).setVisibility(View.GONE);
            community_header.setVisibility(View.GONE);
        }

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        comment_et = (EditText) findViewById(R.id.comment_et);

        coachComments_rl = (RelativeLayout) findViewById(R.id.coach_comments_container);
        communityComments_rl = (RelativeLayout) findViewById(R.id.community_comments_container);
        hapi4u_ll = (RelativeLayout) findViewById(R.id.hapi4u_ll);
        hapi4u_numCount_tv = (TextView) hapi4u_ll.findViewById(R.id.hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_ll.findViewById(R.id.hapi4u_avatar);
        hapi4u_post_iv = (ImageView) hapi4u_ll.findViewById(R.id.hapi4u_iv);
        hapi4u_post_tv = (TextView) hapi4u_ll.findViewById(R.id.hapi4u_tv);
        hapi4u_ll.setVisibility(View.GONE);

        //default
        coachComments_rl.setSelected(true);
        ((TextView) coachComments_rl.findViewById(R.id.coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.community_comments_btn)).setTextColor(Color.LTGRAY);

        if (currentMeal != null) {
            updateHeader(1, AppUtil.getMonthDay(currentMeal.meal_creation_date), this);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar_mealView);

        // init listview
        commentlist = (CommentListLayout) findViewById(R.id.commentlist);

        if (currentMeal.photos != null)
            iv_thumbnails = new ImageView[currentMeal.photos.size()];

        if (!fromNotifCommunity) {
            if (currentMeal.meal_id != null) {
                getMeal(ApplicationEx.getInstance().userProfile.getRegID(), currentMeal.meal_id);
            }
            coachComments_rl.setVisibility(View.VISIBLE);

        }else{
            coachComments_rl.setVisibility(View.GONE);
            communityComments_rl.setSelected(true);
        }


        initUI();

        addContent();

        initFacebookSharing();

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.meallist_photo_download));

        this.getApplicationContext().registerReceiver(the_receiver, filter);

        if (from3rdPartyCommunity){
            System.out.println("FROM_NOTIF_3RD_PARTY");
            coachComments_rl.setSelected(false);

            communityCommentsSelected();
        }
    }

    private void updateMeal(String userId, String mealId) {
        GetMealUpdateController mealupdateController = new GetMealUpdateController(this, this);
        mealupdateController.getMealUpdate(userId, mealId);
    }

    private void getMeal(String userId, String mealId) {
        GetMealController mealupdateController = new GetMealController(this, this);
        mealupdateController.getMealDetails(userId, mealId);
    }

    public void run(final String userID, final String mealId) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateMeal(userID, mealId);
            }
        });
    }

    private void addContent() {
        if (currentMeal.photos != null)
            iv_thumbnails = new ImageView[currentMeal.photos.size()];

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
        }

        updateHAPI4ULayout();

        // init fg container
        if (currentMeal.food_group == null || currentMeal.food_group.size() == 0) {
            ((LinearLayout) findViewById(R.id.fg_container)).setVisibility(View.GONE);
        } else {
            ((LinearLayout) findViewById(R.id.fg_container)).setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboard() {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);
    }

    public void onResume() {

        super.onResume();
        // hide virtual keyboard
        hideKeyboard();
    }

    private void initUI() {

        // title
        tv_desc = (TextView) findViewById(R.id.mealdesc);
        tv_time = (TextView) findViewById(R.id.mealtime);
        tv_title = (TextView) findViewById(R.id.mealtitle);
        iv_mainPhoto = (ImageView) findViewById(R.id.mealphoto);

        tv_title.setText(AppUtil.getMealTitle(this, currentMeal.meal_type));
        tv_time.setText(AppUtil.getMealTime(currentMeal.meal_creation_date));
        tv_desc.setText(currentMeal.meal_description);

        //setup rating UI
        coachRating_ll = (LinearLayout) findViewById(R.id.coach_rating_container);
        personalRating_ll = (LinearLayout) findViewById(R.id.personal_rating_container);

        personalRating_tv = (TextView) findViewById(R.id.personal_rating_text);
        coachRating_tv = (TextView) findViewById(R.id.coach_rating_text);

        coachRatingBar = (RatingBar) findViewById(R.id.coachRatingBar);
        personalRatingBar = (RatingBar) findViewById(R.id.personalRatingBar);

        ratingSeparator = (View) findViewById(R.id.rating_separator);

        if (currentMeal.coachRating > 0) {

            ratingSeparator.setVisibility(View.VISIBLE);
            coachRating_ll.setVisibility(View.VISIBLE);
            coachRatingBar.setRating(currentMeal.coachRating);

            switch (Math.round(currentMeal.coachRating)) {
                case 0:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                    break;
                case 1:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_1));
                    break;
                case 2:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_2));
                    break;
                case 3:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_3));
                    break;
                case 4:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_4));
                    break;
                case 5:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_5));
                    break;
                default:
                    coachRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                    break;
            }

        } else {
            coachRating_ll.setVisibility(View.GONE);
            ratingSeparator.setVisibility(View.GONE);
        }

        if (currentMeal.userRating > 0) {

            ratingSeparator.setVisibility(View.VISIBLE);

            personalRating_ll.setVisibility(View.VISIBLE);
            personalRatingBar.setRating(currentMeal.userRating);

            switch (Math.round(currentMeal.userRating)) {
                case 0:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                    break;
                case 1:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_1));
                    break;
                case 2:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_2));
                    break;
                case 3:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_3));
                    break;
                case 4:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_4));
                    break;
                case 5:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_5));
                    break;
                default:
                    personalRating_tv.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                    break;
            }
        } else {
            personalRating_ll.setVisibility(View.GONE);
        }


        // check approved /commented UIedit
        if (currentMeal.isApproved) {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.approved_text)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.approved_icon)).setVisibility(View.VISIBLE);
            // add spacer too
            ((View) findViewById(R.id.spacer)).setVisibility(View.VISIBLE);

        } else {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.approved_text)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.approved_icon)).setVisibility(View.GONE);
            // remove spacer too
            ((View) findViewById(R.id.spacer)).setVisibility(View.GONE);
        }

        if (currentMeal.isCommented) {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.comment_text)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.comment_icon)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.comment_text)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.comment_icon)).setVisibility(View.GONE);
        }

        // foodgroups
        // remove all foodgroupd
        ((LinearLayout) findViewById(R.id.row1)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_1)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_2)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_3)).setVisibility(View.GONE);

        ((LinearLayout) findViewById(R.id.row2)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_4)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_5)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_6)).setVisibility(View.GONE);

        ((LinearLayout) findViewById(R.id.row3)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_7)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_8)).setVisibility(View.GONE);

        if (currentMeal.food_group != null && currentMeal.food_group.size() > 0) {

            for (int i = 0; i < currentMeal.food_group.size(); i++) {

                int res = getResourceByFoodGroup(currentMeal.food_group.get(i));

                if (i == 0) {
                    ((LinearLayout) findViewById(R.id.row1)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_1)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_1)).setImageResource(res);
                } else if (i == 1) {
                    ((ImageButton) findViewById(R.id.fg_2)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_2)).setImageResource(res);
                } else if (i == 2) {
                    ((ImageButton) findViewById(R.id.fg_3)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_3)).setImageResource(res);
                } else if (i == 3) {
                    ((LinearLayout) findViewById(R.id.row2)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_4)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_4)).setImageResource(res);
                } else if (i == 4) {
                    ((ImageButton) findViewById(R.id.fg_5)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_5)).setImageResource(res);
                } else if (i == 5) {
                    ((ImageButton) findViewById(R.id.fg_6)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_6)).setImageResource(res);
                } else if (i == 6) {
                    ((LinearLayout) findViewById(R.id.row3)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_7)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_7)).setImageResource(res);
                } else if (i == 7) {
                    ((ImageButton) findViewById(R.id.fg_8))
                            .setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_8))
                            .setImageResource(res);
                }

            }// end for
        } else {
            ((View) findViewById(R.id.separate)).setVisibility(View.GONE);
        }// end if

        // check if the meal has photos
        iv_mainPhoto = (ImageView) findViewById(R.id.mealphoto);

        //	// hide thumbnail by default
        ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.GONE);

        if (currentMeal == null || currentMeal.photos == null || currentMeal.photos.size() <= 0) {
            // update the placeholder photo depending on the meal type
            iv_mainPhoto.setImageResource(AppUtil.getPhotoResource(currentMeal.meal_type));
            iv_mainPhoto.setOnClickListener(this);

        } else {
            // display the bitmap and thumbnail

            List<Photo> photos = currentMeal.photos;
            if (photos != null && photos.size() > 1) { //do not display thumbnail for just one photo

                ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer)).setVisibility(View.VISIBLE);

                for (int i = 0; i < iv_thumbnails.length; i++) {
                    if (i == 0) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb1);
                    } else if (i == 1) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb2);
                    } else if (i == 2) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb3);
                    } else if (i == 3) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb4);
                    } else if (i == 4) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb5);
                    }

                    if (i > photos.size()) {
                        iv_thumbnails[i].setImageResource(AppUtil.getPhotoResource(currentMeal.meal_type)); // use

                    } else {
                        if (photos.get(i).image != null)
                            iv_thumbnails[i].setImageBitmap(photos.get(i).image); //
                        else {
                            Bitmap bmp = ImageManager.getInstance().findImage(photos.get(i).photo_id);
                            if (bmp == null)
                                iv_thumbnails[i].setImageResource(AppUtil.getPhotoResource(currentMeal.meal_type)); // use
                            else
                                iv_thumbnails[i].setImageBitmap(bmp); //
                        }
                    }

                    iv_thumbnails[i].setOnClickListener(this);
                    iv_thumbnails[i].setTag(i);// set photo index as tag;
                }
            } else {
                // remove the thumbnails
                ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer))
                        .setVisibility(View.GONE);
            }

            // set the selected image
            if (photos.get(selectedPhotoIndex).image != null)
                iv_mainPhoto.setImageBitmap(photos.get(selectedPhotoIndex).image); //
            else {
                Bitmap bmp = ImageManager.getInstance().findImage(photos.get(selectedPhotoIndex).photo_id);
                if (bmp == null) {
                        new DownloadImageTask(iv_mainPhoto, Integer.parseInt(photos.get(selectedPhotoIndex).photo_id)).execute((photos.get(selectedPhotoIndex)).photo_url_large);
//                    iv_mainPhoto.setImageResource(AppUtil.getPhotoResource(currentMeal.meal_type)); // use
                }
                else
                    iv_mainPhoto.setImageBitmap(bmp); //
            }

            iv_mainPhoto.setOnClickListener(this);
            iv_mainPhoto.setTag(selectedPhotoIndex);
        }

        popupMenu = new PopupMenu(this, findViewById(R.id.header_right_tv));
        popupMenu.getMenu().add(Menu.NONE, MENU_EDIT_MEAL, Menu.NONE, getResources().getString(R.string.MENU_EDIT_MEAL));
        popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_FACEBOOK, Menu.NONE, getResources().getString(R.string.MENU_SHARE_FACEBOOK));
        popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_TWITTER, Menu.NONE, getResources().getString(R.string.MENU_SHARE_TWITTER));
        popupMenu.getMenu().add(Menu.NONE, MENU_DELETE_MEAL, Menu.NONE, getResources().getString(R.string.MENU_DELETE_MEAL));
        popupMenu.getMenu().add(Menu.NONE, MENU_CANCEL, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CANCEL));
        popupMenu.setOnMenuItemClickListener(this);

        //hide UI if fromCommunity
        if (fromNotifCommunity){
            personalRating_ll.setVisibility(View.GONE);
            coachRating_ll.setVisibility(View.GONE);
            findViewById(R.id.header_right_tv).setVisibility(View.GONE);

            communityCommentsSelected();
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
        comment.isHAPI = false;
        comment.meal_id = currentMeal.meal_id;
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = STATUS.ONGOING_COMMENTUPLOAD;

        currentMeal.comments.add(comment);

        commentlist = (CommentListLayout) findViewById(R.id.commentlist);

        hideKeyboard();

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
        }

        //update the runtime
        ApplicationEx.getInstance().tempList.put(currentMeal.meal_id, currentMeal);

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //update the DB
                MealDAO mealdao = new MealDAO(MealViewActivity.this, null);
                DaoImplementer dao = new DaoImplementer(mealdao, MealViewActivity.this);
                dao.updatedMeal(currentMeal);
            }
        });

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

    public void updateMeal() {
        String mealId = currentMeal.meal_id;

        //find the meal id in the temp list
        Meal mealWComment = ApplicationEx.getInstance().tempList.get(mealId);

        if (mealWComment != null) {
            currentMeal = mealWComment;
        }
        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
        }
    }

    private void isCommentHapi(Comment comment) {
        if (isHapiController == null) {
            isHapiController = new AddISHapiCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null)
            isHapiController.uploadMealiSHapiComment(currentMeal.meal_id, comment, username);
    }

    public void submitComment(Comment currentcomment) {
        if (controller == null) {
            controller = new AddMealCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            if (isCommunityTabSelected) {
                controller.uploadMealCommunityComment(currentMeal.meal_id, currentcomment, username);
            } else {
                controller.uploadMealComment(currentMeal.meal_id, currentcomment, username);
            }
        }
    }

    private void postHAPI4UtoAPI() {
        if (controller == null) {
            controller = new AddMealCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            controller.postHAPI4U(currentMeal.meal_id, null, username);
        }
    }

    public void loadFullScreenImage(View v) {
        // TODO Auto-generated method stub
        // click event for photo
        if (v.getTag() == null)
            return;

        Intent intent = new Intent(this, FullScreenImageActivity.class);

        int photoIndex = (Integer) v.getTag();
        String title = AppUtil.getMealTitle(this, currentMeal.meal_type);

        intent.putExtra("MEAL_TITLE", title);
        intent.putExtra("PHOTO_ID", (ApplicationEx.getInstance().currentMealView.photos.get(photoIndex).photo_id));
        intent.putExtra("MEAL_ID", (ApplicationEx.getInstance().currentMealView.meal_id));

        this.startActivity(intent);

    }

    private void editMeal(Meal meal) {
        ApplicationEx.getInstance().currentMealView = meal;

        Intent mainIntent;
        mainIntent = new Intent(this, MealAddActivity.class);
        mainIntent.putExtra("MEAL_STATE_VIEW", Meal.MEALSTATE_EDIT);
        mainIntent.putExtra("MEAL_TYPE", meal.meal_type.getValue());
        this.startActivityForResult(mainIntent, ApplicationEx.REQUESTCODE_MEALEDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ApplicationEx.REQUESTCODE_MEALEDIT) {

            if (resultCode == Activity.RESULT_OK) {
                //update the meal in view
                String tempMealID = data.getStringExtra("tempmealid");
                Boolean hasPhoto = data.getBooleanExtra("withphoto", false);
                Boolean isDelete = data.getBooleanExtra("isdeleted", false);

                if (isDelete) {
                    Intent deletIntent = new Intent();
                    deletIntent.putExtra("IS_DELETED", true);
                    setResult(resultCode, deletIntent);
                    finish();
                } else {
                    //refresh this view
                    currentMeal = ApplicationEx.getInstance().currentMealView;
                    // init listview
                    commentlist = (CommentListLayout) findViewById(R.id.commentlist);

                    try {
                        if (currentMeal.photos != null && currentMeal.photos.size() > 0)
                            iv_thumbnails = new ImageView[currentMeal.photos.size()];

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                    initUI();
                    addContent();
                    processUpdateMeal(hasPhoto, tempMealID, this);
                }
            }
        } else if (requestCode == TWITTER_REQUEST_CODE) {
            int TWITTER_SUCCESFUL_POST = 0;
            if (resultCode == TWITTER_SUCCESFUL_POST) {
                showCustomDialog(getResources().getString(R.string.TWITTER_SUCCESSFUL_CONTENT_ACTIVITY), getResources().getString(R.string.TWITTER_SUCCESSFUL_TITLE_ACTIVITY));
            } else {
                showCustomDialog(getResources().getString(R.string.TWITTER_FAILED_CONTENT), getResources().getString(R.string.TWITTER_FAILED_TITLE));
            }
        }
    }

    @Override
    public void uploadMealSuccess(String response) {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void uploadMealFailedWithError(MessageObj response, String entryID) {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            popupMenu.show();
        } else if (v.getId() == R.id.chat_status) {
            if (v.getTag() != null && v.getTag() instanceof Comment) {
                Comment comment = (Comment) v.getTag();
                isCommentHapi(comment);
            }
        } else if (v instanceof ImageView) {
            // thumbnail or main photo
            if (v.getId() == R.id.mealphoto) {
                // if photo exist full screen it, else do nothing
                loadFullScreenImage(v);

            } else if (v.getId() == R.id.mealphoto_thumb1
                    || v.getId() == R.id.mealphoto_thumb2
                    || v.getId() == R.id.mealphoto_thumb3
                    || v.getId() == R.id.mealphoto_thumb4
                    || v.getId() == R.id.mealphoto_thumb5) {
                loadFullScreenImage(v);
            }
        } else if (v == submit_tv) {
            // check if the textview has something
            if (comment_et != null && comment_et.getText() != null && comment_et.getText().length() > 0) {
                String comment_message = comment_et.getText().toString();
                comment_et.setText("");
                createNewComment(comment_message);
            }
        } else if (v.getId() == R.id.CloseButton) {
            dialog.dismiss();
        } else if (v.getId() == R.id.OtherButton) {
            dialog.dismiss();
        } else if (v.getId() == R.id.OtherButton || v.getId() == R.id.YesButton || v.getId() == R.id.NoButton) {
            //delete meal
            if (dialog != null)
                dialog.dismiss();
            if (v.getId() == R.id.YesButton)
                deleteCurrentMeal();
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }

    private int getResourceByFoodGroup(FOODGROUP fg) {

        switch (fg) {
            case DRINKS:
                return R.drawable.btn_fg_drinks;
            case VEGETABLE:
                return R.drawable.btn_fg_vegetables;
            case SWEETS:
                return R.drawable.btn_fg_sweets;
            case FRUITS:
                return R.drawable.btn_fg_fruits;
            case STARCHES:
                return R.drawable.btn_fg_starches;
            case PROTEIN:
                return R.drawable.btn_fg_protein;
            case DAIRY:
                return R.drawable.btn_fg_dairy;
            default:
                return R.drawable.btn_fg_fat;
        }
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == context.getResources().getString(
                    R.string.meallist_photo_download)) {// update timeline only
                // check if the view is the same as the download image meal id
                try {
                    String _mealId = intent.getStringExtra("MEAL_ID");
                    if (_mealId.startsWith(currentMeal.meal_id)) {
                        initUI();
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void updateMealUI() {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                updateMeal();
            }
        });
    }

    @Override
    public void uploadMealCommentSuccess(String response) {
        // TODO Auto-generated method stub
        //update UI
//        updateMealUI();
        if (currentMeal.meal_id != null) {
            run(ApplicationEx.getInstance().userProfile.getRegID(), currentMeal.meal_id);
        }
    }

    @Override
    public void uploadMealCommentFailedWithError(MessageObj response,
                                                 String entryID) {
        // TODO Auto-generated method stub
        updateMealUI();
    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopProgress() {
        // TODO Auto-generated method stub

    }

    @Override
    public void uploadMealCommentRefresh(Comment comment) {
        // TODO Auto-generated method stub
        //find this comment
        if (currentMeal != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentMeal.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    commentFail.status = STATUS.ONGOING_COMMENTUPLOAD;
                    currentMeal.comments.remove(i);
                    currentMeal.comments.add(i, comment);
                    i = comments.size() + 1;
                }
            }
        }

        //submit to server
        submitComment(comment);

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
        }
    }

    @Override
    public void uploadMealCommentDelete(Comment comment) {
        // TODO Auto-generated method stub
        if (currentMeal != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentMeal.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    currentMeal.comments.remove(i);
                    i = comments.size() + 1;
                }
            }
        }

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
        }
    }

    @Override
    public void uploadISHapiCommentSuccess(String response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void uploadISHapiCommentFailedWithError(MessageObj response, String entryID) {
        // TODO Auto-generated method stub
    }

    @Override
    public void getMealUpdateSuccess(String response) {
        // TODO Auto-generated method stub
        //refresh the pages for new meals

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentMeal = ApplicationEx.getInstance().currentMealView;

                updateHAPI4ULayout();

                if (isCommunityTabSelected) {
                    commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
                } else {
                    commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
                }
            }
        });
    }

    @Override
    public void getMealUpdateFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
    }

    @Override
    public void download(String photoId, String url, String mealId) {

        new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
        if (commentlist != null && currentMeal != null && currentMeal.comments != null && currentMeal.comments.size() > 0) {

            updateHAPI4ULayout();

            if (isCommunityTabSelected) {
                commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
            } else {
                commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
            }
        }
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
    }

    /**
     * PopUp Menu Callback
     **/
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT_MEAL:
                editMeal(currentMeal);
                break;
            case MENU_SHARE_FACEBOOK:
                shareViaFacebook();
                break;
            case MENU_SHARE_TWITTER:
                shareViaTwitter();
                break;
            case MENU_DELETE_MEAL:
                showCustomDialog();
//                deleteCurrentMeal();
                break;
            case MENU_CANCEL:
                break;
        }
        return false;
    }

    private void deleteCurrentMeal() {

        if (deleteMealController == null) {
            deleteMealController = new DeleteMealController(this, this, this, Meal.MEALSTATE_DELETE);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                deleteMealController.deleteMeal(currentMeal, ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    @Override
    public void deleteMealSuccess(String response) {

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        });

        //delete from DB:
        MealDAO daomeal = new MealDAO(this, null);
        DaoImplementer dao = new DaoImplementer(daomeal, this);
        dao.deleteMeal(currentMeal);

        //delete from the list
        ApplicationEx.getInstance().tempList.remove(currentMeal.meal_id);

        //broadcast the changes
        Intent broadint = new Intent();
        broadint.setAction(getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadint);

        Intent intent = new Intent();
        intent.putExtra("isdeleted", true);

        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void deleteMealError(MessageObj response, String entryID) {

        String message;

        if (response.getMessage_string().contains("offline")) {
            message = getResources().getString(R.string.ALERTMESSAGE_OFFLINE);
        } else {
            message = response.getMessage_string();
        }

        final String messageDialog = message;
        //else if message is offline: display offline alert message
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                showCustomDialog(messageDialog, null);
            }
        });
    }

    private void showCustomDialog() {
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), false, getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL), null, this);
        dialog.show();
    }

    private void showCustomDialog(String message, String title) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, message, title, this);
        dialog.show();
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
                showCustomDialog(getResources().getString(R.string.FACEBOOK_SUCCESSFUL_CONTENT), getResources().getString(R.string.FACEBOOK_SUCCESSFUL_TITLE));
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

        String fbContentURL;

        if (ApplicationEx.language.equals("fr")) {
            fbContentURL = "http://www.savoirmanger.fr/my-meals/share/";
        } else {
            fbContentURL = "http://www.hapicoach.com/my-meals/share/";
        }

        fbContentURL = fbContentURL + UrlSafeBase64(currentMeal.meal_id) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

        if (currentMeal != null) {

            String fbTitle = AppUtil.getMealTitle(this, currentMeal.meal_type) + " " + getResources().getString(R.string.FACEBOOK_TITLE) + " " + AppUtil.getMealTime(currentMeal.meal_creation_date);

            String fbDescription;

            if (currentMeal.meal_description != null && currentMeal.meal_description.length() > 0) {
                fbDescription = currentMeal.meal_description + ". " + getResources().getString(R.string.FACEBOOK_DESCRIPTION);

            } else {
                fbDescription = getResources().getString(R.string.FACEBOOK_CONTENT);
            }

            if (currentMeal.photos == null || currentMeal.photos.size() <= 0) {
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentTitle(fbTitle)
                            .setContentDescription(
                                    fbDescription)
                            .setContentUrl(Uri.parse(fbContentURL))
                            .build();

                    shareDialog.show(linkContent);
                }

            } else {
                // display the bitmap and thumbnail

                List<Photo> photos = currentMeal.photos;
                if (photos != null && photos.size() > 0) {
                    Uri imageUri = Uri.parse(photos.get(0).photo_url_large);
                    if (ShareDialog.canShow(ShareLinkContent.class)) {
                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                .setContentTitle(fbTitle)
                                .setContentDescription(fbDescription)
                                .setImageUrl(imageUri)
                                .setContentUrl(Uri.parse(fbContentURL))
                                .build();

                        shareDialog.show(linkContent);
                    }
                }
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

        if (currentMeal != null) {

            String twitterContentURL;

            if (ApplicationEx.language.equals("fr")) {
                twitterContentURL = "http://www.savoirmanger.fr/my-meals/share/";
            } else {
                twitterContentURL = "http://www.hapicoach.com/my-meals/share/";
            }

            twitterContentURL = twitterContentURL + UrlSafeBase64(currentMeal.meal_id) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

            // Create intent using ACTION_VIEW and a normal Twitter url:
            String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                    urlEncode(getResources().getString(R.string.TWITTER_CONTENT)),
                    removeWhiteSpaces(urlEncode(twitterContentURL)));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

            // Narrow down to official Twitter app, if available:
            List<ResolveInfo> matches = getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                    intent.setPackage(info.activityInfo.packageName);
                }
            }
//            startActivity(intent);
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

    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_ll.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.community_comments_btn)).setTextColor(Color.LTGRAY);
    }

    public void communityCommentsSelected(View view) {

        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));

        coachComments_rl.setSelected(false);
        communityComments_rl.setSelected(true);
        hapi4u_ll.setVisibility(View.VISIBLE);

        ((TextView) coachComments_rl.findViewById(R.id.coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.community_comments_btn)).setTextColor(Color.BLACK);

    }

    public void communityCommentsSelected() {

        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));

        communityComments_rl.setSelected(true);
        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) communityComments_rl.findViewById(R.id.community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void postHAPI4U(View view) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                postHAPI4UtoAPI();
            }
        });
    }

    private void updateHAPI4ULayout() {
        System.out.println("updateHAPI4ULayout : " + currentMeal.hapi4Us);

        int hapi4U_count = currentMeal.hapi4Us.size();

            if (currentMeal.hapi4Us.size() <= 0) {
                hapi4u_numCount_tv.setVisibility(View.GONE);
                hapi4u_user_iv.setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.hapi4u_smiley)).setVisibility(View.GONE);
                ((RoundedImageView) findViewById(R.id.hapi4u_num_image)).setVisibility(View.GONE);
            } else {
                //check user liked his own post
                for (HAPI4U hapi4U: currentMeal.hapi4Us){
                    if (hapi4U.user.user_id.equalsIgnoreCase(ApplicationEx.getInstance().userProfile.getRegID())){
                        isHAPI4USelected = true;
                    }
                }
                if (isHAPI4USelected){
                    hapi4u_post_tv.setTextColor(ContextCompat.getColor(this, R.color.text_orangedark));
                    hapi4u_post_iv.setImageDrawable(getResources().getDrawable(R.drawable.meal_smiley_orange));
                }else{
                    hapi4u_post_tv.setTextColor(ContextCompat.getColor(this, R.color.text_blue_comment));
                    hapi4u_post_iv.setImageDrawable(getResources().getDrawable(R.drawable.hapi4u_blue));
                }

                hapi4u_numCount_tv.setVisibility(View.VISIBLE);
                hapi4u_user_iv.setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.hapi4u_smiley)).setVisibility(View.VISIBLE);
                ((RoundedImageView) findViewById(R.id.hapi4u_num_image)).setVisibility(View.VISIBLE);
                hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                //Get latest HAPI4u
                HAPI4U hapi4U = currentMeal.hapi4Us.get(currentMeal.hapi4Us.size() - 1);

                Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                if (bmp == null) {
                    new DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
                } else
                    hapi4u_user_iv.setImageBitmap(bmp);

            }
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

    private void updateCommunityHeader(String userId, String mealId){
        ((RelativeLayout)findViewById(R.id.meal_community_header)).setVisibility(View.VISIBLE);

        String user3rdName = ApplicationEx.getInstance().currentCommunityUser.firstname + " " + ApplicationEx.getInstance().currentCommunityUser.lastname;
        ((TextView)findViewById(R.id.meal_community_name)).setText(user3rdName);

        ImageView community_avatar = (ImageView)findViewById(R.id.meal_community_avatar);
        //update useravatar
        Bitmap bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().currentCommunityUser.user_id);
        if (bmp == null) {
            new DownloadImageTask(community_avatar, Integer.parseInt(ApplicationEx.getInstance().currentCommunityUser.user_id)).execute(ApplicationEx.getInstance().currentCommunityUser.picture_url_large);
        } else
            community_avatar.setImageBitmap(bmp);
    }

    public void getMealSuccess(String response){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentMeal = ApplicationEx.getInstance().currentMealView;

                updateHAPI4ULayout();

                if (isCommunityTabSelected) {
                    commentlist.updateData(AppUtil.getCommunityComments(currentMeal.comments));
                } else {
                    commentlist.updateData(AppUtil.getCoachComments(currentMeal.comments));
                }
            }
        });
    }
    public void getMealFailedWithError(MessageObj messageObj){

    }
}
