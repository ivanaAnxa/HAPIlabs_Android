package com.anxa.hapilabs.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hapilabs.R;
import com.anxa.hapilabs.activities.ExerciseActivity;
import com.anxa.hapilabs.common.util.AppUtil;
import com.anxa.hapilabs.models.Workout;

import java.util.List;

/**
 * Created by elaineanxa on 05/08/2016.
 */
public class ExerciseListAdapter extends ArrayAdapter<Workout>
{
    private final Context context;
    private List<Workout> workoutArrayList;
    LayoutInflater layoutInflater;

    public ExerciseListAdapter(Context context, List<Workout> workoutTempArrayList)
    {
        super(context,R.layout.exercise_itemcell_data, workoutTempArrayList);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        workoutArrayList = workoutTempArrayList;

        refreshUI();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Workout currentWorkout =  workoutArrayList.get(position);

        View rowView = layoutInflater.inflate(R.layout.exercise_itemcell_data, parent, false);

        /*Display Add Button*/
        if(position != 0)
        {
            ((ImageView)rowView.findViewById(R.id.exercise_displayAddButton)).setVisibility(View.GONE);
        }

        ((ImageView)rowView.findViewById(R.id.exercise_displayAddButton)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ExerciseActivity.class);
                v.getContext().startActivity(i);
            }
        });

        /*Description*/
        TextView exerciseDesc = (TextView)rowView.findViewById(R.id.exercise_displayDescription);

        if (currentWorkout.workout_desc == "null" || currentWorkout.workout_desc.isEmpty() || currentWorkout.workout_desc == null)
        {
            exerciseDesc.setVisibility(View.GONE);
        }
        else
        {
            exerciseDesc.setVisibility(View.VISIBLE);
            exerciseDesc.setText(currentWorkout.workout_desc);
        }

        /*Body*/
        TextView exerciseDescBody = (TextView)rowView.findViewById(R.id.exercise_selectedBody);

        if (currentWorkout.exercise_type.getValue() == -1)
        {
            if (currentWorkout.steps == 0)
            {
                if (currentWorkout.duration == 0.0)
                {
                    exerciseDescBody.setText(currentWorkout.calories+" cal");
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.duration+" min - "+currentWorkout.calories+" cal");
                }
            }
            else if (currentWorkout.duration == 0)
            {
                if (currentWorkout.steps == 0)
                {
                    exerciseDescBody.setText(currentWorkout.calories+" cal");
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS)+" - "+currentWorkout.calories+" cal");
                }
            }
            else if (currentWorkout.calories == 0)
            {
                if (currentWorkout.duration == 0.0)
                {
                    exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS));
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS)+" - "+currentWorkout.duration+" min");
                }
            }
            else
            {
                exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS)+" - "+currentWorkout.duration+" min - "+currentWorkout.calories+" cal");
            }
        }
        else
        {
            if (currentWorkout.steps == 0)
            {
                if (currentWorkout.distance == 0.0)
                {
                    exerciseDescBody.setText(currentWorkout.duration+" min");
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.duration+" min - "+currentWorkout.distance+" km");
                }
            }
            else if (currentWorkout.duration == 0)
            {
                if (currentWorkout.distance == 0.0)
                {
                    exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS)+" - ");
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.distance+" km - "+currentWorkout.steps+" "+context.getString(R.string.STEPS));
                }
            }
            else if (currentWorkout.distance == 0.0)
            {
                if (currentWorkout.duration == 0)
                {
                    exerciseDescBody.setText(currentWorkout.steps+" "+context.getString(R.string.STEPS));
                }
                else
                {
                    exerciseDescBody.setText(currentWorkout.duration+" min - "+currentWorkout.steps+" "+context.getString(R.string.STEPS));
                }
            }
            else
            {
                exerciseDescBody.setText(currentWorkout.duration+" min - "+currentWorkout.distance+" km - "+currentWorkout.steps+" "+context.getString(R.string.STEPS));
            }
        }

        /*Time*/
        ((TextView)rowView.findViewById(R.id.exercise_displayTime)).setText(AppUtil.getMealTime(currentWorkout.exercise_datetime));

        /*Title and Image*/
        TextView exercise_displayTitle = (TextView)rowView.findViewById(R.id.exercise_displayTitle);
        ImageView exercise_displaylogo = (ImageView)rowView.findViewById(R.id.exercise_displaylogo);

        if (currentWorkout.device_name == "Garmin" || currentWorkout.device_name == "RunKeeper")
        {
            String displayTitle = context.getString(R.string.EXERCISE_MENU_RUN_DEVICE);
            displayTitle = displayTitle.replace("%@",currentWorkout.device_name);
            exercise_displayTitle.setText(displayTitle);
            exercise_displaylogo.setImageResource(R.drawable.exercise_display_run);
        }
        else if (currentWorkout.exercise_type.getValue() == -1)
        {
            String displayTitle = context.getString(R.string.EXERCISE_MENU_ACTIVITY);
            displayTitle = displayTitle.replace("%@",currentWorkout.device_name);
            exercise_displayTitle.setText(displayTitle);
            exercise_displaylogo.setImageResource(R.drawable.exercise_display_steps);
        }
        else
        {
            exercise_displayTitle.setText(AppUtil.getExerciseValue(context, currentWorkout.exercise_type));
            exercise_displaylogo.setImageResource(this.context.getResources().getIdentifier("drawable/"+currentWorkout.workout_image,null,this.context.getPackageName()));
        }

        return rowView;
    }

    private void refreshUI()
    {
        notifyDataSetChanged();
    }
}