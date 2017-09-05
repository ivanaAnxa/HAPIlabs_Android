package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;

import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aprilanxa on 08/07/2016.
 */
public class JsonMarkAsReadResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;
    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;
    android.os.Message msg = new android.os.Message();


    public JsonMarkAsReadResponseHandler(Handler handler, String strJson) {
        super(handler);
        this.handler = handler;

        /**
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
         * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
         */
        if (strJson.isEmpty()) {
            strJson = "";
        }

        this.strJson = strJson;

    }

    @Override
    public void handleMessage(android.os.Message msg) {
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
            System.out.println("JsonMarkAsReadResponseHandler " + strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("api_response")) {
                JSONObject api_response = jsonResponse
                        .getJSONObject("api_response");
                String requestStatus = api_response.optString("status");

                System.out.println("api_response: " + requestStatus);

                // for failed request
                if (requestStatus == null
                        || requestStatus.equalsIgnoreCase("Failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }

                if (requestStatus.equalsIgnoreCase("Successful")) {

                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }else{
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
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
