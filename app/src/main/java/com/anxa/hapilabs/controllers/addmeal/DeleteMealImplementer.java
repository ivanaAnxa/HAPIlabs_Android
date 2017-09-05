package com.anxa.hapilabs.controllers.addmeal;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.MealDeleteListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDeleteMealResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

/**
 * Created by aprilanxa on 5/10/2016.
 */
public class DeleteMealImplementer {

    JsonDeleteMealResponseHandler jsonDeleteMealResponseHandler;
    protected ProgressChangeListener progressChangeListener;
    MealDeleteListener mealDeleteListener;
    Context context;
    String mealID = "";
    byte mealCommand;

    public DeleteMealImplementer(Context context, String username, Meal meal, ProgressChangeListener progressChangeListener,
                                 MealDeleteListener mealDeleteListener, byte mealCommand) {
        this.context = context;
        this.mealDeleteListener = mealDeleteListener;
        this.mealID = meal.meal_id;
        this.progressChangeListener = progressChangeListener;
        this.mealCommand = mealCommand;

        String data = JsonRequestWriter.getInstance().createUploadMealJson(meal, mealCommand);

        jsonDeleteMealResponseHandler = new JsonDeleteMealResponseHandler(mealDeleteHandler);
        deleteMealService(username, data, jsonDeleteMealResponseHandler);
    }

    public void deleteMealService(String username, String data, Handler responseHandler) {

        String url = WebServices.getURL(WebServices.SERVICES.UPLOAD_MEAL);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.UPLOAD_MEAL) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler mealDeleteHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                    break;

                case JsonDefaultResponseHandler.COMPLETED:

                    //STEP 1: stop progress here
                    Log.i("DELETE MEAL", "SUCCESSFUL");
                    Object tmpObj = jsonDeleteMealResponseHandler.getResponseObj();

                    //meal id deleted
                    mealDeleteListener.deleteMealSuccess(tmpObj.toString());
                    break;

                case JsonDefaultResponseHandler.ERROR:
                    //update the UI for error display

                    MessageObj mesObj = (MessageObj)jsonDeleteMealResponseHandler.getResponseObj();
                    mesObj.setMessage_id(mesObj.getMessage_id());
                    mesObj.setMessage_string(mesObj.getMessage_string());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    mealDeleteListener.deleteMealError(mesObj, mealID);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
