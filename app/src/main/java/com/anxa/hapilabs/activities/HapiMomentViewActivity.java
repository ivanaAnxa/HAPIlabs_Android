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
import android.widget.Toast;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.AddHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.MealAddCommentListener;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.addmeal.AddMealCommentController;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentCommentController;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentController;
import com.anxa.hapilabs.controllers.hapimoment.GetHapiMomentController;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.CommentUser;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class HapiMomentViewActivity extends HAPIActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, AddHapiMomentListener, GetHapiMomentListener,
        MealAddCommentListener {

    private ImageView smileyView;
    private ImageView mainPhotoView;
    private TextView description;
    private TextView location;
    private TextView viewTitle;
    private LinearLayout photoThumbContainer;
    private PopupMenu popupMenu;
    private CustomDialog dialog;
    private HapiMoment currentHapimoment;
    private ProgressBar hapiProgressBar;

    private TextView hapi4u_post_tv;
    private TextView hapi4u_numCount_tv;
    private ImageView hapi4u_post_iv;
    private RoundedImageView hapi4u_user_iv;
    private RelativeLayout hapi4u_ll;
    private CommentListLayout commentlist;
    private RelativeLayout communityComments_rl;
    private RelativeLayout coachComments_rl;

    private RelativeLayout community_header;

    private TextView submit_tv;
    private EditText comment_et;

    private AddHapiMomentController addHapiMomentController;
    private LinearLayout approvedIconContainer;

    private boolean isHAPI4USelected = false;
    private Boolean fromNotif;
    private Boolean fromNotifCommunity;
    private Boolean from3rdPartyCommunity;

    private Boolean isCommunityTabSelected = false;

    private final static int MENU_EDIT_HAPIMOMENT = 11;
    private final static int MENU_SHARE_FACEBOOK = 12;
    private final static int MENU_SHARE_TWITTER = 13;
    private final static int MENU_DELETE_HAPIMOMENT = 15;
    private final static int MENU_CANCEL = 16;

    private final static int TWITTER_REQUEST_CODE = 111;

    private CallbackManager callbackManager;
    private ShareDialog shareDialog;

    final Context context = this;

    GetHapiMomentController getHapiMomentController;
    AddHapiMomentCommentController addHapiMomentCommentController;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.hapimoment_view);

        fromNotif = getIntent().getBooleanExtra("FROM_NOTIF", false);
        fromNotifCommunity = getIntent().getBooleanExtra("FROM_NOTIF_COMMUNITY", false);
        from3rdPartyCommunity = getIntent().getBooleanExtra("FROM_NOTIF_3RD_PARTY", false);

        if (fromNotif) {
            updateHeader(1, getResources().getString(R.string.MEALTYPE_HAPIMOMENT), this);
        } else {
            updateHeader(0, getResources().getString(R.string.MEALTYPE_HAPIMOMENT), this);
        }

        currentHapimoment = ApplicationEx.getInstance().currentHapiMoment;
        community_header = (RelativeLayout) findViewById(R.id.hapimoment_community_header);

        smileyView = ((ImageView) findViewById(R.id.hapiMoodImage));
        description = ((TextView) findViewById(R.id.hapimomentDescription));
        location = ((TextView) findViewById(R.id.hapimomentLocation));
        viewTitle = ((TextView) findViewById(R.id.hapimomentViewTitle));
        mainPhotoView = (ImageView) findViewById(R.id.mealphoto);
        photoThumbContainer = (LinearLayout) findViewById(R.id.mealphoto_thumbcontainer);
        approvedIconContainer = (LinearLayout) findViewById(R.id.approvedIconContainer);
        hapiProgressBar = (ProgressBar) findViewById(R.id.hapimomentview_progressBar);

        commentlist = (CommentListLayout) findViewById(R.id.hapimoment_commentlist);

        hapi4u_ll = (RelativeLayout) findViewById(R.id.hapimoment_hapi4u_ll);
        communityComments_rl = (RelativeLayout) findViewById(R.id.hapimoment_community_comments_container);
        coachComments_rl = (RelativeLayout) findViewById(R.id.hapimoment_coach_comments_container);
        hapi4u_numCount_tv = (TextView) hapi4u_ll.findViewById(R.id.hapimoment_hapi4u_num_label);
        hapi4u_user_iv = (RoundedImageView) hapi4u_ll.findViewById(R.id.hapimoment_hapi4u_avatar);
        hapi4u_post_iv = (ImageView) hapi4u_ll.findViewById(R.id.hapimoment_hapi4u_iv);
        hapi4u_post_tv = (TextView) hapi4u_ll.findViewById(R.id.hapimoment_hapi4u_tv);
        hapi4u_ll.setVisibility(View.GONE);

        communityComments_rl.setSelected(false);
        coachComments_rl.setSelected(true);

        comment_et = (EditText) findViewById(R.id.comment_et);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        if (fromNotifCommunity) {
            community_header.setVisibility(View.VISIBLE);
            if (from3rdPartyCommunity) {
                updateCommunityHeader();
            } else {
                getHAPIMoment(String.valueOf(currentHapimoment.mood_id));

                community_header.setVisibility(View.GONE);
            }
            communityCommentsSelected();

        } else {
            community_header.setVisibility(View.GONE);
            getHAPIMoment(String.valueOf(currentHapimoment.mood_id));

            popupMenu = new PopupMenu(this, findViewById(R.id.header_right_tv));
            popupMenu.getMenu().add(Menu.NONE, MENU_EDIT_HAPIMOMENT, Menu.NONE, getResources().getString(R.string.MENU_EDIT_HAPIMOMENT));
            popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_FACEBOOK, Menu.NONE, getResources().getString(R.string.MENU_SHARE_FACEBOOK));
            popupMenu.getMenu().add(Menu.NONE, MENU_SHARE_TWITTER, Menu.NONE, getResources().getString(R.string.MENU_SHARE_TWITTER));
            popupMenu.getMenu().add(Menu.NONE, MENU_DELETE_HAPIMOMENT, Menu.NONE, getResources().getString(R.string.MENU_DELETE_HAPIMOMENT));
            popupMenu.getMenu().add(Menu.NONE, MENU_CANCEL, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CANCEL));
            popupMenu.setOnMenuItemClickListener(this);

            initFacebookSharing();
        }

        if (currentHapimoment != null) {
            refreshUI();
        }


    }

    public void onClick(View v) {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }
        if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            popupMenu.show();
        }
        if (v.getId() == R.id.YesButton || v.getId() == R.id.NoButton) {
            if (dialog != null)
                dialog.dismiss();
            if (v.getId() == R.id.YesButton)
                deleteHapimoment();
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
        System.out.println("onActivityResult: " + requestCode + " resultCode: " + resultCode);

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


    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT_HAPIMOMENT:
                editHapimoment();
                break;
            case MENU_SHARE_FACEBOOK:
                shareViaFacebook();
                break;
            case MENU_SHARE_TWITTER:
                shareViaTwitter();
                break;
            case MENU_DELETE_HAPIMOMENT:
                showCustomDialog();
                break;
            case MENU_CANCEL:
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        refreshUI();
        super.onResume();
    }

    private void showCustomDialog() {
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), false, getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL), null, this);
        dialog.show();
    }

    private void showCustomDialog(String message, String title) {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, message, title, this);
        dialog.show();
    }

    private void updateHapiMood(int moodValue, long time) {
        if (moodValue == 1) {
            smileyView.setBackgroundResource(R.drawable.mood1);
            viewTitle.setText(getResources().getString(R.string.HAPIMOMENT_GREAT) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 2) {
            smileyView.setBackgroundResource(R.drawable.mood2);
            viewTitle.setText(getResources().getString(R.string.HAPIMOMENT_GOOD) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 3) {
            smileyView.setBackgroundResource(R.drawable.mood3);
            viewTitle.setText(getResources().getString(R.string.HAPIMOMENT_OKAY) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 4) {
            smileyView.setBackgroundResource(R.drawable.mood4);
            viewTitle.setText(getResources().getString(R.string.HAPIMOMENT_NOTGOOD) + " @ " + AppUtil.getTimeOnly12(time));
        }
        if (moodValue == 5) {
            smileyView.setBackgroundResource(R.drawable.mood5);
            viewTitle.setText(getResources().getString(R.string.HAPIMOMENT_BAD) + " @ " + AppUtil.getTimeOnly12(time));
        }
    }

    private void editHapimoment() {
        Intent mainIntent = new Intent(this, HapimomentAddActivity.class);
        mainIntent.putExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_EDIT);
        startActivityForResult(mainIntent, HapiMoment.HAPIMOMENTSTATE_EDIT);
    }

    private void deleteHapimoment() {
        currentHapimoment.command = "deleted";

        addHapiMomentController = new AddHapiMomentController(getApplicationContext(), this, this);
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                hapiProgressBar.setVisibility(View.VISIBLE);
                addHapiMomentController.uploadHapiMoment(currentHapimoment, ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    public void addHapiMomentSuccess(String response) {

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hapiProgressBar.setVisibility(View.GONE);
            }
        });

        System.out.println("deleteHapiMomentSuccess");
        if (currentHapimoment.command == "deleted") {
            int index = -1;
            for (HapiMoment p : ApplicationEx.getInstance().hapiMomentList) {
                if (p.mood_id == currentHapimoment.mood_id) {
                    index = ApplicationEx.getInstance().hapiMomentList.indexOf(p);
                }
            }
            if (index > -1) {
                ApplicationEx.getInstance().hapiMomentList.remove(index);
                ApplicationEx.getInstance().tempHapimomentList.remove(String.valueOf(currentHapimoment.mood_id));
                Intent broadint = new Intent();
                broadint.setAction(this.getResources().getString(R.string.meallist_deletehapimoment_refresh));
                sendBroadcast(broadint);
            }
        }

        finish();
    }

    public void addHapiMomentFailedWithError(MessageObj response) {
        System.out.println("deleteHapiMomentFailedWithError: " + response);
        final String message = response.getMessage_string();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hapiProgressBar.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT);
            }
        });

    }

    private void refreshUI() {
        updateHapiMood(currentHapimoment.moodValue, currentHapimoment.mood_datetime.getTime());
        description.setText(currentHapimoment.description);
        if (currentHapimoment.isChecked) {
            approvedIconContainer.setVisibility(View.VISIBLE);
        } else {
            approvedIconContainer.setVisibility(View.GONE);
        }
        if (!currentHapimoment.location.isEmpty() && currentHapimoment.location != null && currentHapimoment.location != "null") {
            location.setText(" - at " + currentHapimoment.location);
        }
        // set the selected image
        if (currentHapimoment.photos == null || currentHapimoment.photos.isEmpty()) {
            mainPhotoView.setBackgroundResource(R.drawable.hapi_default_image);
            photoThumbContainer.setVisibility(View.GONE);
        } else {
            List<Photo> photos;
            photos = currentHapimoment.photos;
            Bitmap bmp = getHapiPhoto(currentHapimoment.photos.get(0));


            if (bmp == null)
                mainPhotoView.setBackgroundResource(R.drawable.hapi_default_image);
            else
                mainPhotoView.setImageBitmap(bmp); //

            if (currentHapimoment.photos.size() > 1) {
                for (int i = 0; i < currentHapimoment.photos.size(); i++) {
                    bmp = getHapiPhoto(currentHapimoment.photos.get(i));
                    if (bmp != null) {
                        if (i == 0) {
                            ((ImageView) findViewById(R.id.mealphoto_thumb1)).setImageBitmap(bmp);
                            ((LinearLayout) findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.VISIBLE);
                        } else if (i == 1) {
                            ((ImageView) findViewById(R.id.mealphoto_thumb2)).setImageBitmap(bmp);
                            ((LinearLayout) findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.VISIBLE);
                        } else if (i == 2) {
                            ((ImageView) findViewById(R.id.mealphoto_thumb3)).setImageBitmap(bmp);
                            ((LinearLayout) findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.VISIBLE);
                        } else if (i == 3) {
                            ((ImageView) findViewById(R.id.mealphoto_thumb4)).setImageBitmap(bmp);
                            ((LinearLayout) findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.VISIBLE);
                        } else if (i == 4) {
                            ((ImageView) findViewById(R.id.mealphoto_thumb5)).setImageBitmap(bmp);
                            ((LinearLayout) findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.VISIBLE);
                        }
                    }
                }
            } else {
                photoThumbContainer.setVisibility(View.GONE);
            }
        }

        if (isCommunityTabSelected) {
            updateHAPI4ULayout();

            commentlist.updateData(AppUtil.getCommunityComments(currentHapimoment.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentHapimoment.comments));
        }
    }

    private Bitmap getHapiPhoto(Photo mealPhoto) {
        Bitmap mealPhotoBMP = null;

        mealPhotoBMP = ImageManager.getInstance().findImage(mealPhoto.photo_id);

        if (mealPhotoBMP == null) {
            download(mealPhoto.photo_id, mealPhoto.photo_url_large, "0");
        }
        return mealPhotoBMP;
    }

    private void download(String photoId, String url, String mealId) {
        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(this,
                url,
                photoId,
                mealId, this);
    }

    @Override
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub

        refreshUI();
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
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
                showCustomDialog(getResources().getString(R.string.FACEBOOK_SUCCESSFUL_CONTENT_HAPIMOMENT), getResources().getString(R.string.FACEBOOK_SUCCESSFUL_TITLE_HAPIMOMENT));
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
            fbContentURL = "http://www.savoirmanger.fr/my-meals/hapimoment/share/";
        } else {
            fbContentURL = "http://www.hapicoach.com/my-meals/hapimoment/share/";
        }

        fbContentURL = fbContentURL + UrlSafeBase64(Integer.toString(currentHapimoment.mood_id)) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

        if (currentHapimoment != null) {

            String fbTitle = AppUtil.getHAPIMoodTitle(this, currentHapimoment.moodValue) + " " + getResources().getString(R.string.FACEBOOK_TITLE) + " " + AppUtil.getTimeOnly12(currentHapimoment.mood_datetime.getTime());

            String fbDescription;

            if (currentHapimoment.description.length() > 0) {
                fbDescription = currentHapimoment.description + ". " + getResources().getString(R.string.FACEBOOK_DESCRIPTION_HAPIMOMENT);
            } else {
                fbDescription = getResources().getString(R.string.FACEBOOK_HAPIMOMENT_CONTENT);
            }

            if (currentHapimoment.photos == null || currentHapimoment.photos.size() <= 0) {
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

                List<Photo> photos = currentHapimoment.photos;
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

        if (currentHapimoment != null) {
            String twitterContentURL;

            if (ApplicationEx.language.equals("fr")) {
                twitterContentURL = "http://www.savoirmanger.fr/my-meals/hapimoment/share/";
            } else {
                twitterContentURL = "http://www.hapicoach.com/my-meals/hapimoment/share/";
            }

            twitterContentURL = twitterContentURL + UrlSafeBase64(Integer.toString(currentHapimoment.mood_id)) + "." + UrlSafeBase64(ApplicationEx.getInstance().userProfile.getRegID());

            // Create intent using ACTION_VIEW and a normal Twitter url:
            String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                    urlEncode(getResources().getString(R.string.FACEBOOK_HAPIMOMENT_CONTENT)),
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

    private void getHAPIMoment(String moodId) {
        if (getHapiMomentController == null) {
            getHapiMomentController = new GetHapiMomentController(this, this);
        }
        getHapiMomentController.getHapiMoment(moodId, ApplicationEx.getInstance().userProfile.getRegID());
    }


    public void getHapiMomentSuccess(HapiMoment hapiMoment) {
        currentHapimoment = hapiMoment;
//        if(isCommunityTabSelected){
//            updateHAPI4ULayout();
//        }

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                refreshUI();
            }
        });
    }

    public void getHapiMomentFailedWithError(MessageObj response) {

    }

    private void updateHAPI4ULayout() {

        final int hapi4U_count = currentHapimoment.hapi4Us.size();

        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                hapi4u_ll.setVisibility(View.VISIBLE);

                if (currentHapimoment.hapi4Us.size() <= 0) {
                    hapi4u_numCount_tv.setVisibility(View.GONE);
                    hapi4u_user_iv.setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.hapimoment_hapi4u_smiley)).setVisibility(View.GONE);
                    ((RoundedImageView) findViewById(R.id.hapimoment_hapi4u_num_image)).setVisibility(View.GONE);
                } else {
                    //check user liked his own post
                    for (HAPI4U hapi4U : currentHapimoment.hapi4Us) {
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
                    ((ImageView) findViewById(R.id.hapimoment_hapi4u_smiley)).setVisibility(View.VISIBLE);
                    ((RoundedImageView) findViewById(R.id.hapimoment_hapi4u_num_image)).setVisibility(View.VISIBLE);
                    hapi4u_numCount_tv.setText(String.valueOf(hapi4U_count));

                    //Get latest HAPI4u
                    HAPI4U hapi4U = currentHapimoment.hapi4Us.get(currentHapimoment.hapi4Us.size() - 1);

                    Bitmap bmp = ImageManager.getInstance().findImage(hapi4U.user.user_id);
                    if (bmp == null) {
                        new DownloadImageTask(hapi4u_user_iv, Integer.parseInt(hapi4U.user.user_id)).execute(hapi4U.user.picture_url_large);
                    } else
                        hapi4u_user_iv.setImageBitmap(bmp);

                }
            }
        });

    }

    @Override
    public void uploadMealCommentSuccess(String response) {
        // TODO Auto-generated method stub
        //update UI
//        updateMealUI();
        if (currentHapimoment.mood_id >= 0) {
            run(ApplicationEx.getInstance().userProfile.getRegID(), String.valueOf(currentHapimoment.mood_id));
        }
    }

    public void run(final String userID, final String mealId) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                getHAPIMoment(mealId);
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
        if (currentHapimoment != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentHapimoment.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    commentFail.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;
                    currentHapimoment.comments.remove(i);
                    currentHapimoment.comments.add(i, comment);
                    i = comments.size() + 1;
                }
            }
        }

        //submit to server
        submitComment(comment);

        updateHAPI4ULayout();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentHapimoment.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentHapimoment.comments));
        }
    }

    @Override
    public void uploadMealCommentDelete(Comment comment) {
        // TODO Auto-generated method stub
        if (currentHapimoment != null) {
            //upDATE COMMENT STATUS
            List<Comment> comments = currentHapimoment.comments;
            for (int i = 0; i < comments.size(); i++) {
                Comment commentFail = comments.get(i);
                //update status to on going
                if (comment.comment_id == commentFail.comment_id) {
                    currentHapimoment.comments.remove(i);
                    i = comments.size() + 1;
                }
            }
        }

        updateHAPI4ULayout();
        commentlist.updateData(AppUtil.getAllComments(currentHapimoment.comments));
    }

    public void submitComment(Comment currentcomment) {
        System.out.println("submitComment: " + currentcomment);
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            if (isCommunityTabSelected) {
                addHapiMomentCommentController.uploadMealCommunityComment(String.valueOf(currentHapimoment.mood_id), currentcomment, username);
            } else {
                addHapiMomentCommentController.uploadMealComment(String.valueOf(currentHapimoment.mood_id), currentcomment, username);
            }
        }
    }

    private void postHAPI4UtoAPI() {
        if (addHapiMomentCommentController == null) {
            addHapiMomentCommentController = new AddHapiMomentCommentController(this, this, this);
        }
        String username = ApplicationEx.getInstance().userProfile.getRegID();
        if (username != null) {
            addHapiMomentCommentController.postHAPI4U(String.valueOf(currentHapimoment.mood_id), null, username);
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

    public void postHAPI4U(View view) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                postHAPI4UtoAPI();
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

        comment.meal_id = String.valueOf(currentHapimoment.mood_id);
        comment.message = comment_message;
        comment.timestamp = new Date();
        comment.status = Comment.STATUS.ONGOING_COMMENTUPLOAD;

        currentHapimoment.comments.add(comment);
        hideKeyboard();

        if (isCommunityTabSelected) {
            commentlist.updateData(AppUtil.getCommunityComments(currentHapimoment.comments));
        } else {
            commentlist.updateData(AppUtil.getCoachComments(currentHapimoment.comments));
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

    private void updateCommunityHeader() {

        (findViewById(R.id.header_right_tv)).setVisibility(View.GONE);

        ((RelativeLayout) findViewById(R.id.hapimoment_community_header)).setVisibility(View.VISIBLE);

        try {
            String user3rdName = ApplicationEx.getInstance().currentCommunityUser.firstname + " " + ApplicationEx.getInstance().currentCommunityUser.lastname;
            ((TextView) findViewById(R.id.hapimoment_community_name)).setText(user3rdName);
        } catch (NullPointerException e) {
            ((TextView) findViewById(R.id.hapimoment_community_name)).setText("");
        }

        ImageView community_avatar = (ImageView) findViewById(R.id.hapimoment_community_avatar);
        //update useravatar
        Bitmap bmp = ImageManager.getInstance().findImage(ApplicationEx.getInstance().currentCommunityUser.user_id);
        if (bmp == null) {
            new DownloadImageTask(community_avatar, Integer.parseInt(ApplicationEx.getInstance().currentCommunityUser.user_id)).execute(ApplicationEx.getInstance().currentCommunityUser.picture_url_large);
        } else
            community_avatar.setImageBitmap(bmp);

        coachComments_rl.setVisibility(View.GONE);
    }

    public void communityCommentsSelected() {
        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentHapimoment.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.hapimoment_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.hapimoment_community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void communityCommentsSelected(View view) {
        isCommunityTabSelected = true;

        commentlist.updateData(AppUtil.getCommunityComments(currentHapimoment.comments));

        communityComments_rl.setSelected(true);
        coachComments_rl.setSelected(false);

        hapi4u_ll.setVisibility(View.VISIBLE);
        ((TextView) coachComments_rl.findViewById(R.id.hapimoment_coach_comments_btn)).setTextColor(Color.LTGRAY);
        ((TextView) communityComments_rl.findViewById(R.id.hapimoment_community_comments_btn)).setTextColor(Color.BLACK);
    }

    public void coachCommentsSelected(View view) {
        isCommunityTabSelected = false;

        commentlist.updateData(AppUtil.getCoachComments(currentHapimoment.comments));

        coachComments_rl.setSelected(true);
        communityComments_rl.setSelected(false);
        hapi4u_ll.setVisibility(View.GONE);

        ((TextView) coachComments_rl.findViewById(R.id.hapimoment_coach_comments_btn)).setTextColor(Color.BLACK);
        ((TextView) communityComments_rl.findViewById(R.id.hapimoment_community_comments_btn)).setTextColor(Color.LTGRAY);
    }
}
