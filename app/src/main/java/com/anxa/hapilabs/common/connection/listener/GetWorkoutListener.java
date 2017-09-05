package com.anxa.hapilabs.common.connection.listener;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;


public interface GetWorkoutListener
{
    public void getWorkoutSuccess(Workout response);
    public void getWorkoutFailedWithError(MessageObj response);
}
