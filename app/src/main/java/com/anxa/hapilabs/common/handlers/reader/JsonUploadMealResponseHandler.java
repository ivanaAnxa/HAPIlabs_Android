package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonUploadMealResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    /******
     * STEP 1: { "upload": { "api_response": { "status": "Successful",
     * "message_detail": "All meals successfully uploaded." }, "meals": {
     * "meal": { "command": "Added", "status": "Successful", "client_id":
     * "7459356794509475049375", "meal_id": "86841431", "meal_type": "4",
     * "timestamp_utc": "1400555088", "meal_creation_date": "1400555088",
     * "description": "Karlo's meal", "album": { "photo_count": "1", "photos": {
     * "photo": { "photo_id": "2617", "url":
     * "http://img.hapilabs.com/mobile/1/418b7d22-6cef-4024-a6fb-4ac20e671ff5-large.png"
     * } } }, "haspaidsubscription": "True", "foodgroups": { "foodgroup": {
     * "groupid": "8" } }, "commentgroup": { "comment_count": "0" } } } } }
     * <p/>
     * added on aug 28 "meal_log":
     * <p/>
     * {
     * "meal_post": [
     * {
     * "date": "7\/24\/2015",
     * "posted_count": 3
     * },
     * {
     * "date": "7\/25\/2015",
     * "posted_count": 1
     * }
     * ],
     * "total_meals": 50
     * }
     */

    public JsonUploadMealResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;

        /**
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
         * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
         */

    }

    //	@Override
    //	public void handleMessage(Message msg) {
    //		handler.sendMessage(msg);
    //	}

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

            System.out.println("JsonUploadMealResponseHandler " + strJson);


            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.


            if (jsonResponse.has("api_response")) {

                JSONObject api_respose = jsonResponse.getJSONObject("api_response");

                String status = api_respose.optString("status");
                String message = api_respose.optString("message");

                // for failed request
                if (status == null || status.equalsIgnoreCase("Failed") || message.equalsIgnoreCase("failed")) {

                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MESSAGE_TYPE.FAILED, api_respose);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }

                JSONArray mealArr = jsonResponse.getJSONArray("meal");

                List<Meal> mealList = new ArrayList<Meal>();

                if (mealArr != null && mealArr.length() > 0) {
                    for (int i = 0; i < mealArr.length(); i++) {
                        if (JsonUtil.getMeal(mealArr.getJSONObject(i), 0) != null)
                            mealList.add(JsonUtil.getMeal(mealArr.getJSONObject(i), 0));
                    }
                    setResponseObj(mealList);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }


                String error_count = jsonResponse.optString("error_count");
                if (error_count != null && error_count.length() > 0 && Integer.parseInt(error_count) > 0) {
                    MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED,
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
                    MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, jsonResponse);
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