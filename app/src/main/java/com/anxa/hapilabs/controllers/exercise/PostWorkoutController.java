package com.anxa.hapilabs.controllers.exercise;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.PostWorkoutListener;
import com.anxa.hapilabs.models.Workout;

/**
 * Created by elaineanxa on 18/08/2016.
 */
public class PostWorkoutController
{
    Context context;

    PostWorkoutImplementer postWorkoutImplementer;

    PostWorkoutListener listener;

    public PostWorkoutController(Context context, PostWorkoutListener listener)
    {
        this.context = context;
        this.listener = listener;
    }

    public void postWorkout(String userId, String command, Workout workoutObj)
    {
        postWorkoutImplementer = new PostWorkoutImplementer(context, userId, command, workoutObj, listener);
    }
}
