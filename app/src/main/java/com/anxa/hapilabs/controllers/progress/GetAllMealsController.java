package com.anxa.hapilabs.controllers.progress;

import android.content.Context;

import com.anxa.hapilabs.common.connection.listener.GetAllMealsListener;

/**
 * Created by elaineanxa on 08/08/2016.
 */
public class GetAllMealsController
{
    Context context;

    GetAllMealsImplementer getAllMealsImplementer;

    GetAllMealsListener listener;

    public GetAllMealsController(Context context, GetAllMealsListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void getAllMeals(String command, String userId, int pageNumber, int limit)
    {
        getAllMealsImplementer = new GetAllMealsImplementer(context, command, userId, pageNumber, limit, listener);
    }
}
