package com.anxa.hapilabs.activities;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Collections;
import java.util.Comparator;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.MealGraphListener;
import com.anxa.hapilabs.common.connection.listener.MealDeleteListener;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.addmeal.AddMealController;
import com.anxa.hapilabs.controllers.addmeal.DeleteMealController;
import com.anxa.hapilabs.controllers.images.GetImageUploadController;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.FOODGROUP;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.Meal.STATE;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo.PHOTO_STATUS;
import com.anxa.hapilabs.models.UserProfile.MEMBER_TYPE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.ui.CustomDialog;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;


public class MealAddActivity extends HAPIActivity implements OnClickListener, OnTimeChangedListener, MealGraphListener, MealDeleteListener {

    final int MAX_DESC = 250;
    final int RESULTCODE_CRMDONE = 80;
    final int CAMERA_SELECT = 999;
    final int GALLERY_SELECT = 888;
    final int MEAL_OPTIONS_CODE = 777;

    private TextView mealDescriptionCount;
    private TextView mealTime;
    private TextView options_tv;
     //    TextView submit_tv;
    private TextView progressTitle;
    private TextView mealRatingText;

    private EditText mealDesc;

    private ImageView delete_iv;
    private ImageView fg_info;
    private ImageView addPhoto;

    private Button done_picker;
    private Button btn_retry;
    private Button btn_later;
    private Button btn_submit;

    private ImageButton btn_cancelSaving;

    private AlertDialog.Builder builderFoodGroupTips;
    private Dialog dialogfoodGroupTips;
    private CustomDialog dialog;
    private CustomDialog errorDialog;

    private ProgressBar progress;
    private ProgressBar savingProgressBar;

    private RatingBar mealRatingBar;

    private RelativeLayout mealTimeContainer;
    private RelativeLayout savingLayout;
    private LinearLayout progressLayout;
    private LinearLayout retryLayout;
    private LinearLayout timePickerContainer;
    private LinearLayout activeMealLayout;

    private LinearLayout mealFoodGroupLayout;
    private LinearLayout mealRatingLayout;

    private Meal mealToAdd;
    private TimePicker timepicker;
    private List<ImageButton> foodGroups;

    private int descRemainCount = 250;
    private int DATE_YEAR;
    private int DATE_MONTH;
    private int DATE_DAY;
    private byte mealViewState;
    private boolean hasSubmitted = false;
    private boolean hasError = false;
    private boolean mealFoodGroup = false;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private Bitmap bitmap = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.mymeals_add);

        final MEAL_TYPE mealtype = MEAL_TYPE.getMealType(getIntent().getIntExtra("MEAL_TYPE", 1));
        mealViewState = (getIntent().getByteExtra("MEAL_STATE_VIEW", Meal.MEALSTATE_ADD));

        // init progressBar
        progress = (ProgressBar) findViewById(R.id.progressBar);

//        addPhoto = ((ImageView) findViewById(R.id.mealphoto));
//        addPhoto.setOnClickListener(this);

        mealTimeContainer = ((RelativeLayout) findViewById(R.id.meal_time_container));
        mealTimeContainer.setOnClickListener(this);

        btn_submit = (Button) findViewById(R.id.submitButton);
        btn_submit.setOnClickListener(this);

        done_picker = (Button) findViewById(R.id.date_save_tv);
        done_picker.setBackgroundDrawable(null);
        done_picker.setOnClickListener(this);

        timepicker = (TimePicker) findViewById(R.id.TimePicker);
        fg_info = (ImageView) findViewById(R.id.fg_info);
        fg_info.setOnClickListener(this);
        timepicker.setCurrentHour(AppUtil.getHour());
        timepicker.setCurrentMinute(AppUtil.getMinute());
        timePickerContainer = (LinearLayout) findViewById(R.id.timepickercontainer);

        //retry saving layout
        savingLayout = (RelativeLayout) findViewById(R.id.savingLayout);
        retryLayout = (LinearLayout) findViewById(R.id.retryLayout);
        progressLayout = (LinearLayout) findViewById(R.id.retryLayout);
        activeMealLayout = (LinearLayout) findViewById(R.id.addmealLayout);
        mealFoodGroupLayout = (LinearLayout) findViewById(R.id.meal_food_group_ll);
        mealRatingLayout = (LinearLayout) findViewById(R.id.meal_rating_ll);

        mealRatingText = (TextView) findViewById(R.id.meal_rating_text);

        mealRatingBar = (RatingBar) findViewById(R.id.mealRatingBar);
        //display the current rating value in the result (textview) automatically
        mealRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {

                switch (Math.round(rating)) {
                    case 0:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                        break;
                    case 1:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_1));
                        break;
                    case 2:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_2));
                        break;
                    case 3:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_3));
                        break;
                    case 4:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_4));
                        break;
                    case 5:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_5));
                        break;
                    default:
                        mealRatingText.setText(getResources().getString(R.string.MEAL_RATING_EVALUATE_0));
                        break;
                }

                mealToAdd.userRating = Math.round(rating);

            }
        });

        progressTitle = (TextView) findViewById(R.id.progressTitle);
        savingProgressBar = (ProgressBar) findViewById(R.id.savingProgressBar);
        savingProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.text_orange), PorterDuff.Mode.SRC_IN);

        btn_cancelSaving = (ImageButton) findViewById(R.id.btn_cancelSaving);

        btn_retry = (Button) findViewById(R.id.btn_retry);
        btn_later = (Button) findViewById(R.id.btn_later);

        showSavingLayout(false, false);

        initFoodGroups();

        if (ApplicationEx.getInstance().userRatingSetting) {
            mealRatingLayout.setVisibility(View.VISIBLE);
            mealFoodGroupLayout.setVisibility(View.GONE);
        } else {
            mealRatingLayout.setVisibility(View.GONE);
            mealFoodGroupLayout.setVisibility(View.VISIBLE);
        }

        if (mealViewState == Meal.MEALSTATE_ADD) {

            mealToAdd = new Meal();
            mealToAdd.meal_type = mealtype;
            mealToAdd.isApproved = false;
            mealToAdd.isCommented = false;
            mealToAdd.state = STATE.ADD_ONGOING;
            mealToAdd.meal_status = "ADD_ONGOING";

            btn_submit.setVisibility(View.VISIBLE);

            int day_Day = ApplicationEx.getInstance().currentSelectedDate.getDate();
            int month_Day = ApplicationEx.getInstance().currentSelectedDate.getMonth();
            int year_Day = ApplicationEx.getInstance().currentSelectedDate.getYear() + 1900;
            int hour = new Date().getHours();
            int min = new Date().getMinutes();
            int sec = new Date().getMonth();

            // get from date by adding today and index
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, month_Day);
            c.set(Calendar.DATE, day_Day);
            c.set(Calendar.YEAR, year_Day);

            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, min);
            c.set(Calendar.SECOND, sec);

            mealToAdd.meal_creation_date = c.getTime();

            String time = AppUtil.getTimeOnly12();//get current time
            mealTime = (TextView) findViewById(R.id.mealtime);
            mealTime.setText(time);
            setTimerPicker(false);

//            updateHeader(0, AppUtil.getMonthDateinString(mealToAdd.meal_creation_date), this);
            updateHeader(0, AppUtil.getMonthDay(mealToAdd.meal_creation_date), this);

            ((LinearLayout) findViewById(R.id.footerbuttonlayout2)).setVisibility(View.GONE);

            options_tv = ((TextView) findViewById(R.id.header_right_tv));
//            submit_tv = ((TextView) findViewById(R.id.header_right_tv));
            //update header left color
            ((TextView) findViewById(R.id.header_left_tv)).setTextColor(getResources().getColor(R.color.text_white)); //imageview


        } else { //edit meal include all necessary data

            mealToAdd = ApplicationEx.getInstance().currentMealView;

            mealToAdd.state = STATE.ADD_ONGOING;
            mealToAdd.meal_status = "ADD_ONGOING";

            String time = AppUtil.getTimeOnly12(mealToAdd.meal_creation_date.getTime());//get current time
            mealTime = (TextView) findViewById(R.id.mealtime);
            mealTime.setText(time);

            timepicker.setCurrentHour(AppUtil.getHour(mealToAdd.meal_creation_date));
            timepicker.setCurrentMinute(AppUtil.getMinute(mealToAdd.meal_creation_date));

            setTimerPicker(false);

            //set picker date
            timepicker.setCurrentHour(AppUtil.getHour(mealToAdd.meal_creation_date));
            timepicker.setCurrentMinute(AppUtil.getMinute(mealToAdd.meal_creation_date));

//            updateHeader(11, getString(R.string.btn_editmeal), this, AppUtil.getMonthDateinString(mealToAdd.meal_creation_date), null);
            updateHeader(11, AppUtil.getMonthDay(mealToAdd.meal_creation_date), this);

            ((LinearLayout) findViewById(R.id.footerbuttonlayout2)).setVisibility(View.VISIBLE);

            btn_submit.setVisibility(View.GONE);

            updateFooterButton(getResources().getString(R.string.btn_save), this);

            delete_iv = ((ImageView) findViewById(R.id.header_right));

            mealDesc = ((EditText) findViewById(R.id.mealdesc));
            mealDesc.setText(mealToAdd.meal_description);

            descRemainCount = MAX_DESC - mealToAdd.meal_description.length();

            if (mealToAdd.food_group != null && mealToAdd.food_group.size() > 0) {
                List<FOODGROUP> foodG = mealToAdd.food_group;
                for (int i = 0; i < foodG.size(); i++) {
                    updateFoodGroup(foodG.get(i).getValue());
                }
            }
            if (mealToAdd.photos != null && mealToAdd.photos.size() > 0) {
                updatePhotoUI();
            }

            mealRatingBar.setRating(mealToAdd.userRating);
        }

        //set date
        Calendar c = Calendar.getInstance();
        c.setTime(mealToAdd.meal_creation_date);

        DATE_YEAR = c.get(Calendar.YEAR);
        DATE_MONTH = c.get(Calendar.MONTH) + 1;
        DATE_DAY = c.get(Calendar.DAY_OF_MONTH);

        ((TextView) findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(this, mealToAdd.meal_type)); //imageview

        mealDesc = ((EditText) findViewById(R.id.mealdesc));
        mealDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                descRemainCount = MAX_DESC - s.length();
                updateMealDescCount();
            }
        });

        mealDescriptionCount = ((TextView) findViewById(R.id.mealdesccount));
        updateMealDescCount();
        timepicker.setOnTimeChangedListener(this);

    }

    private void updateMealDescCount() {
        mealDescriptionCount.setText(descRemainCount + "/" + MAX_DESC);
    }

    private void initFoodGroups() {

        ((ImageButton) findViewById(R.id.fg_protein)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_starch)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_vegetable)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_fats)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_dairy)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_fruit)).setOnClickListener(this);

        foodGroups = new ArrayList<ImageButton>();
        foodGroups.add((ImageButton) findViewById(R.id.fg_protein));
        foodGroups.add((ImageButton) findViewById(R.id.fg_starch));
        foodGroups.add((ImageButton) findViewById(R.id.fg_vegetable));
        foodGroups.add((ImageButton) findViewById(R.id.fg_fats));
        foodGroups.add((ImageButton) findViewById(R.id.fg_dairy));
        foodGroups.add((ImageButton) findViewById(R.id.fg_fruit));

    }

    private void updateFoodGroup(int id) {

        if (id == R.id.fg_protein || id == FOODGROUP.PROTEIN.getValue()) {
            if (foodGroups.get(0).isSelected())
                foodGroups.get(0).setSelected(false);
            else
                foodGroups.get(0).setSelected(true);
        } else if (id == R.id.fg_starch || id == FOODGROUP.STARCHES.getValue()) {
            if (foodGroups.get(1).isSelected())
                foodGroups.get(1).setSelected(false);
            else
                foodGroups.get(1).setSelected(true);
        } else if (id == R.id.fg_vegetable || id == FOODGROUP.VEGETABLE.getValue()) {
            if (foodGroups.get(2).isSelected())
                foodGroups.get(2).setSelected(false);
            else
                foodGroups.get(2).setSelected(true);
        } else if (id == R.id.fg_fats || id == FOODGROUP.FATS.getValue()) {
            if (foodGroups.get(3).isSelected())
                foodGroups.get(3).setSelected(false);
            else
                foodGroups.get(3).setSelected(true);
        } else if (id == R.id.fg_dairy || id == FOODGROUP.DAIRY.getValue()) {
            if (foodGroups.get(4).isSelected())
                foodGroups.get(4).setSelected(false);
            else
                foodGroups.get(4).setSelected(true);
        } else if (id == R.id.fg_fruit || id == FOODGROUP.FRUITS.getValue()) {
            if (foodGroups.get(5).isSelected())
                foodGroups.get(5).setSelected(false);
            else
                foodGroups.get(5).setSelected(true);
        }
    }


    private void fetchMeal(Boolean isUpdate, Boolean isDeleted) {

        //Check completeness . It needs to have atleast one photo or a  text description
        if (mealDesc.getText() == null || mealDesc.getText().length() <= 0) {
            try {
                if (mealToAdd.photos == null) {
                    showCustomDialog(getResources().getString(R.string.ALERTMESSAGE_MEALDESCRIPTION), getResources().getString(R.string.MYMEALS_TITLE));
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        if (!hasSubmitted) {
            List<FOODGROUP> foodgroup = new ArrayList<Meal.FOODGROUP>();
            for (int i = 0; i < foodGroups.size(); i++) {

                if (((ImageView) foodGroups.get(i)).isSelected()) {
                    switch (i) {
                        case 0:
                            foodgroup.add(FOODGROUP.PROTEIN);
                            break;
                        case 1:
                            foodgroup.add(FOODGROUP.STARCHES);
                            break;
                        case 2:
                            foodgroup.add(FOODGROUP.VEGETABLE);
                            break;
                        case 3:
                            foodgroup.add(FOODGROUP.FATS);
                            break;
                        case 4:
                            foodgroup.add(FOODGROUP.DAIRY);
                            break;
                        case 5:
                            foodgroup.add(FOODGROUP.FRUITS);
                            break;
                    }
                }//end if

            }//end for

            mealToAdd.food_group = foodgroup;

            if (mealDesc.getText() != null)
                mealToAdd.meal_description = mealDesc.getText().toString();

            Intent intent = new Intent();
            //public Date timestamp; //date today, date the meal is added
            //public Date meal_creation_date;  //date of the meal(date tab)


            if (!isUpdate) { // use the previous time stamp from
                mealToAdd.timestamp = AppUtil.getCurrentDateinDate();
                mealToAdd.meal_id = UniqueIDgen();
            }

            mealToAdd.userRating = Math.round(mealRatingBar.getRating());

            if (isDeleted) {
                intent.putExtra("isdeleted", isDeleted);
            }

            if (mealToAdd.photos != null && mealToAdd.photos.size() > 0) {
                intent.putExtra("withphoto", true);
            } else {
                intent.putExtra("withphoto", false);
            }

            intent.putExtra("tempmealid", mealToAdd.meal_id);


            ApplicationEx.getInstance().mealsToAdd.put(mealToAdd.meal_id, mealToAdd);

            String message;

            //remove the meal if its deleted
            message = getResources().getString(R.string.ALERTMESSAGE_MEALADDDED_ONGOING);

            //add
            if (mealViewState == Meal.MEALSTATE_ADD) {
                MealDAO daomeal = new MealDAO(this, null);
                DaoImplementer dao = new DaoImplementer(daomeal, this);
                dao.addMeal(mealToAdd);

            } else { //updated

                MealDAO daomeal = new MealDAO(this, null);
                DaoImplementer dao = new DaoImplementer(daomeal, this);
                dao.updatedMeal(mealToAdd);

            }
            ApplicationEx.getInstance().tempList.put(mealToAdd.meal_id, mealToAdd);

            ApplicationEx.getInstance().currentMealView = mealToAdd;

            hasSubmitted = true;

            if (isUpdate || isDeleted) {
                setResult(RESULT_OK, intent);
                finish();

            } else { //process meal on this stage for add meal only.

                //add progress display here
                if (mealToAdd.photos != null && mealToAdd.photos.size() > 0) {
                    processPostMeal(true, ApplicationEx.getInstance().currentMealView.meal_id);
                } else
                    processPostMeal(false, ApplicationEx.getInstance().currentMealView.meal_id);
            }
        }
    }

    private void deleteCurrentMeal() {

//        String message = getResources().getString(R.string.ALERTMESSAGE_MEALDELETE_ONGOING);
//        showAddMealDialog(message, getResources().getString(R.string.MYMEALS_TITLE));
        String userID = ApplicationEx.getInstance().userProfile.getRegID();
        if (userID != null) {
            uploadMeal(mealToAdd, userID, Meal.MEALSTATE_DELETE);
        }
    }

    public String UniqueIDgen() {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        if (mealViewState == Meal.MEALSTATE_ADD) {
            //delete meal if existing in core data
        }

    }

    private void setTimerPicker(Boolean isPickerShown) {

        if (isPickerShown) {
            //need to update with the current time on the time selected
            done_picker.setVisibility(View.VISIBLE);
            timePickerContainer.setVisibility(View.VISIBLE);

        } else { //hide picker
            done_picker.setVisibility(View.GONE);
            timePickerContainer.setVisibility(View.GONE);
        }
        //set time
    }

    private void setTimerPicker() {

        if (done_picker.getVisibility() == View.VISIBLE) { //hide
            setTimerPicker(false);

        } else { //show picker
            setTimerPicker(false);
        }
        //set time
    }

    private void showCustomDialog(String message, String title) {

        dialog = new CustomDialog(this, null, null, null, true, message, title, this);
        dialog.show();
    }

    private void showAddMealDialog(String message, String title) {

        dialog = new CustomDialog(this, null, null, null, false, message, title, this);
        dialog.show();
    }

    private void showCustomDialog() {
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), false, getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL), null, this);
        dialog.show();
    }

    private void showCameraDialog() {
        // custom dialog
        final Dialog cameraDialog = new Dialog(this);
        cameraDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cameraDialog.setContentView(R.layout.camera_dialogs);
        LinearLayout cameraOption = (LinearLayout) cameraDialog.findViewById(R.id.image_camera);
        final LinearLayout galleryOption = (LinearLayout) cameraDialog.findViewById(R.id.image_gallery);

        cameraOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
                cameraPage(Camera2Activity.REQUEST_CODE_CAMERA);
            }
        });

        galleryOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
                galleryIntent();
            }
        });

        cameraDialog.show();

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_SELECT);
    }


    private void cameraPage(int imageCode) {

        Intent mainIntent = new Intent(this, Camera2Activity.class);
        mainIntent.putExtra("MEDIA_TYPE", imageCode);
        startActivityForResult(mainIntent, CAMERA_SELECT);
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.fg_dairy || v.getId() == R.id.fg_fruit || v.getId() == R.id.fg_protein ||
                v.getId() == R.id.fg_starch || v.getId() == R.id.fg_fats || v.getId() == R.id.fg_vegetable) {
            updateFoodGroup(v.getId());

        } else if (v.getId() == R.id.date_save_tv) {
            setTimerPicker(false);

        } else if (v.getId() == R.id.footerbutton) {
            fetchMeal(true, false);
        } else if (v.getId() == R.id.meal_time_container) {
            //show time picker
            setTimerPicker(true);
        } else if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        } else if (v == options_tv) {
            proceedToMealOptionsPage();
        } else if (v.getId() == R.id.OtherButton || v.getId() == R.id.YesButton || v.getId() == R.id.NoButton) {
            //delete meal
            if (dialog != null)
                dialog.dismiss();
            if (v.getId() == R.id.YesButton)
                deleteCurrentMeal();
        } else if (v.getId() == R.id.header_right || v.getId() == R.id.header_right_tv) {
            //delete meal
            showCustomDialog();
        } else if (v.getId() == R.id.CloseButton) {
            if (dialogfoodGroupTips != null)
                dialogfoodGroupTips.dismiss();
            if (dialog != null)
                dialog.dismiss();
            if (hasError)
                finish();
        } else if (v == addPhoto) {
            showCameraDialog();
        } else if (v == fg_info) {
            showFoodGroupDialog();
        } else if (v.getId() == R.id.btn_close_1 || v.getId() == R.id.btn_close_2 || v.getId() == R.id.btn_close_3 || v.getId() == R.id.btn_close_4 || v.getId() == R.id.btn_close_5) {
            //delete photo
            deleteImage((Integer) v.getTag());
        } else if (v == btn_submit) {
            //submit button
            fetchMeal(false, false);
        }
    }


    private void deleteImage(int photoIndex) {
        ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.VISIBLE);

        if (mealToAdd.photos == null) {
            mealToAdd.photos = new ArrayList<Photo>();
        }

        try {
            mealToAdd.photos.remove(photoIndex);
        } catch (Exception e) {

        }
        clearPhotoUI();
        updatePhotoUI();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Date date = AppUtil.formatDate(view, DATE_MONTH, DATE_DAY, DATE_YEAR);

        String time = AppUtil.getTimeOnly12(date.getTime());//get current time
        if (mealTime != null)
            mealTime.setText(time);
        mealToAdd.meal_creation_date = date;
    }

    //to receive the camera activity result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULTCODE_CRMDONE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("isAdd", true);
                intent.putExtra("LOADCOACH", data.getBooleanExtra("LOADCOACH", false));
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (requestCode == 999/*camera page*/) {
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("ACTIVITY_PHOTO") != null) {

                    Boolean isGallery = data.getBooleanExtra("ACTIVITY_ISGALLERY", false);
                    int inSampleSize = data.getIntExtra("ACTIVITY_SAMPLESIZE", 2);
                    String selectedImageURI = data.getStringExtra("ACTIVITY_PHOTO");
                    File file = new File(selectedImageURI);

                    //if (file.exists()){
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inSampleSize = inSampleSize;
                    try {
                        Bitmap b = null;
                        if (isGallery) {
                            try {
                                b = BitmapFactory.decodeFile(file.getPath(), options);
                            } catch (Exception e) {
                            }

                        } else {
                            ExifInterface ei = new ExifInterface(file.getPath());
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                            int scaleW = 0, scaleH = 0;

                            int imgW = ei.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ApplicationEx.getInstance().maxWidthCameraView);
                            int imgH = ei.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ApplicationEx.getInstance().maxHeightCameraView);

                            if (imgW > ApplicationEx.getInstance().maxWidthCameraView || imgH > ApplicationEx.getInstance().maxHeightCameraView) { //max w & h
                                int percentW = (ApplicationEx.getInstance().maxWidthCameraView * 100) / (imgW);
                                int percentH = (ApplicationEx.getInstance().maxHeightCameraView * 100) / (imgH);

                                if (percentW >= percentH) {
                                    scaleW = (int) (imgW * percentH / 200);
                                    scaleH = (int) (imgH * percentH / 200);
                                } else {
                                    scaleW = (int) (imgW * percentW / 200);
                                    scaleH = (int) (imgH * percentW / 200);
                                }

                            } else {
                                scaleW = imgW;
                                scaleH = imgH;
                            }

                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90://portait
                                    b = AppUtil.rotateImage(this, Uri.fromFile(file), 90, imgW, imgH, scaleW, scaleH);
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180: //landscape inverse
                                    b = AppUtil.rotateImage(this, Uri.fromFile(file), 180, imgW, imgH, scaleW, scaleH);

                                    break;

                                default://landcape normal do nothing
                                    b = AppUtil.rotateImage(this, Uri.fromFile(file), 0, imgW, imgH, scaleW, scaleH);
                                    break;
                            }

                            System.out.println("camera: rotate H " + b.getHeight() + "***W" + b.getWidth());

                            if (b.getWidth() > ApplicationEx.getInstance().maxWidthCameraView || b.getHeight() > ApplicationEx.getInstance().maxHeightCameraView) { //max w & h
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                options.inSampleSize=2; //4, 8, etc. the more value, the worst quality of image
                                if (b != null)
                                    b.compress(Bitmap.CompressFormat.JPEG, 60, bs);

                                System.out.println("camera: MAX rotate H " + b.getHeight() + "***W" + b.getWidth());
                            }
                        }

                        updateAlbum(b);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE") != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    int inSampleSize = data.getIntExtra("ACTIVITY_SAMPLESIZE", 2);
                    options.inSampleSize = inSampleSize;
                    Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE"), 0, data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE").length, options);
                    updateAlbum(b);

                    b = null;
                }

            }//result_ok
        } else if (requestCode == GALLERY_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();

                    Bitmap bitmap = decodeUri(selectedImage);
                    if (bitmap != null)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bs);

                    updateAlbum(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == MEAL_OPTIONS_CODE) {
            if (ApplicationEx.getInstance().userRatingSetting) {
                mealRatingLayout.setVisibility(View.VISIBLE);
                mealFoodGroupLayout.setVisibility(View.GONE);
            } else {
                mealRatingLayout.setVisibility(View.GONE);
                mealFoodGroupLayout.setVisibility(View.VISIBLE);
            }

        }
        //onActivityResult
    }//end on activity

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateAlbum(bm);
    }

    private void updateAlbum(Bitmap b) {

        com.anxa.hapilabs.models.Photo newPhoto = new com.anxa.hapilabs.models.Photo();
        newPhoto.image = b;
        newPhoto.photo_url_large = "";
        newPhoto.state = PHOTO_STATUS.ONGOING_UPLOADPHOTO;

        if (mealToAdd.photos == null) {
            mealToAdd.photos = new ArrayList<Photo>();
        }

        mealToAdd.photos.add(newPhoto);

        updatePhotoUI();
    }

    private void clearPhotoUI() {
        ((RelativeLayout) findViewById(R.id.ll_imagethumb2)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb3)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb4)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb5)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb1)).setVisibility(View.GONE);

    }

    private void updatePhotoUI() {
        List<Photo> photos = mealToAdd.photos;

        for (int i = 0; i < photos.size(); i++) {

            Photo photo = photos.get(i);

            if ((photo.photo_id != null) && i == 0) { //display thumb at thumb 2

                ((RelativeLayout) findViewById(R.id.ll_imagethumb2)).setVisibility(View.VISIBLE);
                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageResource(AppUtil.getPhotoResource(mealToAdd.meal_type)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_2)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_2)).setTag(0/*photo index*/);

            } else if (i == 1) { //display thumb at thumb 3
                ((RelativeLayout) findViewById(R.id.ll_imagethumb3)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageResource(AppUtil.getPhotoResource(mealToAdd.meal_type)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(bmp); // use

                }

                ((ImageView) findViewById(R.id.btn_close_3)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_3)).setTag(1/*photo index*/);

            } else if (i == 2) { //display thumb at thumb 4

                ((RelativeLayout) findViewById(R.id.ll_imagethumb4)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(photo.image);

                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageResource(AppUtil.getPhotoResource(mealToAdd.meal_type)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_4)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_4)).setTag(2/*photo index*/);

            } else if (i == 3) { //display thumb at thumb 5

                ((RelativeLayout) findViewById(R.id.ll_imagethumb5)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageResource(AppUtil.getPhotoResource(mealToAdd.meal_type)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_5)).setOnClickListener(this);

                ((ImageView) findViewById(R.id.btn_close_5)).setTag(3/*photo index*/);

            } else if (i == 4) { //display thumb at thumb 1
                ((RelativeLayout) findViewById(R.id.ll_imagethumb1)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.INVISIBLE);

                if (photo.image != null) {
                    ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(photo.image);
                }else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null) {
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageResource(AppUtil.getPhotoResource(mealToAdd.meal_type)); // use
                    }else {
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(bmp); // use
                    }
                }

                ((ImageView) findViewById(R.id.btn_close_1)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_1)).setTag(4/*photo index*/);
            }
        }
    }

    private void showFoodGroupDialog() {
        LayoutInflater inflater = getLayoutInflater();

        //location dialogs
        if (builderFoodGroupTips == null)
            builderFoodGroupTips = new AlertDialog.Builder(this);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.foodgroup_dialog, null);

        ((ImageView) layout.findViewById(R.id.CloseButton)).setOnClickListener(this);

        builderFoodGroupTips.setView(layout);
        dialogfoodGroupTips = builderFoodGroupTips.create();
        dialogfoodGroupTips.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogfoodGroupTips.show();
    }


    synchronized void uploadMeal(final Meal meal, final String username, byte mealCommand) {
        if (mealCommand == Meal.MEALSTATE_ADD) {

            if (addmealController == null) {
                addmealController = new AddMealController(this, this, this, this, mealCommand);
            }

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    addmealController.uploadMeal(meal, username);
                }
            });
        } else if (mealCommand == Meal.MEALSTATE_DELETE) {
            if (deleteMealController == null) {
                deleteMealController = new DeleteMealController(this, this, this, mealCommand);
            }
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deleteMealController.deleteMeal(meal, username);
                }
            });
        }
    }

    private void uploadPhoto(Meal meal) {

        GetImageUploadController getImageUploadController = new GetImageUploadController(this, this, this);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();

        if (userID != null) {
            getImageUploadController.startImageUpload(userID, meal.meal_id);
        }
    }


    private void processPostMeal(Boolean hasphoto, String tempMealID) {

        showSavingLayout(true, false);

        Meal meal = ApplicationEx.getInstance().mealsToAdd.get(tempMealID);

        if (meal == null)
            return;

        if (hasphoto) {
            uploadPhoto(meal);
        } else {
            String userID = ApplicationEx.getInstance().userProfile.getRegID();
            if (userID != null) {
                uploadMeal(meal, userID, Meal.MEALSTATE_ADD);
            }
        }
    }

    @Override
    public void uploadMealSuccess(String response) {
        // TODO Auto-generated method stub

        System.out.println("uploadMealSuccess");

        //update the meal_status in SQL
        MealDAO daomeal = new MealDAO(this, null);
        mealToAdd.meal_status = "SYNC";
        mealToAdd.state = STATE.SYNC;
        daomeal.updateMeal(mealToAdd);

        //broadcast the update
        Intent broadInt = new Intent();
        broadInt.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadInt);

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //
                showSavingLayout(false, false);
            }
        });

    }

    @Override
    public void uploadMealFailedWithError(MessageObj response, String entryID) {
        // TODO Auto-generated method stub

        //dismiss keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mealDesc.getWindowToken(), 0);

        //broadcast the update
        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadint);

        hasSubmitted = false;

        //display alert message: no internet, then upon closing - go back to Meals page
        //update the meal status in the database
        MealDAO daomeal = new MealDAO(this, null);
        mealToAdd.meal_status = "FAILED";
        daomeal.updateMeal(mealToAdd);

        hasError = true;

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showSavingLayout(true, true);
            }
        });
    }

    @Override
    public void startProgress() {
        // TODO Auto-generated method stub
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopProgress() {
        // TODO Auto-generated method stub
        progress.setVisibility(View.GONE);
    }

    @Override
    public void BitmapUploadSuccess(Boolean forUpload, String mealId) {
        // TODO Auto-generated method stub
        if (forUpload) {
            //find the meal in the meal list
            Meal meal = ApplicationEx.getInstance().mealsToAdd.get(mealId);
            String username = ApplicationEx.getInstance().userProfile.getRegID();

            if (username != null) {
                uploadMeal(meal, username, Meal.MEALSTATE_ADD);
            }
        }
    }

    @Override
    public void BitmapUploadFailedWithError(MessageObj response) {
        // TODO Auto-generated method stub

        hasError = true;
        hasSubmitted = false;

        MealDAO daomeal = new MealDAO(this, null);
        mealToAdd.meal_status = "FAILED";
        mealToAdd.state = STATE.FAILED;
        daomeal.updateMeal(mealToAdd);

        //broadcast the update
        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.meallist_getmeal_week));
        sendBroadcast(broadint);


        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showSavingLayout(true, true);
            }
        });
    }

    private void callGraph(int total, List<GraphMeal> response) {

        //display CRM PAGES
        Intent mainIntent = new Intent(this, CRMActivity.class);

        if (ApplicationEx.getInstance().userProfile != null) {

            if (ApplicationEx.getInstance().userProfile.getMember_type() != MEMBER_TYPE.FREE) {
                mainIntent.putExtra("ISPREMIUM_USER", true);
            }

            long[] dates = new long[response.size()];
            int[] postcount = new int[response.size()];

            for (int i = 0; i < response.size(); i++) {
                GraphMeal item = new GraphMeal();
                item.date = response.get(i).date;
                item.mealcount = response.get(i).mealcount;
            }

            Collections.sort(response, new Comparator<GraphMeal>() {
                public int compare(GraphMeal o1, GraphMeal o2) {
                    return o1.date.compareTo(o2.date);
                }
            });

            for (int i = 0; i < response.size(); i++) {
                dates[i] = response.get(i).date.getTime();
                postcount[i] = response.get(i).mealcount;
            }
            mainIntent.putExtra("DATES", dates);
            mainIntent.putExtra("POSTCOUNT", postcount);
        }
        mainIntent.putExtra("TOTAL_MEAL", total);
        startActivityForResult(mainIntent, RESULTCODE_CRMDONE);
    }


    private void showSavingLayout(boolean saving, boolean failed) {

        if (saving) {
//            updateHeader(13, AppUtil.getMonthDateinString(mealToAdd.meal_creation_date), this);
            updateHeader(13, AppUtil.getMonthDay(mealToAdd.meal_creation_date), this);

            savingLayout.setVisibility(View.VISIBLE);
            activeMealLayout.setEnabled(false);

            if (failed) {
                //if failed, show the retry layout.
                //change text of progress bar to FAILED
                //set progress to 0
                retryLayout.setVisibility(View.VISIBLE);
                progressTitle.setText(getResources().getString(R.string.SAVING_PROGRESS_FAILED));
                savingProgressBar.setIndeterminate(false);
                savingProgressBar.setProgress(0);

            } else {
                retryLayout.setVisibility(View.GONE);
                progressTitle.setText(getResources().getString(R.string.SAVING_PROGRESS_TEXT));
                savingProgressBar.setIndeterminate(true);
                savingProgressBar.setVisibility(View.VISIBLE);
            }
        } else {
            savingLayout.setVisibility(View.GONE);
            activeMealLayout.setEnabled(true);
        }
    }

    @Override
    public void mealGraphSuccess(List<GraphMeal> response, int mealtotal) {
        // TODO Auto-generated method stub
        callGraph(mealtotal, response);
    }

    @Override
    public void deleteMealSuccess(String response) {

        //delete from DB:
        MealDAO daomeal = new MealDAO(this, null);
        DaoImplementer dao = new DaoImplementer(daomeal, this);
        dao.deleteMeal(mealToAdd);

        //delete from the list
        ApplicationEx.getInstance().tempList.remove(mealToAdd.meal_id);

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
                showCustomDialog(messageDialog, null);
            }
        });
    }

    public void cancelSaving(View view) {
        MessageObj msgObj = new MessageObj();
        msgObj.setMessage_string("Saving cancelled");

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showSavingLayout(false, false);
            }
        });

//        updateHeader(0, AppUtil.getMonthDateinString(mealToAdd.meal_creation_date), this);
        updateHeader(0, AppUtil.getMonthDay(mealToAdd.meal_creation_date), this);
    }

    public void retrySaving(View view) {
        showSavingLayout(true, false);

        if (mealToAdd.photos != null && mealToAdd.photos.size() > 0) {
            processPostMeal(true, ApplicationEx.getInstance().currentMealView.meal_id);
        } else
            processPostMeal(false, ApplicationEx.getInstance().currentMealView.meal_id);
    }

    public void laterSaving(View view) {
        //go back to Meals Home Page
        showSavingLayout(false, false);
        finish();
    }

    public void goToCameraPage(View view){
        cameraPage(Camera2Activity.REQUEST_CODE_CAMERA);
    }

    public void goToGalleryPage(View view){
        galleryIntent();
    }


    private void proceedToMealOptionsPage() {
        Intent mealOptionsIntent = new Intent(this, MealOptionsActivity.class);
        mealOptionsIntent.putExtra("dateHeader", AppUtil.getMonthDay(mealToAdd.meal_creation_date));
        startActivityForResult(mealOptionsIntent, MEAL_OPTIONS_CODE);
    }

    private void updateRatingView() {

    }
}