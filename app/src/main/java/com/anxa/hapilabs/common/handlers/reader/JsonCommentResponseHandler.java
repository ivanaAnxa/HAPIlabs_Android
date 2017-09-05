package com.anxa.hapilabs.common.handlers.reader;

import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonCommentResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p>
     * {"api_response":{"message_detail":"Successfully added meal comment",
     * "status":"Successful"},"comment":{"comment_id":null,"comment_message":
     * "Dinner today"
     * ,"comment_timestamp_utc":1413964395,"comment_type":null,"ishapi":"false"}}
     * <p>
     * Failed:
     * <p>
     * {"api_response":{"message_detail":"Invalid meal","status":"Failed"}}
     */

    public JsonCommentResponseHandler(Handler handler) {
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

            System.out.println("JsonCommentResponseHandler: " + strJson);
            jsonResponse = new JSONObject(strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("api_response")) {

                JSONObject api_response = jsonResponse.getJSONObject("api_response");

                String requestStatus = api_response.optString("status");

                // for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }

                // if success
                if (jsonResponse.has("comment")) {
                    JSONObject commentJson = jsonResponse.getJSONObject("comment");

                    if (commentJson != null) {
                        setResponseObj(JsonUtil.getComment(commentJson));
                        msg.what = JsonDefaultResponseHandler.COMPLETED;
                        handler.handleMessage(msg);
                        return;
                    }
                }

                if (requestStatus.equalsIgnoreCase("Successful")){
                    MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.SUCCESS, api_response);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }
            }

            String error_count = jsonResponse.optString("error_count");

            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED,
                        jsonResponse);
                setResponseObj(msgObj);
                msg.what = JsonDefaultResponseHandler.COMPLETED;
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
