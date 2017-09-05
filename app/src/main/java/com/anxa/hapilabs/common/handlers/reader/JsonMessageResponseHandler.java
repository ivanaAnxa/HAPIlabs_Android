package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonMessageResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    android.os.Message msg = new android.os.Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p/>
     * {"api_response":{"status":"Successful"},"message":[{"body":
     * "Good day! TGIF Going to the movies? What about some popcorn for movie time munchies? Recent studies have shown that popcorn, the hull part, has some good nutritional qualities. It has fiber and polyphenols, antioxidants that prevents damage to the cells. The best kind of popcorn is the air-popped one which has the fewest calories. Plain air-popped popcorn has about 30 calories per cup. Some low fat microwave popcorns are almost as low in calories. Also make it a point to enjoy the sun and the great outdoors this weekend to catch up on some much-needed physical activity!"
     * ,"coach":{"avatar_url":
     * "http://img.hapilabs.com/hapicoach/coach/coach_charmaine_manango_default@2x.png","coach_id":"4","firstname":"Charmaine","lastname":"Manango"},"message_id":112901,"message_timestamp_utc":1425027368,"message_type":4}],"messages_last_timestamp":142624158
     * 1 }
     */

    public JsonMessageResponseHandler(Handler handler, String strJson) {
        super(handler);
        this.handler = handler;

        /**
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
         * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
         */
        if (strJson.isEmpty()) {
            strJson = "{\"api_response\":{\"status\":\"Successful\"},\"message\":[{\"body\":\"Good day! #TGIF Going to the movies? What about some popcorn for movie time munchies? Recent studies have shown that popcorn, the hull part, has some good nutritional qualities. It has fiber and polyphenols, antioxidants that prevents damage to the cells. The best kind of popcorn is the air-popped one which has the fewest calories. Plain air-popped popcorn has about 30 calories per cup. Some low fat microwave popcorns are almost as low in calories. Also make it a point to enjoy the sun and the great outdoors this weekend to catch up on some much-needed physical activity!\",\"coach\":{\"avatar_url\":\"http://img.hapilabs.com/hapicoach/coach/coach_charmaine_manango_default@2x.png\",\"coach_id\":\"4\",\"firstname\":\"Charmaine\",\"lastname\":\"Manango\"},\"message_id\":112901,\"message_timestamp_utc\":1425027368,\"message_type\":4}],\"messages_last_timestamp\":1426241581}";
        }

        this.strJson = strJson;

    }

    @Override
    public void handleMessage(android.os.Message msg) {
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
            System.out.println("JsonMessageResponseHandler " + strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("api_response")) {
                JSONObject api_response = jsonResponse
                        .getJSONObject("api_response");
                String requestStatus = api_response.optString("status");
                String messageStatus = api_response.optString("message");

                // for failed request
                if (requestStatus == null
                        || requestStatus.equalsIgnoreCase("Failed") || messageStatus.equalsIgnoreCase("Failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }

                if (requestStatus.equalsIgnoreCase("Successful")) {

                    /** new implementation: CURSOR BASED PAGINATION{
                     "api_paging": {
                     "cursor": {
                     "after": 1463642823, //← values from the parameter
                     "before": 1463642823, //← values from the parameter
                     "next": 1459739552, //← determines the value to be used to fetch next records
                     "previous": 1459739552 //← determines the value to be used to fetch previous records
                     }
                     },
                     {"api_paging":{"cursor":{"before":1464073958,"next":1457071860,"previous":1447344182}},**/


                    JSONObject api_paging = jsonResponse.getJSONObject("api_paging");
                    JSONObject cursor = api_paging.getJSONObject("cursor");

                    JSONArray messageArr = jsonResponse.getJSONArray("message");
                    List<Message> messages = new ArrayList<Message>();

                    if (messageArr != null && messageArr.length() > 0) {
                        for (int i = 0; i < messageArr.length(); i++) {
                            JSONObject messageObj = messageArr.getJSONObject(i);
                            Message msg = JsonUtil.getMessage(messageObj);
                            messages.add(msg);
                        }
                    }

                    setResponseObj(messages);
                    msg.obj = api_paging;
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                } else {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MESSAGE_TYPE.FAILED, jsonResponse);
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