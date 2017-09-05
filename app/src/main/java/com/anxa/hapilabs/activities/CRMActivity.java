package com.anxa.hapilabs.activities;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.MealGraphicsListener;
import com.anxa.hapilabs.models.Coach;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.common.util.ApplicationEx;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;


public class CRMActivity extends HAPIActivity implements OnClickListener, MealGraphicsListener, OnChartGestureListener, OnChartValueSelectedListener {

    private List<String> dayArray;
    public List<GraphMeal> graphList = new ArrayList<GraphMeal>();
    ArrayList<LineDataSet> dataSets;

    protected int _splashTime = 3000; // time to display the splash screen in ms

    private final byte PAGE_1 = 1;
    private final byte PAGE_2 = 2;
    private final int MAX_PAGE = 3;
    private int currentPage = PAGE_1;

    TextView contentView;
    ImageView contentImageView;

    Button footerbutton;
    Button footerpremium;
    Button footercontinue;
    Boolean isPremium;

    /*param for Graph*/
    private int totalMeals = 0;

    // Added global integer for number of days. for animation

    int dayArraySize = 0;
    int sum = 0;

    LineChart mChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        isPremium = getIntent().getBooleanExtra("ISPREMIUM_USER", false);
        totalMeals = getIntent().getIntExtra("TOTAL_MEAL", 0);

        if (getIntent().getLongArrayExtra("DATES") != null) {

            long[] dates = getIntent().getLongArrayExtra("DATES");
            int[] postcount = new int[dates.length];

            if (getIntent().getIntArrayExtra("POSTCOUNT") != null) {
                postcount = getIntent().getIntArrayExtra("POSTCOUNT");
            }

            //createmeal graph objects
            graphList = new ArrayList<GraphMeal>();

            for (int i = 0; i < postcount.length; i++) {

                GraphMeal item = new GraphMeal();
                item.date = new Date(dates[i]);

                sum += postcount[i];
//				item.mealcount = sum;
                item.mealcount = postcount[i];

                graphList.add(item);
            }
        }
        setContentView(R.layout.crm_graph);

        // input for graph is below:
        // graphList - list of graph objects
        // totalmeals = total meal count return by CMSs

        //call mealgrahics

        createGraph(graphList);
        //MealGraphicsImplementer mealGraphicsImplementer = new MealGraphicsImplementer(graphList, totalMeals,this);

        contentView = (TextView) findViewById(R.id.content1);

        contentImageView = (ImageView) findViewById(R.id.viewcontent);

        footerpremium = (Button) findViewById(R.id.footerbutton2premium);
        footerpremium.setText(getResources().getString(R.string.btn_premium));
        footerpremium.setOnClickListener(this);

        footercontinue = (Button) findViewById(R.id.footerbutton2continue);
        footercontinue.setText(getResources().getString(R.string.btn_continue));
        footercontinue.setOnClickListener(this);

        footerbutton = (Button) findViewById(R.id.footerbutton);
        footerbutton.setText(getResources().getString(R.string.btn_continue));
        footerbutton.setOnClickListener(this);

        updateUI(currentPage);

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v.getId() == R.id.YesButton) {

            Intent intent = new Intent();
            intent.putExtra("LOADCOACH", true);
            setResult(RESULT_OK, intent);
            finish();

        } else if (v.getId() == R.id.NoButton) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();

        } else if (v == footerbutton) {
            if (currentPage < MAX_PAGE) {
                currentPage = currentPage + 1;
                updateUI(currentPage);
            } else {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (v == footercontinue) {
            //TODO: remove again
            if (!isPremium) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (v == footerpremium) {
            Intent intent = new Intent();
            intent.putExtra("LOADCOACH", true);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    private void updateUI(int currentPage) {
        ((LinearLayout) findViewById(R.id.footerbuttonlayout)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.footerbuttonlayout2)).setVisibility(View.GONE);
        switch (currentPage) {

            case PAGE_1:
                ((LineChart) findViewById(R.id.viewcontentGraph)).setVisibility(View.VISIBLE);
                updateHeader(2, getResources().getString(R.string.CRM_HEADER_1), this);
                String updated = getResources().getString(R.string.CRM_CONTENT_1);
                updated = updated.replace("%s", "" + ordinal(totalMeals));
                contentView.setText(updated);

                break;
            case PAGE_2:
                ((LineChart) findViewById(R.id.viewcontentGraph)).setVisibility(View.GONE);

                updateHeader(2, getResources().getString(R.string.CRM_HEADER_2), this);

                /*if (!isPremium && totalMeals % 10 > 0) { //not a multiple of 10th

                    if (totalMeals % 2 == 0) {
                        contentImageView.setImageResource(R.drawable.objective_food1);
                        contentView.setText(getResources().getString(R.string.CRM_CONTENT_2even));
                    } else {
                        contentImageView.setImageResource(R.drawable.objective_food2);
                        contentView.setText(getResources().getString(R.string.CRM_CONTENT_2odd));
                    }
                } else {
                    contentImageView.setImageResource(R.drawable.objective_food1);
                    contentView.setText(getResources().getString(R.string.CRM_CONTENT_2even));

                }*/
                mealCRMObjective();
                break;
            default:
                ((LineChart) findViewById(R.id.viewcontentGraph)).setVisibility(View.GONE);

                updateHeader(2, getResources().getString(R.string.CRM_HEADER_3), this);

                if (isPremium) {

                    ((LinearLayout) findViewById(R.id.footerbuttonlayout)).setVisibility(View.VISIBLE);
                    ((LinearLayout) findViewById(R.id.footerbuttonlayout2)).setVisibility(View.GONE);

                } else {

                    ((LinearLayout) findViewById(R.id.footerbuttonlayout)).setVisibility(View.GONE);
                    ((LinearLayout) findViewById(R.id.footerbuttonlayout2)).setVisibility(View.VISIBLE);
                }

                contentImageView.setImageResource(R.drawable.reward_figure1);

                if (isPremium) {
                    //MEAL_REWARD_DESCRIPTION_PREMIUM_USER
                    contentView.setText(getResources().getString(R.string.MEAL_REWARD_DESCRIPTION_PREMIUM_USER));
                } else {
                    //TODO: return again
                    Coach userProfileCoach = ApplicationEx.getInstance().userProfile.getCoach();

                    if (totalMeals % 10 > 0) { //not a multiple of 10th
                        String updated2 = getResources().getString(R.string.MEAL_REWARD_DESCRIPTION_FREE_USER).replace("%s", "" + (10 - (totalMeals % 10)));
                        contentView.setText(updated2);
                    } else {
                        if (userProfileCoach==null){
                            contentView.setText(Html.fromHtml(getResources().getString(R.string.MEAL_REWARD_DESCRIPTION_GOAL_REACHED_FREE_HTML)));
                        }else {
                            contentView.setText(Html.fromHtml(getResources().getString(R.string.MEAL_REWARD_DESCRIPTION_GOAL_REACHED_HTML)));
                        }
                    }
                }

                break;
        }
    }

    private void mealCRMObjective () {
        int maxMealCRM = 30, tmp_mealPostCount;
        tmp_mealPostCount = totalMeals;

        while (tmp_mealPostCount > maxMealCRM) {
            tmp_mealPostCount = tmp_mealPostCount - maxMealCRM;
        }

        switch (tmp_mealPostCount) {
            case 1:
                contentImageView.setImageResource(R.drawable.objective_food1);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL1));
                break;
            case 2:
                contentImageView.setImageResource(R.drawable.objective_food2);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL2));
                break;
            case 3:
                contentImageView.setImageResource(R.drawable.objective_food3);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL3));
                break;
            case 4:
                contentImageView.setImageResource(R.drawable.objective_food4);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL4));
                break;
            case 5:
                contentImageView.setImageResource(R.drawable.objective_food5);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL5));
                break;
            case 6:
                contentImageView.setImageResource(R.drawable.objective_food6);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL6));
                break;
            case 7:
                contentImageView.setImageResource(R.drawable.objective_food7);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL7));
                break;
            case 8:
                contentImageView.setImageResource(R.drawable.objective_food8);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL8));
                break;
            case 9:
                contentImageView.setImageResource(R.drawable.objective_food9);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL9));
                break;
            case 10:
                contentImageView.setImageResource(R.drawable.objective_food10);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL10));
                break;
            case 11:
                contentImageView.setImageResource(R.drawable.objective_food11);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL11));
                break;
            case 12:
                contentImageView.setImageResource(R.drawable.objective_food12);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL12));
                break;
            case 13:
                contentImageView.setImageResource(R.drawable.objective_food13);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL13));
                break;
            case 14:
                contentImageView.setImageResource(R.drawable.objective_food14);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL14));
                break;
            case 15:
                contentImageView.setImageResource(R.drawable.objective_food15);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL15));
                break;
            case 16:
                contentImageView.setImageResource(R.drawable.objective_food16);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL16));
                break;
            case 17:
                contentImageView.setImageResource(R.drawable.objective_food17);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL17));
                break;
            case 18:
                contentImageView.setImageResource(R.drawable.objective_food18);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL18));
                break;
            case 19:
                contentImageView.setImageResource(R.drawable.objective_food19);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL19));
                break;
            case 20:
                contentImageView.setImageResource(R.drawable.objective_food20);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL20));
                break;
            case 21:
                contentImageView.setImageResource(R.drawable.objective_food21);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL21));
                break;
            case 22:
                contentImageView.setImageResource(R.drawable.objective_food22);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL22));
                break;
            case 23:
                contentImageView.setImageResource(R.drawable.objective_food23);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL23));
                break;
            case 24:
                contentImageView.setImageResource(R.drawable.objective_food24);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL24));
                break;
            case 25:
                contentImageView.setImageResource(R.drawable.objective_food25);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL25));
                break;
            case 26:
                contentImageView.setImageResource(R.drawable.objective_food26);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL26));
                break;
            case 27:
                contentImageView.setImageResource(R.drawable.objective_food27);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL27));
                break;
            case 28:
                contentImageView.setImageResource(R.drawable.objective_food28);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL28));
                break;
            case 29:
                contentImageView.setImageResource(R.drawable.objective_food29);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL29));
                break;
            default:
                contentImageView.setImageResource(R.drawable.objective_food30);
                contentView.setText(getResources().getString(R.string.CRM_CONTENT_2_MEAL30));
        }
    }


    @Override
    public void updateGraph(View graph) {
        // TODO Auto-generated method stub

        contentImageView.setBackgroundColor(0xffffffff);

    }


    private void createGraph(List<GraphMeal> graphMealList) {
        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.tada);
        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });


        //11-4-2015, remove the delay (we already removed the animation)

//			new Handler().postDelayed(new Runnable()

//			{

//			@Override

//			public void run()

//			{

        mediaPlayer.start();

//			}

//			}, 2500/* same with animation delay */);


        mChart = (LineChart) findViewById(R.id.viewcontentGraph);
        mChart.setVisibility(View.VISIBLE);

        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.setDescription("");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(false);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setScaleXEnabled(false);
        mChart.setScaleYEnabled(true);
        mChart.getLegend().setEnabled(false);

        mChart.setDrawBorders(false);


        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // x-axis limit line
        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();
        xAxis.addLimitLine(llXAxis); // add x-axis limit line
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setEnabled(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(true);


        xAxis.setAxisLineColor(Color.LTGRAY);

        //remove rightyaxis border line - 10/20/15

        rightAxis.setDrawAxisLine(false);

        //set grid color to light gray - 10/20/15
        rightAxis.setGridColor(Color.LTGRAY);

        dataToGraphArray(graphMealList);

        // set an alternative background color
        mChart.setBackgroundColor(Color.WHITE);

        if (sum == 1) {
            rightAxis.setLabelCount(1, true);
            rightAxis.setAxisMaxValue(1f);
            rightAxis.setAxisMinValue(1f);
            rightAxis.setStartAtZero(true);
        } else if (sum < 7) {
            rightAxis.setLabelCount(sum, false);
        } else {
            rightAxis.setStartAtZero(false);
            leftAxis.setStartAtZero(false);
        }

        mChart.getAxisRight().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return String.valueOf((int) Math.floor(v));
            }
        });

        mChart.invalidate();

    }

    /**
     * @param graphMealList - list of GraphMeals to be plotted in line graph
     * @return void
     **/
    private void dataToGraphArray(List<GraphMeal> graphMealList) {


        //new ArrayList<DataPoint>();
        dayArray = new ArrayList<String>();

        dataSets = new ArrayList<LineDataSet>();

        ArrayList<Entry> valsList = new ArrayList<Entry>();


        int sumGraph = totalMeals - sum;

        Log.i("graph", "sum: " + sum + "g:" + sumGraph);
        for (int i = 0; i < graphList.size(); i++) {

            sumGraph = sumGraph + graphMealList.get(i).mealcount;

            Log.i("graph", "sum: " + sumGraph + "g:" + graphMealList.get(i).mealcount);

            Entry c1e1 = new Entry(sumGraph, i);

            valsList.add(c1e1);
            String dayData = new SimpleDateFormat("EEE").format(graphMealList.get(i).date);
            dayArray.add(dayData.substring(0, 2).toUpperCase(Locale.ENGLISH));
        }

        LineDataSet dataSet1 = new LineDataSet(valsList, "Data 1");
        dataSet1.setDrawValues(false);
        dataSet1.setLineWidth(3.0f);
        dataSet1.setColor(Color.parseColor("#3399FF"));

        dataSet1.setCircleColor(Color.parseColor("#3399FF"));

        LineData data = new LineData(dayArray, dataSet1);

        mChart.setData(data);

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
                                    ChartGesture lastPerformedGesture) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartGesture lastPerformedGesture) {
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

    public static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};

        if (ApplicationEx.language.equals("fr")) {
            if (i == 1) {
                return i + "er";
            } else {
                return i + "ème";
            }
        } else {
            switch (i % 100) {
                case 11:
                case 12:
                case 13:
                    return i + "th";
                default:
                    return i + sufixes[i % 10];
            }
        }
    }

    private void callCoach() {
        // call My listview
        Intent mainIntent;
        mainIntent = new Intent(this, CoachSelectionActivity.class);
        finish();
        this.startActivity(mainIntent);
    }

}


