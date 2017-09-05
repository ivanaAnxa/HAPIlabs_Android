package com.anxa.hapilabs.controllers.mymeals;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetMealUpdateListener;

/**
 * Created by aprilanxa on 09/03/2017.
 */

public class GetMealUpdateController {

    Context context;
    private GetMealUpdateImplementer implementer;
    private GetMealUpdateListener listener;

    public GetMealUpdateController(Context context, GetMealUpdateListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getMealUpdate(String userId, String mealID) {
        implementer = new GetMealUpdateImplementer(context, userId, mealID, listener);
    }
}
