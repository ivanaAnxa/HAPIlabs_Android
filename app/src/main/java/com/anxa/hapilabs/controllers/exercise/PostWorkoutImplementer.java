package com.anxa.hapilabs.controllers.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.listener.PostWorkoutListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostWorkoutResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.storage.DaoImplementer;
import com.anxa.hapilabs.common.storage.WorkoutDAO;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;

/**
 * Created by elaineanxa on 18/08/2016.
 */
public class PostWorkoutImplementer
{
    private JsonPostWorkoutResponseHandler jsonResponseHandler;

    private Context context;
    private PostWorkoutListener listener;
    public Workout workout;
    private String data;
    private String commandString;


    public PostWorkoutImplementer(Context context, String userId, String command, Workout workoutObj, PostWorkoutListener listener)
    {
        this.context = context;
        jsonResponseHandler = new JsonPostWorkoutResponseHandler(mainHandler);
        this.workout = workoutObj;
        this.listener = listener;
        this.commandString = command;


        if (command=="updated" || command == "deleted")
        {
            data = JsonRequestWriter.getInstance().editExerciseJson(workoutObj, command);
        }
        else
        {
            data = JsonRequestWriter.getInstance().createExerciseJson(workoutObj, command);
        }

        postWorkout(userId, data, jsonResponseHandler);
    }


    //where data = xml string format post data
    private void postWorkout(String userID, String data, Handler responseHandler)
    {
        String url = WebServices.getURL(WebServices.SERVICES.POST_WORKOUT);

        Connection connection = new Connection(responseHandler);

        connection.addParam("userid", userID);

        connection.addParam("signature", connection.createSignature(WebServices.getCommand(WebServices.SERVICES.POST_WORKOUT) + userID));
        connection.addHeader("Content-Type", "application/json");
        connection.addHeader("charset", "utf-8");
        connection.addHeader("Accept", "application/json");

        connection.create(Connection.POST, url, data);
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
                    if (jsonResponseHandler.getResponseObj() != null)
                    {
                        Workout workoutObj = (Workout) jsonResponseHandler.getResponseObj();

                        DaoImplementer implDao = new DaoImplementer(new WorkoutDAO(context, null), context);

                        if (commandString == "added") {
                            implDao.add(workoutObj);

                            ApplicationEx.getInstance().workoutList.put(workoutObj.activity_id, workoutObj);
                        }
                        else if (commandString == "updated") {
                            implDao.update(workoutObj);

                            ApplicationEx.getInstance().workoutList.put(workoutObj.activity_id, workoutObj);
                        }
                        else if (commandString == "deleted") {
                            implDao.delete(workoutObj.activity_id);

                            ApplicationEx.getInstance().workoutList.remove(workoutObj.activity_id);
                        }
                    }

                    listener.postWorkoutSuccess(jsonResponseHandler.getResultCode() + " " + jsonResponseHandler.getResultMessage(), workout);

                    break;
                case JsonDefaultResponseHandler.ERROR:
                    //stop progress here
                    MessageObj mesObj = new MessageObj();
                    mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                    mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                    mesObj.setType(MessageObj.MESSAGE_TYPE.FAILED);

                    listener.postWorkoutFailedWithError(mesObj, workout);

                    //dismiss progress here
                    break;
            }//end Switch
        }
    };
}
