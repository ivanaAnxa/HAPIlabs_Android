package com.anxa.hapilabs.controllers.mymeals;

import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetMealUpdateListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonMealUpdateResponseHandler;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
//import com.google.android.gms.internal.ms;

public class GetMealUpdateImplementer {

    JsonMealUpdateResponseHandler jsonResponseHandler;
    GetMealUpdateListener listener;
    Context context;

    String mealId;

    public GetMealUpdateImplementer(Context context, String userId, String mealID, GetMealUpdateListener listener) {
        this.context = context;
        this.mealId = mealID;
        jsonResponseHandler = new JsonMealUpdateResponseHandler(mainHandler, "");
        syncServices(userId, mealID, jsonResponseHandler);

        this.listener = listener;
    }

    //where data = xml string format post data
    public void syncServices(String userID, String mealId, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.GET_MEAL);

        Connection connection = new Connection(responseHandler);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.GET_MEAL) + userID + mealId));
        connection.addParam("userid", userID);
        connection.addParam("meal_id", mealId);
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }

    final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>) {
                        List<Comment> _commentList = (List<Comment>) jsonResponseHandler.getResponseObj();

                        if (_commentList != null) {

                            //update runTime Meal for the meal's comment
                            Meal meal = ApplicationEx.getInstance().tempList.get(mealId);

                            if (meal != null) {
                                meal.comments = _commentList;
                            }

                            ApplicationEx.getInstance().tempList.put(mealId, meal);

                            //update DB Meal for the meal's comment
                            MealDAO meaDao = new MealDAO(context, null);
                            meaDao.updateMeal(meal);
                        }
                    }

                    //STEP 3:
                    //launch my meal list
                    listener.getMealUpdateSuccess((jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage()));

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    listener.getMealUpdateFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
