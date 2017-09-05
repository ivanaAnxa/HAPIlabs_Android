package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 5/10/2016.
 */
public class JsonDeleteMealResponseHandler extends JsonDefaultResponseHandler{

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonDeleteMealResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();

    }

    @Override
    public void start() {
        /** STEP3: START JSON PARSING HERE: */
        try {

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            System.out.println("deletejson: " + strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.

            if (jsonResponse.has("api_response")) {

                JSONObject api_response = jsonResponse.getJSONObject("api_response");

                String status = api_response.optString("status");
                String message = api_response.optString("message");

                // for failed request
                if (status == null || status.equalsIgnoreCase("Failed") || message.equalsIgnoreCase("Failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MessageObj.MESSAGE_TYPE.FAILED, api_response);

                    setResponseObj(msgObj);

                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }

                JSONArray mealArr = jsonResponse.getJSONArray("meal");
                List<Meal> mealList = new ArrayList<Meal>();

                if (mealArr != null && mealArr.length() > 0) {
                    JSONObject mealResponse = mealArr.getJSONObject(0);
                    String meal_id = mealResponse.optString("meal_id");

                    setResponseObj(meal_id);

                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }

                String error_count = jsonResponse.optString("error_count");

                if (error_count != null && error_count.length() > 0 && Integer.parseInt(error_count) > 0) {
                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED,
                            jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }
            } else {

                String error_count = jsonResponse.optString("error_count");
                String message = jsonResponse.optString("message");
                String message_detail = jsonResponse.optString("message_detail");

                if (error_count != null && error_count.length() > 0 && Integer.parseInt(error_count) > 0) {
                    MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    msgObj.setMessage_id(message);
                    msgObj.setMessage_string(message_detail);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }
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
