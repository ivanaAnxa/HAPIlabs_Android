package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by aprilanxa on 16/09/2016.
 */
public class JsonGetMealResponseHandler extends JsonDefaultResponseHandler {
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;
    String command = "";

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonGetMealResponseHandler(Handler handler, String command) {
        super(handler);
        this.handler = handler;
        this.command = command;
    }

    @Override
    public void handleMessage(Message msg) {
        //handler.sendMessage(msg);
    }

    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();
    }

    @Override
    public void start() {
        try {
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            System.out.println("GET MEAL: " + strJson);

            if (jsonResponse.has("api_response")) {

                JSONObject api_response = jsonResponse.getJSONObject("api_response");
                String requestStatus = api_response.optString("status");

                // for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {

                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);

                    return;
                }

                JSONObject meal_res = jsonResponse.getJSONObject("meal");

                TimeZone tz = TimeZone.getDefault();
                Calendar cal = GregorianCalendar.getInstance(tz);
                int offsetInMillis = (tz.getOffset(cal.getTimeInMillis())) / 1000;

                Meal mealObj = new Meal();

                if (JsonUtil.getMeal(meal_res, offsetInMillis) != null)
                    mealObj = JsonUtil.getMeal(meal_res, offsetInMillis);

                //transfer the message obj to the response handler
                setResponseObj(mealObj);

                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);

                return;
            }

        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }
    }

    @Override
    public void start(String strJson, String id) {
        // TODO Auto-generated method stub

    }
}
