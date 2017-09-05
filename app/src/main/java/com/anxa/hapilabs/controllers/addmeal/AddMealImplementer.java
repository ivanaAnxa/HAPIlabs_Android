package com.anxa.hapilabs.controllers.addmeal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.MealGraphListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonUploadMealResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonUploadGraphMealResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.GraphMeal;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MealResponseWithGraph;
import com.anxa.hapilabs.models.Meal.STATE;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;
import android.util.Log;

public class AddMealImplementer {


    JsonUploadMealResponseHandler jsonResponseHandler;
    JsonUploadGraphMealResponseHandler jsonGraphResponseHandler;

    Handler responseHandler;
    Handler responseHandlerGraph;
    protected ProgressChangeListener progresslistener;

    MealAddListener mealAddlistener;
    MealGraphListener mealGraphListener;

    Context context;

    String mealID = "";
    byte mealCommand;

    public AddMealImplementer(Context context, String username, Meal meal,
                              ProgressChangeListener progresslistener,
                              MealAddListener mealAddlistener,
                              MealGraphListener mealGraphListener,
                              byte mealCommand) {

        this.context = context;
        this.mealAddlistener = mealAddlistener;
        this.progresslistener = progresslistener;
        this.mealGraphListener = mealGraphListener;
        mealID = meal.meal_id;
        this.mealCommand = mealCommand;

        String data = JsonRequestWriter.getInstance().createUploadMealJson(meal, mealCommand);

        if (mealGraphListener == null) {
            jsonResponseHandler = new JsonUploadMealResponseHandler(mealAddHandler);
            addMealService(username, data, jsonResponseHandler);

        } else {
            jsonGraphResponseHandler = new JsonUploadGraphMealResponseHandler(mealGraphHandler);
            addMealService(username, data, jsonGraphResponseHandler);
        }
    }

    //where data = xml string format post data
    public void addMealService(String username, String data, Handler responseHandler) {
        String url = WebServices.getURL(SERVICES.UPLOAD_MEAL);

        Connection connection = new Connection(responseHandler);
        connection.addParam("userid", username);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.UPLOAD_MEAL) + username));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.POST, url, data);
    }

    final Handler mealGraphHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here

                    MealResponseWithGraph objResponse = new MealResponseWithGraph();

                    List<Meal> mealList = new ArrayList<Meal>();

                    List<GraphMeal> graphList = new ArrayList<GraphMeal>();

                    Object tmpObj = jsonGraphResponseHandler.getResponseObj();

                    if (tmpObj instanceof MealResponseWithGraph) {
//                        System.out.println("fetch YES");
                    } else {
//                        System.out.println("fetch step9 NO");
                    }

                    objResponse = (MealResponseWithGraph) jsonGraphResponseHandler.getResponseObj();

                    mealList = objResponse.mealList;
                    graphList = objResponse.graphList;

                    //process meal list
                    String newID = mealList.get(0).meal_id;
                    //remove from meal add  hasttable

                    ApplicationEx.getInstance().mealsToAdd.remove(mealID);

                    //update status from meal list hashtable

                    Meal meal = ApplicationEx.getInstance().tempList.get(mealID);
                    meal.state = STATE.SYNC;
                    meal.meal_status = "SYNC";

                    //update meal id
                    meal.meal_id = newID;

                    if (newID != mealID) {
                        ApplicationEx.getInstance().tempList.remove(mealID);
                    }

                    ApplicationEx.getInstance().tempList.put(newID, meal);

                    mealAddlistener.uploadMealSuccess("");

                    //process graph list
                    mealGraphListener.mealGraphSuccess(graphList, objResponse.mealTotal);


                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //update the UI for error display
                    //ApplicationEx.getInstance().mealsToAdd.remove(mealID);
                    Log.i("ResponseHandler", "FAILED");
                    Meal meal_failed = ApplicationEx.getInstance().tempList.get(mealID);
                    meal_failed.state = STATE.FAILED;
                    meal_failed.meal_status = "FAILED";

                    ApplicationEx.getInstance().tempList.put(mealID, meal_failed);

                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj = (MessageObj) jsonGraphResponseHandler.getResponseObj();
                    mesObj.setMessage_id(jsonGraphResponseHandler.getResultCode());
                    mesObj.setMessage_string(mesObj.getMessage_string());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    mealAddlistener.uploadMealFailedWithError(mesObj, mealID);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };

    final Handler mealAddHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    List<Meal> mealList = new ArrayList<Meal>();

                    mealList = (List<Meal>) jsonResponseHandler.getResponseObj();

                    String newID = mealList.get(0).meal_id;
                    //remove from meal add  hasttable
                    ApplicationEx.getInstance().mealsToAdd.remove(mealID);

                    //update status from meal list hashtable

                    Meal meal = ApplicationEx.getInstance().tempList.get(mealID);

                    meal.state = STATE.SYNC;
                    meal.meal_status = "SYNC";
                    //update meal id
                    meal.meal_id = newID;


                    if (newID != mealID) {
                        ApplicationEx.getInstance().tempList.remove(mealID);
                    }

                    ApplicationEx.getInstance().tempList.put(newID, meal);

                    if (mealCommand == Meal.MEALSTATE_DELETE){
                    }

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //update the UI for error display
                    ApplicationEx.getInstance().mealsToAdd.remove(mealID);

                    Meal meal_failed = ApplicationEx.getInstance().tempList.get(mealID);
                    meal_failed.state = STATE.FAILED;
                    meal_failed.meal_status = "FAILED";

                    ApplicationEx.getInstance().tempList.put(mealID, meal_failed);

                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    mealAddlistener.uploadMealFailedWithError(mesObj, mealID);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
