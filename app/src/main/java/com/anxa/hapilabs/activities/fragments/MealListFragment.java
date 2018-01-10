package com.anxa.hapilabs.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.anxa.hapilabs.activities.StepsViewActivity;
import com.anxa.hapilabs.activities.WeightViewActivity;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.ui.adapters.WeightListAdapter;
import com.anxa.hapilabs.ui.adapters.WeightLogsListAdapter;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.ExerciseActivity;
import com.anxa.hapilabs.activities.ExerciseViewActivity;
import com.anxa.hapilabs.activities.HapiMomentViewActivity;
import com.anxa.hapilabs.activities.HapimomentAddActivity;
import com.anxa.hapilabs.activities.MainActivity;
import com.anxa.hapilabs.activities.WaterViewActivity;
import com.anxa.hapilabs.common.connection.listener.AddWaterListener;
import com.anxa.hapilabs.common.connection.listener.DailyMealChangeListener;
import com.anxa.hapilabs.common.connection.listener.DateChangeListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.common.util.ImageManager;
import com.anxa.hapilabs.controllers.water.AddWaterController;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.MEAL_TYPE;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Photo;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Workout;
import com.anxa.hapilabs.ui.CustomListView;
import com.anxa.hapilabs.ui.adapters.ExerciseListAdapter;
import com.anxa.hapilabs.ui.adapters.HapimomentListViewAdapter;
import com.anxa.hapilabs.ui.adapters.StepsListAdapter;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class MealListFragment extends ScrollView implements DateChangeListener, DailyMealChangeListener, AddWaterListener, ProgressChangeListener {
    Context context;

    List<Meal> items;
    List<HapiMoment> hapimomentItems;
    List<Workout> workoutItems;
    List<Steps> stepsItems;
    List<Weight> weightItems;
    Water waterEntry;

    LinearLayout llAMSnack;
    LinearLayout llBreakfast;
    LinearLayout llLunch;
    LinearLayout llPMSnack;
    LinearLayout llDinner;
    RelativeLayout llWater;
    LinearLayout llExercise;
    LinearLayout llExerciseWithData;
    LinearLayout llSteps;
    LinearLayout llStepsGFitEmpty;
    LinearLayout llWeight;

    private ListView workoutListView;
    private ListView stepsListView;
    private ListView weightListView;
    private LinearLayout isCheckedExercise;
    private LinearLayout isCheckedWater;
    private LinearLayout isCheckedWeight;
    private TextView exerciseChecked;
    LinearLayout llHapimoment;

    RelativeLayout rlWater;
    ImageView glass_iv;
    TextView numberOfGlasses_tv;
//
//    TextView stepsData_tv;
//    TextView stepsData_activity_tv;

    OnClickListener listener;
    LayoutInflater layoutInflater;
    String inflater = LAYOUT_INFLATER_SERVICE;

    DateChangeListener DateListener;

    ImageView exerciseImageView;
    ImageView hapiImageView;

    RelativeLayout hapimomentListContainer;
    RelativeLayout addHapimomentPhotoContainer;

    public MealListFragment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);

        onCreateView(null);

    }

    public MealListFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);

        this.context = context;
        onCreateView(null);
    }

    public MealListFragment(Context context) {
        super(context);

        this.context = context;

        onCreateView(null);
    }

    public void setDateListener(DateChangeListener DateListener) {
        this.DateListener = DateListener;
    }

    public void onResume() {

    }

    public boolean initDate(List<Meal> items, Context context,
                            OnClickListener listener) {

        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.items = items;
        this.context = context;
        this.listener = listener;

        return true;
    }

    public void onCreateView(ViewGroup container) {
        View row = null;

        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(
                    R.layout.mymealslist, null, false);
        }

        llAMSnack = (LinearLayout) row.findViewById(R.id.mealitem_amsnack);
        ((TextView) llAMSnack.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, MEAL_TYPE.AM_SNACK));
        ((TextView) llAMSnack.findViewById(R.id.mealtime)).setText("");

        llBreakfast = (LinearLayout) row.findViewById(R.id.mealitem_breakfast);
        ((TextView) llBreakfast.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, MEAL_TYPE.BREAKFAST));
        ((TextView) llBreakfast.findViewById(R.id.mealtime)).setText("");

        llLunch = (LinearLayout) row.findViewById(R.id.mealitem_lunch);
        ((TextView) llLunch.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, MEAL_TYPE.LUNCH));
        ((TextView) llLunch.findViewById(R.id.mealtime)).setText("");

        llPMSnack = (LinearLayout) row.findViewById(R.id.mealitem_pmsnack);
        ((TextView) llPMSnack.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, MEAL_TYPE.PM_SNACK));
        ((TextView) llPMSnack.findViewById(R.id.mealtime)).setText("");

        llDinner = (LinearLayout) row.findViewById(R.id.mealitem_dinner);
        ((TextView) llDinner.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, MEAL_TYPE.DINNER));
        ((TextView) llDinner.findViewById(R.id.mealtime)).setText("");

        llWater = (RelativeLayout) row.findViewById(R.id.mealitem_water);

        llHapimoment = (LinearLayout) row.findViewById(R.id.mealitem_hapimoment);
        llExercise = (LinearLayout) row.findViewById(R.id.mealitem_exercise);
        exerciseImageView = (ImageView) llExercise.findViewById(R.id.exercisephoto);
        llExerciseWithData = (LinearLayout) row.findViewById(R.id.mealitem_exercisedata);

        workoutListView = (ListView) llExerciseWithData.findViewById(R.id.exerciseListView);
        isCheckedExercise = (LinearLayout) llExerciseWithData.findViewById(R.id.exerciseView_checked);

        exerciseChecked = (TextView) llExerciseWithData.findViewById(R.id.checked);
        exerciseChecked.setText(context.getString(R.string.COACH_CHECKED));

        //water
        rlWater = (RelativeLayout) llWater.findViewById(R.id.waterCell_rl);
        isCheckedWater = (LinearLayout) llWater.findViewById(R.id.water_approvedIconContainer);
        rlWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(v);
            }
        });

        llSteps = (LinearLayout) row.findViewById(R.id.mealitem_stepsdata);
        llWeight = (LinearLayout) row.findViewById(R.id.mealitem_weight);
        stepsListView = (ListView) llSteps.findViewById(R.id.stepsListView);
        weightListView = (ListView) llWeight.findViewById(R.id.weightListView);
        isCheckedWeight = (LinearLayout) llWeight.findViewById(R.id.weightView_checked);

        llStepsGFitEmpty = (LinearLayout) row.findViewById(R.id.mealitem_stepsdata_gfit_empty);

//        stepsData_tv = (TextView) row.findViewById(R.id.steps_selectedBody);
//        stepsData_tv.setText("");
//        stepsData_activity_tv = (TextView) llSteps.findViewById(R.id.steps_displayTitle);
//        stepsData_activity_tv.setText("");

        glass_iv = (ImageView) llWater.findViewById(R.id.glass_iv);
        numberOfGlasses_tv = (TextView) llWater.findViewById(R.id.glasses_water);
        numberOfGlasses_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(v);
            }
        });

        hapiImageView = (ImageView) llHapimoment.findViewById(R.id.addHapimomentPhoto);
        hapiImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        try {
            removeView(row);
        } catch (Exception e) {
        }
        addView(row);
        hapiImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewHapimoment(v);
            }
        });
        hapimomentListContainer = ((RelativeLayout) llHapimoment.findViewById(R.id.hapimomentListContainer));
        addHapimomentPhotoContainer = ((RelativeLayout) llHapimoment.findViewById(R.id.addHapimomentPhotoContainer));
        exerciseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ExerciseActivity.class);
                v.getContext().startActivity(i);
            }
        });
    }

    private void addNewHapimoment(View v) {
        Intent mainIntent = new Intent(v.getContext(), HapimomentAddActivity.class);
        mainIntent.putExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_ADD);
        v.getContext().startActivity(mainIntent);
    }

    private void viewHapimoment(View v, HapiMoment hapiMoment) {
        ApplicationEx.getInstance().currentHapiMoment = hapiMoment;
        Intent mainIntent = new Intent(v.getContext(), HapiMomentViewActivity.class);
        mainIntent.putExtra("HAPIMOMENT_STATE_VIEW", HapiMoment.HAPIMOMENTSTATE_VIEW);
        v.getContext().startActivity(mainIntent);
    }

    private void addWater(View v) {
        Intent mainIntent = new Intent(v.getContext(), WaterViewActivity.class);
        mainIntent.putExtra("WATER_STATUS", "add");
        v.getContext().startActivity(mainIntent);
    }

    private void viewWater(View v, Water water) {
        ApplicationEx.getInstance().currentWater = water;
        Intent mainIntent = new Intent(v.getContext(), WaterViewActivity.class);
        mainIntent.putExtra("WATER_STATUS", "view");
        v.getContext().startActivity(mainIntent);
    }

    @Override
    public void dateChange(Date date, int weekIndex) {
        // TODO Auto-generated method stub
    }

    private void updateMealItem(LinearLayout layout, MEAL_TYPE mealType) {

        // set clickable on meal info;
        ((ImageView) layout.findViewById(R.id.mealinfo)).setOnClickListener(listener);
        ((ImageView) layout.findViewById(R.id.mealinfo)).setTag(mealType);
        ((TextView) layout.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, mealType));
        ((TextView) layout.findViewById(R.id.mealtime)).setVisibility(View.INVISIBLE);

        ImageView photoMain = ((ImageView) layout.findViewById(R.id.mealphoto));
        photoMain.setOnClickListener(listener);
        photoMain.setTag(mealType.ordinal() + 1);
        photoMain.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoMain.setImageResource(R.drawable.meal_addameal);
        ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.mealdesc)).setVisibility(View.GONE);
        ((LinearLayout) layout.findViewById(R.id.icon_container)).setVisibility(View.GONE);
        ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.GONE);
    }

    private void updateMealItem(Meal meal, LinearLayout layout) {

        layout.setTag(meal);

        // set clickacble on meal info;
        ((ImageView) layout.findViewById(R.id.mealinfo)).setOnClickListener(listener);
        ((ImageView) layout.findViewById(R.id.mealinfo)).setTag(meal.meal_type);


            ((TextView) layout.findViewById(R.id.mealtime)).setVisibility(View.VISIBLE);

        ////update meal_creation_date = date of the meal(date tab)
        ((TextView) layout.findViewById(R.id.mealtime)).setText(AppUtil.getMealTime(meal.meal_creation_date));

        ImageView photoMain = ((ImageView) layout.findViewById(R.id.mealphoto));
        photoMain.setOnClickListener(listener);
        photoMain.setTag(meal);
        photoMain.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        ImageView photoCount = ((ImageView) layout.findViewById(R.id.mealphotoCount));


        if (meal.photos != null && meal.photos.size() > 0) {
            // photoMain.setTag(1);


            if (meal.photos.size() > 0)
                updatePhotoMain(meal.photos.get(0), photoMain, meal.meal_type, meal.isHapiForkMeal);

            if (meal.photos.size() > 1) {
                ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.VISIBLE);

                if (meal.photos.size() >= 2) {
                    ImageView photoThumb1 = ((ImageView) layout.findViewById(R.id.mealphoto1));
                    photoThumb1.setOnClickListener(listener);
                    photoThumb1.setTag(meal);
                    updatePhotoThumb(meal.photos.get(0), photoThumb1, meal.meal_type);

                    ImageView photoThumb2 = ((ImageView) layout.findViewById(R.id.mealphoto2));
                    photoThumb2.setOnClickListener(listener);
                    photoThumb2.setTag(meal);
                    updatePhotoThumb(meal.photos.get(1), photoThumb2, meal.meal_type);

                }
                if (meal.photos.size() >= 3) {
                    ImageView photoThumb3 = ((ImageView) layout.findViewById(R.id.mealphoto3));
                    photoThumb3.setOnClickListener(listener);
                    photoThumb3.setTag(meal);
                    updatePhotoThumb(meal.photos.get(2), photoThumb3, meal.meal_type);

                }
                if (meal.photos.size() >= 4) {
                    ImageView photoThumb4 = ((ImageView) layout.findViewById(R.id.mealphoto4));
                    photoThumb4.setOnClickListener(listener);
                    photoThumb4.setTag(meal);
                    updatePhotoThumb(meal.photos.get(3), photoThumb4, meal.meal_type);

                }
                if (meal.photos.size() >= 5) {
                    ImageView photoThumb5 = ((ImageView) layout.findViewById(R.id.mealphoto5));
                    photoThumb5.setOnClickListener(listener);
                    photoThumb5.setTag(meal);
                    updatePhotoThumb(meal.photos.get(4), photoThumb5, meal.meal_type);

                }

            }

        } else {
            updatePhotoMain(null, photoMain, meal.meal_type, meal.isHapiForkMeal);
            ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.GONE);
        }

        ((LinearLayout) layout.findViewById(R.id.icon_container)).setVisibility(View.VISIBLE);


        // approved & commented
        if (meal.isApproved) {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.approved_text))
                    .setVisibility(View.VISIBLE);
            ((ImageView) layout.findViewById(R.id.approved_icon))
                    .setVisibility(View.VISIBLE);
            // add spacer too
            ((View) layout.findViewById(R.id.spacer))
                    .setVisibility(View.VISIBLE);

        } else {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.GONE);
            ((TextView) layout.findViewById(R.id.approved_text))
                    .setVisibility(View.GONE);
            ((ImageView) layout.findViewById(R.id.approved_icon))
                    .setVisibility(View.GONE);
            // remove spacer too
            ((View) layout.findViewById(R.id.spacer))
                    .setVisibility(View.GONE);
        }

        if (meal.isCommented) {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.comment_text))
                    .setVisibility(View.VISIBLE);
            ((ImageView) layout.findViewById(R.id.comment_icon))
                    .setVisibility(View.VISIBLE);
        } else {
            ((TextView) layout.findViewById(R.id.comment_text))
                    .setVisibility(View.GONE);
            ((ImageView) layout.findViewById(R.id.comment_icon))
                    .setVisibility(View.GONE);
        }


        if (meal.coachRating > 0) {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((RatingBar) layout.findViewById(R.id.mealList_ratingBar))
                    .setVisibility(View.VISIBLE);
            ((RatingBar) layout.findViewById(R.id.mealList_ratingBar))
                    .setRating(Math.round(meal.coachRating));

            // add spacer too
            ((View) layout.findViewById(R.id.spacer))
                    .setVisibility(View.VISIBLE);


        } else {
            ((RatingBar) layout.findViewById(R.id.mealList_ratingBar))
                    .setVisibility(View.GONE);

            // remove spacer too
            ((View) layout.findViewById(R.id.spacer))
                    .setVisibility(View.GONE);
        }


        // text Description
        if (meal.meal_description != null && meal.meal_description.length() > 0) {
            ((TextView) layout.findViewById(R.id.mealdesc))
                    .setVisibility(View.VISIBLE);

            ((TextView) layout.findViewById(R.id.mealdesc))
                    .setText(meal.meal_description);

        } else {
            ((TextView) layout.findViewById(R.id.mealdesc))
                    .setVisibility(View.GONE);
        }

        try {
            //check meal status
            if (meal.meal_status.equalsIgnoreCase("FAILED")) {
                ((LinearLayout) layout.findViewById(R.id.meal_uploadresume)).setVisibility(View.GONE);

                // show meal error display
                ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.VISIBLE);
                ((ImageView) layout.findViewById(R.id.meal_uploadfailed_delete)).setOnClickListener(listener);
                ((ImageView) layout.findViewById(R.id.meal_uploadfailed_delete)).setTag(meal.meal_id);

                ((ImageView) layout.findViewById(R.id.meal_uploadfailed_refresh)).setOnClickListener(listener);
                ((ImageView) layout.findViewById(R.id.meal_uploadfailed_refresh)).setTag(meal.meal_id);

            } else if (meal.meal_status.equalsIgnoreCase("RESUME_ONGOING")) {

                //show progressbar
                ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.GONE);
                ((LinearLayout) layout.findViewById(R.id.meal_uploadresume)).setVisibility(View.VISIBLE);

                ((ProgressBar) layout.findViewById(R.id.resumeProgressBar)).getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.text_orange), PorterDuff.Mode.SRC_IN);

                ((ProgressBar) layout.findViewById(R.id.resumeProgressBar)).setVisibility(View.VISIBLE);

            } else {
                ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.GONE);
                ((LinearLayout) layout.findViewById(R.id.meal_uploadresume)).setVisibility(View.GONE);
            }
        } catch (NullPointerException exception) {
            exception.printStackTrace();

            ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.GONE);
            ((LinearLayout) layout.findViewById(R.id.meal_uploadresume)).setVisibility(View.GONE);
        }

        if(!meal.isHapiForkMeal) {
            ((TextView) layout.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, meal.meal_type));
        }else {
            ((TextView) layout.findViewById(R.id.mealtitle)).setText(meal.meal_description);
            ((TextView) layout.findViewById(R.id.mealtime)).setVisibility(GONE);
            ((TextView) layout.findViewById(R.id.mealdesc)).setVisibility(GONE);
        }
    }

    private void updateWaterItem(int progress, boolean isChecked, RelativeLayout layout) {

        ((TextView) llWater.findViewById(R.id.mealtitle)).setText(getResources().getString(R.string.MEALTYPE_WATER));
        if (isChecked){
            isCheckedWater.setVisibility(View.VISIBLE);
        }else {
            isCheckedWater.setVisibility(View.GONE);
        }
        //set amount of water
        updateWaterThumb(progress);
    }

    @Override
    public boolean refreshUI() {

        /** Water Item Cell**/
        waterEntry = new Water();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (int i = 0; i < ApplicationEx.getInstance().waterList.size(); i++) {
            Date waterDate = ApplicationEx.getInstance().waterList.get(i).getWater_datetime();

            if (fmt.format(ApplicationEx.getInstance().currentSelectedDate).equals(fmt.format(waterDate))) {
                waterEntry = ApplicationEx.getInstance().waterList.get(i);
                break;
            }
        }
        if (waterEntry.getNumber_glasses() != null) {
            try {
                ApplicationEx.getInstance().currentWater = waterEntry;
                updateWaterItem(Integer.parseInt(waterEntry.getNumber_glasses()), waterEntry.isChecked, llWater);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            updateWaterItem(0, false, llWater);
        }

        /** Meal Item Cell**/

        // clean up meals again
        updateMealItem(llBreakfast, MEAL_TYPE.BREAKFAST);

        updateMealItem(llAMSnack, MEAL_TYPE.AM_SNACK);

        updateMealItem(llLunch, MEAL_TYPE.LUNCH);

        updateMealItem(llPMSnack, MEAL_TYPE.PM_SNACK);

        updateMealItem(llDinner, MEAL_TYPE.DINNER);

        /** HAPImoment Item Cell**/
        final CustomListView hapimomentListView = (CustomListView) hapimomentListContainer.findViewById(R.id.hapiMomentList);
        hapimomentListView.setItemsCanFocus(false);
        hapimomentListView.setDivider(null);
        hapimomentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                HapiMoment hapiMoment = (HapiMoment) hapimomentListView.getAdapter().getItem(position);
                viewHapimoment(view, hapiMoment);
            }
        });

        if (hapimomentItems != null && hapimomentItems.size() > 0) {
            HapimomentListViewAdapter adapter = new HapimomentListViewAdapter(context, hapimomentItems);
            hapimomentListView.setAdapter(adapter);
            hapimomentListView.setVisibility(VISIBLE);
            addHapimomentPhotoContainer.setVisibility(GONE);
        } else {
            hapimomentListView.setVisibility(GONE);
            addHapimomentPhotoContainer.setVisibility(VISIBLE);
        }

        if (items == null)
            return false;

        for (int i = 0; i < items.size(); i++) {
            Meal currentMeal = items.get(i);
            if (currentMeal == null)
                return false;

            switch (currentMeal.meal_type) {
                case BREAKFAST:
                    updateMealItem(currentMeal, llBreakfast);
                    break;
                case AM_SNACK:
                    updateMealItem(currentMeal, llAMSnack);
                    break;
                case LUNCH:
                    updateMealItem(currentMeal, llLunch);
                    break;
                case PM_SNACK:
                    updateMealItem(currentMeal, llPMSnack);
                    break;
                case DINNER:
                    updateMealItem(currentMeal, llDinner);
                    break;
                default:
                    break;

            }
        }
        return true;
    }

    public static boolean isAllNull(Iterable<?> list) {
        for (Object obj : list) {
            if (obj != null)
                return false;
        }

        return true;
    }

    public static boolean isCheckedExercise(List<Workout> workoutItemsList) {
        for (Workout workoutObj : workoutItemsList) {
            if (workoutObj.isChecked) {
                return true;
            }
        }

        return false;
    }
    public static boolean isCheckedWeight(List<Weight> weightItemsList) {
        for (Weight weightObj : weightItemsList) {
            if (weightObj.isChecked) {
                return true;
            }
        }

        return false;
    }
    public void refreshWeightUI()
    {
        if (weightItems.size() > 0 && weightItems != null) {
            llWeight.setVisibility(LinearLayout.VISIBLE);

            sortWeight(weightItems);

            List<Weight> weightList = new ArrayList<Weight>();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            if(weightItems.size() > 0) {
                //today
                if (fmt.format(ApplicationEx.getInstance().currentSelectedDate).equals(fmt.format(new Date()))) {
                    weightList.add(weightItems.get(weightItems.size() - 1));
                }else
                {
                    weightList.add(weightItems.get(0));
                }

            }
            //sort(weightItems);
            WeightListAdapter weightAdapter = new WeightListAdapter(context, weightList);
            weightAdapter.notifyDataSetChanged();

            weightListView.invalidateViews();
            weightListView.refreshDrawableState();

            weightListView.setAdapter(weightAdapter);

            setListViewHeightBasedOnChildren(weightListView);

            if (isCheckedWeight(weightItems)) {
                isCheckedWeight.setVisibility(LinearLayout.VISIBLE);
            } else {
                isCheckedWeight.setVisibility(LinearLayout.GONE);
            }

            weightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {
                    Weight weight = (Weight) weightListView.getAdapter().getItem(position);


                        ApplicationEx.getInstance().currentWeightView = weight;

                        Intent mainIntent = new Intent(context, WeightViewActivity.class);
                        mainIntent.putExtra("FROM_NOTIF", false);
                        mainIntent.putExtra("FROM_NOTIF_COMMUNITY", false);
                        mainIntent.putExtra("FROM_NOTIF_3RD_PARTY", false);
                        context.startActivity(mainIntent);

                }
            });
        } else {
            llWeight.setVisibility(LinearLayout.GONE);
        }
    }

    public void refreshExerciseUI() {
        if (workoutItems != null && workoutItems.size() > 0) {
            List<Workout> exerciseItems = workoutItems;
            stepsItems = new ArrayList<>();

            //check if steps exist
            boolean stepsGFitExists = false;

            for (Workout workout : new ArrayList<Workout>(workoutItems)) {

                if (workout.exercise_type == Workout.EXERCISE_TYPE.ACTIVITY_IOS) {
                    llSteps.setVisibility(LinearLayout.VISIBLE);

                    Steps stepsData = new Steps();
                    stepsData.steps_count = Integer.toString(workout.steps);
                    stepsData.start_datetime = workout.exercise_datetime;
                    stepsData.activity_id = workout.activity_id;
                    stepsData.steps_distance = workout.distance;
                    stepsData.device_name = workout.device_name;
                    stepsData.steps_calories = workout.calories;
                    stepsData.steps_duration = workout.duration;

//                    //check the date, if current day.
//                    if (AppUtil.getDateinString(stepsData.start_datetime).equalsIgnoreCase(AppUtil.getDateinString(AppUtil.getCurrentDateinDate()))) {
//                        System.out.println("refreshExerciseUI same date");
//                        if (stepsData.device_name.equalsIgnoreCase("Google Fit")) {
//                            if (stepsData.activity_id.equalsIgnoreCase("1234567")) {
//                                stepsItems.add(stepsData);
//                                stepsGFitExists = true;
//                            }
//                        } else {
//                            stepsItems.add(stepsData);
//                        }
//                    } else {
                    stepsItems.add(stepsData);
//                    }

                    exerciseItems.remove(workout);

                    System.out.println("refreshExerciseUI stepsdata: " + AppUtil.getDateinString(stepsData.start_datetime));
                    System.out.println("refreshExerciseUI stepsdata id: " + stepsData.activity_id);
                    System.out.println("refreshExerciseUI currentDate: " + AppUtil.getDateinString(AppUtil.getCurrentDateinDate()));
                    System.out.println("refreshExerciseUI steps count: " + stepsData.steps_count);

                }
            }

            llSteps.findViewById(R.id.stepsinfo).setVisibility(View.VISIBLE);
            llSteps.findViewById(R.id.stepsinfo).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    LayoutInflater layoutInflater
                            = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    final View popupView = layoutInflater.inflate(R.layout.manage_settings_popup, null);
                    final PopupWindow popupWindow = new PopupWindow(
                            popupView,
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT);

                    Button btnManage = (Button) popupView.findViewById(R.id.manage_settings_button);
                    btnManage.setOnClickListener(new Button.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                            Intent broadint = new Intent();
                            broadint.setAction(context.getString(R.string.googlefit_webview));
                            context.sendBroadcast(broadint);
                        }
                    });
                    popupWindow.showAsDropDown(llSteps.findViewById(R.id.stepsinfo), 50, -30);
                }
            });

            if (stepsGFitExists) {
                System.out.println("refreshExerciseUI stepsGFitExists: ");

                if (ApplicationEx.getInstance().isGoogleFitAllowed(context)) {
                    System.out.println("refreshExerciseUI stepsGFitExists: googleFitAllowed");

                    llStepsGFitEmpty.setVisibility(View.GONE);
                } else {

                    System.out.println("refreshExerciseUI stepsGFitExists: googleFitNotAllowed");

                    llStepsGFitEmpty.setVisibility(View.VISIBLE);
                    llStepsGFitEmpty.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent broadint = new Intent();
                            broadint.setAction(context.getString(R.string.googlefit_request));
                            context.sendBroadcast(broadint);
                        }
                    });
                }


            } else {
                if (!ApplicationEx.getInstance().isGoogleFitAllowed(context)) {
                    System.out.println("refreshExerciseUI !stepsGFitExists: !googleFitAllowed");

                    llStepsGFitEmpty.setVisibility(View.VISIBLE);

                    if (stepsItems.size() > 0) {
                        llStepsGFitEmpty.findViewById(R.id.steps_gFit_empty_displayTitle).setVisibility(View.GONE);
                        llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty).setVisibility(View.GONE);

                    } else {
                        llStepsGFitEmpty.findViewById(R.id.steps_gFit_empty_displayTitle).setVisibility(View.VISIBLE);
                        llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty).setVisibility(View.VISIBLE);
                        llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LayoutInflater layoutInflater
                                        = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                                final View popupView = layoutInflater.inflate(R.layout.manage_settings_popup, null);
                                final PopupWindow popupWindow = new PopupWindow(
                                        popupView,
                                        LayoutParams.WRAP_CONTENT,
                                        LayoutParams.WRAP_CONTENT);

                                Button btnManage = (Button) popupView.findViewById(R.id.manage_settings_button);
                                btnManage.setOnClickListener(new Button.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        // TODO Auto-generated method stub
                                        popupWindow.dismiss();
                                        Intent broadint = new Intent();
                                        broadint.setAction(context.getString(R.string.googlefit_webview));
                                        context.sendBroadcast(broadint);
                                    }
                                });
                                popupWindow.showAsDropDown(llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty), 50, -30);
                            }
                        });
                    }

                    llStepsGFitEmpty.setVisibility(View.VISIBLE);
                    llStepsGFitEmpty.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent broadint = new Intent();
                            broadint.setAction(context.getString(R.string.googlefit_request));
                            context.sendBroadcast(broadint);
                        }
                    });


                } else {
                    llStepsGFitEmpty.setVisibility(View.GONE);
                    System.out.println("refreshExerciseUI !stepsGFitExists: googleFitAllowed");


                }
            }

            //if stepslist is not empty
            if (stepsItems.size() > 0 && stepsItems != null) {
                llSteps.setVisibility(LinearLayout.VISIBLE);

                StepsListAdapter stepsListAdapter = new StepsListAdapter(context, stepsItems);
                stepsListAdapter.notifyDataSetChanged();

                stepsListView.invalidateViews();
                stepsListView.refreshDrawableState();
                stepsListView.setAdapter(stepsListAdapter);
                setListViewHeightBasedOnChildren(stepsListView);

                stepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Steps stepsObj = (Steps) stepsListView.getAdapter().getItem(position);
                        ApplicationEx.getInstance().currentStepsView = stepsObj;

                        Intent mainIntent = new Intent(context, StepsViewActivity.class);
                        mainIntent.putExtra("EXERCISE_ACTIVITY_ID", stepsObj.activity_id);
                        context.startActivity(mainIntent);
                    }
                });


            } else {
                llSteps.setVisibility(LinearLayout.GONE);
            }

            //if exerciselist is not empty
            if (exerciseItems.size() > 0 && exerciseItems != null) {
                llExercise.setVisibility(LinearLayout.GONE);

                llExerciseWithData.setVisibility(LinearLayout.VISIBLE);
                sort(exerciseItems);

                ExerciseListAdapter exerciseAdapter = new ExerciseListAdapter(context, exerciseItems);
                exerciseAdapter.notifyDataSetChanged();

                workoutListView.invalidateViews();
                workoutListView.refreshDrawableState();

                workoutListView.setAdapter(exerciseAdapter);

                setListViewHeightBasedOnChildren(workoutListView);

                if (isCheckedExercise(exerciseItems)) {
                    isCheckedExercise.setVisibility(LinearLayout.VISIBLE);
                } else {
                    isCheckedExercise.setVisibility(LinearLayout.GONE);
                }

                workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        Workout workoutObj = (Workout) workoutListView.getAdapter().getItem(position);

                        if (workoutObj.device_name == "null") {
                            ApplicationEx.getInstance().currentWorkoutView = workoutObj;

                            Intent mainIntent = new Intent(context, ExerciseViewActivity.class);
                            mainIntent.putExtra("EXERCISE_ACTIVITY_ID", workoutObj.activity_id);
                            context.startActivity(mainIntent);
                        }
                    }
                });
            } else {
                llExercise.setVisibility(LinearLayout.VISIBLE);
                llExerciseWithData.setVisibility(LinearLayout.GONE);
            }
        } else {

            System.out.println("*** null workoutitems ");
            llExercise.setVisibility(LinearLayout.VISIBLE);
            llExerciseWithData.setVisibility(LinearLayout.GONE);
            llSteps.setVisibility(LinearLayout.GONE);

            if (!ApplicationEx.getInstance().isGoogleFitAllowed(context)) {
                llStepsGFitEmpty.setVisibility(View.VISIBLE);
                llStepsGFitEmpty.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent broadint = new Intent();
                        broadint.setAction(context.getString(R.string.googlefit_request));
                        context.sendBroadcast(broadint);
                    }
                });

                llStepsGFitEmpty.findViewById(R.id.steps_gFit_empty_displayTitle).setVisibility(View.VISIBLE);
                llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty).setVisibility(View.VISIBLE);
                llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LayoutInflater layoutInflater
                                = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                        final View popupView = layoutInflater.inflate(R.layout.manage_settings_popup, null);
                        final PopupWindow popupWindow = new PopupWindow(
                                popupView,
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);

                        Button btnManage = (Button) popupView.findViewById(R.id.manage_settings_button);
                        btnManage.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                popupWindow.dismiss();
                                Intent broadint = new Intent();
                                broadint.setAction(context.getString(R.string.googlefit_webview));
                                context.sendBroadcast(broadint);
                            }
                        });
                        popupWindow.showAsDropDown(llStepsGFitEmpty.findViewById(R.id.stepsinfo_empty), 50, -30);
                    }
                });
            } else {
                llStepsGFitEmpty.setVisibility(View.GONE);
            }

        }
    }

    /* Sort workout */
    public void sort(final List<Workout> workoutObj) {
        if (workoutObj != null && workoutObj.size() > 0) {
            Collections.sort(workoutObj, new Comparator<Workout>() {
                public int compare(Workout workout1, Workout workout2) {
                    return ((Workout) workout1).exercise_datetime.compareTo(((Workout) workout2).exercise_datetime);
                }
            });
        }
    }

    public void sortWeight(final List<Weight> weightObj) {
        if (weightObj != null && weightObj.size() > 0) {
            Collections.sort(weightObj, new Comparator<Weight>() {
                public int compare(Weight weight1, Weight weight2) {
                    return ((Weight) weight1).start_datetime.compareTo(((Weight) weight2).start_datetime);
                }
            });
        }
    }

    /****
     * Method for Setting the Height of the ListView dynamically.
     * *** Hack to fix the issue of not showing all the items of the ListView
     * *** when placed inside a ScrollView
     ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    public ImageView updatePhotoMain(Photo photo, ImageView item, MEAL_TYPE type, boolean isHapiforkMeal ) {

//        if (photo != null && photo.image != null)
//            item.setImageBitmap(photo.image);
//        else if (photo != null) {
//            // try getting on the ImageManager
//            Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
//            if (bmp == null)
//                item.setImageResource(AppUtil.getPhotoResource(type)); // use
//                // dummy
//            else
//                item.setImageBitmap(bmp);
//        } else
//            item.setImageResource(AppUtil.getPhotoResource(type)); // use
//
//        return item;

        if (photo != null) {
            // try getting on the ImageManager
            System.out.println("updatePhotoMain:  " + photo.photo_id);
            Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
            if (bmp == null) {
                new DownloadImageTask(item, Integer.parseInt(photo.photo_id)).execute(photo.photo_url_large);
            } else
                item.setImageBitmap(bmp);
        } else {
            if(isHapiforkMeal)
            {
                item.setImageResource(R.drawable.meal_hapifork_default);
            }else{
                item.setImageResource(AppUtil.getPhotoResource(type));
            }

        }

        return item;
    }

    public ImageView updatePhotoThumb(Photo photo, ImageView item, MEAL_TYPE type) {
        System.out.println("updatePhotoThumb:  " + photo.photo_id + " url: " + photo.photo_url_large);
//        if (photo != null && photo.image != null)
//            item.setImageBitmap(photo.image);
//        else if (photo != null) {
//            // try getting on the ImageManager
//            Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
//            if (bmp == null)
//                item.setImageResource(AppUtil.getPhotoResource(type)); // use
//                // dummy
//            else
//                item.setImageBitmap(bmp);
//        } else
//            item.setImageResource(AppUtil.getPhotoResource(type)); // use
//
//        return item;

        if (photo != null) {

            // try getting on the ImageManager
            Bitmap bmp = ImageManager.getInstance().findImage(photo.photo_id);
            if (bmp == null) {
                try {
                    new DownloadImageTask(item, Integer.parseInt(photo.photo_id)).execute(photo.photo_url_large);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else
                item.setImageBitmap(bmp);
        } else
            item.setImageResource(AppUtil.getPhotoResource(type)); // use

        return item;
    }


    @Override
    public boolean updateData(List<Meal> items) {
        // TODO Auto-generated method stub
        this.items = items;
        refreshUI();
        return false;
    }

    public boolean updateData(List<Meal> items, List<HapiMoment> hapimomentItems) {
        // TODO Auto-generated method stub
        this.items = items;
        this.hapimomentItems = hapimomentItems;
        refreshUI();
        return false;
    }

    public void updateExerciseData(List<Workout> workoutItems) {
        // TODO Auto-generated method stub
        this.workoutItems = workoutItems;
        refreshExerciseUI();
    }

    public void updateWeightData(List<Weight> weightItems)
    {
        this.weightItems = weightItems;
        refreshWeightUI();
    }

    private void updateWaterThumb(int waterProgress) {

        if (waterProgress == 0) {
            glass_iv.setImageDrawable(getResources().getDrawable(R.drawable.water_glass_gray));
            numberOfGlasses_tv.setText(getResources().getString(R.string.WATER_JOURNAL_EMPTY));
            numberOfGlasses_tv.setTextColor(getResources().getColor(R.color.text_darkgray));
            numberOfGlasses_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWater(v);
                }
            });

            rlWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWater(v);
                }
            });

        } else {
            glass_iv.setImageDrawable(getResources().getDrawable(R.drawable.water_glass_blue));
            String glassesString = getResources().getString(R.string.WATER_JOURNAL_NOT_EMPTY);
            glassesString = glassesString.replace("%@", Integer.toString(waterProgress));
            numberOfGlasses_tv.setText(glassesString);
            numberOfGlasses_tv.setTextColor(getResources().getColor(R.color.water_tint));
            numberOfGlasses_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewWater(v, ApplicationEx.getInstance().currentWater);
                }
            });

            rlWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewWater(v, ApplicationEx.getInstance().currentWater);
                }
            });

        }
    }


    /**
     * ProgressChangeListener
     **/
    public void startProgress() {
    }

    public void stopProgress() {

    }

    /**
     * AddWaterListener
     **/
    public void addWaterSuccess(String response) {
    }

    public void addWaterFailedWithError(MessageObj response) {
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