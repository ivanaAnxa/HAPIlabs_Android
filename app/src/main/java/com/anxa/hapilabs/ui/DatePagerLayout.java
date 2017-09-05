package com.anxa.hapilabs.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.DateChangeListener;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class DatePagerLayout extends HorizontalScrollView implements OnClickListener {

    private static final int SWIPE_MIN_DISTANCE = 20;
    private static final int SCROLL_TO_LEFT = 99;
    private static final int SCROLL_TO_RIGHT = 98;
    private static final int MAX_PAGE = 1;
    private static int scrollType = 0;
    private int currentSelectedDateIndex = 0;
    private int totalDaysindex = 1;//use the indicate future dates
    private boolean iStartSwipe = false;
    private float X1_MOVE = 0;
    private float X2_UP = 0;

    private final Context context;

    private LinearLayout ll1;

    private RelativeLayout day1_rl;
    private RelativeLayout day2_rl;
    private RelativeLayout day3_rl;
    private RelativeLayout day4_rl;
    private RelativeLayout day5_rl;
    private RelativeLayout day6_rl;
    private RelativeLayout day7_rl;
    private TextView dateline_tv;

    private Hashtable<String, Integer> mealCountPerWeek = new Hashtable<>();
    private Hashtable<String, Integer> commentCountPerWeek = new Hashtable<>();
    private Hashtable<String, String> dateTable = new Hashtable<>();

    private Date currentSelectedDate;
    private Date weekStart;//(sunday)
    private Date weekEnd;//(Sat or depending on todays' Date)

    private DateChangeListener DateListener;
    private boolean isNewWeek;

    public void setDateListener(DateChangeListener DateListener) {
        this.DateListener = DateListener;
    }

    @SuppressLint("NewApi")
    public DatePagerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public DatePagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DatePagerLayout(Context context) {
        super(context);
        this.context = context;
    }

    private void initViews(LinearLayout ll) {

        day1_rl = null;
        day1_rl = (RelativeLayout) ll.findViewById(R.id.day1_container);
        day2_rl = (RelativeLayout) ll.findViewById(R.id.day2_container);
        day3_rl = (RelativeLayout) ll.findViewById(R.id.day3_container);
        day4_rl = (RelativeLayout) ll.findViewById(R.id.day4_container);
        day5_rl = (RelativeLayout) ll.findViewById(R.id.day5_container);
        day6_rl = (RelativeLayout) ll.findViewById(R.id.day6_container);
        day7_rl = (RelativeLayout) ll.findViewById(R.id.day7_container);

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

        ((TextView) day1_rl.findViewById(R.id.day1)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        ((TextView) day1_rl.findViewById(R.id.day1_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day1_rl.findViewById(R.id.day1_mealcount_iv).setVisibility(View.GONE);
        day1_rl.findViewById(R.id.day1_mealcount).setVisibility(View.GONE);
        day1_rl.findViewById(R.id.commentCount1_iv).setVisibility(View.GONE);
        day1_rl.findViewById(R.id.commentCount1).setVisibility(View.GONE);

        ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day2_rl.findViewById(R.id.day2_mealcount_iv).setVisibility(View.GONE);
        day2_rl.findViewById(R.id.day2_mealcount).setVisibility(View.GONE);
        day2_rl.findViewById(R.id.commentCount2_iv).setVisibility(View.GONE);
        day2_rl.findViewById(R.id.commentCount2).setVisibility(View.GONE);

        ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day3_rl.findViewById(R.id.day3_mealcount_iv).setVisibility(View.GONE);
        day3_rl.findViewById(R.id.day3_mealcount).setVisibility(View.GONE);
        day3_rl.findViewById(R.id.commentCount3_iv).setVisibility(View.GONE);
        day3_rl.findViewById(R.id.commentCount3).setVisibility(View.GONE);

        ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day4_rl.findViewById(R.id.day4_mealcount_iv).setVisibility(View.GONE);
        day4_rl.findViewById(R.id.day4_mealcount).setVisibility(View.GONE);
        day4_rl.findViewById(R.id.commentCount4_iv).setVisibility(View.GONE);
        day4_rl.findViewById(R.id.commentCount4).setVisibility(View.GONE);

        ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day5_rl.findViewById(R.id.day5_mealcount_iv).setVisibility(View.GONE);
        day5_rl.findViewById(R.id.day5_mealcount).setVisibility(View.GONE);
        day5_rl.findViewById(R.id.commentCount5_iv).setVisibility(View.GONE);
        day5_rl.findViewById(R.id.commentCount5).setVisibility(View.GONE);

        ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day6_rl.findViewById(R.id.day6_mealcount_iv).setVisibility(View.GONE);
        day6_rl.findViewById(R.id.day6_mealcount).setVisibility(View.GONE);
        day6_rl.findViewById(R.id.commentCount6_iv).setVisibility(View.GONE);
        day6_rl.findViewById(R.id.commentCount6).setVisibility(View.GONE);

        ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //
        ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        day7_rl.findViewById(R.id.day7_mealcount_iv).setVisibility(View.GONE);
        day7_rl.findViewById(R.id.day7_mealcount).setVisibility(View.GONE);
        day7_rl.findViewById(R.id.commentCount7_iv).setVisibility(View.GONE);
        day7_rl.findViewById(R.id.commentCount7).setVisibility(View.GONE);
        dateline_tv = (TextView) ll.findViewById(R.id.dateline);
    }

//    private void setSelectedDate(int idx) {
//        currentSelectedDate = AppUtil.updateDate(currentSelectedDate, idx - currentSelectedDateIndex);
//        ApplicationEx.getInstance().currentSelectedDate = currentSelectedDate;
//        currentSelectedDateIndex = idx;
//        setSelectedDateOnClick();
//    }

    private void setSelectedDateFromClick(int idx) {
        Calendar calPrevious = Calendar.getInstance();
        calPrevious.setTime(currentSelectedDate);

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentSelectedDate);
        int dayIdx = idx - 3;
        cal1.add(Calendar.DAY_OF_YEAR, dayIdx);
        currentSelectedDate = cal1.getTime();

        ApplicationEx.getInstance().currentSelectedDate = currentSelectedDate;
        currentSelectedDateIndex = AppUtil.getWeekIndexOfDate(currentSelectedDate) - 1;
        Calendar cal_weekend = Calendar.getInstance();
        cal_weekend.setTime(currentSelectedDate);
        weekEnd = cal_weekend.getTime();

        Calendar cal_today = Calendar.getInstance();
        cal_today.setTime(new Date());

        isNewWeek = (cal1.get(Calendar.DAY_OF_WEEK) == ApplicationEx.getInstance().selectedCalendarDay);

        if (cal_weekend.get(Calendar.DAY_OF_YEAR) == cal_today.get(Calendar.DAY_OF_YEAR)) {
            isNewWeek = false;
        }

        if (!isNewWeek)
            isNewWeek = AppUtil.isDateFromSync(currentSelectedDate);


        //another checking
        Calendar cal_weekStart = Calendar.getInstance();
        cal_weekStart.setTime(currentSelectedDate);
        cal_weekStart.add(Calendar.DAY_OF_YEAR, -6);
        weekStart = cal_weekStart.getTime();

        ApplicationEx.getInstance().fromDateCurrent = weekStart;
        ApplicationEx.getInstance().toDateCurrent = weekEnd;

        if (isNewWeek){
            cal_weekStart.setTime(currentSelectedDate);
            cal_weekStart.set(Calendar.DAY_OF_WEEK, ApplicationEx.getInstance().selectedCalendarDay);

            cal_weekend.set(Calendar.DAY_OF_WEEK, ApplicationEx.getInstance().selectedCalendarDay + 7);

            ApplicationEx.getInstance().fromDateSyncCurrent = cal_weekStart.getTime();
            ApplicationEx.getInstance().toDateSyncCurrent = cal_weekend.getTime();
            setSelectedDateOnSwipe();

        } else {

            setSelectedDateOnChange();
        }

        setSelectedDateOnClick();
    }

    private void setSelectedDateOnClick() {
        updateMealCount();

        dateline_tv.setText(AppUtil.getHeaderDate(currentSelectedDate));
        dateline_tv.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));

        DateListener.dateChange(currentSelectedDate, currentSelectedDateIndex);

        updateSelectedTab();
    }

    private void setSelectedDateOnSwipe() {
        //we will need to get the meals from get meals again
        Intent broadint = new Intent();
        broadint.setAction(context.getResources().getString(R.string.meallist_getmeal_week));
        context.sendBroadcast(broadint);
    }

    private void setSelectedDateOnChange() {
        //we will need to get the meals from get meals again
        Intent broadint = new Intent();
        broadint.setAction(context.getResources().getString(R.string.meallist_get_new_week));
        context.sendBroadcast(broadint);
    }

    public void initDate(Date date, Hashtable<String, Integer> mealCountPerWeek, Hashtable<String, Integer> commentCountPerWeek, Hashtable<String, String> dateTable, int totalDays) {
        initDate(date, mealCountPerWeek, commentCountPerWeek, dateTable, totalDays, currentSelectedDateIndex);
    }

    public void initDate(Date date, Hashtable<String, Integer> mealCountPerWeek, Hashtable<String, Integer> commentCountPerWeek, Hashtable<String, String> dateTable, int totalDays, int selectedindex) {

        this.mealCountPerWeek = mealCountPerWeek;
        this.commentCountPerWeek = commentCountPerWeek;
        this.totalDaysindex = dateTable.size();
//        this.totalDaysindex = totalDays + 1;
        this.dateTable = dateTable;
        currentSelectedDate = date;
        currentSelectedDateIndex = AppUtil.getWeekIndexOfDate(date) - 1;
        weekStart = ApplicationEx.getInstance().fromDateCurrent;
        weekEnd = ApplicationEx.getInstance().toDateCurrent;
        //init all tabs to default; gray text; no icon; no label
        initViews(ll1);

        if (ApplicationEx.getInstance().isCalendarSelected != null) {
            totalDaysindex = 7;
        }

        //update all tabs from 0  total days light gray text, with orang icon, label = 0
        updateOnClickDefault(totalDaysindex);
        //update all tabs from 0  total days with correct meal count
        updateMealCount();
        //update selected tab
        updateSelectedTab();
    }

    private void updateOnClickDefault(int totalDaysIndex) {
        //set all tab to unselected
        day1_rl.setSelected(false);
        day2_rl.setSelected(false);
        day3_rl.setSelected(false);
        day4_rl.setSelected(true);
        day5_rl.setSelected(false);
        day6_rl.setSelected(false);
        day7_rl.setSelected(false);

        for (int i = 0; i <= totalDaysIndex; i++) {
            updateDateTabUI(i, 0, 0);
        }

        dateline_tv.setText(AppUtil.getHeaderDate(currentSelectedDate));
        dateline_tv.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
    }

    private void updateDateTabUI(int i, int label, int commentCount) {
        int indexUI = totalDaysindex - i - 1;
        if (indexUI == 0) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day1_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day1_mealcount).setVisibility(View.VISIBLE);
                day1_rl.findViewById(R.id.day1_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day1_rl.findViewById(R.id.day1_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
                ((TextView) day1_rl.findViewById(R.id.day1_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
            } else {
                findViewById(R.id.day1_mealcount).setVisibility(View.GONE);
                day1_rl.findViewById(R.id.day1_mealcount_iv).setVisibility(GONE);
            }

            ((TextView) day1_rl.findViewById(R.id.day1)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));

            if (commentCount > 0) {
                (day1_rl.findViewById(R.id.commentCount1_iv)).setVisibility(View.VISIBLE);
                (day1_rl.findViewById(R.id.commentCount1)).setVisibility(View.VISIBLE);
                ((TextView) day1_rl.findViewById(R.id.commentCount1)).setText(Integer.toString(commentCount));
                ((TextView) day1_rl.findViewById(R.id.commentCount1)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day1_rl.findViewById(R.id.comment1_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day1_rl.findViewById(R.id.commentCount1_iv)).setImageResource(R.drawable.meal_commented_icon_gray);
            } else {
                (day1_rl.findViewById(R.id.commentCount1_iv)).setVisibility(View.GONE);
                (day1_rl.findViewById(R.id.commentCount1)).setVisibility(View.GONE);
                day1_rl.findViewById(R.id.commentCount1_iv).setVisibility(GONE);

            }

        } else if (indexUI == 1) {

            if (label > 0) {
                ((TextView) findViewById(R.id.day2_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day2_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day2_rl.findViewById(R.id.day2_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                day2_rl.findViewById(R.id.day2_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day2_rl.findViewById(R.id.day2_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
            } else {
                findViewById(R.id.day2_mealcount).setVisibility(View.GONE);
                day2_rl.findViewById(R.id.day2_mealcount_iv).setVisibility(GONE);
            }
            ((TextView) day2_rl.findViewById(R.id.day2)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //


            if (commentCount > 0) {
                (day2_rl.findViewById(R.id.commentCount2_iv)).setVisibility(View.VISIBLE);
                (day2_rl.findViewById(R.id.commentCount2)).setVisibility(View.VISIBLE);
                ((TextView) day2_rl.findViewById(R.id.commentCount2)).setText(Integer.toString(commentCount));
                ((TextView) day2_rl.findViewById(R.id.commentCount2)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day2_rl.findViewById(R.id.comment2_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day2_rl.findViewById(R.id.commentCount2_iv)).setImageResource(R.drawable.meal_commented_icon_gray);

            } else {
                (day2_rl.findViewById(R.id.commentCount2_iv)).setVisibility(View.GONE);
                (day2_rl.findViewById(R.id.commentCount2)).setVisibility(View.GONE);
                day2_rl.findViewById(R.id.commentCount2_iv).setVisibility(GONE);

            }

        } else if (indexUI == 2) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day3_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day3_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day3_rl.findViewById(R.id.day3_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                day3_rl.findViewById(R.id.day3_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day3_rl.findViewById(R.id.day3_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
            } else {
                findViewById(R.id.day3_mealcount).setVisibility(View.GONE);
                day3_rl.findViewById(R.id.day3_mealcount_iv).setVisibility(GONE);
            }

            ((TextView) day3_rl.findViewById(R.id.day3)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //

            if (commentCount > 0) {
                (day3_rl.findViewById(R.id.comment3_rl)).setVisibility(View.VISIBLE);
                (day3_rl.findViewById(R.id.commentCount3)).setVisibility(View.VISIBLE);
                ((TextView) day3_rl.findViewById(R.id.commentCount3)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day3_rl.findViewById(R.id.commentCount3_iv)).setVisibility(VISIBLE);
                ((TextView) day3_rl.findViewById(R.id.commentCount3)).setText(Integer.toString(commentCount));
                ((ImageView) day3_rl.findViewById(R.id.commentCount3_iv)).setImageResource(R.drawable.meal_commented_icon_gray);

            } else {
                (day3_rl.findViewById(R.id.comment3_rl)).setVisibility(View.GONE);
                (day3_rl.findViewById(R.id.commentCount3)).setVisibility(View.GONE);
                day3_rl.findViewById(R.id.commentCount3_iv).setVisibility(GONE);
            }

        } else if (indexUI == 3) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day4_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day4_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                day4_rl.findViewById(R.id.day4_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
            } else {
                findViewById(R.id.day4_mealcount).setVisibility(View.GONE);
                day4_rl.findViewById(R.id.day4_mealcount_iv).setVisibility(GONE);
            }
            ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(ContextCompat.getColor(context, R.color.text_darkgray)); //

            if (commentCount > 0) {
                (day4_rl.findViewById(R.id.commentCount4_iv)).setVisibility(View.VISIBLE);
                (day4_rl.findViewById(R.id.commentCount4)).setVisibility(View.VISIBLE);
                ((TextView) day4_rl.findViewById(R.id.commentCount4)).setText(Integer.toString(commentCount));
                ((TextView) day4_rl.findViewById(R.id.commentCount4)).setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day4_rl.findViewById(R.id.comment4_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day4_rl.findViewById(R.id.commentCount4_iv)).setImageResource(R.drawable.meal_commented_icon);

            } else {
                (day4_rl.findViewById(R.id.commentCount4_iv)).setVisibility(View.GONE);
                (day4_rl.findViewById(R.id.commentCount4)).setVisibility(View.GONE);
                day4_rl.findViewById(R.id.commentCount4_iv).setVisibility(GONE);
            }

        } else if (indexUI == 4) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day5_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day5_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day5_rl.findViewById(R.id.day5_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                day5_rl.findViewById(R.id.day5_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day5_rl.findViewById(R.id.day5_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
            } else {
                findViewById(R.id.day5_mealcount).setVisibility(View.GONE);
                day5_rl.findViewById(R.id.day5_mealcount_iv).setVisibility(GONE);
            }
            ((TextView) day5_rl.findViewById(R.id.day5)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //

            if (commentCount > 0) {
                (day5_rl.findViewById(R.id.commentCount5_iv)).setVisibility(View.VISIBLE);
                (day5_rl.findViewById(R.id.commentCount5)).setVisibility(View.VISIBLE);
                ((TextView) day5_rl.findViewById(R.id.commentCount5)).setText(Integer.toString(commentCount));
                ((TextView) day5_rl.findViewById(R.id.commentCount5)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day5_rl.findViewById(R.id.comment5_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day5_rl.findViewById(R.id.commentCount5_iv)).setImageResource(R.drawable.meal_commented_icon_gray);

            } else {
                (day5_rl.findViewById(R.id.commentCount5_iv)).setVisibility(View.GONE);
                (day5_rl.findViewById(R.id.commentCount5)).setVisibility(View.GONE);
                day5_rl.findViewById(R.id.commentCount5_iv).setVisibility(GONE);
            }

        } else if (indexUI == 5) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day6_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day6_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day6_rl.findViewById(R.id.day6_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                day6_rl.findViewById(R.id.day6_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day6_rl.findViewById(R.id.day6_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
            } else {
                findViewById(R.id.day6_mealcount).setVisibility(View.GONE);
                day6_rl.findViewById(R.id.day6_mealcount_iv).setVisibility(GONE);
            }
            ((TextView) day6_rl.findViewById(R.id.day6)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //

            if (commentCount > 0) {
                (day6_rl.findViewById(R.id.commentCount6_iv)).setVisibility(View.VISIBLE);
                (day6_rl.findViewById(R.id.commentCount6)).setVisibility(View.VISIBLE);
                ((TextView) day6_rl.findViewById(R.id.commentCount6)).setText(Integer.toString(commentCount));
                ((TextView) day6_rl.findViewById(R.id.commentCount6)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day6_rl.findViewById(R.id.comment6_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day6_rl.findViewById(R.id.commentCount6_iv)).setImageResource(R.drawable.meal_commented_icon_gray);

            } else {
                (day6_rl.findViewById(R.id.commentCount6_iv)).setVisibility(View.GONE);
                (day6_rl.findViewById(R.id.commentCount6)).setVisibility(View.GONE);
                day6_rl.findViewById(R.id.commentCount6_iv).setVisibility(GONE);

            }

        } else if (indexUI == 6) {
            if (label > 0) {
                ((TextView) findViewById(R.id.day7_mealcount)).setText(Integer.toString(label));
                findViewById(R.id.day7_mealcount).setVisibility(View.VISIBLE);
                ((TextView) day7_rl.findViewById(R.id.day7_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                day7_rl.findViewById(R.id.day7_mealcount_iv).setVisibility(VISIBLE);
                ((ImageView) day7_rl.findViewById(R.id.day7_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_gray);
            } else {
                findViewById(R.id.day7_mealcount).setVisibility(View.GONE);
                day7_rl.findViewById(R.id.day7_mealcount_iv).setVisibility(GONE);
            }
            ((TextView) day7_rl.findViewById(R.id.day7)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray)); //

            if (commentCount > 0) {
                (day7_rl.findViewById(R.id.commentCount7_iv)).setVisibility(View.VISIBLE);
                (day7_rl.findViewById(R.id.commentCount7)).setVisibility(View.VISIBLE);
                ((TextView) day7_rl.findViewById(R.id.commentCount7)).setText(Integer.toString(commentCount));
                ((TextView) day7_rl.findViewById(R.id.commentCount7)).setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                (day7_rl.findViewById(R.id.comment7_rl)).setVisibility(View.VISIBLE);
                ((ImageView) day7_rl.findViewById(R.id.commentCount7_iv)).setImageResource(R.drawable.meal_commented_icon_gray);

            } else {
                (day7_rl.findViewById(R.id.commentCount7_iv)).setVisibility(View.GONE);
                (day7_rl.findViewById(R.id.commentCount7)).setVisibility(View.GONE);
                day7_rl.findViewById(R.id.commentCount7_iv).setVisibility(GONE);
            }
        }
    }

    private void updateMealCount() {
        for (int i = 0; i < 7; i++) {
            String keyDate;
            int label, labelComment;
            if (dateTable.get(i + "") == null) {
                label = 0;
                labelComment = 0;
            } else {
                keyDate = (dateTable.get("" + i));
                if (mealCountPerWeek.get("" + keyDate) == null)
                    label = 0;
                else
                    label = mealCountPerWeek.get("" + keyDate);

                if (commentCountPerWeek.get("" + keyDate) == null)
                    labelComment = 0;
                else
                    labelComment = commentCountPerWeek.get("" + keyDate);
            }

            if (i <= totalDaysindex)
                updateDateTabUI(i, label, labelComment);
        }
    }

    private void updateSelectedTab() {
        //if selectedDate - current date, day5 - disabled
        //always center the selected date
        ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
        day4_rl.setSelected(true);

        //set text color
        ((TextView) day4_rl.findViewById(R.id.day4)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        ((TextView) day4_rl.findViewById(R.id.day4_mealcount)).setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        ((TextView) day4_rl.findViewById(R.id.commentCount4)).setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));

        //set icon to white
        ((ImageView) day4_rl.findViewById(R.id.day4_mealcount_iv)).setImageResource(R.drawable.meal_tabicon_meals_orange);
        ((ImageView) day4_rl.findViewById(R.id.commentCount4_iv)).setImageResource(R.drawable.meal_commented_icon);

        switch (currentSelectedDateIndex) {
            case 0:
                //SUNDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_WE));

                break;
            case 1:
                //MONDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_TH));

                break;
            case 2:
                //TUESDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_FR));

                break;
            case 3:
                //WEDNESDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_SA));

                break;
            case 4:
                //THURSDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_SU));

                break;
            case 5:
                //FRIDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_TU));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_MO));

                break;
            case 6:
                //SATURDAY
                ((TextView) day1_rl.findViewById(R.id.day1)).setText(getResources().getString(R.string.CALENDAR_WE));
                ((TextView) day2_rl.findViewById(R.id.day2)).setText(getResources().getString(R.string.CALENDAR_TH));
                ((TextView) day3_rl.findViewById(R.id.day3)).setText(getResources().getString(R.string.CALENDAR_FR));
                ((TextView) day4_rl.findViewById(R.id.day4)).setText(getResources().getString(R.string.CALENDAR_SA));
                ((TextView) day5_rl.findViewById(R.id.day5)).setText(getResources().getString(R.string.CALENDAR_SU));
                ((TextView) day6_rl.findViewById(R.id.day6)).setText(getResources().getString(R.string.CALENDAR_MO));
                ((TextView) day7_rl.findViewById(R.id.day7)).setText(getResources().getString(R.string.CALENDAR_TU));

                break;
            default:
                break;
        }

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(currentSelectedDate);
        int dayIdx = cal1.get(Calendar.DAY_OF_YEAR);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        int dayIdx2 = cal2.get(Calendar.DAY_OF_YEAR);

        if (fmt.format(currentSelectedDate).equals(fmt.format(new Date()))) {
            day5_rl.setVisibility(View.INVISIBLE);
            day6_rl.setVisibility(View.INVISIBLE);
            day7_rl.setVisibility(View.INVISIBLE);
        } else if (dayIdx2 - dayIdx == 1) {
            //current selected date is yesterday, disable day day6 and day7
            day5_rl.setVisibility(View.VISIBLE);
            day6_rl.setVisibility(View.INVISIBLE);
            day7_rl.setVisibility(View.INVISIBLE);
        } else if (dayIdx2 - dayIdx == 2) {
            //current selected date is 2 days before, disable day day7
            day5_rl.setVisibility(View.VISIBLE);
            day6_rl.setVisibility(View.VISIBLE);
            day7_rl.setVisibility(View.INVISIBLE);
        } else {
            day5_rl.setVisibility(View.VISIBLE);
            day6_rl.setVisibility(View.VISIBLE);
            day7_rl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        int tagInx = (Integer) v.getTag();

        if (tagInx == 77) {
            goToPreviousWeek();
        } else if (tagInx == 88) {
            goToNextWeek();
        } else if (tagInx <= totalDaysindex) {
            if (tagInx == 3) {
                return;
            } else {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
                if ((tagInx > 3) && (fmt.format(currentSelectedDate).equals(fmt.format(new Date())))) {
                    return;
                } else {
                    setSelectedDateFromClick(tagInx);
                }
            }
        }
    }

    private void updateFields() {
        if (scrollType == SCROLL_TO_LEFT) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            if ((fmt.format(currentSelectedDate).equals(fmt.format(new Date())))) {
                return;
            } else {
                setSelectedDateFromClick(4);
            }
        } else if (scrollType == SCROLL_TO_RIGHT) { //minus one day
            setSelectedDateFromClick(2);
        }
    }

    @SuppressLint("NewApi")
    public void setDatePager() {
        ApplicationEx.getInstance().getWindowDimension(getContext());

        //Create a linear layout to hold each screen in the scroll view
        LinearLayout internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        internalWrapper.setOrientation(LinearLayout.HORIZONTAL);

        try {
            removeView(internalWrapper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addView(internalWrapper);

        //LinearLayout ll = null;
        for (int i = 0; i < MAX_PAGE; i++) {
            View v = inflate(getContext(), R.layout.mymeals_dateheader, null);
            if (i == 0) {
                v.setId(R.id.datepage1);
            }
            v.setLayoutParams(new LayoutParams(ApplicationEx.getInstance().screenWidth, LayoutParams.WRAP_CONTENT));
            try {
                internalWrapper.removeView(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
            internalWrapper.addView(v);
        }

        ll1 = (LinearLayout) internalWrapper.findViewById(R.id.datepage1);
        initViews(ll1);
        //setScrollBarSize(1);

        setOnTouchListener(new OnTouchListener() {
                               @Override
                               public boolean onTouch(View v, MotionEvent event) {
                                   //If the user swipes
                                   if (event.getAction() == MotionEvent.ACTION_UP) {
                                       X2_UP = event.getX();
                                       if (X1_MOVE > X2_UP && X1_MOVE - X2_UP >= SWIPE_MIN_DISTANCE) {
                                           scrollType = SCROLL_TO_LEFT;
                                           X1_MOVE = 0;
                                           X2_UP = 0;
                                           iStartSwipe = false;
                                           updateFields();
                                       } else if (X2_UP > X1_MOVE && X2_UP - X1_MOVE >= SWIPE_MIN_DISTANCE) {
                                           scrollType = SCROLL_TO_RIGHT;
                                           X1_MOVE = 0;
                                           X2_UP = 0;
                                           iStartSwipe = false;
                                           updateFields();
                                       }
                                       return true;
                                   } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                       if (!iStartSwipe) {
                                           iStartSwipe = true;
                                           X1_MOVE = event.getX();
                                       }
                                   }
                                   return false;
                               }
                           }
        );
    }

    public void updateProgressText(boolean finished) {
        if (finished) {
            dateline_tv.setText(AppUtil.getHeaderDate(currentSelectedDate));
        } else {
            dateline_tv.setText(getResources().getString(R.string.LOADING_TEXT));
        }
    }

    public void goToPreviousWeek() {
        scrollType = SCROLL_TO_RIGHT;
        updateFields();
    }

    public void goToNextWeek() {
        scrollType = SCROLL_TO_LEFT;
        updateFields();
    }
}