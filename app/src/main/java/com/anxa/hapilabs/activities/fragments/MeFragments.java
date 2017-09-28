package com.anxa.hapilabs.activities.fragments;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.anxa.hapilabs.models.Weight;
import com.hapilabs.R;
import com.anxa.hapilabs.activities.AboutActivity;
import com.anxa.hapilabs.activities.CoachSelectionActivity;
import com.anxa.hapilabs.activities.LoginPageActivity;
import com.anxa.hapilabs.activities.MainActivity;
import com.anxa.hapilabs.activities.UpgradeActivity;
import com.anxa.hapilabs.activities.WebkitActivity;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.LoginListener;
import com.anxa.hapilabs.common.connection.listener.MainActivityCallBack;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.storage.CoachDAO;
import com.anxa.hapilabs.common.storage.CommentDAO;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.MessageDAO;
import com.anxa.hapilabs.common.storage.NotificationDAO;
import com.anxa.hapilabs.common.storage.PhotoDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.anxamats.AnxaMatsJobControlller;
import com.anxa.hapilabs.controllers.images.GetImageDownloadImplementer;
import com.anxa.hapilabs.controllers.login.LoginController;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.ui.CustomDialog;
import com.anxa.hapilabs.ui.CustomListView;
import com.anxa.hapilabs.ui.RoundedImageView;
import com.anxa.hapilabs.ui.adapters.AboutViewLinksAdapter;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

@SuppressLint("NewApi")
public class MeFragments extends Fragment implements OnClickListener, ProgressChangeListener, LoginListener, BitmapDownloadListener {

    Context context;
    OnClickListener listener;
    ListView listview;

    CustomListView listviewlink;
    CustomDialog dialog;
    List<String> webLinksLabel;

    RelativeLayout helpAndContact;
    LinearLayout myCoachProfile;
    Coach coach = null;
    View rootView;
    CoachDAO coachDAO;

    Boolean isRefreshLogin = false;
    Boolean isLogoutBtnTapped = false;
    LoginController controller;
    MainActivityCallBack MainListener;

    public MeFragments() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }

    private void updateUI() {

        System.out.println("updateUI");
        if (rootView != null) {

//            check if user is null first - something wrong with the data
            if (ApplicationEx.getInstance().userProfile.getFirstname() == null && ApplicationEx.getInstance().userProfile.getLastname() == null) {
                logout();
            } else {
                //update user name tv = user_title
                ((TextView) rootView.findViewById(R.id.user_title)).setText(ApplicationEx.getInstance().userProfile.getFirstname());
//                ((TextView) rootView.findViewById(R.id.user_title)).setText(ApplicationEx.getInstance().userProfile.getFirstname() + " " + ApplicationEx.getInstance().userProfile.getLastname());

                //joined date tv =  user_subtitle
                if (ApplicationEx.getInstance().userProfile.getDate_joined() != null) {

                    if (ApplicationEx.language.equalsIgnoreCase("FR")) {

                        String joinedDate = getResources().getString(R.string.PROFILE_JOINED_SINCE) + " " + AppUtil.getDateinString(ApplicationEx.getInstance().userProfile.getDate_joined());
                        ((TextView) rootView.findViewById(R.id.user_subtitle)).setText(joinedDate);
                    } else {

                    }
                }

                //profile photo tv =  user_avatar
                if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() != null) {
                    ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(ApplicationEx.getInstance().userProfile.getUserProfilePhoto());
                } else if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null) {

                    Bitmap bitmap = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());

                    if (bitmap != null) {

                        ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(bitmap);
                    } else {
                        System.out.println("updateUI null bitmap==null");

                        ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                    }

                } else {
                    ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                }
            }
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        isLogoutBtnTapped = false;
        this.context = getActivity();

        if (rootView == null) {

            rootView = inflater.inflate(R.layout.fragment_mywprofile, container, false);

            //update the ui with user and coach profile pics

            //update user name tv = user_title
            ((TextView) rootView.findViewById(R.id.user_title)).setText(ApplicationEx.getInstance().userProfile.getFirstname());
//            ((TextView) rootView.findViewById(R.id.user_title)).setText(ApplicationEx.getInstance().userProfile.getFirstname() + " " + ApplicationEx.getInstance().userProfile.getLastname());

            //joined date tv =  user_subtitle
            if (ApplicationEx.getInstance().userProfile.getDate_joined() != null) {
                String joinedDate = getResources().getString(R.string.PROFILE_JOINED_SINCE) + " " + AppUtil.getDateinString(ApplicationEx.getInstance().userProfile.getDate_joined());
                ((TextView) rootView.findViewById(R.id.user_subtitle)).setText(joinedDate);
            }
            //profile photo tv =  user_avatar
            if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() != null) {
                ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(ApplicationEx.getInstance().userProfile.getUserProfilePhoto());
            } else if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null) {
                Bitmap bitmap = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());
                if (bitmap != null) {
                    ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(bitmap);
                } else {
                    ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                }

            } else {
                ((RoundedImageView) rootView.findViewById(R.id.user_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
            }


            Coach userProfileCoach = ApplicationEx.getInstance().userProfile.getCoach();

            if (userProfileCoach != null) {
                //update coach name tv = coach_title

                String fullName = getResources().getString(R.string.PROFILE_MY_COACH);

                if (userProfileCoach.firstname != null) {
                    fullName = fullName + " " + userProfileCoach.firstname;
                }

//                if (userProfileCoach.lastname != null) {
//                    fullname = fullname + " " + userProfileCoach.lastname;
//                }

//                if (fullName != null)
                ((TextView) rootView.findViewById(R.id.coach_title)).setText(fullName);
//                else {
                //no default value
//                }

                //coach subtitle tv =  coach_subtitle
                if (ApplicationEx.language.equals("fr")) {
                    ((TextView) rootView.findViewById(R.id.coach_subtitle)).setText(userProfileCoach.coach_title_fr);
                } else {
                    ((TextView) rootView.findViewById(R.id.coach_subtitle)).setText(userProfileCoach.coach_title_en);
                }

                //photo
                coach = userProfileCoach;
                coachDAO = new CoachDAO(context, null);
                try {
                    Coach coachDB = coachDAO.getCoachsbyName(coach.firstname);
                    if (coach != null && coach.coach_id != null) {
                        ((RoundedImageView) rootView.findViewById(R.id.coach_avatar)).setImageBitmap(getAvatar(coach));
                    } else {
                        ((RoundedImageView) rootView.findViewById(R.id.coach_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                    }

                } catch (Exception e) {
                    ((RoundedImageView) rootView.findViewById(R.id.coach_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                    ((TextView) rootView.findViewById(R.id.coach_title)).setText(getResources().getString(R.string.PROFILE_MY_COACH));
                    ((TextView) rootView.findViewById(R.id.coach_subtitle)).setText(getResources().getString(R.string.NAVIGATIONTITLE_SELECTCOACH));
                }
            } else {
                ((RoundedImageView) rootView.findViewById(R.id.coach_avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                ((TextView) rootView.findViewById(R.id.coach_title)).setText(getResources().getString(R.string.PROFILE_MY_COACH));
                ((TextView) rootView.findViewById(R.id.coach_subtitle)).setText(getResources().getString(R.string.NAVIGATIONTITLE_SELECTCOACH));
            }

//            RelativeLayout mycoachlink = (RelativeLayout) rootView.findViewById(R.id.profile_mycoachlink);
//            RelativeLayout myprofilelink = (RelativeLayout) rootView.findViewById(R.id.profile_myprofilelink);
            LinearLayout myProfileLink = (LinearLayout) rootView.findViewById(R.id.profile_ll);
            LinearLayout myCoachLink = (LinearLayout) rootView.findViewById(R.id.coach_ll);
            RelativeLayout weeklysummarylink = (RelativeLayout) rootView.findViewById(R.id.profile_weeklysummary);
            RelativeLayout mysubslink = (RelativeLayout) rootView.findViewById(R.id.profile_mysubslink);
            RelativeLayout mycommunitylink = (RelativeLayout) rootView.findViewById(R.id.profile_mycommunitylink);
            RelativeLayout mysettingslink = (RelativeLayout) rootView.findViewById(R.id.profile_mysettingslink);
            RelativeLayout myhelplink = (RelativeLayout) rootView.findViewById(R.id.profile_myhelplink);

//            ((TextView) mycoachlink.findViewById(R.id.text_label)).setText(getString(R.string.MYCOACH_TITLE));
//            ((TextView) myprofilelink.findViewById(R.id.text_label)).setText(getString(R.string.MYPROFILE));
            ((TextView) weeklysummarylink.findViewById(R.id.text_label)).setText(getString(R.string.MYWEEKLYSUMMARY_TITLE));
            ((TextView) mysubslink.findViewById(R.id.text_label)).setText(getString(R.string.MYSUBS_TITLE));
            ((TextView) mycommunitylink.findViewById(R.id.text_label)).setText(getString(R.string.MYCOMMUNITY_TITLE));
            ((TextView) mysettingslink.findViewById(R.id.text_label)).setText(getString(R.string.MYSETTINGS_TITLE));
            ((TextView) myhelplink.findViewById(R.id.text_label)).setText(getString(R.string.MYHELP_TITLE));

            if (ApplicationEx.getInstance().userProfile != null && coach != null) {
                //03-20-2016, regardless of member type

                //  if (ApplicationEx.getInstance().userProfile.getMember_type() != MEMBER_TYPE.FREE) {

                // else go to native Coach displayed
                //help & contact

                myCoachProfile = ((LinearLayout) rootView.findViewById(R.id.mycoachprofile));
                myCoachProfile.setVisibility(View.GONE);
                //get coach text views

                String coachFullName = coach.firstname + " " + coach.lastname;
                ((TextView) rootView.findViewById(R.id.title)).setText(coachFullName);

                if (ApplicationEx.language.equals("fr")) {
                    ((TextView) rootView.findViewById(R.id.subtitle)).setText(coach.coach_title_fr);
                } else {
                    ((TextView) rootView.findViewById(R.id.subtitle)).setText(coach.coach_title_en);

                }

                String profileFull = "";

                if (ApplicationEx.language.equals("fr")) {
                    //jas 03/15/2016
                    profileFull = coach.coach_style_fr + "\n" + coach.coach_profile_fr;

                } else {
                    profileFull = coach.coach_style_en + "\n" + coach.coach_profile_en;
                }

                ((TextView) rootView.findViewById(R.id.profile_full)).setText(profileFull);

                try {


                    if (coach != null && coach.coach_id != null) {
                        ((ImageView) rootView.findViewById(R.id.avatar)).setImageBitmap(getAvatar(coach));
                    } else {
                        ((ImageView) rootView.findViewById(R.id.avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                    }
                } catch (Exception e) {
                    ((ImageView) rootView.findViewById(R.id.avatar)).setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Profile));
                }
            }
            myProfileLink.setTag(R.id.weblinksurl, updateString((WebServices.getURL(SERVICES.URL_PROFILE)), "%en", ApplicationEx.language) + ApplicationEx.getInstance().userProfile.getRegID());
            mysubslink.setTag(R.id.weblinksurl, updateString((WebServices.getURL(SERVICES.URL_SUBSCR)), "%en", ApplicationEx.language) + ApplicationEx.getInstance().userProfile.getRegID());
            mycommunitylink.setTag(R.id.weblinksurl, WebServices.getURL(SERVICES.GET_COMMUNITY));
            mysettingslink.setTag(R.id.weblinksurl, WebServices.getURL(SERVICES.GET_USER_SETTINGS));
            weeklysummarylink.setTag(R.id.weblinksurl, WebServices.getURL(SERVICES.URL_WEEKLY_SUMMARY));

//            myprofilelink.setTag(R.id.weblinkslabel, getString(R.string.MYPROFILE));
//            mycoachlink.setTag(R.id.weblinkslabel, getString(R.string.MYCOACH_TITLE));
            weeklysummarylink.setTag(R.id.weblinkslabel, getString(R.string.MYWEEKLYSUMMARY_TITLE));
            myCoachLink.setTag(R.id.weblinkslabel, getString(R.string.MYCOACH_TITLE));
            myProfileLink.setTag(R.id.weblinkslabel, getString(R.string.MYPROFILE));
            mysubslink.setTag(R.id.weblinkslabel, getString(R.string.MYSUBS_TITLE));
            mycommunitylink.setTag(R.id.weblinkslabel, getString(R.string.MYCOMMUNITY_TITLE));
            mysettingslink.setTag(R.id.weblinkslabel, getString(R.string.MYSETTINGS_TITLE));

            weeklysummarylink.setOnClickListener(this);
            myProfileLink.setOnClickListener(this);
            myCoachLink.setOnClickListener(this);
            mysubslink.setOnClickListener(this);
            mycommunitylink.setOnClickListener(this);
            mysettingslink.setOnClickListener(this);
            myhelplink.setOnClickListener(this);

            //help & contact
            helpAndContact = ((RelativeLayout) rootView.findViewById(R.id.helpandcontact));
            CustomListView helpAndContactlistview = (CustomListView) helpAndContact.findViewById(R.id.listview);
            List<String> items = new ArrayList<String>();
            List<String> actions = new ArrayList<String>();

            items.add(getResources().getString(R.string.about_about));
            items.add(getResources().getString(R.string.about_terms));
            items.add(getResources().getString(R.string.about_policy));
            items.add(getResources().getString(R.string.about_contactus));
            items.add(getResources().getString(R.string.btn_logout));

            actions.add("ABOUT");
//            actions.add(WebServices.getURL(SERVICES.URL_HELP_ABOUT));
            actions.add(WebServices.getURL(SERVICES.URL_HELP_TERMS));
            actions.add(WebServices.getURL(SERVICES.URL_HELP_PRIVACY));
            actions.add(WebServices.getURL(SERVICES.URL_HELP_CONTACT));
            actions.add("LOGOUT");


            AboutViewLinksAdapter aboutAdapter = new AboutViewLinksAdapter(context, items, actions, this);
            helpAndContactlistview.setAdapter(aboutAdapter);

            //update header left color
            // ((TextView)rootView.findViewById(R.id.header_left_tv)).setTextColor(getResources().getColor(R.color.text_white)); //imageview
            helpAndContact.setVisibility(View.GONE);
        }

        return rootView;
    }

    private void callCoach() {
        // call My listview
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), CoachSelectionActivity.class);
        getActivity().startActivity(mainIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationEx.getInstance().isLogin(context)) {
            callLogin();
        }

        ((MainActivity) getActivity()).updateHeader(4, this);
    }

    private void callLogin() {
        System.out.println("meFragments callLogin");

        if (controller == null)
            controller = new LoginController(context, this, this);
        controller.startLogin(ApplicationEx.getInstance().userProfile.getEmail(), ApplicationEx.getInstance().userProfile.getPasswordPlain(), ApplicationEx.getInstance().fromFBConnect);
    }

    @Override
    public void onClick(View v) {
        if (dialog != null && dialog.isShowing()) {
            if (v instanceof TextView && ((TextView) v).getText().toString().equals(getString(R.string.btn_ok))) {//now
                //show Coach selecttion page
                callCoach();
                dialog.dismiss();
            } else if (v instanceof TextView && ((TextView) v).getText().toString().equals(getString(R.string.btn_later))) {//later
                dialog.dismiss();
            }
            return;
        }

        String url, label;

        try {
            url = (String) v.getTag(R.id.weblinksurl);
        } catch (Exception e) {
        }
        try {

            label = (String) v.getTag(R.id.weblinkslabel);
        } catch (Exception e) {
        }

        if (v.getId() == R.id.coach_ll) {
            if (coach == null) {
                gotoCoach();
            } else {
                myCoachProfile.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).updateHeader(6, this);

            }

        } else if (v.getId() == R.id.profile_myhelplink) {
            helpAndContact.setVisibility(View.VISIBLE);

            ((MainActivity) getActivity()).updateHeader(5, this);

        } else if (v.getTag(R.id.weblinksurl) != null) {
            if (v.getTag(R.id.weblinksurl).equals("LOGOUT")) {
                //to do logout here;
                logout();
            } else if (v.getTag(R.id.weblinksurl).equals("ABOUT")) {
                //launch about native page
                loadAboutPage();
            } else {
                System.out.println("loadwebkit: " + v.getTag());
                loadWebKit(v.getTag(R.id.weblinksurl).toString(), v.getTag(R.id.weblinkslabel).toString());
            }

        } else if (v.getTag() == null) {

            if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
                if ((helpAndContact != null && helpAndContact.getVisibility() == View.VISIBLE) || (myCoachProfile != null && myCoachProfile.getVisibility() == View.VISIBLE)) {
                    if (helpAndContact != null)
                        helpAndContact.setVisibility(View.GONE);

                    if (myCoachProfile != null)
                        myCoachProfile.setVisibility(View.GONE);

                    ((MainActivity) getActivity()).updateHeader(4, this);

                } else { //info is click show info page
                    helpAndContact.setVisibility(View.VISIBLE);
                    ((MainActivity) getActivity()).updateHeader(5, this);
                }
            } else if (v.getId() == R.id.header_title || v.getId() == R.id.header_title_iv) {
            } else if (v.getId() == R.id.header_right) {
                Intent intent = new Intent(this.getActivity(), UpgradeActivity.class);
                this.getActivity().startActivity(intent);
            }
        }
    }

    private void clearTables() {
        //sequence is important

        PhotoDAO photoDAO = new PhotoDAO(context, null);
        photoDAO.clearTable();

        CoachDAO coachDAO = new CoachDAO(context, null);
        coachDAO.clearTable();

        UserProfileDAO userProfileDAO = new UserProfileDAO(context, null);
        userProfileDAO.clearTable();

        CommentDAO commentDAO = new CommentDAO(context, null);
        commentDAO.clearTable();

        MessageDAO messageDAO = new MessageDAO(context, null);
        messageDAO.clearTable();

        MealDAO mealDAO = new MealDAO(context, null);
        mealDAO.clearTable();

        NotificationDAO notificationDAO = new NotificationDAO(context, null);
        notificationDAO.clearTable();

        WorkoutDAO workoutDAO = new WorkoutDAO(context, null);
        workoutDAO.clearTable();
    }

    private void logout() {

        System.out.println("meFragments - logout");
        isLogoutBtnTapped = true;

        ApplicationEx.getInstance().setIsLogin(context, false);
        //clear the saved login credentials
        ApplicationEx.getInstance().clearLoginCredentials();
        ApplicationEx.getInstance().currentSelectedDate = AppUtil.getCurrentDateinDate();
        ApplicationEx.getInstance().currentWeightView = null;
        ApplicationEx.getInstance().currentStepsView = null;
        ApplicationEx.getInstance().tempWeightList = new Hashtable<String, Weight>();
        clearTables();

        Intent mainIntent;

        mainIntent = new Intent(this.getActivity(), LoginPageActivity.class);

        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //restart sessions
        try {

            if (ApplicationEx.getInstance().anxaMatsController == null) {
                ApplicationEx.getInstance().anxaMatsController = new AnxaMatsJobControlller(context, ApplicationEx.getInstance().userProfile.getRegID());
            }

            ApplicationEx.getInstance().anxaMatsController.closeSession(context);

        } catch (Exception E) {

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // Log.d(C.TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            // Log.d(C.TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }

        MainActivity.stopWork();

        this.getActivity().startActivity(mainIntent);
        this.getActivity().finish();
    }


    private void gotoCoach() {

        String message = getString(R.string.ALERTMESSAGE_SELECTCOACH_BTN);
        String title = "";
        String yesButton = getString(R.string.btn_ok);
        String noButton = getString(R.string.btn_later);

        dialog = new CustomDialog(context, null, yesButton, noButton, false, message, null, this);
        dialog.show();
    }

    private void loadWebKit(String url, String labels) {
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), WebkitActivity.class);
        mainIntent.putExtra("URL", url);
        mainIntent.putExtra("TITLE", labels);

        this.getActivity().startActivity(mainIntent);
    }

    private void loadAboutPage() {
        Intent mainIntent;
        mainIntent = new Intent(this.getActivity(), AboutActivity.class);

        this.getActivity().startActivity(mainIntent);
    }


    public static String updateString(String original, String pattern, String value) {

        if (original != null) {
            original = original.replace(pattern, value);
        }
        return original;
    }

    @Override
    public void loginSuccess(String response) {
        //TODO: call get Sync
        ApplicationEx.getInstance().isLoginSync = false;
        //check if your need to call the welcome page.
        if (!isLogoutBtnTapped) {
            ((MainActivity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUI();
                    updateAvatar();
                }
            });
        }
    }

    private void updateAvatar() {
        String url = ApplicationEx.getInstance().userProfile.getPic_url_large();
        url = url.replace("https", "http");
        if (url != null && !url.equalsIgnoreCase("null")) {
            download(ApplicationEx.getInstance().userProfile.getRegID(), url, "0");
        }
    }

    private void download(String photoId, String url, String mealId) {

        GetImageDownloadImplementer downloadImageimplementer = new GetImageDownloadImplementer(context,
                url,
                photoId,
                mealId, this);
    }

    @Override
    public void loginFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub
        System.out.println("loginFailedWithError" + response);
        logout();

    }

    @Override
    public void loginServices(String username, String password,
                              String data, Handler responseHandler) {
        // TODO Auto-generated method stub

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
    public void BitmapDownloadSuccess(String photoId, String mealId) {
        // TODO Auto-generated method stub
        if (ApplicationEx.getInstance().isLogin(context)) {
            updateUI();
        }
    }

    @Override
    public void BitmapDownloadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

    }

    private Bitmap getAvatar(Coach coach) {

        Bitmap avatarBMP = null;

        if (coach != null) {
            //comment out first - issue#6
            avatarBMP = ImageManager.getInstance().findImage(coach.coach_id);

            if (avatarBMP == null) {
                //download the image first
                MainListener.download(coach.coach_id, coach.avatar_url, "0");
            }

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);

            return avatarBMP;
        } else { //user comment use his image instead

            if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() != null)
                avatarBMP = ApplicationEx.getInstance().userProfile.getUserProfilePhoto();
            else if (ApplicationEx.getInstance().userProfile.getUserProfilePhoto() == null)
                avatarBMP = ImageManager.getInstance().findImage(ApplicationEx.getInstance().userProfile.getRegID());

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.hapicoach_default_profilepic, ApplicationEx.getInstance().options_Avatar);
        }
        return avatarBMP;
    }
}


