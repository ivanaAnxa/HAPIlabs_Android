package com.anxa.hapilabs.activities.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.MealViewActivity;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetAllMealsListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.StepsDataListener;
import com.anxa.hapilabs.common.connection.listener.StepsGraphListener;
import com.anxa.hapilabs.common.connection.listener.WeightDataListener;
import com.anxa.hapilabs.common.connection.listener.WeightLossGraphListener;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.progress.GetAllMealsController;
import com.anxa.hapilabs.controllers.progress.StepsDataController;
import com.anxa.hapilabs.controllers.progress.StepsGraphController;
import com.anxa.hapilabs.controllers.progress.WeightDataController;
import com.anxa.hapilabs.controllers.progress.WeightLossGraphController;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.ui.adapters.MealWithRatingsAdapter;
import com.anxa.hapilabs.ui.adapters.StepsLogsListAdapter;
import com.anxa.hapilabs.ui.adapters.WeightLogsListAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import android.support.v4.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProgressFragments extends Fragment implements View.OnClickListener, BitmapDownloadListener, GetAllMealsListener,
        WeightLossGraphListener, ProgressChangeListener, OnChartGestureListener, OnChartValueSelectedListener, WeightDataListener, View.OnKeyListener,
        StepsGraphListener, StepsDataListener {

    private Context context;

    private View rootView;
    private ProgressBar progressBar;

    private LinearLayout meals_ll;
    private LinearLayout weightGraph_ll;
    private LinearLayout stepsGraph_ll;
    private int selectedMenu = 0;
    //meals
    private MealWithRatingsAdapter adapter;
    private GetAllMealsController getAllMealsController;
    private View footerView;
    private Button seeMoreButton;
    private ProgressBar progress1;
    private ProgressBar progress2;
    private TabHost tabHost;
    private TextView mealCountTextView;
    private TextView mealRatedDesc;
    private ListView allMealsListView;
    private ListView allHealthyMealsListView;
    private ListView allJustOKMealsListView;
    private ListView allUnhealthyMealsListView;
    private String command;
    private Boolean changeTab;

    private WeightLossGraphController weighLossGraphController;
    private WeightDataController weightDataController;
    private StepsDataController stepsDataController;
    private StepsGraphController stepsGraphController;

    private Weight latestWeight;
    private TextView currentWeightTitle;
    private ProgressBar weightProgressBar;
    private LineChart weightLineChart;
    private LineChart mealLineChart;
    private BarChart stepsBarChart;
    private List<Weight> weightGraphDataArrayList = new ArrayList<>();
    private List<Steps> stepsGraphDataArrayList = new ArrayList<>();
    private List<Weight> weightLogsList = new ArrayList<>();
    private List<Steps> stepsLogsList = new ArrayList<>();
    private List<Weight> weightLogsListCurrentDisplay = new ArrayList<>();
    private List<Steps> stepsLogsListCurrentDisplay = new ArrayList<>();
    private int selectedDateRange;
    private ListView weightLogsListView;
    private ListView stepsLogsListView;
    private WeightLogsListAdapter weightLogsListAdapter;
    private StepsLogsListAdapter stepsLogsListAdapter;
    private TextView dateRange_tv;
    private Button date_left_btn;
    private Button date_right_btn;
    private Button weight_1w;
    private Button weight_1m;
    private Button weight_3m;
    private Button weight_1y;
    private Button weight_all;
    private Button weight_submitBtn;
    private boolean fromSubmitWeight = false;

    private Button steps_1w;
    private Button steps_1m;
    private Button steps_3m;
    private Button steps_1y;
    private TextView steps_dateRange_tv;
    private Button steps_date_left_btn;
    private Button steps_date_right_btn;
    private Button steps_submitBtn;

    private EditText weight_enter_et;
    private EditText steps_enter_et;

    private double targetWeight = 0.0;
    private double initialWeight = 0.0;
    private double lowestWeight = 0.0;
    private double heighestWeight = 0.0;

    private double heighestSteps = 0.0;
    private double lowestSteps = 0.0;

    private final int DATE_RANGE_1M = 0;
    private final int DATE_RANGE_3M = 1;
    private final int DATE_RANGE_1Y = 2;
    private final int DATE_RANGE_ALL = 3;
    private final int DATE_RANGE_1W = 4;
    private int dateRangeIndex = 0;

    private final int SELECTED_MENU_WEIGHT = 0;
    private final int SELECTED_MENU_STEPS = 1;
    private final int SELECTED_MENU_MEALS = 2;
    String url;

    public ProgressFragments() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //here is your arguments
        this.context = getActivity();

        //here is your list array
        selectedMenu = SELECTED_MENU_WEIGHT;

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.progress_refresh));
        filter.addAction(this.getResources().getString(R.string.WEIGHT_GRAPH_BROADCAST_DELETE));
        filter.addAction(this.getResources().getString(R.string.WEIGHT_GRAPH_BROADCAST_EDIT));
        filter.addAction(this.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_DELETE));
        filter.addAction(this.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_EDIT));

        context.getApplicationContext().registerReceiver(the_receiver, filter);

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_progress, container, false);
            progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

            meals_ll = (LinearLayout) rootView.findViewById(R.id.progress_meals_ll);
            weightGraph_ll = (LinearLayout) rootView.findViewById(R.id.progress_weight_graph_ll);
            stepsGraph_ll = (LinearLayout) rootView.findViewById(R.id.progress_steps_graph_ll);

            //weight
            currentWeightTitle = (TextView) rootView.findViewById(R.id.currentWeight_tv);
            weightProgressBar = (ProgressBar) rootView.findViewById(R.id.weight_graph_progressBar2);
            weightProgressBar.setVisibility(View.VISIBLE);

            weightLineChart = (LineChart) rootView.findViewById(R.id.viewcontentGraph);
            weightLineChart.setVisibility(View.VISIBLE);

            mealLineChart = (LineChart) rootView.findViewById(R.id.mealsGraph);
            mealLineChart.setVisibility(View.GONE);

            stepsBarChart = (BarChart) rootView.findViewById(R.id.viewcontentBarGraph);
            stepsBarChart.setVisibility(View.GONE);

            getLatestWeightData();


            UserProfileDAO dao = new UserProfileDAO(getActivity(), null);
            UserProfile tempUserProfile = dao.getUserProfile();

            targetWeight = Double.parseDouble(tempUserProfile.getTarget_weight()) / 1000;
            initialWeight = Double.parseDouble(tempUserProfile.getStart_weight()) / 1000;

            weight_1m = (Button) rootView.findViewById(R.id.weight_1m_btn);
            weight_3m = (Button) rootView.findViewById(R.id.weight_3m_btn);
            weight_1y = (Button) rootView.findViewById(R.id.weight_1y_btn);
            weight_all = (Button) rootView.findViewById(R.id.weight_all_btn);
            weight_submitBtn = (Button) rootView.findViewById(R.id.weight_submitButton);
            steps_submitBtn = (Button) rootView.findViewById(R.id.steps_submitButton);
            weightLogsListView = (ListView) rootView.findViewById(R.id.weight_graph_listView);
            stepsLogsListView = (ListView) rootView.findViewById(R.id.steps_graph_listView);

            steps_1w = (Button) rootView.findViewById(R.id.steps_1w_btn);
            steps_1m = (Button) rootView.findViewById(R.id.steps_1m_btn);
            steps_3m = (Button) rootView.findViewById(R.id.steps_3m_btn);
            steps_1y = (Button) rootView.findViewById(R.id.steps_1y_btn);

            dateRange_tv = (TextView) rootView.findViewById(R.id.weight_date_range);
            date_left_btn = (Button) rootView.findViewById(R.id.date_left_btn);
            date_right_btn = (Button) rootView.findViewById(R.id.date_right_btn);
            steps_dateRange_tv = (TextView) rootView.findViewById(R.id.steps_date_range);
            steps_date_left_btn = (Button) rootView.findViewById(R.id.steps_date_left_btn);
            steps_date_right_btn = (Button) rootView.findViewById(R.id.steps_date_right_btn);

            date_left_btn.setOnClickListener(this);
            date_right_btn.setOnClickListener(this);
            weight_1m.setOnClickListener(this);
            weight_3m.setOnClickListener(this);
            weight_1y.setOnClickListener(this);
            weight_all.setOnClickListener(this);

            steps_date_left_btn.setOnClickListener(this);
            steps_date_right_btn.setOnClickListener(this);
            steps_1w.setOnClickListener(this);
            steps_1m.setOnClickListener(this);
            steps_3m.setOnClickListener(this);
            steps_1y.setOnClickListener(this);

            weight_submitBtn.setOnClickListener(this);
            steps_submitBtn.setOnClickListener(this);

            rootView.findViewById(R.id.weight_data_rl).setOnClickListener(this);

            //for meals
            progress1 = (ProgressBar) rootView.findViewById(R.id.progressBar1);

            mealCountTextView = (TextView) rootView.findViewById(R.id.mealRatedCount);
            mealRatedDesc = (TextView) rootView.findViewById(R.id.mealRatedDesc);

            weight_enter_et = (EditText) rootView.findViewById(R.id.weight_enter_et);
            weight_enter_et.setOnKeyListener(this);

            steps_enter_et = (EditText) rootView.findViewById(R.id.steps_enter_et);
            steps_enter_et.setOnKeyListener(this);

            allMealsListView = (ListView) rootView.findViewById(R.id.allMealsListView);
            allHealthyMealsListView = (ListView) rootView.findViewById(R.id.allHealthyMealsListView);
            allJustOKMealsListView = (ListView) rootView.findViewById(R.id.allJustOKMealsListView);
            allUnhealthyMealsListView = (ListView) rootView.findViewById(R.id.allUnhealthyMealsListView);

            footerView = View.inflate(getContext(), R.layout.see_more_layout, null);
            seeMoreButton = (Button) footerView.findViewById(R.id.seeMoreButton);
            seeMoreButton.setOnClickListener(this);
            progress2 = (ProgressBar) footerView.findViewById(R.id.progressBar2);

            tabHost = (TabHost) rootView.findViewById(R.id.tabHost);
            tabHost.setup();
            setupTab();

            weightGraph_ll.setVisibility(View.GONE);
            meals_ll.setVisibility(View.GONE);
            stepsGraph_ll.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
            if (selectedMenu == SELECTED_MENU_WEIGHT)
                postWeightData();
            // Perform action on key press
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        fromSubmitWeight = false;
        if (v.getId() == R.id.seeMoreButton) {
            if (selectedMenu == SELECTED_MENU_WEIGHT) {
                seeMoreWeightLogs();
            } else if (selectedMenu == SELECTED_MENU_STEPS) {
                seeMoreStepsLogs();
            } else {
                if (command == "get_meals_all") {
                    ApplicationEx.getInstance().setAllMealsCurrentPage(ApplicationEx.getInstance().getAllMealsCurrentPaged());
                    getAllMeals("get_meals_all", ApplicationEx.getInstance().getAllMealsCurrentPaged());
                } else if (command == "get_meals_healthy") {
                    ApplicationEx.getInstance().setAllHealthyMealsCurrentPage(ApplicationEx.getInstance().getAllHealthyMealsCurrentPaged());
                    getAllMeals("get_meals_healthy", ApplicationEx.getInstance().getAllHealthyMealsCurrentPaged());
                } else if (command == "get_meals_ok") {
                    ApplicationEx.getInstance().setAllOkMealsCurrentPage(ApplicationEx.getInstance().getAllOkMealsCurrentPaged());

                    getAllMeals("get_meals_ok", ApplicationEx.getInstance().getAllOkMealsCurrentPaged());
                } else if (command == "get_meals_unhealthy") {
                    ApplicationEx.getInstance().setAllUnhealthyMealsCurrentPage(ApplicationEx.getInstance().getAllUnhealthyMealsCurrentPaged());

                    getAllMeals("get_meals_unhealthy", ApplicationEx.getInstance().getAllUnhealthyMealsCurrentPaged());
                }
            }
            startProgressBar2();
            seeMoreButton.setVisibility(View.GONE);
        } else if (v.getId() == R.id.weight_1m_btn) {
            selectedDateRange = DATE_RANGE_1M;
            weight_1m.setSelected(true);
            weight_3m.setSelected(false);
            weight_1y.setSelected(false);
            weight_all.setSelected(false);
            dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateWeightGraph(DATE_RANGE_1M);
        } else if (v.getId() == R.id.weight_3m_btn) {
            selectedDateRange = DATE_RANGE_3M;
            weight_3m.setSelected(true);
            weight_1m.setSelected(false);
            weight_1y.setSelected(false);
            weight_all.setSelected(false);
            dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(true, true, dateRangeIndex));
            date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));

            dateRangeIndex = 0;
            updateWeightGraph(DATE_RANGE_3M);
        } else if (v.getId() == R.id.weight_1y_btn) {
            selectedDateRange = DATE_RANGE_1Y;
            weight_1y.setSelected(true);
            weight_1m.setSelected(false);
            weight_all.setSelected(false);
            weight_3m.setSelected(false);
            dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(true, false));
            dateRangeIndex = 0;
            date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateWeightGraph(DATE_RANGE_1Y);
        } else if (v.getId() == R.id.weight_all_btn) {
            selectedDateRange = DATE_RANGE_ALL;
            weight_all.setSelected(true);
            weight_1m.setSelected(false);
            weight_3m.setSelected(false);
            weight_1y.setSelected(false);
            date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(true, false));
            dateRangeIndex = 0;
            updateWeightGraph(DATE_RANGE_ALL);
        } else if (v.getId() == R.id.weight_submitButton) {
            postWeightData();
        } else if (v.getId() == R.id.steps_submitButton) {
            postStepsData();
        } else if (v == date_left_btn) {
            if (selectedDateRange == DATE_RANGE_1M) {
                dateRangeIndex++;
                dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, true, dateRangeIndex));

                weightGraphDataArrayList = AppUtil.get1MWeightList(false, dateRangeIndex);
                populateData();

            } else if (selectedDateRange == DATE_RANGE_3M) {
                dateRangeIndex++;
                dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, true, dateRangeIndex));
                weightGraphDataArrayList = AppUtil.get3MWeightList(false);
                populateData();
            } else if (selectedDateRange == DATE_RANGE_1Y || selectedDateRange == DATE_RANGE_ALL) {
                dateRangeIndex++;
                dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, true));

                weightGraphDataArrayList = AppUtil.get1YWeightList(false, dateRangeIndex);
                populateData();
            }
            date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_orangedark));

        } else if (v == date_right_btn) {
            if (selectedDateRange == DATE_RANGE_1M) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, false, dateRangeIndex));
                    weightGraphDataArrayList = AppUtil.get1MWeightList(false, dateRangeIndex);
                    populateData();
                    if (dateRangeIndex <= 0) {
                        date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            } else if (selectedDateRange == DATE_RANGE_3M) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, false, dateRangeIndex));
                    weightGraphDataArrayList = AppUtil.get3MWeightList(false);
                    populateData();

                    if (dateRangeIndex <= 0) {
                        date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            } else if (selectedDateRange == DATE_RANGE_1Y || selectedDateRange == DATE_RANGE_ALL) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, false));
                    weightGraphDataArrayList = AppUtil.get1YWeightList(false, dateRangeIndex);
                    populateData();
                    if (dateRangeIndex <= 0) {
                        date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            }
        } else if (v.getId() == R.id.weight_data_rl) {
            dismissKeyboard();
        } else if (v.getId() == R.id.steps_1w_btn) {
            selectedDateRange = DATE_RANGE_1W;
            steps_1w.setSelected(true);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1W);
        } else if (v.getId() == R.id.steps_1m_btn) {
            selectedDateRange = DATE_RANGE_1M;
            steps_1w.setSelected(false);
            steps_1m.setSelected(true);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1M);
        } else if (v.getId() == R.id.steps_3m_btn) {
            selectedDateRange = DATE_RANGE_3M;
            steps_1w.setSelected(false);
            steps_1m.setSelected(false);
            steps_3m.setSelected(true);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_3M);
        } else if (v.getId() == R.id.steps_1y_btn) {
            selectedDateRange = DATE_RANGE_1Y;
            steps_1w.setSelected(false);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(true);
            steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(true, false));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1Y);
        } else if (v == steps_date_left_btn) {
            if (selectedDateRange == DATE_RANGE_1M) {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, true, dateRangeIndex));
                stepsGraphDataArrayList = AppUtil.get1MStepsList(false, dateRangeIndex);
                populateStepsData();
            } else if (selectedDateRange == DATE_RANGE_1W) {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(false, true));
                stepsGraphDataArrayList = AppUtil.get1WStepsList(false);
                populateStepsData();
            } else if (selectedDateRange == DATE_RANGE_3M) {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, true, dateRangeIndex));
                stepsGraphDataArrayList = AppUtil.get3MStepsList(false);

                populateStepsData();
            } else if (selectedDateRange == DATE_RANGE_1Y) {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, true));
                stepsGraphDataArrayList = AppUtil.get1YStepsList(false, dateRangeIndex);

                populateStepsData();
//                updateStepsGraph(DATE_RANGE_1Y);
            }
            steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_orangedark));

        } else if (v == steps_date_right_btn) {
            if (selectedDateRange == DATE_RANGE_1W) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(false, false));
                    stepsGraphDataArrayList = AppUtil.get1WStepsList(false);
                    populateStepsData();

                    if (dateRangeIndex <= 0) {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            } else if (selectedDateRange == DATE_RANGE_1M) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, false, dateRangeIndex));
                    stepsGraphDataArrayList = AppUtil.get1MStepsList(false, dateRangeIndex);
                    populateStepsData();

                    if (dateRangeIndex <= 0) {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            } else if (selectedDateRange == DATE_RANGE_3M) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, false, dateRangeIndex));
                    stepsGraphDataArrayList = AppUtil.get3MStepsList(false);

                    populateStepsData();
//                    updateStepsGraph(DATE_RANGE_3M);

                    if (dateRangeIndex <= 0) {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            } else if (selectedDateRange == DATE_RANGE_1Y) {
                if (dateRangeIndex > 0) {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, false));
                    stepsGraphDataArrayList = AppUtil.get1YStepsList(false, dateRangeIndex);

                    populateStepsData();
//                    updateStepsGraph(DATE_RANGE_1Y);

                    if (dateRangeIndex <= 0) {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    }
                } else {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                }
            }
        }
    }

    private final BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase(context.getString(R.string.progress_refresh))) {
                selectedMenu = ApplicationEx.getInstance().selectedProgressMenu;

                switch (selectedMenu) {
                    case SELECTED_MENU_WEIGHT:
                        weightGraph_ll.setVisibility(View.VISIBLE);
                        meals_ll.setVisibility(View.GONE);
                        stepsGraph_ll.setVisibility(View.GONE);
                        stepsBarChart.setVisibility(View.GONE);
                        fromSubmitWeight = false;

                        break;
                    case SELECTED_MENU_STEPS:
                        weightGraph_ll.setVisibility(View.GONE);
                        meals_ll.setVisibility(View.GONE);
                        stepsGraph_ll.setVisibility(View.VISIBLE);
                        stepsBarChart.setVisibility(View.VISIBLE);
                        fromSubmitWeight = false;

                        getLatestStepsData();
                        break;
                    case SELECTED_MENU_MEALS:
                        weightGraph_ll.setVisibility(View.GONE);
                        meals_ll.setVisibility(View.VISIBLE);
                        stepsGraph_ll.setVisibility(View.GONE);
                        stepsBarChart.setVisibility(View.GONE);
                        mealLineChart.setVisibility(View.VISIBLE);
                        fromSubmitWeight = false;

                        ((ScrollView)rootView.findViewById(R.id.progress_meals_sv)).smoothScrollTo(0,0);

                        loadMealsUI();
                        break;
                    default:
                        weightGraph_ll.setVisibility(View.VISIBLE);
                        meals_ll.setVisibility(View.GONE);
                        stepsGraph_ll.setVisibility(View.GONE);
                        break;
                }
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.WEIGHT_GRAPH_BROADCAST_DELETE))) {
                deleteWeightData(ApplicationEx.getInstance().currentWeight);
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.WEIGHT_GRAPH_BROADCAST_EDIT))) {
                updateWeightData(ApplicationEx.getInstance().currentWeight);
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.STEPS_GRAPH_BROADCAST_DELETE))) {
                deleteStepsData(ApplicationEx.getInstance().currentSteps);
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.STEPS_GRAPH_BROADCAST_EDIT))) {
                updateStepsData(ApplicationEx.getInstance().currentSteps);
            }
        }
    };

    private void loadMealsUI() {
        refreshMealData();
        changeTab = false;

        startUpdate();
    }

    //meals
    private void setupTab() {
        // Tab for All Meals
        TabHost.TabSpec allMealsSpec = tabHost.newTabSpec(getString(R.string.ALL_BUTTON)).setIndicator(getString(R.string.ALL_BUTTON)).setContent(R.id.tab_all);
        // Tab for Healthy Meals
        TabHost.TabSpec healthyMealsSpec = tabHost.newTabSpec(getString(R.string.HEALTHY_BUTTON)).setIndicator(getString(R.string.HEALTHY_BUTTON)).setContent(R.id.tab_healthy);
        // Tab for Just OK Meals
        TabHost.TabSpec justOkMealsSpec = tabHost.newTabSpec(getString(R.string.JUSTOK_BUTTON)).setIndicator(getString(R.string.JUSTOK_BUTTON)).setContent(R.id.tab_justok);
        // Tab for Unhealthy Meals
        TabHost.TabSpec unhealthyMealsSpec = tabHost.newTabSpec(getString(R.string.UNHEALTHY_BUTTON)).setIndicator(getString(R.string.UNHEALTHY_BUTTON)).setContent(R.id.tab_unhealthy);

        // Adding all TabSpec to TabHost
        tabHost.addTab(allMealsSpec); // Adding all meals tab
        tabHost.addTab(healthyMealsSpec); // Adding healthy meals tab
        tabHost.addTab(justOkMealsSpec); // Adding just ok meals tab
        tabHost.addTab(unhealthyMealsSpec); // Adding unhealthy meals tab

        /* Change the color of tab when selected */
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                setTabColor(tabHost);
                startProgressBar1();
                changeTab = true;
                seeMoreButton.setVisibility(View.GONE);
                switch (tabHost.getCurrentTab()) {
                    case 0:
                        getAllMeals("get_meals_all", ApplicationEx.getInstance().getAllMealsCurrentPaged());
                        break;
                    case 1:
                        getAllMeals("get_meals_healthy", ApplicationEx.getInstance().getAllHealthyMealsCurrentPaged());
                        break;
                    case 2:
                        getAllMeals("get_meals_ok", ApplicationEx.getInstance().getAllOkMealsCurrentPaged());
                        break;
                    case 3:
                        getAllMeals("get_meals_unhealthy", ApplicationEx.getInstance().getAllUnhealthyMealsCurrentPaged());
                        break;
                    default:
                        getAllMeals("get_meals_all", ApplicationEx.getInstance().getAllMealsCurrentPaged());
                        break;
                }
            }
        });

        /* Improve UI */
        for (int i = 0; i < 4; i++) {
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_rounded_corners);
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (30 * this.getResources().getDisplayMetrics().density);
        }

        /* Set Default Selected Tab */
        tabHost.getTabWidget().setCurrentTab(0);
        setTabColor(tabHost);
    }

    private void refreshMealData() {
        MealDAO dao = new MealDAO(this.context, null);

        for (Meal tempMeal : ApplicationEx.getInstance().allMealList.values()) {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            dao.updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allHealthyMealList.values()) {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            dao.updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allOKMealList.values()) {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            dao.updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allUnhealthyList.values()) {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            dao.updateMeal(tempMeal);
        }

        ApplicationEx.getInstance().allMealList.clear();
        ApplicationEx.getInstance().allHealthyMealList.clear();
        ApplicationEx.getInstance().allOKMealList.clear();
        ApplicationEx.getInstance().allUnhealthyList.clear();
    }

    private void startUpdate() {
        if (getActivity() == null)
            return;

        this.getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ApplicationEx.getInstance().resetAllMealsCurrentPage();
                ApplicationEx.getInstance().resetAllHealthyMealsCurrentPage();
                ApplicationEx.getInstance().resetAllOkMealsCurrentPage();
                ApplicationEx.getInstance().resetAllUnhealthyMealsCurrentPage();
                getAllMeals("get_meals_all", ApplicationEx.getInstance().getAllMealsCurrentPaged());
            }
        });
    }

    private void getAllMeals(String command, int page) {
        if (getAllMealsController == null) {
            getAllMealsController = new GetAllMealsController(getActivity(), this);
        }

        UserProfileDAO dao = new UserProfileDAO(getActivity(), null);
        UserProfile tempUserProfile = dao.getUserProfile();

        getAllMealsController.getAllMeals(command, tempUserProfile.getRegID(), page, 10);
    }

    /* Sort meal in descending order (newest to oldest) */
    private void sort(final ArrayList<Meal> mealObj) {
        if (mealObj != null && mealObj.size() > 0) {
            Collections.sort(mealObj, new Comparator<Meal>() {
                public int compare(Meal meal1, Meal meal2) {
                    return meal2.meal_creation_date.compareTo(meal1.meal_creation_date);
                }
            });
        }
    }

    private void startProgressBar1() {
        progress1.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar1() {
        progress1.setVisibility(View.GONE);
    }

    private void startProgressBar2() {
        progress2.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar2() {
        progress2.setVisibility(View.GONE);
    }

    private void hideSeeMoreButton(ListView listView) {
        listView.removeFooterView(footerView);
        seeMoreButton.setVisibility(View.GONE);
    }

    private void showSeeMoreButton(ListView listView) {
        listView.addFooterView(footerView);
        seeMoreButton.setVisibility(View.VISIBLE);
    }

    /**
     * GetAllMealsListener
     **/
    public void getAllMealsSuccess(String response, String command) {
        this.command = command;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopProgressBar1();
                stopProgressBar2();

                String displayString = AppUtil.getTotalMealsDisplayString(context, ApplicationEx.getInstance().registrationDate);
                final SpannableStringBuilder str = new SpannableStringBuilder(displayString);
                str.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, AppUtil.getEndOffsetMeal(context, displayString), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                ((TextView)rootView.findViewById(R.id.totalMeals_reg_tv)).setText(str);
                createMealGraph();
                updateMealsUI();
            }
        });
    }

    public void getAllMealsFailedWithError(MessageObj response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopProgressBar1();
                stopProgressBar2();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateMealsUI() {

        mealLineChart.setVisibility(View.VISIBLE);

        showListView(command);

        /*Meals Count*/
        String mealCountString = "";
        String mealRatedDescString = "";
        double percentageValue;

        ArrayList<Meal> mealListWithPhoto;
        ArrayList<Meal> mealListWithoutPhoto;
        ArrayList<Meal> tempMealList;
        ArrayList<Meal> mealList;
        if (command == "get_meals_all") {
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.ALL_MEALS_RATED),
                    ApplicationEx.getInstance().getAllMealsRated());
            mealRatedDescString = getString(R.string.YOUR_MEAL_HISTORY);

            /*ListView*/

            tempMealList = new ArrayList<>(ApplicationEx.getInstance().allMealList.values());

            mealListWithPhoto = new ArrayList<>();
            mealListWithoutPhoto = new ArrayList<>();
            mealList = new ArrayList<>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList) {
                if (mealObj.photos != null) {
                    mealListWithPhoto.add(mealObj);
                } else {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0) {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0) {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allMealsListView.getAdapter() == null || changeTab) {
                adapter = new MealWithRatingsAdapter(context, mealList);

                adapter.notifyDataSetChanged();
                allMealsListView.invalidateViews();
                allMealsListView.refreshDrawableState();

                allMealsListView.setAdapter(adapter);

                changeTab = false;
            } else if (mealList.size() > 0 && !ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating()) {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10) {
                hideSeeMoreButton(allMealsListView);
            } else {
                hideSeeMoreButton(allMealsListView);

                showSeeMoreButton(allMealsListView);
            }

            allMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    ApplicationEx.getInstance().currentMealView = (Meal) allMealsListView.getAdapter().getItem(position);

                    Intent mainIntent = new Intent(context, MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });

            AppUtil.setListViewHeightBasedOnChildren(allMealsListView);

        } else if (command == "get_meals_healthy") {
            percentageValue = ((double) ApplicationEx.getInstance().getAllHealthyMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.HEALTHY_MEALS),
                    ApplicationEx.getInstance().getAllHealthyMealsRated());
            mealCountString = mealCountString.concat(" (" + String.format("%.1f", percentageValue) + " %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_AS_OFTEN_AS_YOU_LIKE);

            /*ListView*/

            tempMealList = new ArrayList<>(ApplicationEx.getInstance().allHealthyMealList.values());

            mealListWithPhoto = new ArrayList<>();
            mealListWithoutPhoto = new ArrayList<>();
            mealList = new ArrayList<>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList) {
                if (mealObj.photos != null) {
                    mealListWithPhoto.add(mealObj);
                } else {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0) {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0) {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allHealthyMealsListView.getAdapter() == null || changeTab) {
                adapter = new MealWithRatingsAdapter(getActivity(), mealList);

                adapter.notifyDataSetChanged();
                allHealthyMealsListView.invalidateViews();
                allHealthyMealsListView.refreshDrawableState();

                allHealthyMealsListView.setAdapter(adapter);

                changeTab = false;
            } else if (mealList.size() > 0 && !ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating()) {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10) {
                hideSeeMoreButton(allHealthyMealsListView);
            } else {
                hideSeeMoreButton(allHealthyMealsListView);

                showSeeMoreButton(allHealthyMealsListView);
            }

            allHealthyMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    ApplicationEx.getInstance().currentMealView = (Meal) allHealthyMealsListView.getAdapter().getItem(position);

                    Intent mainIntent = new Intent(context, MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });

            AppUtil.setListViewHeightBasedOnChildren(allHealthyMealsListView);

        } else if (command == "get_meals_ok") {
            percentageValue = ((double) ApplicationEx.getInstance().getAllJustOkMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.JUST_OK_MEALS),
                    ApplicationEx.getInstance().getAllJustOkMealsRated());
            mealCountString = mealCountString.concat(" (" + String.format("%.1f", percentageValue) + " %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_NOT_TOO_OFTEN);

            /*ListView*/

            tempMealList = new ArrayList<>(ApplicationEx.getInstance().allOKMealList.values());

            mealListWithPhoto = new ArrayList<>();
            mealListWithoutPhoto = new ArrayList<>();
            mealList = new ArrayList<>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList) {
                if (mealObj.photos != null) {
                    mealListWithPhoto.add(mealObj);
                } else {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0) {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0) {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allJustOKMealsListView.getAdapter() == null || changeTab) {
                adapter = new MealWithRatingsAdapter(getContext(), mealList);

                adapter.notifyDataSetChanged();
                allJustOKMealsListView.invalidateViews();
                allJustOKMealsListView.refreshDrawableState();

                allJustOKMealsListView.setAdapter(adapter);

                changeTab = false;
            } else if (mealList.size() > 0 && !ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating()) {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10) {
                hideSeeMoreButton(allJustOKMealsListView);
            } else {
                hideSeeMoreButton(allJustOKMealsListView);

                showSeeMoreButton(allJustOKMealsListView);
            }

            allJustOKMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    ApplicationEx.getInstance().currentMealView = (Meal) allJustOKMealsListView.getAdapter().getItem(position);

                    Intent mainIntent = new Intent(context, MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });

            AppUtil.setListViewHeightBasedOnChildren(allJustOKMealsListView);

        } else if (command == "get_meals_unhealthy") {
            percentageValue = ((double) ApplicationEx.getInstance().getAllUnhealthyMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.UNHEALTHY_MEALS),
                    ApplicationEx.getInstance().getAllUnhealthyMealsRated());
            mealCountString = mealCountString.concat(" (" + String.format("%.1f", percentageValue) + " %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_IN_MODERATION);

            /*ListView*/

            tempMealList = new ArrayList<>(ApplicationEx.getInstance().allUnhealthyList.values());

            mealListWithPhoto = new ArrayList<>();
            mealListWithoutPhoto = new ArrayList<>();
            mealList = new ArrayList<>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList) {
                if (mealObj.photos != null) {
                    mealListWithPhoto.add(mealObj);
                } else {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0) {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0) {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allUnhealthyMealsListView.getAdapter() == null || changeTab) {
                adapter = new MealWithRatingsAdapter(getContext(), mealList);

                adapter.notifyDataSetChanged();
                allUnhealthyMealsListView.invalidateViews();
                allUnhealthyMealsListView.refreshDrawableState();

                allUnhealthyMealsListView.setAdapter(adapter);

                changeTab = false;
            } else if (mealList.size() > 0 && !ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating()) {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10) {
                hideSeeMoreButton(allUnhealthyMealsListView);
            } else {
                hideSeeMoreButton(allUnhealthyMealsListView);

                showSeeMoreButton(allUnhealthyMealsListView);
            }

            allUnhealthyMealsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    ApplicationEx.getInstance().currentMealView = (Meal) allUnhealthyMealsListView.getAdapter().getItem(position);

                    Intent mainIntent = new Intent(context, MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });

            AppUtil.setListViewHeightBasedOnChildren(allUnhealthyMealsListView);

        }

        mealCountTextView.setText(mealCountString);
        mealRatedDesc.setText(mealRatedDescString);

        ((ScrollView)rootView.findViewById(R.id.progress_meals_sv)).smoothScrollTo(0,0);

    }

    private void showListView(String command) {
        if (command == "get_meals_all") {
            allMealsListView.setVisibility(View.VISIBLE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        } else if (command == "get_meals_healthy") {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.VISIBLE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        } else if (command == "get_meals_ok") {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.VISIBLE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        } else if (command == "get_meals_unhealthy") {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.VISIBLE);
        }
    }

    private void setTabColor(TabHost tabhost) {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.TRANSPARENT);
            TextView unSelectedTextView = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            unSelectedTextView.setTextColor(Color.parseColor("#5D5D5D"));
            unSelectedTextView.setTypeface(null, Typeface.BOLD);
            unSelectedTextView.setAllCaps(false);
        }

        //for selected tab
        tabHost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundResource(R.drawable.rounded_button_orange_borderless);
        TextView selectedTabTextView = (TextView) tabhost.getCurrentTabView().findViewById(android.R.id.title);
        selectedTabTextView.setTextColor(Color.WHITE);
        selectedTabTextView.setTypeface(null, Typeface.BOLD);
        selectedTabTextView.setAllCaps(false);
    }

    public void BitmapDownloadSuccess(String photoId, String mealId) {
    }

    public void BitmapDownloadFailedWithError(MessageObj response) {
    }

    public void getWeightGraphSuccess(String response) {
        latestWeight = ApplicationEx.getInstance().latestWeight;
        weightLogsList = AppUtil.getWeightLogsList();

        String currentWeightString = getResources().getString(R.string.WEIGHT_GRAPH_CURRENT_WEIGHT_ASOF) + " " + AppUtil.getWeightDateFormat(latestWeight.end_datetime);
        String currentWeight = String.valueOf(latestWeight.currentWeightGrams / 1000);
        final String stringToDisplay = currentWeightString.replace("%@", currentWeight);

        final double weightLost = initialWeight - latestWeight.currentWeightGrams / 1000;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                weightGraph_ll.setVisibility(View.VISIBLE);
                weightProgressBar.setVisibility(View.GONE);

                currentWeightTitle.setText(stringToDisplay);
                dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, 1));

                ((TextView) rootView.findViewById(R.id.targetWeightValue_tv)).setText(String.valueOf(targetWeight) + " kg");
                ((TextView) rootView.findViewById(R.id.lostWeightValue_tv)).setText(String.format("%.1f", weightLost) + " kg");
                ((TextView) rootView.findViewById(R.id.bmiValue_tv)).setText(String.format("%.1f", latestWeight.BMI / 1000));

                if (weightLogsList.size() < 10) {
                    rootView.findViewById(R.id.weight_graph_date_ll).setVisibility(View.GONE);
                    updateWeightGraph(DATE_RANGE_ALL);
                    populateWeightLogs();
                } else {
                    rootView.findViewById(R.id.weight_graph_date_ll).setVisibility(View.VISIBLE);
                    populateWeightLogs();
                    weight_1m.setSelected(false);
                    weight_3m.setSelected(false);
                    weight_1y.setSelected(false);
                    weight_all.setSelected(false);

                    if (fromSubmitWeight) {
                        selectedDateRange = DATE_RANGE_1M;
                        weight_1m.setSelected(true);
                        dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
                    } else {
                        if (AppUtil.isWeightDataHistory1Year()) {
                            selectedDateRange = DATE_RANGE_1Y;
                            weight_1y.setSelected(true);
                            weight_all.setVisibility(View.VISIBLE);
                            dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(true, true));
                        } else if (AppUtil.isWeightDataHistory3MonthsMore()) {
                            selectedDateRange = DATE_RANGE_3M;
                            weight_3m.setSelected(true);
                            weight_all.setVisibility(View.GONE);
                            dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(true, true, dateRangeIndex));
                        } else if (AppUtil.isWeightDataHistory3MonthsLess()) {
                            selectedDateRange = DATE_RANGE_1M;
                            weight_1m.setSelected(true);
                            dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
                        } else {
                            selectedDateRange = DATE_RANGE_ALL;
                            weight_all.setSelected(true);
                        }
                    }
                    updateWeightGraph(selectedDateRange);
                }
            }
        });
    }

    public void getWeightGraphFailedWithError(MessageObj response) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void startProgress() {
    }

    public void stopProgress() {
    }


    /**
     * Weightloss Graph
     **/
    private void populateData() {
        lowestWeight = AppUtil.getLowestWeight(weightGraphDataArrayList) / 1000;
        heighestWeight = AppUtil.getHeighestWeight(weightGraphDataArrayList) / 1000;
        createGraph(weightGraphDataArrayList);
    }

    /**
     * Steps Graph
     **/
    private void populateStepsData() {

        heighestSteps = AppUtil.getHeighestSteps(stepsGraphDataArrayList);
        lowestSteps = AppUtil.getLowestSteps(stepsGraphDataArrayList);

        System.out.println("heighest steps: " + AppUtil.getHeighestSteps(stepsGraphDataArrayList) + " lowest steps: " + AppUtil.getLowestSteps(stepsGraphDataArrayList));
        if (selectedDateRange == DATE_RANGE_1W) {
            ((TextView) rootView.findViewById(R.id.calories_burned_value_tv)).setText(String.format("%.0f", AppUtil.getTotalCalories(stepsGraphDataArrayList)));
            ((TextView) rootView.findViewById(R.id.km_travelled_value_tv)).setText(String.format("%.1f", AppUtil.getTotalKmTravelled(stepsGraphDataArrayList)));

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String stepsString = formatter.format(AppUtil.getTotalSteps(stepsGraphDataArrayList));

            ((TextView) rootView.findViewById(R.id.steps_taken_value_tv)).setText(stepsString);
        } else {
            ((TextView) rootView.findViewById(R.id.calories_burned_value_tv)).setText(String.format("%.0f", ApplicationEx.getInstance().currentTotalCalories));
            ((TextView) rootView.findViewById(R.id.km_travelled_value_tv)).setText(String.format("%.1f", ApplicationEx.getInstance().currentKmTravelled));

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String stepsString = formatter.format(ApplicationEx.getInstance().currentTotalSteps);

            ((TextView) rootView.findViewById(R.id.steps_taken_value_tv)).setText(stepsString);
        }

        createStepsGraph(stepsGraphDataArrayList);
    }

    private void createGraph(List<Weight> graphMealList) {

        weightLineChart.clear();

        weightLineChart.setOnChartGestureListener(this);
        weightLineChart.setOnChartValueSelectedListener(this);
        weightLineChart.setDrawGridBackground(false);

        // no description text
        weightLineChart.setDescription("");
        weightLineChart.setNoDataTextDescription(getResources().getString(R.string.WEIGHT_GRAPH_NO_DATA));

        // enable value highlighting
        weightLineChart.setHighlightEnabled(true);

        // enable touch gestures
        weightLineChart.setTouchEnabled(false);

        // enable scaling and dragging
        weightLineChart.setDragEnabled(true);
        weightLineChart.setScaleEnabled(true);
        weightLineChart.setScaleXEnabled(false);
        weightLineChart.setScaleYEnabled(true);
        weightLineChart.getLegend().setEnabled(false);
        weightLineChart.setDrawBorders(true);
        weightLineChart.setBorderWidth(0.5f);
        weightLineChart.setBorderColor(Color.LTGRAY);

        // if disabled, scaling can be done on x- and y-axis separately
        weightLineChart.setPinchZoom(true);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine((float) targetWeight, "Objective: " + (int) targetWeight + "kg");
        llXAxis.setLineWidth(2f);
        llXAxis.setLineColor(Color.parseColor("#ffa135"));
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        llXAxis.setTextSize(8f);
        llXAxis.setTextColor(Color.GRAY);

        XAxis xAxis = weightLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setSpaceBetweenLabels(1);

        YAxis rightAxis = weightLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = weightLineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawAxisLine(true);
        //set grid color to light gray - 10/20/15
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.addLimitLine(llXAxis);

        if (heighestWeight > initialWeight) {
            leftAxis.setAxisMaxValue((float) heighestWeight + 3);
        } else {
            leftAxis.setAxisMaxValue((float) initialWeight + 3);
        }

        if (lowestWeight < targetWeight) {
            leftAxis.setAxisMinValue((float) lowestWeight - 3);
        } else {
            leftAxis.setAxisMinValue((float) targetWeight - 3);
        }

        leftAxis.setTextColor(Color.GRAY);

        dataToGraphArray(graphMealList);

        weightLineChart.setBackgroundColor(Color.WHITE);

        leftAxis.setStartAtZero(false);

        weightLineChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                System.out.println("y value: " + v);

                return String.valueOf((int) Math.floor(v));
            }
        });

        System.out.println("entries: " + weightLineChart.getAxisLeft().mEntries.length);

        System.out.println("setAxisMinValue: " + weightLineChart.getAxisLeft().getAxisMinValue() + " max: " + weightLineChart.getAxisLeft().getAxisMaxValue());


        weightLineChart.invalidate();
    }

    private void createMealGraph() {
        mealLineChart.clear();

        mealLineChart.setOnChartGestureListener(this);
        mealLineChart.setOnChartValueSelectedListener(this);
        mealLineChart.setDrawGridBackground(false);

        // no description text
        mealLineChart.setDescription("");
        mealLineChart.setNoDataTextDescription(getResources().getString(R.string.WEIGHT_GRAPH_NO_DATA));

        // enable value highlighting
        mealLineChart.setHighlightEnabled(true);

        // enable touch gestures
        mealLineChart.setTouchEnabled(false);

        // enable scaling and dragging
        mealLineChart.setDragEnabled(true);
        mealLineChart.setScaleEnabled(true);
        mealLineChart.setScaleXEnabled(false);
        mealLineChart.setScaleYEnabled(true);
        mealLineChart.getLegend().setEnabled(false);
        mealLineChart.setDrawBorders(true);
        mealLineChart.setBorderWidth(0.5f);
        mealLineChart.setBorderColor(Color.LTGRAY);

        // if disabled, scaling can be done on x- and y-axis separately
        mealLineChart.setPinchZoom(true);

        XAxis xAxis = mealLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setGridLineWidth(0.5f);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setTextSize(10.0f);
        xAxis.setAvoidFirstLastClipping(true);

        YAxis rightAxis = mealLineChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = mealLineChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawAxisLine(true);
        //set grid color to light gray - 10/20/15
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);

        leftAxis.setTextColor(Color.GRAY);

        int currentTotalMeals = AppUtil.getTotalMealsSinceRegDate(ApplicationEx.getInstance().registrationDate);

        if (currentTotalMeals > 0){
            leftAxis.setAxisMaxValue((float) currentTotalMeals + 5);
        }

        try{
            if (currentTotalMeals-15>0) {
                leftAxis.setAxisMinValue((float) currentTotalMeals - 15);
                leftAxis.setStartAtZero(false);
                leftAxis.setLabelCount(7, false);
            }else{
                leftAxis.setAxisMinValue(0.0f);
                leftAxis.setStartAtZero(true);
            }
        }catch (NullPointerException e){
            leftAxis.setStartAtZero(true);
            e.printStackTrace();
        }

        List<String> dayArray = AppUtil.getDayArrayMeals();
        ArrayList<Entry> valsList = new ArrayList<>();

        int minTotalMeals = (AppUtil.getTotalMealsToday(ApplicationEx.getInstance().registrationDate)) - 12;

        for (int i = 4; i >= 0; i--) {
            if (currentTotalMeals > 0) {
                Entry c1e1 = new Entry((float) minTotalMeals + 3*i , i);
                valsList.add(c1e1);
            }
        }

        LineDataSet dataSet1 = new LineDataSet(valsList, "Data 1");
        dataSet1.setDrawValues(false);
        dataSet1.setLineWidth(2.0f);
        dataSet1.setColor(Color.parseColor("#4CB6EB"));
        dataSet1.setCircleColor(Color.parseColor("#4CB6EB"));
        dataSet1.setCircleSize(4.0f);
        dataSet1.setCircleColorHole(Color.WHITE);
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setDrawCubic(true);

        LineData data = new LineData(dayArray, dataSet1);

        mealLineChart.setData(data);
        mealLineChart.setBackgroundColor(Color.WHITE);

        mealLineChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf((int) Math.floor(v));
            }
        });

        mealLineChart.invalidate();
    }

    /**
     * @param graphMealList - list of GraphMeals to be plotted in line graph
     **/
    private void dataToGraphArray(List<Weight> graphMealList) {

        List<String> dayArray = new ArrayList<>();
        ArrayList<Entry> valsList = new ArrayList<>();

        for (int i = 0; i < graphMealList.size(); i++) {
            if (graphMealList.get(i).currentWeightGrams > 0) {
                Entry c1e1 = new Entry((float) graphMealList.get(i).currentWeightGrams / 1000, i);
                valsList.add(c1e1);
            }
            String dayData = "";

            if (selectedDateRange == DATE_RANGE_1M) {
                dayData = new SimpleDateFormat("MM/dd").format(graphMealList.get(i).start_datetime);
            } else {
                dayData = new SimpleDateFormat("MMM").format(graphMealList.get(i).start_datetime);
            }
            dayArray.add(dayData);
        }

        LineDataSet dataSet1 = new LineDataSet(valsList, "Data 1");
        dataSet1.setDrawValues(false);
        dataSet1.setLineWidth(2.0f);
        dataSet1.setColor(Color.parseColor("#4CB6EB"));
        dataSet1.setCircleColor(Color.parseColor("#4CB6EB"));
        dataSet1.setCircleSize(3.0f);
        dataSet1.setCircleColorHole(Color.parseColor("#4CB6EB"));
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet1.setDrawCubic(true);

        LineData data = new LineData(dayArray, dataSet1);

        weightLineChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture lastPerformedGesture) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture lastPerformedGesture) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        // TODO Auto-generated method stub

    }

    private void updateWeightGraph(int range) {
        switch (range) {
            case DATE_RANGE_1M:
                weightGraphDataArrayList = AppUtil.get1MWeightList(true, 0);
                break;
            case DATE_RANGE_3M:
                weightGraphDataArrayList = AppUtil.get3MWeightList(true);
                break;
            case DATE_RANGE_1Y:
                weightGraphDataArrayList = AppUtil.get1YWeightList(true, 0);
                break;
            case DATE_RANGE_ALL:
                weightGraphDataArrayList = AppUtil.getAllWeightList();
                break;
            default:
                break;
        }
        populateData();
    }

    private void postWeightData() {
        if (weightDataController == null) {
            weightDataController = new WeightDataController(context, this, this);
        }


        String weightInput_tv = ((EditText) rootView.findViewById(R.id.weight_enter_et)).getText().toString();

        if (weightInput_tv.length() > 0) {
            if (weightInput_tv.contains(",")) {
                weightInput_tv.replace(",", ".");
            }
        } else {
            return;
        }

//        double weightInput_double = Double.parseDouble(((EditText) rootView.findViewById(R.id.weight_enter_et)).getText().toString());
        double weightInput_double = Double.parseDouble(weightInput_tv);

        final String weightInput = String.valueOf(weightInput_double * 1000);
        long unixTime = System.currentTimeMillis() / 1000L;
        final String toDate = String.valueOf(unixTime);

        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.VISIBLE);
                weight_submitBtn.setEnabled(false);
                dismissKeyboard();
                weight_enter_et.setText("");
                weightDataController.postGraphData(ApplicationEx.getInstance().userProfile.getRegID(), weightInput, toDate);
            }
        });
    }

    private void postStepsData() {
        if (stepsDataController == null) {
            stepsDataController = new StepsDataController(context, this, this);
        }

        try {
            double stepsInput_double = Double.parseDouble(((EditText) rootView.findViewById(R.id.steps_enter_et)).getText().toString());

            final String stepsInput = String.valueOf(stepsInput_double);
            long unixTime = System.currentTimeMillis() / 1000L;
            final String toDate = String.valueOf(unixTime);

            this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weightProgressBar.setVisibility(View.VISIBLE);
                    steps_submitBtn.setEnabled(false);
                    dismissKeyboard();
                    steps_enter_et.setText("");
                    stepsDataController.postGraphData(ApplicationEx.getInstance().userProfile.getRegID(), stepsInput, toDate);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
            ((EditText) rootView.findViewById(R.id.steps_enter_et)).setText("");
        }
    }

    private void deleteWeightData(final Weight weightToDelete) {
        if (weightDataController == null) {
            weightDataController = new WeightDataController(context, this, this);
        }

        weightProgressBar.setVisibility(View.VISIBLE);
        weightDataController.deleteGraphData(ApplicationEx.getInstance().userProfile.getRegID(), weightToDelete.activity_id, Double.toString(weightToDelete.currentWeightGrams), weightToDelete.start_datetime.toString());
    }

    private void updateWeightData(Weight weightToUpdate) {
        if (weightDataController == null) {
            weightDataController = new WeightDataController(context, this, this);
        }

        weightProgressBar.setVisibility(View.VISIBLE);
        weightDataController.updateGraphData(ApplicationEx.getInstance().userProfile.getRegID(), weightToUpdate.activity_id, Double.toString(weightToUpdate.currentWeightGrams), String.valueOf(AppUtil.dateToUnixTimestamp(weightToUpdate.start_datetime)));
    }

    private void deleteStepsData(final Steps stepsToDelete) {
        if (stepsDataController == null) {
            stepsDataController = new StepsDataController(context, this, this);
        }

        weightProgressBar.setVisibility(View.VISIBLE);
        stepsDataController.deleteGraphData(ApplicationEx.getInstance().userProfile.getRegID(), stepsToDelete.activity_id, stepsToDelete.steps_count, String.valueOf(AppUtil.dateToUnixTimestamp(stepsToDelete.start_datetime)));
    }

    private void updateStepsData(Steps stepsToUpdate) {
        if (stepsDataController == null) {
            stepsDataController = new StepsDataController(context, this, this);
        }

        weightProgressBar.setVisibility(View.VISIBLE);
        stepsDataController.updateGraphData(ApplicationEx.getInstance().userProfile.getRegID(), stepsToUpdate.activity_id, stepsToUpdate.steps_count, String.valueOf(AppUtil.dateToUnixTimestamp(stepsToUpdate.start_datetime)));
    }


    public void postWeightDataSuccess(String response) {
        if (weighLossGraphController == null) {
            weighLossGraphController = new WeightLossGraphController(context, this, this);
        }

        fromSubmitWeight = true;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.GONE);
                weight_submitBtn.setEnabled(true);
                weighLossGraphController.getGraphData("get_weight", ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    public void postWeightDataFailedWithError(MessageObj response) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.GONE);
                weight_submitBtn.setEnabled(true);
            }
        });
    }

    @Override
    public void getWeightDataSuccess(Weight response) {

    }

    @Override
    public void getWeightDataFailedWithError(MessageObj response) {

    }

    private void populateWeightLogs() {
        Collections.sort(weightLogsList, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        if (fromSubmitWeight) {
            weightLogsListCurrentDisplay.clear();
            selectedDateRange = DATE_RANGE_1M;
            weight_1m.setSelected(true);
            weight_3m.setSelected(false);
            weight_1y.setSelected(false);
            weight_all.setSelected(false);

            dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            date_right_btn.setTextColor(getResources().getColor(R.color.text_darkgray));

            updateWeightGraph(DATE_RANGE_1M);
        }

        if (weightLogsList.size() > 7) {
            showSeeMoreButton(weightLogsListView);
            stopProgressBar2();

            for (int i = 0; i < 7; i++) {
                weightLogsListCurrentDisplay.add(weightLogsList.get(i));
            }

        } else {
            hideSeeMoreButton(weightLogsListView);
            weightLogsListCurrentDisplay = weightLogsList;
        }

        if (weightLogsListView.getAdapter() == null) {
            weightLogsListAdapter = new WeightLogsListAdapter(context, weightLogsListCurrentDisplay);
            weightLogsListAdapter.notifyDataSetChanged();
            weightLogsListView.invalidateViews();
            weightLogsListView.refreshDrawableState();
            weightLogsListView.setAdapter(weightLogsListAdapter);
        } else {
            weightLogsListAdapter.updateItems(weightLogsListCurrentDisplay);
        }
        AppUtil.setListViewHeightBasedOnChildren(weightLogsListView);
    }

    private void seeMoreWeightLogs() {
        Collections.sort(weightLogsList, new Comparator<Weight>() {
            public int compare(Weight o1, Weight o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        stopProgressBar2();

        hideSeeMoreButton(weightLogsListView);
        weightLogsListCurrentDisplay = weightLogsList;

        if (weightLogsListView.getAdapter() == null) {
            weightLogsListAdapter = new WeightLogsListAdapter(context, weightLogsListCurrentDisplay);
            weightLogsListAdapter.notifyDataSetChanged();
            weightLogsListView.invalidateViews();
            weightLogsListView.refreshDrawableState();
            weightLogsListView.setAdapter(weightLogsListAdapter);
        } else {
            weightLogsListAdapter.updateItems(weightLogsListCurrentDisplay);
        }
        AppUtil.setListViewHeightBasedOnChildren(weightLogsListView);
    }

    private void seeMoreStepsLogs() {
        Collections.sort(stepsLogsList, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        stopProgressBar2();

        hideSeeMoreButton(stepsLogsListView);
        stepsLogsListCurrentDisplay = stepsLogsList;

        if (stepsLogsListView.getAdapter() == null) {
            stepsLogsListAdapter = new StepsLogsListAdapter(context, stepsLogsListCurrentDisplay);
            stepsLogsListAdapter.notifyDataSetChanged();
            stepsLogsListView.invalidateViews();
            stepsLogsListView.refreshDrawableState();
            stepsLogsListView.setAdapter(stepsLogsListAdapter);
        } else {
            stepsLogsListAdapter.updateItems(stepsLogsListCurrentDisplay);
        }
        AppUtil.setListViewHeightBasedOnChildren(stepsLogsListView);
    }

    private void getLatestWeightData() {
        if (weighLossGraphController == null) {
            weighLossGraphController = new WeightLossGraphController(context, this, this);
        }
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weighLossGraphController.getGraphData("get_weight", ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    private void getLatestStepsData() {
        if (stepsGraphController == null) {
            stepsGraphController = new StepsGraphController(context, this, this);
        }

        // here you check the value of getActivity() and break up if needed
        if (getActivity() == null)
            return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.VISIBLE);
                stepsGraphController.getStepsGraphData("get_steps", ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (selectedMenu == SELECTED_MENU_WEIGHT) {
            imm.hideSoftInputFromWindow(weight_enter_et.getWindowToken(), 0);
        } else {
            imm.hideSoftInputFromWindow(steps_enter_et.getWindowToken(), 0);
        }
    }

    public void getStepsGraphSuccess(String response) {
        Steps latestSteps = ApplicationEx.getInstance().latestSteps;

        if (!fromSubmitWeight)
            stepsLogsListCurrentDisplay.clear();

        stepsLogsList = AppUtil.getStepsLogsList();

        String currenStepsString = getResources().getString(R.string.LAST_RECORDED_STEPS) + " " + AppUtil.getWeightDateFormat(latestSteps.end_datetime);
        final String stringToDisplay = currenStepsString.replace("%d", latestSteps.steps_count);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stepsGraph_ll.setVisibility(View.VISIBLE);
                weightProgressBar.setVisibility(View.GONE);

                ((TextView) rootView.findViewById(R.id.currentSteps_tv)).setText(stringToDisplay);

                if (stepsLogsList.size() < 10) {
                    rootView.findViewById(R.id.steps_graph_date_ll).setVisibility(View.GONE);
                } else {
                    rootView.findViewById(R.id.steps_graph_date_ll).setVisibility(View.VISIBLE);
                    steps_1w.setSelected(true);
                    steps_1m.setSelected(false);
                    steps_3m.setSelected(false);
                    steps_1y.setSelected(false);

                    steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
                }

                populateStepsLogs();
                selectedDateRange = DATE_RANGE_1W;

                stepsGraphDataArrayList = AppUtil.get1WStepsList(true);
                updateStepsGraph(selectedDateRange);
            }
        });


    }

    public void getStepsGraphFailedWithError(MessageObj response) {

    }

    public void updateStepsGraph(int selectedDateRange_) {

        switch (selectedDateRange_) {
            case DATE_RANGE_1M:
                stepsGraphDataArrayList = AppUtil.get1MStepsList(true, 0);
                break;
            case DATE_RANGE_3M:
                stepsGraphDataArrayList = AppUtil.get3MStepsList(true);
                break;
            case DATE_RANGE_1Y:
                stepsGraphDataArrayList = AppUtil.get1YStepsList(true, 0);
                break;
            case DATE_RANGE_1W:
                stepsGraphDataArrayList = AppUtil.get1WStepsList(true);
                break;
            default:
                break;
        }
        populateStepsData();

        createStepsGraph(stepsGraphDataArrayList);
    }

    private void populateStepsLogs() {

        if (fromSubmitWeight) {
            stepsLogsListCurrentDisplay.clear();
            selectedDateRange = DATE_RANGE_1W;
            steps_1w.setSelected(true);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);

            steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(getResources().getColor(R.color.text_darkgray));

            updateStepsGraph(DATE_RANGE_1W);
        }

        Collections.sort(stepsLogsList, new Comparator<Steps>() {
            public int compare(Steps o1, Steps o2) {
                return o2.start_datetime.compareTo(o1.start_datetime);
            }
        });

        if (stepsLogsList.size() > 7) {
            showSeeMoreButton(stepsLogsListView);
            stopProgressBar2();

            for (int i = 0; i < 7; i++) {
                stepsLogsListCurrentDisplay.add(stepsLogsList.get(i));
            }
        } else {
            hideSeeMoreButton(stepsLogsListView);
            stepsLogsListCurrentDisplay = stepsLogsList;
        }


        if (stepsLogsListView.getAdapter() == null) {
            stepsLogsListAdapter = new StepsLogsListAdapter(context, stepsLogsListCurrentDisplay);
            stepsLogsListAdapter.notifyDataSetChanged();
            stepsLogsListView.invalidateViews();
            stepsLogsListView.refreshDrawableState();
            stepsLogsListView.setAdapter(stepsLogsListAdapter);
        } else {
            stepsLogsListAdapter.updateItems(stepsLogsListCurrentDisplay);
        }

        AppUtil.setListViewHeightBasedOnChildren(stepsLogsListView);
    }

    private void createStepsGraph(List<Steps> stepsGraphList) {

        stepsBarChart.setOnChartGestureListener(this);
        stepsBarChart.setOnChartValueSelectedListener(this);
        stepsBarChart.setDrawGridBackground(false);

        // no description text
        stepsBarChart.setDescription("");
        stepsBarChart.setNoDataTextDescription(getResources().getString(R.string.NO_DATA));

        // enable value highlighting
        stepsBarChart.setHighlightEnabled(true);

        // enable touch gestures
        stepsBarChart.setTouchEnabled(false);

        // enable scaling and dragging
        stepsBarChart.setDragEnabled(true);
        stepsBarChart.setScaleEnabled(true);
        stepsBarChart.setScaleXEnabled(false);
        stepsBarChart.setScaleYEnabled(true);
        stepsBarChart.getLegend().setEnabled(false);
        stepsBarChart.setDrawBorders(true);
        stepsBarChart.setBorderWidth(0.5f);
        stepsBarChart.setBorderColor(Color.LTGRAY);

        // if disabled, scaling can be done on x- and y-axis separately
        stepsBarChart.setPinchZoom(true);

        XAxis xAxis = stepsBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(0.6f);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setSpaceBetweenLabels(2);

        YAxis rightAxis = stepsBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = stepsBarChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawAxisLine(true);
        //set grid color to light gray - 10/20/15
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setAxisMaxValue((float) heighestSteps + 1000);
        if (lowestSteps - 1000 > 0) {
            leftAxis.setAxisMinValue((float) lowestSteps - 1000);
        } else {
            leftAxis.setAxisMinValue(0f);
        }

        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setStartAtZero(false);
        stepsDataToGraphArray(stepsGraphList);
        stepsBarChart.setBackgroundColor(Color.WHITE);
        stepsBarChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return AppUtil.format((double) v, 0);
            }
        });

        stepsBarChart.invalidate();
    }

    /**
     **/
    private void stepsDataToGraphArray(List<Steps> stepsList) {
        List<String> dayArray = new ArrayList<>();
        ArrayList<BarEntry> valsList = new ArrayList<>();

        for (int i = 0; i < stepsList.size(); i++) {
            float steps = (Float.parseFloat(stepsList.get(i).steps_count));
            BarEntry c1e1 = new BarEntry(steps, i);
            valsList.add(c1e1);

            String dayData = "";

            if (selectedDateRange == DATE_RANGE_1W || selectedDateRange == DATE_RANGE_1M) {
                dayData = new SimpleDateFormat("MMM dd").format(stepsList.get(i).start_datetime);
            } else if (selectedDateRange == DATE_RANGE_1Y || selectedDateRange == DATE_RANGE_3M) {
                dayData = new SimpleDateFormat("MMM").format(stepsList.get(i).start_datetime);
            } else {
                dayData = new SimpleDateFormat("MMM dd").format(stepsList.get(i).start_datetime);
            }

            dayArray.add(dayData);
        }
        BarDataSet dataSet1 = new BarDataSet(valsList, "Data 2");
        dataSet1.setDrawValues(false);
        dataSet1.setBarSpacePercent(50f);
        dataSet1.setColor(Color.parseColor("#4CB6EB"));
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData data = new BarData(dayArray, dataSet1);
        stepsBarChart.setData(data);
        stepsBarChart.animateY(4000);
    }

    public void postStepsDataSuccess(String response) {
        System.out.print("postStepsDataSuccess");
        fromSubmitWeight = true;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                weightProgressBar.setVisibility(View.GONE);
                steps_submitBtn.setEnabled(true);
                stepsGraphController.getStepsGraphData("get_steps", ApplicationEx.getInstance().userProfile.getRegID());
            }
        });
    }

    public void postStepsDataFailedWithError(MessageObj response) {

    }

    @Override
    public void getStepsDataSuccess(Steps response) {

    }

    @Override
    public void getStepsDataFailedWithError(MessageObj response) {

    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
//        final ImageView bmImage;
//        String photoid = "";
//
//        public DownloadImageTask(ImageView bmImage, int photoID) {
//            this.bmImage = bmImage;
//            this.photoid = Integer.toString(photoID);
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urlDisplay = urls[0];
//            Bitmap mIcon11 = null;
//            try {
//                InputStream in = new java.net.URL(urlDisplay).openStream();
//                mIcon11 = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//
//            return mIcon11;
//        }
//
//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//
//            ImageManager.getInstance().addImage(photoid, result);
//
//        }
//    }

}