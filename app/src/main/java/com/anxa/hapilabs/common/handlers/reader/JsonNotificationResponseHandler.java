package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Notification;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonNotificationResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    Message msg = new Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p/>
     * { "api_response": { "status": "Successful" }, "notification": [ {
     * "coach_id": 1, "notification_id": 688093, "notification_timestamp_utc":
     * 1412937776, "notification_type": 1, "ref_id": "86842437", "ref_type":
     * "MealId" }, { "coach_id": 1, "notification_id": 688088,
     * "notification_message":
     * "Votre coach a comment\u00e9 votre petit-d\u00e9jeuner",
     * "notification_timestamp_utc": 1411710195, "notification_type": 2,
     * "ref_id": "86840057", "ref_type": "MealId" } ],
     * "notification_last_timestamp": 1413363631 }
     * <p/>
     * <p/>
     * {"error":null,"error_count":1,"message":"Unauthorized","message_detail":"Invalid authentication signature."}
     */

    public JsonNotificationResponseHandler(Handler handler, String strJson) {
        super(handler);
        this.handler = handler;

        /**
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
         * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
         */

        this.strJson = strJson;

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

        /** STEP3: START JSON PARSING HERE: */

        try {

            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);
            System.out.println("GET NOTIF: jsonResponse" + strJson);


            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("api_response")) {

                JSONObject api_response = jsonResponse.getJSONObject("api_response");
                String requestStatus = api_response.optString("status");

                // for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }

                if (requestStatus.equalsIgnoreCase("Successful")) {
                    //last json notification timestamp
                    //TODO: need to check where to use this timestamp
                    System.out.println("notification_last_timestamp " + jsonResponse.getLong("notification_last_timestamp"));
                    long lastTimeStamp = jsonResponse.getLong("notification_last_timestamp");

                    // save this to preference for now.
                    ApplicationEx.getInstance().fromDateNotificationSync = lastTimeStamp;

                    JSONArray notificationArr = jsonResponse.getJSONArray("notification");

                    List<Notification> notifications = new ArrayList<Notification>();

                    if (notificationArr != null && notificationArr.length() > 0) {
                        for (int i = 0; i < notificationArr.length(); i++) {
                            JSONObject notificationObj = notificationArr.getJSONObject(i);
                            if (JsonUtil.getNotification(notificationObj) != null) {
                                notifications.add(JsonUtil.getNotification(notificationObj));
                            }
                        }
                    }

                    setResponseObj(notifications);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }
            }

            String error_count = jsonResponse.optString("error_count");
            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, jsonResponse);
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
