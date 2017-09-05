package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Weight;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 11/10/2016.
 */

public class JsonWeightGraphResponseHandler extends JsonDefaultResponseHandler {
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p/>
     * { "update_profile": { "api_response": { "status": "Successful",
     * "message_detail": "Successfully updated user profile" } } }
     * <p/>
     * { "update_profile": { "api_response": { "status": "Failed",
     * "message_detail": "Value cannot be null. Parameter name: value" } } }
     */

    public JsonWeightGraphResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    //	@Override
//	public void handleMessage(Message msg) {
//		handler.sendMessage(msg);
//	}
//
    @Override
    public void start(String strJson) {
        this.strJson = strJson;
        start();
    }

    @Override
    public void start() {

        /** STEP3: START JSON PARSING HERE: */

        try {

            System.out.println("JsonWeightGraphResponseHandler 1");
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);
            System.out.println("JsonWeightGraphResponseHandler 2");


            jsonResponse = new JSONObject(strJson);
            System.out.println("JsonWeightGraphResponseHandler 3");


            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String status = api_response.optString("status");

            // for failed request
            if (status == null || status.equalsIgnoreCase("Failed")) {
                setResponseObj(jsonResponse);
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                return;
            } else if (status.equalsIgnoreCase("Successful")) {

                JSONArray graph_data = jsonResponse.getJSONArray("graph_data");

                ApplicationEx.getInstance().weightList = new ArrayList<Weight>();
                List<Weight> weightList = new ArrayList<Weight>();
                System.out.println("JsonWeightGraphResponseHandler 7");


                for (int index = 0; index < graph_data.length(); index++) {
                    JSONObject json = graph_data.getJSONObject(index);

                    Weight toSaveWeight = JsonUtil.getWeight(json);
                    if (!toSaveWeight.isDeleted) {
                        weightList.add(toSaveWeight);
                    }
                }

                //save the list
                ApplicationEx.getInstance().weightList = weightList;

                //get the latest weight data
                JSONObject latest_data = jsonResponse.getJSONObject("latest_data");
                ApplicationEx.getInstance().latestWeight = JsonUtil.getWeight(latest_data);
                // if success
                MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.SUCCESS, jsonResponse);

                //transfer the message obj to the response handler
                setResponseObj(messageObj);
                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;

            } else {
                String error_count = jsonResponse.optString("error_count");
                if (error_count != null && Integer.parseInt(error_count) > 0) {
                    setResponseObj(jsonResponse);
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
