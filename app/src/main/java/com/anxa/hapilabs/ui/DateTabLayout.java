package com.anxa.hapilabs.ui;

import java.util.Date;
import java.util.Hashtable;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.DateChangeListener;
import com.anxa.hapilabs.common.util.AppUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DateTabLayout extends LinearLayout implements OnClickListener {

    Context context;
    OnClickListener listener;
    RelativeLayout day1_rl;
    RelativeLayout day2_rl;
    RelativeLayout day3_rl;
    RelativeLayout day4_rl;
    RelativeLayout day5_rl;
    RelativeLayout day6_rl;
    RelativeLayout day7_rl;
    HorizontalScrollView horizscroolView;
    TextView dateline_tv;

    int selectedDateIndex = 0;
    int previousDateIndex = 0;
    int totalDaysindex = 0;//use the indicate future dates

    Hashtable<String, Integer> mealCountPerWeek = new Hashtable<String, Integer>();
    Hashtable<String, Integer> dateTable = new Hashtable<String, Integer>();

    Date currentDate;

    DateChangeListener DateListener;

    public void setDateListener(DateChangeListener DateListener) {
        this.DateListener = DateListener;
    }

    @SuppressLint("NewApi")
    public DateTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreateView(null);
    }

    public DateTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        onCreateView(null);
    }

    public DateTabLayout(Context context) {
        super(context);
        onCreateView(null);
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void onCreateView(ViewGroup container) {
        View v = inflate(context, R.layout.mymeals_dateheader, null);
        try {
            removeView(v);
        } catch (Exception e) {

        }
        addView(v);

//		 	LinearLayout currentWeekDateHeader = (LinearLayout)findViewById(R.id.weekcurrent);
//		 	horizscroolView =  (HorizontalScrollView)findViewById(R.id.horizontalScrollView1);
        day1_rl = (RelativeLayout) findViewById(R.id.day1_container);
        day2_rl = (RelativeLayout) findViewById(R.id.day2_container);
        day3_rl = (RelativeLayout) findViewById(R.id.day3_container);
        day4_rl = (RelativeLayout) findViewById(R.id.day4_container);
        day5_rl = (RelativeLayout) findViewById(R.id.day5_container);
        day6_rl = (RelativeLayout) findViewById(R.id.day6_container);
        day7_rl = (RelativeLayout) findViewById(R.id.day7_container);

        day1_rl.setOnClickListener(this);
        day1_rl.setTag(0);

        day2_rl.setOnClickListener(this);
        day2_rl.setTag(1);

        day3_rl.setOnClickListener(this);
        day3_rl.setTag(2);

        day4_rl.setOnClickListener(this);
        day4_rl.setTag(3);

        day5_rl.setOnClickListener(this);
        day5_rl.setTag(4);

        day6_rl.setOnClickListener(this);
        day6_rl.setTag(5);

        day7_rl.setOnClickListener(this);
        day7_rl.setTag(6);

        ((TextView) day1_rl.findViewById(R.id.day1)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day1_rl.findViewById(R.id.day1_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(getResources().getColor(R.color.text_lightgray)); //imageview
        ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(getResources().getColor(R.color.text_lightgray));

        dateline_tv = (TextView) findViewById(R.id.dateline);
    }

    public void setSelectedDate(int idx) {

        if (idx != selectedDateIndex) {
            currentDate = AppUtil.updateDate(currentDate, idx - selectedDateIndex);
            previousDateIndex = selectedDateIndex;
            selectedDateIndex = idx;
            updateDateTab();
            dateline_tv.setText(AppUtil.getMonthDay(currentDate));
            dateline_tv.setTextColor(getResources().getColor(R.color.text_darkgray)); //unselected date
            DateListener.dateChange(currentDate, selectedDateIndex);
        }//if equal no need to update the UI,
    }

    public void initDate(Date date, Hashtable<String, Integer> mealCountPerWeek, Hashtable<String, Integer> dateTable, int totalDays, int selectedindex) {
        currentDate = date;

        this.mealCountPerWeek = mealCountPerWeek;

        this.totalDaysindex = totalDays;

        this.dateTable = dateTable;

        selectedDateIndex = selectedindex;

        updateOnClickDefault(totalDaysindex);

        updateMealCount();

        updateDateTab();

        dateline_tv.setText(AppUtil.getMonthDay(currentDate));

        dateline_tv.setTextColor(getResources().getColor(R.color.text_gray));


    }

    private void updateOnClickDefault(int totalDaysIndex) {
        for (int i = 0; i < totalDaysIndex; i++) {
            updateDateTabUI(i, 0);
        }

        dateline_tv.setText(AppUtil.getMonthDay(currentDate));
        dateline_tv.setTextColor(getResources().getColor(R.color.text_gray));
    }

    private void updateDateTabUI(int i, int label) {
        if (i == 0) {
            ((TextView) findViewById(R.id.day1_mealcount)).setText("" + label);
            ((TextView) day1_rl.findViewById(R.id.day1)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day1_rl.findViewById(R.id.day1_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 1) {
            ((TextView) findViewById(R.id.day2_mealcount)).setText("" + label);
            ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 2) {
            ((TextView) findViewById(R.id.day3_mealcount)).setText("" + label);
            ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 3) {
            ((TextView) findViewById(R.id.day4_mealcount)).setText("" + label);
            ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 4) {
            ((TextView) findViewById(R.id.day5_mealcount)).setText("" + label);
            ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 5) {
            ((TextView) findViewById(R.id.day6_mealcount)).setText("" + label);
            ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setVisibility(VISIBLE);

        } else if (i == 6) {
            ((TextView) findViewById(R.id.day7_mealcount)).setText("" + label);
            ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
            ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));
            ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setVisibility(VISIBLE);

        }
    }

    private void updateMealCount() {


        for (int i = 0; i < dateTable.size(); i++) {

            int keyDate;
            int label;
            if (dateTable.get(i + "") == null) {
                keyDate = 0;
                label = keyDate;
            } else {
                keyDate = (dateTable.get("" + i));
                if (mealCountPerWeek.get("" + keyDate) == null)
                    label = 0;
                else
                    label = mealCountPerWeek.get("" + keyDate);
            }

            updateDateTabUI(i, label);


        }


    }

    private void setPreviousTab() {
        switch (previousDateIndex) {
            case 0:
                day1_rl.setSelected(false);

                //set text color
                ((TextView) day1_rl.findViewById(R.id.day1)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day1_rl.findViewById(R.id.day1_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 1:
                day2_rl.setSelected(false);
                //set text color
                ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(getResources().getColor(R.color.text_darkgray)); //imageview
                ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 2:
                day3_rl.setSelected(false);
                //set text color
                ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 3:
                day4_rl.setSelected(false);
                //set text color
                ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 4:
                day5_rl.setSelected(false);
                //set text color
                ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 5:
                day6_rl.setSelected(false);
                //set text color
                ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 6:
                day7_rl.setSelected(false);
                //set text color
                ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(getResources().getColor(R.color.text_darkgray));
                ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(getResources().getColor(R.color.text_darkgray));

                //set icon to white
                ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
                ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setVisibility(VISIBLE);

                break;
        }
    }


    private void updateDateTab() {

        switch (selectedDateIndex) {
            case 0:
                day1_rl.setSelected(true);

                //set text color
                //set icon to white
                ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 1:
                day2_rl.setSelected(true);
                //set text color
                ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(getResources().getColor(R.color.text_white)); //imageview
                ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setVisibility(VISIBLE);

                break;
            case 2:
                day3_rl.setSelected(true);
                //set text color
                ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(getResources().getColor(R.color.text_white));
                ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setVisibility(VISIBLE);


                break;
            case 3:
                day4_rl.setSelected(true);
                //set text color
                ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(getResources().getColor(R.color.text_white));
                ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setVisibility(VISIBLE);


                break;
            case 4:
                day5_rl.setSelected(true);
                //set text color
                ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(getResources().getColor(R.color.text_white));
                ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setVisibility(VISIBLE);


                break;
            case 5:
                day6_rl.setSelected(true);
                //set text color
                ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(getResources().getColor(R.color.text_white));
                ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setVisibility(VISIBLE);


                break;
            case 6:
                day7_rl.setSelected(true);
                //set text color
                ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(getResources().getColor(R.color.text_white));
                ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(getResources().getColor(R.color.text_white));

                //set icon to white
                ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_white);
                ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setVisibility(VISIBLE);


                break;
        }
        setPreviousTab();

    }

    public int getSelectedDate() {
        return selectedDateIndex;
    }

    @Override
    public void onClick(View v) {

        //check if selected tab is a future date or not
        // TODO Auto-generated method stub
        if (v == day1_rl || totalDaysindex <= 0) {
            setSelectedDate(0);
        } else if (v == day2_rl || totalDaysindex <= 1) {
            setSelectedDate(1);
        } else if (v == day3_rl || totalDaysindex <= 2) {
            setSelectedDate(2);
        } else if (v == day4_rl || totalDaysindex <= 3) {
            setSelectedDate(3);
        } else if (v == day5_rl || totalDaysindex <= 4) {
            setSelectedDate(4);
        } else if (v == day6_rl || totalDaysindex <= 5) {
            setSelectedDate(5);
        } else if (v == day7_rl || totalDaysindex <= 6) {
            setSelectedDate(6);
        }
    }

}
