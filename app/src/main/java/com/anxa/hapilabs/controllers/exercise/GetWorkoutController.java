package com.anxa.hapilabs.controllers.exercise;

import android.content.Context;
import com.anxa.hapilabs.common.connection.listener.GetWorkoutListener;
import com.anxa.hapilabs.models.Workout;


public class GetWorkoutController {
    Context context;

    GetWorkoutImplementer getWorkoutImplementer;

    GetWorkoutListener listener;

    public GetWorkoutController(Context context, GetWorkoutListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public Workout getWorkout(String userId, String activity_id) {
        getWorkoutImplementer = new GetWorkoutImplementer(context, userId, activity_id, listener);
        return null;
    }
}
