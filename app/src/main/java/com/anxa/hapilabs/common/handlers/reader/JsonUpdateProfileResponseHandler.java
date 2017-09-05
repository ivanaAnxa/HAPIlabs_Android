package com.anxa.hapilabs.common.handlers.reader;

import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonUpdateProfileResponseHandler extends JsonDefaultResponseHandler {

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

    public JsonUpdateProfileResponseHandler(Handler handler) {
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

            Log.i("RESPONSE JSON", strJson);
/**04-15 17:19:12.594 27491-29905/com.hapilabs
 * I/RESPONSE JSON:
 * {"api_response":
 * {"message_detail":"Successfully updated user profile",
 * "status":"Successful"}}
 */

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
//				JSONObject update_profile = jsonResponse.getJSONObject("update_profile");
            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String status = api_response.optString("status");
            // for failed request
            if (status == null || status.equalsIgnoreCase("Failed")) {
                setResponseObj(jsonResponse);
                msg.what = JsonDefaultResponseHandler.COMPLETED;
                handler.handleMessage(msg);
                return;
            } else if (status.equalsIgnoreCase("Successful")) {

                Log.i("RESPONSE JSON", ""+api_response);
                // if success
                MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.SUCCESS,jsonResponse);

                //transfer the message obj to the response handler
                setResponseObj(messageObj);
                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
//                setResponseObj(jsonResponse);
//                msg.what = JsonDefaultResponseHandler.COMPLETED;
//                handler.handleMessage(msg);
//                return;

            } else {
                String error_count = jsonResponse.optString("error_count");
                if (error_count != null && Integer.parseInt(error_count) > 0) {
                    setResponseObj(jsonResponse);
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
