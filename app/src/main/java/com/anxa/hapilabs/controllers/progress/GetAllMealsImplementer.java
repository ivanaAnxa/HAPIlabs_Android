package com.anxa.hapilabs.controllers.progress;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetAllMealsListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetAllMealsResponseHandler;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

import java.util.List;

/**
 * Created by elaineanxa on 08/08/2016.
 */
public class GetAllMealsImplementer
{
    private JsonGetAllMealsResponseHandler jsonResponseHandler;

    private Context context;
    private GetAllMealsListener listener;
    String command = "";

    public GetAllMealsImplementer(Context context, String command, String userId, int pageNumber, int limit, GetAllMealsListener listener)
    {
        this.context = context;

        jsonResponseHandler = new JsonGetAllMealsResponseHandler(mainHandler, command);

        syncServices(command, userId, pageNumber, limit, jsonResponseHandler);

        this.listener = listener;
        this.command = command;
    }

    //where data = xml string format post data
    private void syncServices(String command, String userID, int pageNumber, int limit, Handler responseHandler)
    {
        String url = WebServices.getURL(WebServices.SERVICES.GET_MEALS_ALL);

        Connection connection = new Connection(responseHandler);

        if (command == "get_meals_all")
        {
            connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_MEALS_ALL) + userID + pageNumber + limit));
        }
        else if (command == "get_meals_healthy")
        {
            connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_MEALS_HEALTHY) + userID + pageNumber + limit));
        }
        else if (command == "get_meals_ok")
        {
            connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_MEALS_OK) + userID + pageNumber + limit));
        }
        else if (command == "get_meals_unhealthy")
        {
            connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_MEALS_UNHEALTHY) + userID + pageNumber + limit));
        }

        connection.addParam("command", command);
        connection.addParam("userid", userID);
        connection.addParam("page", String.valueOf(pageNumber));
        connection.addParam("limit", String.valueOf(limit));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.GET, url, "");
    }

    @SuppressLint("HandlerLeak")
    private final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>) {

                        List<Meal> mealList = (List<Meal>) jsonResponseHandler.getResponseObj();

                        if (mealList.size() == 0)
                        {
                            ApplicationEx.getInstance().setNoMoreContentsToShowForMealRating(true);
                        }
                        else
                        {
                            ApplicationEx.getInstance().setNoMoreContentsToShowForMealRating(false);
                        }

                        DaoImplementer implDao = new DaoImplementer(new MealDAO(context, null), context);

                        for (int i = 0; i < mealList.size(); i++) {

                            Meal meal = mealList.get(i);

                            MealDAO dao = new MealDAO(context, null);
                            Meal tempMeal = dao.getMealbyID(meal.meal_id);

                            if (tempMeal != null)
                            {
                                //updateDB
                                implDao.updateMealWithRating(meal, command, "update");
                            }
                            else
                            {
                                //addDB
                                implDao.updateMealWithRating(meal, command, "add");
                            }

                            if (command == "get_meals_all")
                            {
                                ApplicationEx.getInstance().allMealList.put(meal.meal_id, meal);
                            }
                            else if (command == "get_meals_healthy")
                            {
                                ApplicationEx.getInstance().allHealthyMealList.put(meal.meal_id, meal);
                            }
                            else if (command == "get_meals_ok")
                            {
                                ApplicationEx.getInstance().allOKMealList.put(meal.meal_id, meal);
                            }
                            else if (command == "get_meals_unhealthy")
                            {
                                ApplicationEx.getInstance().allUnhealthyList.put(meal.meal_id, meal);
                            }
                        }

                    }

                    listener.getAllMealsSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage(), command);

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.getAllMealsFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
