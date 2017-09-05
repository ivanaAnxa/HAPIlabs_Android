package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Workout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by elaineanxa on 18/08/2016.
 */
public class JsonPostWorkoutResponseHandler extends JsonDefaultResponseHandler
{
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    JSONObject jsonResponse;
    String strJson;

    Message msg = new Message();

    public JsonPostWorkoutResponseHandler(Handler handler)
    {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void start(String strJson)
    {
        this.strJson = strJson;
        start();
    }

    @Override
    public void start()
    {
        try {
            /****** inform handler that the jsn processing has started; this is optional *********/
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            jsonResponse = new JSONObject(strJson);
            System.out.println("JsonPostWorkoutResponseHandler " + strJson);

            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
            /*******  Returns null otherwise.  *******/
            try {
                JSONObject api_response = jsonResponse.getJSONObject("api_response");

                String requestStatus = api_response.optString("message");

                //for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
                    //set message obj for the failed request
                    MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    //transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }

                JSONObject json = jsonResponse.getJSONObject("workout");
                Workout workoutObj = JsonUtil.getWorkout(json);

                //transfer the message obj to the response handler
                setResponseObj(workoutObj);
                setResultMessage(requestStatus);

                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            } catch (JSONException e)
            {
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                e.printStackTrace();
            }
        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }
    }

    @Override
    public void start(String strJson, String id)
    {
        // TODO Auto-generated method stub

    }
}
