package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by elaineanxa on 08/08/2016.
 */
public class JsonGetAllMealsResponseHandler extends JsonDefaultResponseHandler
{
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;
    String command = "";

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonGetAllMealsResponseHandler(Handler handler, String command)
    {
        super(handler);
        this.handler = handler;
        this.command = command;
    }

    @Override
    public void handleMessage(Message msg)
    {
        //handler.sendMessage(msg);
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
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            System.out.println("GET ALL MEALS: " + strJson);

            try {

                String requestStatus = jsonResponse.optString("status");

                //for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
                    //set message obj for the failed request
                    MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    //transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }

                System.out.println("MEALS_RATED: "+jsonResponse.getInt("meals_rated"));
                System.out.println("REGISTRATION_DATE: "+jsonResponse.optLong("registration_date"));
                ApplicationEx.getInstance().registrationDate = jsonResponse.optLong("registration_date");

                if (command == "get_meals_all")
                {
                    ApplicationEx.getInstance().setAllMealsRated(jsonResponse.getInt("meals_rated"));
                }
                else if (command == "get_meals_healthy")
                {
                    ApplicationEx.getInstance().setAllHealthyMealsRated(jsonResponse.getInt("meals_rated"));
                }
                else if (command == "get_meals_ok")
                {
                    ApplicationEx.getInstance().setAllJustOkMealsRated(jsonResponse.getInt("meals_rated"));
                }
                else if (command == "get_meals_unhealthy")
                {
                    ApplicationEx.getInstance().setAllUnhealthyMealsRated(jsonResponse.getInt("meals_rated"));
                }

                // GET ALL MEALS ARRAY
                JSONArray _array = jsonResponse.getJSONArray("meals");
                List<Meal> _meallist = new ArrayList<Meal>();

                TimeZone tz = TimeZone.getDefault();
                Calendar cal = GregorianCalendar.getInstance(tz);
                int offsetInMillis = (tz.getOffset(cal.getTimeInMillis())) / 1000;

                for (int index = 0; index < _array.length(); index++) {
                    JSONObject json = _array.getJSONObject(index);
                    if (JsonUtil.getMeal(json, offsetInMillis) != null)
                        _meallist.add(JsonUtil.getMeal(json, offsetInMillis));
                }

                //transfer the message obj to the response handler
                setResponseObj(_meallist);

                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);

                return;
            } catch (JSONException e) {
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                e.printStackTrace();
            }

        } catch (JSONException e)
        {
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
