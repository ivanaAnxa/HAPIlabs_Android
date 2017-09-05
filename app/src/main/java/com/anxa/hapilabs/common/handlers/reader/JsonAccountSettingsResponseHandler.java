package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.MessageObj;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aprilanxa on 03/08/2016.
 */
public class JsonAccountSettingsResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;
    Message msg = new Message();


    public JsonAccountSettingsResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;

    }

    public JsonAccountSettingsResponseHandler(Handler handler, String strJson) {
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
            System.out.println("JsonAccountSettingsResponseHandler " + strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse!=null) {
                String requestStatus = jsonResponse.optString("status");
                String messageStatus = jsonResponse.optString("message");

                /**  "error_count" = 0;
                 message = Successful;
                 "message_detail" = "<null>";}**/

                // for failed request
                if (requestStatus == null
                        || requestStatus.equalsIgnoreCase("Failed") || messageStatus.equalsIgnoreCase("Failed")) {
                    MessageObj msgObj = JsonUtil.getMessageObj(
                            MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    handler.handleMessage(msg);
                    return;
                }

                if (messageStatus.equalsIgnoreCase("Successful")) {
                    // if success
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);

                    return;
                }else{
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);
                    return;
                }
            }

            String error_count = jsonResponse.optString("error_count");
            if (error_count != null && Integer.parseInt(error_count) > 0) {
                MessageObj msgObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED,
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
