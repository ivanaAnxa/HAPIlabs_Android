package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.ExerciseActivity;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Workout;

import java.util.List;

/**
 * Created by aprilanxa on 16/02/2017.
 */

public class StepsListAdapter extends ArrayAdapter<Steps> {

    private final Context context;
    private List<Steps> stepsList;
    LayoutInflater layoutInflater;

    public StepsListAdapter(Context context, List<Steps> stepsTempList) {
        super(context, R.layout.steps_itemcell, stepsTempList);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.stepsList = stepsTempList;

        refreshUI();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Steps stepsData = stepsList.get(position);

        View row = layoutInflater.inflate(R.layout.steps_itemcell, parent, false);

        TextView stepsData_tv = (TextView) row.findViewById(R.id.steps_selectedBody);
        stepsData_tv.setText("");
        TextView stepsData_activity_tv = (TextView) row.findViewById(R.id.steps_displayTitle);
        stepsData_activity_tv.setText("");
        TextView stepsData_time_tv = (TextView) row.findViewById(R.id.steps_displayTime);
        stepsData_time_tv.setText("");

        stepsData_activity_tv.setText(stepsData.device_name);

        //1 min - 1.368 km - 2559 steps
        String stepsData_string = AppUtil.doubleToString(stepsData.steps_duration) + " min - " + +stepsData.steps_distance + " km - " +
                stepsData.steps_count + " " + context.getString(R.string.STEPS);

        if (stepsData.device_name.contains("Google Fit") || stepsData.device_name.contains("iOS")){
            stepsData_string = stepsData.steps_count + " " + context.getString(R.string.STEPS) + " - " +
                    AppUtil.doubleToString(stepsData.steps_duration) + " min - " +
                    stepsData.steps_calories + " cal";

            if (stepsData.device_name.contains("iOS")){
                stepsData_activity_tv.setText("Motion & Fitness (" + stepsData.device_name + ")");
            }
        }
        stepsData_tv.setText(stepsData_string);

        stepsData_time_tv.setText(AppUtil.getMealTime(stepsData.start_datetime));

        if (!ApplicationEx.getInstance().isGoogleFitAllowed(context)){
            if (stepsData.device_name.equalsIgnoreCase("Google Fit")){
                stepsData_tv.setText(context.getString(R.string.GOOGLE_FIT_PAIR_ACCOUNT));
                stepsData_time_tv.setText("");
            }
        }

        return row;
    }

    private void refreshUI() {
        notifyDataSetChanged();
    }

}
