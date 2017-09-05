package com.anxa.hapilabs.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.common.connection.listener.BitmapDownloadListener;
import com.anxa.hapilabs.common.connection.listener.GetAllMealsListener;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.UserProfileDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.controllers.progress.GetAllMealsController;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.ui.adapters.MealWithRatingsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by elaineanxa on 05/08/2016.
 */
public class MealWithRatingsActivity extends HAPIActivity implements View.OnClickListener, BitmapDownloadListener, GetAllMealsListener
{
    private MealWithRatingsAdapter adapter;
    private GetAllMealsController getAllMealsController;
    private ArrayList<Meal> mealListWithPhoto;
    private ArrayList<Meal> mealListWithoutPhoto;
    private ArrayList<Meal> tempMealList;
    private ArrayList<Meal> mealList;

    View footerView;
    Button seeMoreButton;
    ProgressBar progress1;
    ProgressBar progress2;
    TabHost tabHost;
    TextView mealCountTextView;
    TextView mealRatedDesc;
    ListView allMealsListView;
    ListView allHealthyMealsListView;
    ListView allJustOKMealsListView;
    ListView allUnhealthyMealsListView;
    String command;
    Boolean changeTab;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_mealwithratings);

        updateHeader(14, getString(R.string.MYMEALS_TITLE), this, getString(R.string.PROGRESS_TITLE), null);

        progress1 = (ProgressBar) findViewById(R.id.progressBar1);

        mealCountTextView = (TextView) findViewById(R.id.mealRatedCount);
        mealRatedDesc = (TextView) findViewById(R.id.mealRatedDesc);

        allMealsListView = (ListView) findViewById(R.id.allMealsListView);
        allHealthyMealsListView = (ListView) findViewById(R.id.allHealthyMealsListView);
        allJustOKMealsListView = (ListView) findViewById(R.id.allJustOKMealsListView);
        allUnhealthyMealsListView = (ListView) findViewById(R.id.allUnhealthyMealsListView);

        footerView = View.inflate(this, R.layout.see_more_layout, null);
        seeMoreButton = (Button) footerView.findViewById(R.id.seeMoreButton);
        seeMoreButton.setOnClickListener(this);
        progress2 = (ProgressBar) footerView.findViewById(R.id.progressBar2);

        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        setupTab();
        refreshMealData();
        changeTab = false;

        startUpdate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void startUpdate() {
        this.runOnUiThread(new Runnable() {

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

    public void refreshMealData()
    {
        MealDAO dao = new MealDAO(this.getApplicationContext(), null);

        for (Meal tempMeal : ApplicationEx.getInstance().allMealList.values())
        {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            ((MealDAO)dao).updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allHealthyMealList.values())
        {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            ((MealDAO)dao).updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allOKMealList.values())
        {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            ((MealDAO)dao).updateMeal(tempMeal);
        }

        for (Meal tempMeal : ApplicationEx.getInstance().allUnhealthyList.values())
        {
            tempMeal.isAllMealWithRating = false;
            tempMeal.isHealthyMealWithRating = false;
            tempMeal.isJustOkMealWithRating = false;
            tempMeal.isUnhealthyMealWithRating = false;

            ((MealDAO)dao).updateMeal(tempMeal);
        }

        ApplicationEx.getInstance().allMealList.clear();
        ApplicationEx.getInstance().allHealthyMealList.clear();
        ApplicationEx.getInstance().allOKMealList.clear();
        ApplicationEx.getInstance().allUnhealthyList.clear();
    }

    public void setupTab()
    {
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
            public void onTabChanged(String tabId)
            {
                setTabColor(tabHost);

                startProgressBar1();

                changeTab = true;
                seeMoreButton.setVisibility(View.GONE);

                switch (tabHost.getCurrentTab())
                {
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
        for (int i = 0; i < 4; i++)
        {
            tabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.tab_rounded_corners);
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = (int) (30 * this.getResources().getDisplayMetrics().density);
        }

        /* Set Default Selected Tab */
        tabHost.getTabWidget().setCurrentTab(0);
        setTabColor(tabHost);
    }

    /* Change The Background Color of Tabs */
    public void setTabColor(TabHost tabhost)
    {
        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
        {
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

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.header_left || v.getId() == R.id.header_left_tv) {
            onBackPressed();
        }
        else if (v.getId() == R.id.seeMoreButton)
        {
            if (command == "get_meals_all")
            {
                ApplicationEx.getInstance().setAllMealsCurrentPage(ApplicationEx.getInstance().getAllMealsCurrentPaged());

                getAllMeals("get_meals_all", ApplicationEx.getInstance().getAllMealsCurrentPaged());
            }
            else if (command == "get_meals_healthy")
            {
                ApplicationEx.getInstance().setAllHealthyMealsCurrentPage(ApplicationEx.getInstance().getAllHealthyMealsCurrentPaged());

                getAllMeals("get_meals_healthy", ApplicationEx.getInstance().getAllHealthyMealsCurrentPaged());
            }
            else if (command == "get_meals_ok")
            {
                ApplicationEx.getInstance().setAllOkMealsCurrentPage(ApplicationEx.getInstance().getAllOkMealsCurrentPaged());

                getAllMeals("get_meals_ok", ApplicationEx.getInstance().getAllOkMealsCurrentPaged());
            }
            else if (command == "get_meals_unhealthy")
            {
                ApplicationEx.getInstance().setAllUnhealthyMealsCurrentPage(ApplicationEx.getInstance().getAllUnhealthyMealsCurrentPaged());

                getAllMeals("get_meals_unhealthy", ApplicationEx.getInstance().getAllUnhealthyMealsCurrentPaged());
            }

            startProgressBar2();
            seeMoreButton.setVisibility(View.GONE);
        }
    }

    private void getAllMeals(String command, int page)
    {
        if (getAllMealsController == null) {
            getAllMealsController = new GetAllMealsController(this, this);
        }

        UserProfileDAO dao = new UserProfileDAO(this, null);
        UserProfile tempUserProfile = dao.getUserProfile();

        getAllMealsController.getAllMeals(command, tempUserProfile.getRegID(),page,10);
    }

    /**
     * GetAllMealsListener
     **/
    public void getAllMealsSuccess(String response, String command)
    {
        this.command = command;

        System.out.println("getAllMealsSuccess");

        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                stopProgressBar1();
                stopProgressBar2();
                updateUI();
            }
        });
    }

    public void getAllMealsFailedWithError(MessageObj response)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run() {
                stopProgressBar1();
                stopProgressBar2();
            }
        });
    }

    public void showListView(String command)
    {
        if (command == "get_meals_all")
        {
            allMealsListView.setVisibility(View.VISIBLE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        }
        else if (command == "get_meals_healthy")
        {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.VISIBLE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        }
        else if (command == "get_meals_ok")
        {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.VISIBLE);
            allUnhealthyMealsListView.setVisibility(View.GONE);
        }
        else if (command == "get_meals_unhealthy")
        {
            allMealsListView.setVisibility(View.GONE);
            allHealthyMealsListView.setVisibility(View.GONE);
            allJustOKMealsListView.setVisibility(View.GONE);
            allUnhealthyMealsListView.setVisibility(View.VISIBLE);
        }
    }

    public void updateUI()
    {
        showListView(command);

        /*Meals Count*/

        String mealCountString = "";
        String mealRatedDescString = "";
        double percentageValue = 0;

        if (command == "get_meals_all")
        {
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.ALL_MEALS_RATED),
                    ApplicationEx.getInstance().getAllMealsRated());
            mealRatedDescString = getString(R.string.YOUR_MEAL_HISTORY);

            /*ListView*/

            tempMealList = new ArrayList<Meal>(ApplicationEx.getInstance().allMealList.values());

            mealListWithPhoto = new ArrayList<Meal>();
            mealListWithoutPhoto = new ArrayList<Meal>();
            mealList = new ArrayList<Meal>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList)
            {
                if (mealObj.photos != null)
                {
                    mealListWithPhoto.add(mealObj);
                }
                else
                {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0)
            {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0)
            {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allMealsListView.getAdapter() == null || changeTab == true)
            {
                adapter = new MealWithRatingsAdapter(this, mealList);

                adapter.notifyDataSetChanged();
                allMealsListView.invalidateViews();
                allMealsListView.refreshDrawableState();

                allMealsListView.setAdapter(adapter);

                changeTab = false;
            }
            else if (mealList.size() > 0 && ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() == false)
            {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10)
            {
                hideSeeMoreButton(allMealsListView);
            }
            else
            {
                hideSeeMoreButton(allMealsListView);

                showSeeMoreButton(allMealsListView);
            }

            allMealsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id)
                {
                    Meal mealObj = (Meal) allMealsListView.getAdapter().getItem(position);

                    ApplicationEx.getInstance().currentMealView = mealObj;

                    Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });
        }
        else if (command == "get_meals_healthy")
        {
            percentageValue = ((double) ApplicationEx.getInstance().getAllHealthyMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.HEALTHY_MEALS),
                   ApplicationEx.getInstance().getAllHealthyMealsRated());
            mealCountString = mealCountString.concat(" ("+String.format("%.1f",percentageValue)+" %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_AS_OFTEN_AS_YOU_LIKE);

            /*ListView*/

            tempMealList = new ArrayList<Meal>(ApplicationEx.getInstance().allHealthyMealList.values());

            mealListWithPhoto = new ArrayList<Meal>();
            mealListWithoutPhoto = new ArrayList<Meal>();
            mealList = new ArrayList<Meal>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList)
            {
                if (mealObj.photos != null)
                {
                    mealListWithPhoto.add(mealObj);
                }
                else
                {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0)
            {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0)
            {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allHealthyMealsListView.getAdapter() == null || changeTab == true)
            {
                adapter = new MealWithRatingsAdapter(this, mealList);

                adapter.notifyDataSetChanged();
                allHealthyMealsListView.invalidateViews();
                allHealthyMealsListView.refreshDrawableState();

                allHealthyMealsListView.setAdapter(adapter);

                changeTab = false;
            }
            else if (mealList.size() > 0 && ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() == false)
            {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10)
            {
                hideSeeMoreButton(allHealthyMealsListView);
            }
            else
            {
                hideSeeMoreButton(allHealthyMealsListView);

                showSeeMoreButton(allHealthyMealsListView);
            }

            allHealthyMealsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id)
                {
                    Meal mealObj = (Meal) allHealthyMealsListView.getAdapter().getItem(position);

                    ApplicationEx.getInstance().currentMealView = mealObj;

                    Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });
        }
        else if (command == "get_meals_ok")
        {
            percentageValue = ((double) ApplicationEx.getInstance().getAllJustOkMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.JUST_OK_MEALS),
                    ApplicationEx.getInstance().getAllJustOkMealsRated());
            mealCountString = mealCountString.concat(" ("+String.format("%.1f",percentageValue)+" %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_NOT_TOO_OFTEN);

            /*ListView*/

            tempMealList = new ArrayList<Meal>(ApplicationEx.getInstance().allOKMealList.values());

            mealListWithPhoto = new ArrayList<Meal>();
            mealListWithoutPhoto = new ArrayList<Meal>();
            mealList = new ArrayList<Meal>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList)
            {
                if (mealObj.photos != null)
                {
                    mealListWithPhoto.add(mealObj);
                }
                else
                {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0)
            {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0)
            {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allJustOKMealsListView.getAdapter() == null || changeTab == true)
            {
                adapter = new MealWithRatingsAdapter(this, mealList);

                adapter.notifyDataSetChanged();
                allJustOKMealsListView.invalidateViews();
                allJustOKMealsListView.refreshDrawableState();

                allJustOKMealsListView.setAdapter(adapter);

                changeTab = false;
            }
            else if (mealList.size() > 0 && ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() == false)
            {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10)
            {
                hideSeeMoreButton(allJustOKMealsListView);
            }
            else
            {
                hideSeeMoreButton(allJustOKMealsListView);

                showSeeMoreButton(allJustOKMealsListView);
            }

            allJustOKMealsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id)
                {
                    Meal mealObj = (Meal) allJustOKMealsListView.getAdapter().getItem(position);

                    ApplicationEx.getInstance().currentMealView = mealObj;

                    Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });
        }
        else if (command == "get_meals_unhealthy")
        {
            percentageValue = ((double) ApplicationEx.getInstance().getAllUnhealthyMealsRated() / (double) ApplicationEx.getInstance().getAllMealsRated()) * 100;
            mealCountString = String.format(Locale.getDefault(),
                    getString(R.string.UNHEALTHY_MEALS),
                    ApplicationEx.getInstance().getAllUnhealthyMealsRated());
            mealCountString = mealCountString.concat(" ("+String.format("%.1f",percentageValue)+" %)");
            mealRatedDescString = getString(R.string.MEALS_LIKE_THIS_IN_MODERATION);

            /*ListView*/

            tempMealList = new ArrayList<Meal>(ApplicationEx.getInstance().allUnhealthyList.values());

            mealListWithPhoto = new ArrayList<Meal>();
            mealListWithoutPhoto = new ArrayList<Meal>();
            mealList = new ArrayList<Meal>();

            /* 1. Check if tempMealList has photo or not */
            for (Meal mealObj : tempMealList)
            {
                if (mealObj.photos != null)
                {
                    mealListWithPhoto.add(mealObj);
                }
                else
                {
                    mealListWithoutPhoto.add(mealObj);
                }
            }

            mealList.clear();

            /* 2. Sort and add meal with photo */
            if (mealListWithPhoto != null && mealListWithPhoto.size() > 0)
            {
                sort(mealListWithPhoto);
                mealList.addAll(mealListWithPhoto);
            }

            /* 2. Sort and add meal without photo */
            if (mealListWithoutPhoto != null && mealListWithoutPhoto.size() > 0)
            {
                sort(mealListWithoutPhoto);
                mealList.addAll(mealListWithoutPhoto);
            }

            /* 3. Add mealList to adapter */
            if (allUnhealthyMealsListView.getAdapter() == null || changeTab == true)
            {
                adapter = new MealWithRatingsAdapter(this, mealList);

                adapter.notifyDataSetChanged();
                allUnhealthyMealsListView.invalidateViews();
                allUnhealthyMealsListView.refreshDrawableState();

                allUnhealthyMealsListView.setAdapter(adapter);

                changeTab = false;
            }
            else if (mealList.size() > 0 && ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() == false)
            {
                adapter.clear();
                adapter.addAll(mealList);
            }

            if (ApplicationEx.getInstance().getNoMoreContentsToShowForMealRating() || mealList.size() < 10)
            {
                hideSeeMoreButton(allUnhealthyMealsListView);
            }
            else
            {
                hideSeeMoreButton(allUnhealthyMealsListView);

                showSeeMoreButton(allUnhealthyMealsListView);
            }

            allUnhealthyMealsListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id)
                {
                    Meal mealObj = (Meal) allUnhealthyMealsListView.getAdapter().getItem(position);

                    ApplicationEx.getInstance().currentMealView = mealObj;

                    Intent mainIntent = new Intent(getBaseContext(), MealViewActivity.class);

                    startActivity(mainIntent);
                }
            });
        }

        mealCountTextView.setText(mealCountString);
        mealRatedDesc.setText(mealRatedDescString);
    }

    /* Sort meal in descending order (newest to oldest) */
    public void sort(final ArrayList<Meal> mealObj)
    {
        if (mealObj != null && mealObj.size() > 0)
        {
            Collections.sort(mealObj, new Comparator<Meal>() {
                public int compare(Meal meal1, Meal meal2) {
                    return ((Meal) meal2).meal_creation_date.compareTo(((Meal) meal1).meal_creation_date);
                }
            });
        }
    }

    public void startProgressBar1()
    {
        progress1.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar1()
    {
        progress1.setVisibility(View.GONE);
    }

    public void startProgressBar2()
    {
        progress2.setVisibility(View.VISIBLE);
    }

    public void stopProgressBar2()
    {
        progress2.setVisibility(View.GONE);
    }

    public void hideSeeMoreButton(ListView listView)
    {
        listView.removeFooterView(footerView);
        seeMoreButton.setVisibility(View.GONE);
    }

    public void showSeeMoreButton(ListView listView)
    {
        listView.addFooterView(footerView);
        seeMoreButton.setVisibility(View.VISIBLE);
    }
}
