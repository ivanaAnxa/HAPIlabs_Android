package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;

/**
 * Created by elaineanxa on 18/08/2016.
 */
public interface PostWorkoutListener
{
    public void postWorkoutSuccess(String response, Workout workoutObj);
    public void postWorkoutFailedWithError(MessageObj response, Workout workoutObj);
}
