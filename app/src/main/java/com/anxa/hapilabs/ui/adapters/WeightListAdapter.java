package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anxa.hapilabs.activities.ExerciseActivity;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;
import com.hapilabs.R;

import java.util.List;

/**
 * Created by angelaanxa on 9/26/2017.
 */

public class WeightListAdapter extends ArrayAdapter<Weight> {
    private final Context context;
    private List<Weight> weightArrayList;
    LayoutInflater layoutInflater;

    public WeightListAdapter(Context context, List<Weight> weightTempArrayList)
    {
        super(context, R.layout.weight_itemcell_data, weightTempArrayList);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        weightArrayList = weightTempArrayList;

        refreshUI();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Weight currentWeight =  weightArrayList.get(position);

        View rowView = layoutInflater.inflate(R.layout.weight_itemcell_data, parent, false);


        /*Body*/
        TextView weightDescBody = (TextView)rowView.findViewById(R.id.weight_selectedBody);

        if (currentWeight != null)
        {

            weightDescBody.setText(String.format("%.2f", currentWeight.currentWeightGrams / 1000) + "kg - " + String.format("%.2f", currentWeight.BMI / 1000) + " " +  context.getString(R.string.BMI));

        }


        /*Time*/
        ((TextView)rowView.findViewById(R.id.weight_displayTime)).setText(AppUtil.getMealTime(currentWeight.start_datetime));

        /*Title and Image*/
        TextView weight_displayTitle = (TextView)rowView.findViewById(R.id.weight_displayTitle);

        weight_displayTitle.setText(currentWeight.device_name);

        return rowView;
    }

    private void refreshUI()
    {
        notifyDataSetChanged();
    }
}
