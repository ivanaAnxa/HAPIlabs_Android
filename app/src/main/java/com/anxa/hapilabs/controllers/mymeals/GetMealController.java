package com.anxa.hapilabs.controllers.mymeals;


import java.util.TimerTask;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetMealListener;
import com.anxa.hapilabs.common.connection.listener.GetMealUpdateListener;

public class GetMealController {

    Context context;
    private GetMealUpdateImplementer implementer;
    private GetMealImplementer getMealImplementer;
    TimerTask mTimerTask;
    private GetMealUpdateListener listener;
    private GetMealListener getMealListener;


    public GetMealController(Context context, GetMealListener listener) {
        this.context = context;
        this.getMealListener = listener;
    }



    public void getMealDetails(String userid, String mealID){
        getMealImplementer = new GetMealImplementer(context, userid, mealID, getMealListener);
    }
}
