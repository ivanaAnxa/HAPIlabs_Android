package com.anxa.hapilabs.controllers.timelineactivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetTimelineActivityListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonTimelineActivityResponseHandler;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.HapiMoment;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.TimelineActivity;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Weight;
import com.anxa.hapilabs.models.Workout;

import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by aprilanxa on 08/03/2017.
 */

public class GetTimelineActivityImplementer {

    JsonTimelineActivityResponseHandler jsonTimelineActivityResponseHandler;
    protected ProgressChangeListener progresslistener;
    GetTimelineActivityListener getTimelineActivityListener;
    Context context;
    String entryType;

    private static String APIKey = "An2x A3ct h9p1m36";

    public GetTimelineActivityImplementer(Context context, String userID, String entryID, String entryType, ProgressChangeListener progresslistener, GetTimelineActivityListener listener) {
        this.context = context;
        this.getTimelineActivityListener = listener;
        this.progresslistener = progresslistener;

        this.entryType = entryType;

        jsonTimelineActivityResponseHandler = new JsonTimelineActivityResponseHandler(timelineActivityHandler);
        getTimelineActivity(userID, entryID, jsonTimelineActivityResponseHandler);
    }

    public void getTimelineActivity(String userID, String entryID, Handler responseHandler) {
        //api.hapilabs.com/hapilabs/activity?&command=get_timelineactivity&UserId=%@&Id=%@&signature=%@

        String url = WebServices.getURL(WebServices.SERVICES.GET_TIMELINE_ACTIVITY);
        Connection connection = new Connection(responseHandler);
        connection.addParam("UserId", userID);
        connection.addParam("Id", entryID);
        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_TIMELINE_ACTIVITY) + userID + entryID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");
        connection.create(Connection.GET, url, "");

    }

    @SuppressLint("HandlerLeak")
    private final Handler timelineActivityHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case JsonDefaultResponseHandler.START:

                    break;
                case JsonDefaultResponseHandler.COMPLETED:

                    if (entryType.equalsIgnoreCase("meal")) {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        Meal currentMeal = new Meal();
                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {
                            timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                            currentMeal = timelineActivity.meal;
                            currentMeal.comments = timelineActivity.comments;
                            currentMeal.hapi4Us = timelineActivity.hapi4Us;

                            if (currentMeal != null) {
                                ApplicationEx.getInstance().currentMealView = timelineActivity.meal;
                            }
                        }

                        getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);

                    } else if (entryType.equalsIgnoreCase("water")) {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {
                            Water currentWater = new Water();

                            timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                            currentWater = timelineActivity.water;
                            currentWater.comments = timelineActivity.comments;
                            currentWater.water_id = timelineActivity.activity_id;

                            currentWater.hapi4Us = timelineActivity.hapi4Us;


                            if (currentWater != null) {
                                ApplicationEx.getInstance().currentWater = timelineActivity.water;
                            }
                        }

                        getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);

                    } else if (entryType.equalsIgnoreCase("exercise")) {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {
                            Workout currentWorkout = new Workout();

                            timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                            currentWorkout = timelineActivity.workout;
                            if (currentWorkout != null) {
                                currentWorkout.comments = timelineActivity.comments;
                                currentWorkout.activity_id = timelineActivity.activity_id;

                                currentWorkout.hapi4Us = timelineActivity.hapi4Us;

                                ApplicationEx.getInstance().currentWorkoutView = timelineActivity.workout;
                            }else if (timelineActivity.steps!=null){
                                Steps currentSteps = new Steps();
                                currentWorkout = new Workout();

                                currentSteps = timelineActivity.steps;

                                if (currentSteps!=null){
                                    currentSteps.activity_id = timelineActivity.activity_id;

                                    currentWorkout.steps = Integer.parseInt(currentSteps.steps_count);
                                    currentWorkout.exercise_type = Workout.EXERCISE_TYPE.OTHER;
                                    currentWorkout.exercise_datetime = currentSteps.start_datetime;
                                    currentWorkout.activity_id = currentSteps.activity_id;
                                    currentWorkout.distance = currentSteps.steps_distance;
                                    currentWorkout.device_name = currentSteps.device_name;
                                    currentWorkout.calories = (int)currentSteps.steps_calories;
                                    currentWorkout.duration = (int)currentSteps.steps_duration;

                                    currentWorkout.comments = timelineActivity.comments;
                                    currentWorkout.hapi4Us = timelineActivity.hapi4Us;
                                    currentWorkout.isChecked = false;

                                    ApplicationEx.getInstance().currentWorkoutView = currentWorkout;
                                    timelineActivity.workout = currentWorkout;
                                }

                            }
                        }

                        getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);

                    }else if (entryType.equalsIgnoreCase("weight")) {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        Weight currentWeightView = null;
                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {

                        {
                            timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                            currentWeightView = timelineActivity.weight;
                            currentWeightView.comments = timelineActivity.comments;
                            currentWeightView.activity_id = timelineActivity.activity_id;

                            currentWeightView.hapi4Us = timelineActivity.hapi4Us;

                                if (currentWeightView!=null) {
                                    ApplicationEx.getInstance().currentWeightView = currentWeightView;
                                }

                        }

                        getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);

                    }}
                    else if (entryType.equalsIgnoreCase("steps")) {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        Steps currentStepsView = null;
                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {

                            {
                                timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                                currentStepsView = timelineActivity.steps;
                                currentStepsView.comments = timelineActivity.comments;
                                currentStepsView.activity_id = timelineActivity.activity_id;
                                currentStepsView.isChecked = timelineActivity.isChecked;

                                currentStepsView.hapi4Us = timelineActivity.hapi4Us;

                                if (currentStepsView!=null) {
                                    ApplicationEx.getInstance().currentStepsView = currentStepsView;
                                }

                            }

                            getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);

                        }}
                    else {
                        TimelineActivity timelineActivity = new TimelineActivity();
                        HapiMoment hapiMomentObj = null;

                        //STEP 1: stop progress here
                        if (jsonTimelineActivityResponseHandler.getResponseObj() != null) {
                            timelineActivity = (TimelineActivity) jsonTimelineActivityResponseHandler.getResponseObj();
                            hapiMomentObj = timelineActivity.hapiMoment;
                            hapiMomentObj.comments = timelineActivity.comments;
                            hapiMomentObj.hapi4Us = timelineActivity.hapi4Us;
                            hapiMomentObj.mood_id = Integer.parseInt(timelineActivity.activity_id);

                            if (hapiMomentObj != null) {
                                ApplicationEx.getInstance().currentHapiMoment = hapiMomentObj;
                            }
                        }
                        getTimelineActivityListener.getTimelineActivitySuccess(timelineActivity);
                    }

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonTimelineActivityResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonTimelineActivityResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    getTimelineActivityListener.getTimelineActivityFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };


}
