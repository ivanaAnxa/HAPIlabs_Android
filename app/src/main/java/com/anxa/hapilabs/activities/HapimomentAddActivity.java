package com.anxa.hapilabs.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.AddHapiMomentListener;
import com.anxa.hapilabs.common.connection.listener.GenericImageUploadListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.hapimoment.AddHapiMomentController;
import com.anxa.hapilabs.controllers.images.GenericImageUploadController;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by angelaanxa on 8/8/2016.
 */
public class HapimomentAddActivity extends HAPIActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener, AddHapiMomentListener, GenericImageUploadListener {
    final int MAX_DESC = 250;
    final int CAMERA_SELECT = 999;
    final int GALLERY_SELECT = 888;

    RelativeLayout savingLayout;
    LinearLayout activeHapiMomentLayout;
    LinearLayout retryLayout;
    LinearLayout timePickerContainer;
    RelativeLayout mealTimeContainer;
    TimePicker timepicker;
    Button done_picker;
    TextView hapimomentTime;
    EditText hapimomentDesc;
    EditText hapimomentLocation;
    TextView hapimomentDescriptionCount;
    Button btn_submit;
    ImageView addPhoto;
    TextView progressTitle;

    ProgressBar progress;
    ProgressBar savingProgressBar;

    List<ImageButton> hapiMoodList;
    HapiMoment currentHapiMoment;

    byte hapimomentViewState;
    int descRemainCount = 250;
    int DATE_YEAR;
    int DATE_MONTH;
    int DATE_DAY;
    boolean hasError, hasSubmitted;

    AddHapiMomentController addHapiMomentController;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.hapimoment_add);

        hapimomentViewState = (getIntent().getByteExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_ADD));
        //hapimomentViewState = HapiMoment.HAPIMOMENTSTATE_EDIT;
        /* time manipulation */
        mealTimeContainer = ((RelativeLayout) findViewById(R.id.hapimoment_time_container));
        mealTimeContainer.setOnClickListener(this);
        timepicker = (TimePicker) findViewById(R.id.TimePicker);
        timepicker.setCurrentHour(AppUtil.getHour());
        timepicker.setCurrentMinute(AppUtil.getMinute());
        timePickerContainer = (LinearLayout) findViewById(R.id.timepickercontainer);
        done_picker = (Button) findViewById(R.id.date_save_tv);
        done_picker.setBackgroundDrawable(null);
        done_picker.setOnClickListener(this);

        hapimomentDesc = ((EditText) findViewById(R.id.hapimomentDesc));
        hapimomentLocation = ((EditText) findViewById(R.id.location));

//        addPhoto = ((ImageView) findViewById(R.id.hapiMomentPhoto));
//        addPhoto.setOnClickListener(this);

        savingLayout = (RelativeLayout) findViewById(R.id.savingLayout);
        activeHapiMomentLayout = (LinearLayout) findViewById(R.id.addhapimomentLayout);
        retryLayout = (LinearLayout) findViewById(R.id.retryLayout);

        progressTitle = (TextView) findViewById(R.id.progressTitle);
        savingProgressBar = (ProgressBar) findViewById(R.id.savingProgressBar);
        savingProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.text_orange), PorterDuff.Mode.SRC_IN);

        hapimomentDesc.addTextChangedListener(new TextWatcher() {

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

        hapimomentDescriptionCount = ((TextView) findViewById(R.id.hapimomentDescCount));
        updateMealDescCount();
        timepicker.setOnTimeChangedListener(this);

        hapiMoodList = new ArrayList<ImageButton>();
        hapiMoodList.add((ImageButton) findViewById(R.id.mood1));
        hapiMoodList.add((ImageButton) findViewById(R.id.mood2));
        hapiMoodList.add((ImageButton) findViewById(R.id.mood3));
        hapiMoodList.add((ImageButton) findViewById(R.id.mood4));
        hapiMoodList.add((ImageButton) findViewById(R.id.mood5));

        ((ImageButton) findViewById(R.id.mood1)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood2)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood3)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood4)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood5)).setOnClickListener(this);

        btn_submit = (Button) findViewById(R.id.submitButton);
        btn_submit.setOnClickListener(this);
        /* add new hapimoment */
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_ADD) {
            currentHapiMoment = new HapiMoment();
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

            currentHapiMoment.mood_datetime = c.getTime();

            String time = AppUtil.getTimeOnly12();//get current time
            hapimomentTime = (TextView) findViewById(R.id.hapimomentTime);
            hapimomentTime.setText(time);
            setTimerPicker(false);

            /* customize hint of desc */
            if (ApplicationEx.getInstance().userProfile.getFirstname() != null) {
                hapimomentDesc.setHint(hapimomentDesc.getHint().toString().replace("$name", ApplicationEx.getInstance().userProfile.getFirstname()));
            }
            /* set default mood */
            currentHapiMoment.moodValue = 1;
            hapiMoodList.get(0).setSelected(true);
            hapiMoodList.get(0).setBackgroundResource(R.drawable.rounded_button_orange_withborder);

            btn_submit.setVisibility(View.VISIBLE);
        }
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_EDIT) {

            this.currentHapiMoment = ApplicationEx.getInstance().currentHapiMoment;
            hapimomentDesc.setText(currentHapiMoment.description);
            int day_Day = currentHapiMoment.mood_datetime.getDay();
            int month_Day = currentHapiMoment.mood_datetime.getMonth();
            int year_Day = currentHapiMoment.mood_datetime.getYear();

            int hour = currentHapiMoment.mood_datetime.getHours();
            int min = currentHapiMoment.mood_datetime.getMinutes();
            int sec = currentHapiMoment.mood_datetime.getSeconds();

            // get from date by adding today and index
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, month_Day);
            c.set(Calendar.DATE, day_Day);
            c.set(Calendar.YEAR, year_Day);

            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, min);
            c.set(Calendar.SECOND, sec);
            hapimomentTime = (TextView) findViewById(R.id.hapimomentTime);
            hapimomentTime.setText(AppUtil.getTimeOnly12(currentHapiMoment.mood_datetime.getTime()));
            setTimerPicker(false);
            /* customize hint of desc */
            if (ApplicationEx.getInstance().userProfile.getFirstname() != null) {
                hapimomentDesc.setHint(hapimomentDesc.getHint().toString().replace("$name", ApplicationEx.getInstance().userProfile.getFirstname()));
            }
            /* set default mood */
            hapiMoodList.get(currentHapiMoment.moodValue - 1).setSelected(true);
            hapiMoodList.get(currentHapiMoment.moodValue - 1).setBackgroundResource(R.drawable.rounded_button_orange_withborder);

            if (!currentHapiMoment.location.isEmpty() && currentHapiMoment.location != null && currentHapiMoment.location != "null") {
                hapimomentLocation.setText(currentHapiMoment.location);
            }
            btn_submit.setVisibility(View.VISIBLE);
            updatePhotoUI();
        }

        //set date
        Calendar c = Calendar.getInstance();
        c.setTime(currentHapiMoment.mood_datetime);

        DATE_YEAR = c.get(Calendar.YEAR);
        DATE_MONTH = c.get(Calendar.MONTH) + 1;
        DATE_DAY = c.get(Calendar.DAY_OF_MONTH);
        updateHeader(15, getResources().getString(R.string.MEALTYPE_HAPIMOMENT), this);

    }

    public void updateHapiMood(int moodValue) {
        for (int i = 1; i <= 5; i++) {
            if (i == moodValue) {
                hapiMoodList.get(i - 1).setSelected(true);
                hapiMoodList.get(i - 1).setBackgroundResource(R.drawable.rounded_button_orange_withborder);
                currentHapiMoment.moodValue = moodValue;
            } else {
                hapiMoodList.get(i - 1).setSelected(false);
                hapiMoodList.get(i - 1).setBackgroundResource(0);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.date_save_tv) {
            setTimerPicker(false);

        } else if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        } else if (v.getId() == R.id.hapimoment_time_container) {
            //show time picker
            setTimerPicker(true);
        } else if (v.getId() == R.id.mood1) {
            updateHapiMood(1);
        } else if (v.getId() == R.id.mood2) {
            updateHapiMood(2);
        } else if (v.getId() == R.id.mood3) {
            updateHapiMood(3);
        } else if (v.getId() == R.id.mood4) {
            updateHapiMood(4);
        } else if (v.getId() == R.id.mood5) {
            updateHapiMood(5);
        } else if (v == btn_submit) {
            //submit button
            processSubmit();
        } else if (v == addPhoto) {
            showCameraDialog();
        } else if (v.getId() == R.id.btn_close_1 || v.getId() == R.id.btn_close_2 || v.getId() == R.id.btn_close_3 || v.getId() == R.id.btn_close_4 || v.getId() == R.id.btn_close_5) {
            //delete photo
            deleteImage((Integer) v.getTag());
        }
    }

    private void processSubmit() {
        if (hapimomentDesc.getText() != null) {
            currentHapiMoment.description = hapimomentDesc.getText().toString();
        }

        currentHapiMoment.location = hapimomentLocation.getText().toString();
        showSavingLayout(true, false);
        //add
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_ADD) {
            currentHapiMoment.command = "added";

            ApplicationEx.getInstance().currentHapiMoment = currentHapiMoment;

            if (currentHapiMoment.photos != null && !currentHapiMoment.photos.isEmpty()) {
                boolean isUpload = false;
                for (Photo photo : currentHapiMoment.photos) {
                    if (photo.state != Photo.PHOTO_STATUS.SYNC_UPLOADPHOTO) {
                        isUpload = true;
                    }
                }
                if (isUpload) {
                    uploadPhoto();
                } else {
                    uploadHapiMoment();
                }
            } else {
                uploadHapiMoment();
            }

        } else if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_EDIT) {
            currentHapiMoment.command = "updated";
            if (currentHapiMoment.photos != null && !currentHapiMoment.photos.isEmpty()) {
                boolean isUpload = false;
                for (Photo photo : currentHapiMoment.photos) {
                    if (photo.state != Photo.PHOTO_STATUS.SYNC_UPLOADPHOTO) {
                        isUpload = true;
                    }
                }
                if (isUpload) {
                    uploadPhoto();
                } else {
                    uploadHapiMoment();
                }

            } else {
                uploadHapiMoment();
            }
        }
    }

    private void uploadHapiMoment() {
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_ADD || hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_EDIT) {
            if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_ADD) {
                ApplicationEx.getInstance().currentHapiMoment = currentHapiMoment;
            }

            if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_EDIT) {

            }
            if (addHapiMomentController == null) {
                addHapiMomentController = new AddHapiMomentController(getApplicationContext(), this, this);
            }

            this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    addHapiMomentController.uploadHapiMoment(currentHapiMoment, ApplicationEx.getInstance().userProfile.getRegID());
                }
            });
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Date date = AppUtil.formatDate(view, DATE_MONTH, DATE_DAY, DATE_YEAR);

        String time = AppUtil.getTimeOnly12(date.getTime());//get current time
        if (hapimomentTime != null)
            hapimomentTime.setText(time);
        currentHapiMoment.mood_datetime = date;
    }

    private void updateMealDescCount() {
        hapimomentDescriptionCount.setText(descRemainCount + "/" + MAX_DESC);
    }

    private void showCameraDialog() {
        // custom dialog
        final Dialog cameraDialog = new Dialog(this);
        cameraDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        cameraDialog.setContentView(R.layout.camera_dialogs);
        LinearLayout cameraOption = (LinearLayout) cameraDialog.findViewById(R.id.image_camera);
        final LinearLayout galleryOption = (LinearLayout) cameraDialog.findViewById(R.id.image_gallery);

        cameraOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
                cameraPage(Camera2Activity.REQUEST_CODE_CAMERA);
            }
        });

        galleryOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraDialog.dismiss();
                galleryIntent();
            }
        });

        cameraDialog.show();

    }

    private void cameraPage(int imageCode) {

        Intent mainIntent = new Intent(this, Camera2Activity.class);
        mainIntent.putExtra("MEDIA_TYPE", imageCode);
        startActivityForResult(mainIntent, CAMERA_SELECT);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_SELECT);
    }

    private void deleteImage(int photoIndex) {
        ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.VISIBLE);

        if (currentHapiMoment.photos == null) {
            currentHapiMoment.photos = new ArrayList<Photo>();
        }

        try {
            currentHapiMoment.photos.remove(photoIndex);
        } catch (Exception e) {

        }
        clearPhotoUI();
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
        List<Photo> photos = currentHapiMoment.photos;
        if (photos == null)
            return;

        for (int i = 0; i < photos.size(); i++) {
            Photo photo = photos.get(i);
            if ((photo.photo_id != null) && i == 0) { //display thumb at thumb 2

                ((RelativeLayout) findViewById(R.id.ll_imagethumb2)).setVisibility(View.VISIBLE);
                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageResource(AppUtil.getDefaultHapimomentPhoto()); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_2)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_2)).setTag(0/*photo index*/);

            } else if (i == 1) { //display thumb at thumb 2
                ((RelativeLayout) findViewById(R.id.ll_imagethumb3)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageResource(AppUtil.getDefaultHapimomentPhoto()); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(bmp); // use

                }

                ((ImageView) findViewById(R.id.btn_close_3)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_3)).setTag(1/*photo index*/);

            } else if (i == 2) { //display thumb at thumb 2

                ((RelativeLayout) findViewById(R.id.ll_imagethumb4)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(photo.image);

                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageResource(AppUtil.getDefaultHapimomentPhoto()); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_4)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_4)).setTag(2/*photo index*/);

            } else if (i == 3) { //display thumb at thumb 2

                ((RelativeLayout) findViewById(R.id.ll_imagethumb5)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageResource(AppUtil.getDefaultHapimomentPhoto()); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_5)).setOnClickListener(this);

                ((ImageView) findViewById(R.id.btn_close_5)).setTag(3/*photo index*/);

            } else if (i == 4) { //display thumb at thumb 2
                ((RelativeLayout) findViewById(R.id.ll_imagethumb1)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.INVISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageResource(AppUtil.getDefaultHapimomentPhoto()); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_1)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_1)).setTag(4/*photo index*/);
            }
        }
    }

    //to receive the camera activity result
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("onActivityResult: " + requestCode + " resultCode: " + resultCode + " data: " + data);

        if (requestCode == 999/*camera page*/) {
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

                            if (b.getWidth() > ApplicationEx.getInstance().maxWidthCameraView || b.getHeight() > ApplicationEx.getInstance().maxHeightCameraView) { //max w & h
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
                            }
                        }

                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        if (b != null)
                            b.compress(Bitmap.CompressFormat.JPEG, 60, bs);

                        updateAlbum(b);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE") != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = data.getIntExtra("ACTIVITY_SAMPLESIZE", 2);
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
        }
        //onActivityResult
    }//end on activity

    private void updateAlbum(Bitmap b) {

        Photo newPhoto = new Photo();
        newPhoto.image = b;
        newPhoto.photo_url_large = "";
        newPhoto.state = Photo.PHOTO_STATUS.ONGOING_UPLOADPHOTO;

        if (currentHapiMoment.photos == null) {
            currentHapiMoment.photos = new ArrayList<Photo>();
        }

        currentHapiMoment.photos.add(newPhoto);

        updatePhotoUI();
    }

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

    private void uploadPhoto() {

        GenericImageUploadController controller = new GenericImageUploadController(this, this, this);
        String userID = ApplicationEx.getInstance().userProfile.getRegID();

        if (userID != null) {
            controller.startImageUpload(userID, Integer.toString(currentHapiMoment.mood_id), currentHapiMoment.photos);
        }
    }

    private void showSavingLayout(boolean saving, boolean failed) {

        if (saving) {
            updateHeader(13, getResources().getString(R.string.MEALTYPE_HAPIMOMENT), this);

            savingLayout.setVisibility(View.VISIBLE);
            activeHapiMomentLayout.setEnabled(false);

            if (failed) {
                //if failed, show the retry layout, change text of progress bar to FAILED - set progress to 0
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
            activeHapiMomentLayout.setEnabled(true);
        }
    }

    /**
     * Listeners
     **/
    public void addHapiMomentSuccess(String response) {
        System.out.println("addHapiMomentSuccess");
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_EDIT) {
            int index = -1;
            for (HapiMoment p : ApplicationEx.getInstance().hapiMomentList) {
                if (p.mood_id == currentHapiMoment.mood_id) {
                    index = ApplicationEx.getInstance().hapiMomentList.indexOf(p);
                    ApplicationEx.getInstance().hapiMomentList.set(index, currentHapiMoment);
                    ApplicationEx.getInstance().currentHapiMoment = currentHapiMoment;
                    ApplicationEx.getInstance().tempHapimomentList.remove(String.valueOf(currentHapiMoment.mood_id));
                    ApplicationEx.getInstance().tempHapimomentList.put(String.valueOf(currentHapiMoment.mood_id), currentHapiMoment);
                }
            }
        }
        if (hapimomentViewState == HapiMoment.HAPIMOMENTSTATE_ADD) {
            currentHapiMoment.mood_id = ApplicationEx.getInstance().currentHapiMoment.mood_id;
            ApplicationEx.getInstance().currentHapiMoment = currentHapiMoment;
            ApplicationEx.getInstance().hapiMomentList.add(currentHapiMoment);
            ApplicationEx.getInstance().tempHapimomentList.put(String.valueOf(currentHapiMoment.mood_id), currentHapiMoment);
            Intent broadint = new Intent();
            broadint.setAction(this.getResources().getString(R.string.meallist_addhapimoment_refresh));

            sendBroadcast(broadint);
        }
        finish();
    }

    public void addHapiMomentFailedWithError(MessageObj response) {
    }

    public void ImageUploadSuccess(Boolean forUpload, String hapiMomentId) {
        if (forUpload) {
            HapiMoment hapiMoment = ApplicationEx.getInstance().currentHapiMoment;
            //find the meal in the meal list
            String username = ApplicationEx.getInstance().userProfile.getRegID();

            if (username != null) {
                uploadHapiMoment();
            }
        }


    }

    public void ImageUploadFailedWithError(MessageObj messageObj) {
        showSavingLayout(true, true);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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
    }

    public void retrySaving(View view) {
        showSavingLayout(true, false);

        processSubmit();
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

}
