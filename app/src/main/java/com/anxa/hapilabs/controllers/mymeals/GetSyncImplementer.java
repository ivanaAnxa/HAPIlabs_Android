package com.anxa.hapilabs.controllers.mymeals;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.SyncListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetSyncResponseHandler;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.MealDAO;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;

import android.os.Message;

public class GetSyncImplementer {

    JsonGetSyncResponseHandler jsonResponseHandler;
    Context context;
    SyncListener syncListener;

    public GetSyncImplementer(Context context, String userId, String toDate, String fromDate, SyncListener syncListener) {
        this.context = context;
        jsonResponseHandler = new JsonGetSyncResponseHandler(mainHandler);
        syncServices(userId, fromDate, toDate, jsonResponseHandler);
        this.syncListener = syncListener;
    }

    //where data = xml string format post data
    public void syncServices(String userID, String fromDate, String toDate, Handler responseHandler){
        String url = WebServices.getURL(SERVICES.GET_SYNC);

        ApplicationEx.getInstance().fromDateGetSync.add(fromDate);

        Connection connection = new Connection(responseHandler);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(SERVICES.GET_SYNC) + userID + fromDate + toDate));
        connection.addParam("userid", userID);
        connection.addParam("to", toDate);
        connection.addParam("from", fromDate);
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");
    }


    final Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                    break;
                case JsonDefaultResponseHandler.COMPLETED:
                    //STEP 1: stop progress here
                    if (jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>) {

                        //STEP 2: check if the array is empty or not
                        List<Object> returnedList = (List<Object>)jsonResponseHandler.getResponseObj();
                        List<Meal> _meallist = new ArrayList<Meal>();
                        List<HapiMoment> _hapiMomentList= new ArrayList<HapiMoment>();
                        List<Workout> _workoutList = new ArrayList<Workout>();
                        List<Weight> _weightList = new ArrayList<Weight>();

                        ApplicationEx.getInstance().tempHapimomentList = new Hashtable<String, HapiMoment>();


                        for(Object o : returnedList)
                        {
                            if(o.getClass() == Meal.class)
                            {
                                _meallist.add((Meal)o);
                            }
                            if(o.getClass() == HapiMoment.class)
                            {
                                _hapiMomentList.add((HapiMoment)o);
                            }
                            if(o.getClass() == Workout.class)
                            {
                                _workoutList.add((Workout) o);
                            }
                            if(o.getClass() == Weight.class)
                            {
                                _weightList.add((Weight) o);
                            }
                        }
                        DaoImplementer implDao = new DaoImplementer(new MealDAO(context, null), context);
                        for (int i = 0; i < _meallist.size(); i++) {

                            Meal meal = _meallist.get(i);
                            //updateDB
                            implDao.updatedMeal(meal);
                            ApplicationEx.getInstance().tempList.put(meal.meal_id, meal);
                        }

                        for (int i = 0; i < _hapiMomentList.size(); i++) {

                            HapiMoment moment = _hapiMomentList.get(i);
                            ApplicationEx.getInstance().tempHapimomentList.put(String.valueOf(moment.mood_id), moment);
                        }
                        for (int i = 0; i < _weightList.size(); i++) {

                            Weight weight = _weightList.get(i);
                            ApplicationEx.getInstance().tempWeightList.put(String.valueOf(weight.activity_id), weight);
                        }
                         /*Workout*/

                        //List<Workout> _workoutList = (List<Workout>) jsonResponseHandler.getResponseObj();

//                        List<Workout> _workoutList = (List<Workout>) syncList.get(1);

                        DaoImplementer workoutDAO = new DaoImplementer(new WorkoutDAO(context, null), context);

                        for (int i = 0; i < _workoutList.size(); i++)
                        {
                            Workout workoutObj = _workoutList.get(i);

                            Workout tempWorkout = ApplicationEx.getInstance().workoutList.get(workoutObj.activity_id);

                            if (tempWorkout != null)
                            {
                                workoutObj.exercise_state = Workout.EXERCISE_STATE.SYNC;

                                workoutDAO.update(tempWorkout);
                            }
                            else
                            {
                                workoutObj.exercise_state = Workout.EXERCISE_STATE.SYNC;

                                workoutDAO.add(workoutObj);
                            }

                            ApplicationEx.getInstance().workoutList.put(workoutObj.activity_id, workoutObj);
                        }
                    }

                    //STEP 3:
                    //launch my meal list
                    syncListener.getSyncSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage());

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MESSAGE_TYPE.FAILED);

                    syncListener.getSyncFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
