package com.anxa.hapilabs.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.GetWorkoutListener;
import com.anxa.hapilabs.common.connection.listener.PostWorkoutListener;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.exercise.GetWorkoutController;
import com.anxa.hapilabs.controllers.exercise.PostWorkoutController;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CustomDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class ExerciseActivity extends HAPIActivity implements OnClickListener, TimePicker.OnTimeChangedListener, GetWorkoutListener, PostWorkoutListener {

    final int MAX_DESC = 250;

    RelativeLayout exerciseOption, exerciseTimeContainer;

    ImageView exerciseWalkButton, exerciseRunButton, exerciseBikeButton, exerciseSwimButton, exerciseWorkOutButton, exerciseOtherButton;
    TextView selectOtherExercise, exerciseTime, durationTime, exerciseDescCount, step_labelSteps ;
    EditText distanceET, stepsET, exerciseDescription;
    LinearLayout timePickerContainer, lldescription;
    Button done_picker, saveButton;
    TimePicker timepicker;
    SeekBar durationSeekBar;
    Workout myCurrentWorkout;
    CustomDialog dialog;

    RelativeLayout savingLayout;
    LinearLayout progressLayout;
    LinearLayout retryLayout;
    LinearLayout addExerciseLayout;

    TextView progressTitle;
    ProgressBar savingProgressBar;

    private Workout.EXERCISE_TYPE selectedExerciseType;
    private int indexType;

    private GetWorkoutController getWorkoutController;
    private PostWorkoutController postWorkoutController;

    int DATE_YEAR;
    int DATE_MONTH;
    int DATE_DAY;
    int descRemainCount = 250;
    String workoutid;
    String selectedWorkoutDate;

    String [] exerciseOtherArray;

    public static final String LOG_TAG = "myLogs";

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.exercise);

        //retry saving layout
        savingLayout = (RelativeLayout) findViewById(R.id.savingLayout);
        retryLayout = (LinearLayout) findViewById(R.id.retryLayout);
        progressLayout = (LinearLayout) findViewById(R.id.retryLayout);
        addExerciseLayout = (LinearLayout) findViewById(R.id.addExerciseLayout);

        progressTitle = (TextView) findViewById(R.id.progressTitle);
        savingProgressBar = (ProgressBar) findViewById(R.id.savingProgressBar);
        savingProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.text_orange), PorterDuff.Mode.SRC_IN);

        showSavingLayout(false, false);

        exerciseDescription = (EditText) findViewById(R.id.exercise_desc);
        exerciseDescCount = (TextView)findViewById(R.id.exercise_desccount);
        updateExerciseDescCount();
        distanceET = (EditText) findViewById(R.id.distance_text);
        stepsET = (EditText)findViewById(R.id.step_text);

        //other
        exerciseOption = (RelativeLayout)findViewById(R.id.exercise_otheroption);
        Resources res = getResources();
        exerciseOtherArray = res.getStringArray(R.array.otherexercise_array);
        selectOtherExercise = (TextView) findViewById(R.id.exercise_otheroption_text);
        exerciseOption.setVisibility(View.GONE);

        selectExercise();

        timepicker = (TimePicker) findViewById(R.id.TimePicker);
        timePickerContainer = (LinearLayout) findViewById(R.id.timepickercontainer);
        exerciseTimeContainer = ((RelativeLayout) findViewById(R.id.exercise_time_container));
        exerciseTime = (TextView)findViewById(R.id.exercise_time);
        done_picker = (Button) findViewById(R.id.date_save_tv);
        done_picker.setBackgroundDrawable(null);

        done_picker.setOnClickListener(this);
        exerciseTimeContainer.setOnClickListener(this);

        timepicker.setOnTimeChangedListener(this);
        setTimerPicker(false);

        durationSeekBar = (SeekBar)findViewById(R.id.seekBar_duration);
        durationTime = (TextView)findViewById(R.id.duration_minutes);
        processTimeDuration();

        step_labelSteps = (TextView)findViewById(R.id.step_labelSteps);
        step_labelSteps.setText(getString(R.string.STEPS));

        saveButton = (Button)findViewById(R.id.submitButton);
        saveButton.setOnClickListener(this);

        //getWorkout details
        if (getWorkoutController == null){
            Log.d(LOG_TAG, "New Exercise!");
            getWorkoutController = new GetWorkoutController(this.getApplicationContext(), this);
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null){
            workoutid = extra.getString("EXERCISE_ACTIVITY_ID");
            getWorkoutController.getWorkout(ApplicationEx.getInstance().userProfile.getRegID(),workoutid);

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

        if (myCurrentWorkout == null)
        {
            String time = AppUtil.getTimeOnly12();//get current time
            exerciseTime.setText(time);
            updateHeader(15,getResources().getString(R.string.LOG_EXERCISE_HEADER),this);
        }
        else
        {
            updateHeader(15,getResources().getString(R.string.EDIT_EXERCISE_HEADER),this);
        }

        if (myCurrentWorkout != null)
        {
            String exerciseTimeString = AppUtil.getExerciseTimeWith24Format(myCurrentWorkout.exercise_datetime);
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
            Date dTime = null;

            try {
                dTime = formatter.parse(exerciseTimeString);
            } catch (ParseException e) {
            }

            Calendar c = Calendar.getInstance();
            c.setTime(dTime);

            timepicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timepicker.setCurrentMinute(c.get(Calendar.MINUTE));
        }

        exerciseDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descRemainCount = MAX_DESC - s.length();
                updateExerciseDescCount ();
            }

        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        selectedWorkoutDate =sdf.format(ApplicationEx.getInstance().currentSelectedDate);

    }

    private void updateExerciseDescCount ()
    {
        exerciseDescCount.setText(descRemainCount + "/" + MAX_DESC);
    }

    private void processTimeDuration()
    {
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationTime.setText( " " + String.valueOf(progress*5)+ " mins");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void selectExercise ()
    {
        lldescription = (LinearLayout)findViewById(R.id.exercise_description);

        exerciseWalkButton = (ImageView)findViewById(R.id.exercise_walk_button);
        exerciseRunButton = (ImageView)findViewById(R.id.exercise_run_button);
        exerciseBikeButton = (ImageView)findViewById(R.id.exercise_bike_button);
        exerciseSwimButton = (ImageView)findViewById(R.id.exercise_swim_button);
        exerciseWorkOutButton = (ImageView)findViewById(R.id.exercise_workout_button);
        exerciseOtherButton = (ImageView)findViewById(R.id.exercise_other_button);

        exerciseWalkButton.setOnClickListener(this);
        exerciseRunButton.setOnClickListener(this);
        exerciseBikeButton.setOnClickListener(this);
        exerciseSwimButton.setOnClickListener(this);
        exerciseWorkOutButton.setOnClickListener(this);
        exerciseOtherButton.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        //walk
        if (v.getId()== R.id.exercise_walk_button){
            selectedExerciseType = Workout.EXERCISE_TYPE.WALKING;
            if (exerciseWalkButton.isSelected()) exerciseDeselect(exerciseWalkButton);
            else exerciseSelect(exerciseWalkButton);
        }
        //run
        else if (v.getId() == R.id.exercise_run_button){
            selectedExerciseType = Workout.EXERCISE_TYPE.RUNNING;
            if (exerciseRunButton.isSelected()) exerciseDeselect(exerciseRunButton);
            else exerciseSelect(exerciseRunButton);
        }
        //bike
        else if (v.getId()== R.id.exercise_bike_button) {
            selectedExerciseType = Workout.EXERCISE_TYPE.CYCLING;
            if (exerciseBikeButton.isSelected()) exerciseDeselect(exerciseBikeButton);
            else exerciseSelect(exerciseBikeButton);
        }
        //swim
        else if (v.getId()== R.id.exercise_swim_button){
            selectedExerciseType = Workout.EXERCISE_TYPE.SWIMMING;
            if (exerciseSwimButton.isSelected())exerciseDeselect(exerciseSwimButton);
            else exerciseSelect(exerciseSwimButton);
        }
        //workout
        else if (v.getId()== R.id.exercise_workout_button){
            selectedExerciseType = Workout.EXERCISE_TYPE.WORKOUT;
            if (exerciseWorkOutButton.isSelected())exerciseDeselect(exerciseWorkOutButton);
            else exerciseSelect(exerciseWorkOutButton);
        }
        //other
        else if (v.getId()== R.id.exercise_other_button){
            selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
            if (exerciseOtherButton.isSelected())exerciseDeselect(exerciseOtherButton);
            else exerciseSelect(exerciseOtherButton);
        }
        //datepicker
        else if (v.getId()== R.id.date_save_tv){
            setTimerPicker(false);
        }
        else if (v.getId() == R.id.exercise_time_container) {
            setTimerPicker(true);
        }
        //submit (Save) Button
        else if (v.getId() == R.id.submitButton) {
            Boolean isFormCompleted = isFormCompleted();
            if (isFormCompleted) postWorkout(v);
            else System.out.println("Invalid Form!");
        }

        else if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }

        else if(v.getId() == R.id.OtherButton)
        {
            if (dialog != null)
                dialog.dismiss();
        }
    }

    private void exerciseDeselect (ImageView buttonDeselect)
    {
        buttonDeselect.setSelected(false);
        exerciseOption.setVisibility(View.GONE);
        selectedExerciseType = null;
    }
    private void exerciseSelect (ImageView buttonSelect)
    {
        exerciseWalkButton.setSelected(false);
        exerciseRunButton.setSelected(false);
        exerciseBikeButton.setSelected(false);
        exerciseSwimButton.setSelected(false);
        exerciseWorkOutButton.setSelected(false);
        exerciseOtherButton.setSelected(false);
        exerciseOption.setVisibility(View.GONE);

        buttonSelect.setSelected(true);
        if (exerciseOtherButton.isSelected())
            exerciseOption.setVisibility(View.VISIBLE);
    }

    //other exercise option
    public void showOtherExerciseOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Other");
        builder.setItems(exerciseOtherArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //Log.d(LOG_TAG, exerciseOtherArray[item]);
                selectOtherExercise.setText(exerciseOtherArray[item]);
            }
        });
        Dialog genericDialog = builder.create();
        genericDialog.show();

    }

    //time picker
    private void setTimerPicker(Boolean isPickerShown)
    {
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

    private void setTimerPicker()
    {
        if (done_picker.getVisibility() == View.VISIBLE) { //hide
            setTimerPicker(false);

        } else { //show picker
            setTimerPicker(false);
        }
        //set time
    }
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
    {
        exerciseTime.setText(AppUtil.getExerciseTimeFromPicker(hourOfDay,minute));
    }

    /*Save button pressed*/
    private void postWorkout(View view)
    {
        showSavingLayout(true, false);

        if (postWorkoutController == null){
            postWorkoutController = new PostWorkoutController(this.getApplicationContext(), this);
        }
        Double stringToBeDouble =  new Double(distanceET.getText().toString());

        Workout myWorkout = new Workout();

        myWorkout.workout_desc = exerciseDescription.getText().toString();
        myWorkout.distance = stringToBeDouble * 1000 ;
        myWorkout.duration = Integer.valueOf(durationTime.getText().toString().replace(" mins","").replace(" ","")) ;
        myWorkout.steps = Integer.valueOf(stepsET.getText().toString());

        if (selectedExerciseType == Workout.EXERCISE_TYPE.OTHER)
        {
            //System.out.println("Workout.EXERCISE_TYPE.OTHER ");
            String answerSur = (String) (selectOtherExercise).getText().toString();
            indexType = Arrays.asList(exerciseOtherArray).indexOf(answerSur);
            //System.out.println("index Type: " + indexType);

            switch (indexType){
                case 0:selectedExerciseType = Workout.EXERCISE_TYPE.AQUAGYM; break;
                case 1:selectedExerciseType = Workout.EXERCISE_TYPE.MOUNTAIN_BIKING; break;
                case 2: selectedExerciseType = Workout.EXERCISE_TYPE.HIKING; break;
                case 3: selectedExerciseType = Workout.EXERCISE_TYPE.DOWNHILL_SKIING; break;
                case 4: selectedExerciseType = Workout.EXERCISE_TYPE.CROSSCOUNTRY_SKIING; break;
                case 5: selectedExerciseType = Workout.EXERCISE_TYPE.SNOWBOARDING; break;
                case 6: selectedExerciseType = Workout.EXERCISE_TYPE.SKATING; break;
                case 7: selectedExerciseType = Workout.EXERCISE_TYPE.ROWING; break;
                case 8: selectedExerciseType = Workout.EXERCISE_TYPE.ELLIPTICAL; break;
                case 9: selectedExerciseType = Workout.EXERCISE_TYPE.YOGA; break;
                case 10: selectedExerciseType = Workout.EXERCISE_TYPE.PILATES; break;
                case 11: selectedExerciseType = Workout.EXERCISE_TYPE.ZUMBA; break;
                case 12: selectedExerciseType = Workout.EXERCISE_TYPE.BARRE; break;
                case 13: selectedExerciseType = Workout.EXERCISE_TYPE.GROUP_WORKOUT; break;
                case 14: selectedExerciseType = Workout.EXERCISE_TYPE.DANCE; break;
                case 15: selectedExerciseType = Workout.EXERCISE_TYPE.BOOTCAMP; break;
                case 16: selectedExerciseType = Workout.EXERCISE_TYPE.BOXING; break;
                case 17: selectedExerciseType = Workout.EXERCISE_TYPE.MMA; break;
                case 18: selectedExerciseType = Workout.EXERCISE_TYPE.MEDITATION; break;
                case 19: selectedExerciseType = Workout.EXERCISE_TYPE.CORE_STRENGTHENING; break;
                case 20: selectedExerciseType = Workout.EXERCISE_TYPE.ARC_TRAINER; break;
                case 21: selectedExerciseType = Workout.EXERCISE_TYPE.STAIR_MASTER; break;
                case 22: selectedExerciseType = Workout.EXERCISE_TYPE.NORDIC_WALKING; break;
                case 23: selectedExerciseType = Workout.EXERCISE_TYPE.SPORTS; break;
                case 24: selectedExerciseType = Workout.EXERCISE_TYPE.GOLF; break;
                default:
                    selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
                    break;
            }
        }

        myWorkout.exercise_type = selectedExerciseType;

        if (selectedWorkoutDate.isEmpty()==true)
        {
            myWorkout.exercise_date = ApplicationEx.getInstance().currentSelectedDate.toString();
            myWorkout.exercise_datetime = ApplicationEx.getInstance().currentSelectedDate;
        }
        else {
            Date date = new Date();

            String dtStart = selectedWorkoutDate + " " + exerciseTime.getText();
            SimpleDateFormat gmtFormat;

            if (ApplicationEx.language.equalsIgnoreCase("fr"))
            {
                gmtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            else {
                gmtFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            }

            try {
                TimeZone gmtTime = TimeZone.getTimeZone("GMT");
                gmtFormat.setTimeZone(gmtTime);
                date = gmtFormat.parse(dtStart);
                myWorkout.exercise_date = selectedWorkoutDate;
                myWorkout.exercise_datetime = date;

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //added
        if (myCurrentWorkout == null){
            myWorkout.command = "added";
            postWorkoutController.postWorkout(ApplicationEx.getInstance().userProfile.getRegID(),myWorkout.command,myWorkout);
        }
        else {//updated
            myWorkout.activity_id = myCurrentWorkout.activity_id;
            myWorkout.coreData_id = myCurrentWorkout.coreData_id;
            myWorkout.command = "updated";
             /*Update first the DB before posting it to API*/
            DaoImplementer implDao = new DaoImplementer(new WorkoutDAO(this, null), this);
            implDao.update(myWorkout);

            /*Send the data to the API*/
            postWorkoutController.postWorkout(ApplicationEx.getInstance().userProfile.getRegID(),myWorkout.command,myWorkout);
        }
    }

    private boolean isFormCompleted ()
    {
        if (selectedExerciseType == null)
        {
            showCustomDialog();
            return false;
        }
        else if (selectedExerciseType == Workout.EXERCISE_TYPE.OTHER && selectOtherExercise.getText().toString().equalsIgnoreCase(getString(R.string.EXERCISE_OTHEROPTION)))
        {
            showCustomDialog();
            return false;
        }
        else{
            if (exerciseDescription.length() == 0) exerciseDescription.setText("");
            if (distanceET.length() == 0) distanceET.setText("0");
            if (stepsET.length() ==0) stepsET.setText("0");
        }
        return true;
    }

    private void showCustomDialog()
    {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, getResources().getString(R.string.ALERTMESSAGE_EXERCISETYPE), null, this);
        dialog.show();
    }

    private void updateUI()
    {
        exerciseDescription.setText(myCurrentWorkout.workout_desc);
        distanceET.setText(String.valueOf(myCurrentWorkout.distance));
        stepsET.setText(String.valueOf(myCurrentWorkout.steps));
        selectedWorkoutDate = myCurrentWorkout.exercise_date;

        exerciseTime.setText(AppUtil.getExerciseTime(myCurrentWorkout.exercise_datetime));
        durationTime.setText(" " + myCurrentWorkout.duration + " mins");
        durationSeekBar.setProgress(myCurrentWorkout.duration/5);
        processTimeDuration();

        switch (myCurrentWorkout.exercise_type) {
            case OTHER:
                selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
                exerciseOtherButton.setSelected(true);
                exerciseOption.setVisibility(View.GONE);
                selectOtherExercise.setText(AppUtil.getExerciseValue(this, myCurrentWorkout.exercise_type));
                break;
            case WALKING:
                selectedExerciseType = Workout.EXERCISE_TYPE.WALKING;
                exerciseSelect(exerciseWalkButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case CYCLING:
                selectedExerciseType = Workout.EXERCISE_TYPE.CYCLING;
                exerciseSelect(exerciseBikeButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case RUNNING:
                selectedExerciseType = Workout.EXERCISE_TYPE.RUNNING;
                exerciseSelect(exerciseRunButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case SWIMMING:
                selectedExerciseType = Workout.EXERCISE_TYPE.SWIMMING;
                exerciseSelect(exerciseSwimButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case WORKOUT:
                selectedExerciseType = Workout.EXERCISE_TYPE.WORKOUT;
                exerciseSelect(exerciseWorkOutButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            default:
                selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
                exerciseOtherButton.setSelected(true);
                exerciseOption.setVisibility(View.VISIBLE);
                selectOtherExercise.setText(AppUtil.getExerciseValue(this, myCurrentWorkout.exercise_type));
                break;
        }
    }

    /*
    WorkoutListner
    * */
    public void getWorkoutSuccess(Workout response) {
        Log.d(LOG_TAG, "SUCCESS!");
    }
    public void getWorkoutFailedWithError(MessageObj response){ Log.d(LOG_TAG, "FAILED!"); }

    /*
    * PostWorkOutListner
    * */
    public void postWorkoutSuccess(String response, Workout workoutObj)
    {
        Log.d(LOG_TAG, "SAVED SUCCESS!");

        Intent broadint = new Intent();
        broadint.setAction(this.getResources().getString(R.string.workoutListUpdate));
        this.sendBroadcast(broadint);

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                finish();
                showSavingLayout(false, false);
            }
        });
    }
    public void postWorkoutFailedWithError(MessageObj response, Workout workoutObj)
    {
        Log.d(LOG_TAG, "SAVED FAILED!");

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showSavingLayout(true, true);
            }
        });
    }

    private void showSavingLayout(boolean saving, boolean failed)
    {
        if (saving)
        {
            if (myCurrentWorkout == null)
            {
                updateHeader(15,getResources().getString(R.string.LOG_EXERCISE_HEADER),this);
            }
            else
            {
                updateHeader(15,getResources().getString(R.string.EDIT_EXERCISE_HEADER),this);
            }

            savingLayout.setVisibility(View.VISIBLE);
            addExerciseLayout.setEnabled(false);

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
            addExerciseLayout.setEnabled(true);
        }
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

        if (myCurrentWorkout == null)
        {
            updateHeader(15,getResources().getString(R.string.LOG_EXERCISE_HEADER),this);
        }
        else
        {
            updateHeader(15,getResources().getString(R.string.EDIT_EXERCISE_HEADER),this);
        }
    }

    public void retrySaving(View view) {
        showSavingLayout(true, false);

        postWorkout(view);
    }

    public void laterSaving(View view) {
        //go back to Meals Home Page
        showSavingLayout(false, false);
        finish();
    }
}
