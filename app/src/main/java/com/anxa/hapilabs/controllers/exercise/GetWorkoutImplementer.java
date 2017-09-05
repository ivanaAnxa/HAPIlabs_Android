package com.anxa.hapilabs.controllers.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.GetWorkoutListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetWorkoutResponseHandler;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;


public class GetWorkoutImplementer {
    private JsonGetWorkoutResponseHandler jsonResponseHandler;

    private Context context;
    private GetWorkoutListener listener;

    public GetWorkoutImplementer(Context context, String userId, String activity_id, GetWorkoutListener listener) {
        this.context = context;

        jsonResponseHandler = new JsonGetWorkoutResponseHandler(mainHandler);

        getWorkout(userId, activity_id, jsonResponseHandler);

        this.listener = listener;
    }

    //where data = xml string format post data
    private void getWorkout(String userID, String activity_id, Handler responseHandler) {
        String url = WebServices.getURL(WebServices.SERVICES.GET_WORKOUT);

        Connection connection = new Connection(responseHandler);

        connection.addParam("userid", userID);
        connection.addParam("id", activity_id);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.GET_WORKOUT) + userID + activity_id));
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
                    if (jsonResponseHandler.getResponseObj() != null) {
                        Workout workoutObj = (Workout) jsonResponseHandler.getResponseObj();

                        DaoImplementer implDao = new DaoImplementer(new WorkoutDAO(context, null), context);

                        WorkoutDAO dao = new WorkoutDAO(context, null);
                        Workout tempWorkout = dao.getWorkoutByActivityID(workoutObj.activity_id);

                        if (tempWorkout == null) {
                            //add to db
                            implDao.add(workoutObj);
                        } else {
                            //updateDB
                            implDao.update(workoutObj);
                        }

                        ApplicationEx.getInstance().workoutList.put(workoutObj.activity_id, workoutObj);

                        listener.getWorkoutSuccess(workoutObj);

                    }


                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.getWorkoutFailedWithError(mesObj);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}