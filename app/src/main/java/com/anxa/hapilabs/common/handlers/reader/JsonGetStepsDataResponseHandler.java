package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Steps;
import com.anxa.hapilabs.models.Water;
import com.anxa.hapilabs.models.Weight;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by angelaanxa on 9/20/2017.
 */

public class JsonGetStepsDataResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    public JsonGetStepsDataResponseHandler(Handler handler) {
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

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

//            System.out.println("JsonWeightDataResponseHandler: " +  strJson);

//            JSONObject api_response = jsonResponse.getJSONObject("message_detail");
            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String status = api_response.optString("status");

            // for failed request
            if (status == null || status.equalsIgnoreCase("Failed")) {
                setResponseObj(jsonResponse);
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                return;
            } else if (status.equalsIgnoreCase("Successful")) {

                Log.i("RESPONSE status", ""+jsonResponse);

                // if success
                MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.SUCCESS,jsonResponse);

                //transfer the message obj to the response handler
                JSONObject json = jsonResponse.getJSONObject("steps");
                Steps steps = JsonUtil.getStepsView(json);

                //transfer the message obj to the response handler
                setResponseObj(steps);
                setResultMessage(status);

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
