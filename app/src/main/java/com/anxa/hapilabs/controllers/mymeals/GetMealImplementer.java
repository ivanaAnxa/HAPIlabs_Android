package com.anxa.hapilabs.controllers.mymeals;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetMealListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetMealResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 16/09/2016.
 */
public class GetMealImplementer {

    JsonGetMealResponseHandler jsonResponseHandler;
    GetMealListener listener;
    Context context;

    String mealId;

    public GetMealImplementer(Context context, String userId, String mealID, GetMealListener listener) {
        this.context = context;
        this.mealId = mealID;
        jsonResponseHandler = new JsonGetMealResponseHandler(mainHandler, "");
        getMeal(userId, mealID, jsonResponseHandler);

        this.listener = listener;
    }

    //where data = xml string format post data
    public void getMeal(String userID, String mealId, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.GET_MEAL);

        Connection connection = new Connection(responseHandler);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_MEAL) + userID + mealId));
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
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof Meal) {
                       Meal mealObj = (Meal) jsonResponseHandler.getResponseObj();
                        if (mealObj != null) {

                            //update runTime Meal
                           ApplicationEx.getInstance().currentMealView = mealObj;

                        }
                    }

                    listener.getMealSuccess("SUCCESS");

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.getMealFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
