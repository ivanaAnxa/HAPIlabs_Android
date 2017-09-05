package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 25/10/2016.
 */

public class JsonStepsGraphResponseHandler extends JsonDefaultResponseHandler {
    protected Handler handler;
    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;
    Message msg = new Message();
    public JsonStepsGraphResponseHandler(Handler handler) {
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

            Log.i("STEPS RESPONSE JSON", strJson);

            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String status = api_response.optString("status");
            // for failed request
            if (status == null || status.equalsIgnoreCase("Failed")) {
                setResponseObj(jsonResponse);
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                return;
            } else if (status.equalsIgnoreCase("Successful")) {

                Log.i("RESPONSE status", ""+api_response);

                JSONArray graph_data = jsonResponse.getJSONArray("graph_data");
                Log.i("graph_data status", ""+graph_data);

                ApplicationEx.getInstance().stepsList = new ArrayList<Steps>();
                List<Steps> stepsList = new ArrayList<Steps>();

                for (int index = 0; index < graph_data.length(); index++) {
                    JSONObject json = graph_data.getJSONObject(index);

                    Steps toSaveSteps = JsonUtil.getSteps(json);
                    if (!toSaveSteps.isDeleted) {
                        stepsList.add(toSaveSteps);
                    }
                }

                //save the list
                ApplicationEx.getInstance().stepsList = stepsList;

                //get the latest steps data
                JSONObject latest_data = jsonResponse.getJSONObject("latest_data");
                ApplicationEx.getInstance().latestSteps = JsonUtil.getSteps(latest_data);

                // if success
                MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.SUCCESS,jsonResponse);

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
