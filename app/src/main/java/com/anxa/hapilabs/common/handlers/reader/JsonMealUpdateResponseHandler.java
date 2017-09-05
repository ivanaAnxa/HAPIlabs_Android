package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.HAPI4U;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;

import static com.anxa.hapilabs.common.handlers.reader.JsonUtil.getHAPI4U;

/****
 * Notes: - Return = MessageObj for successful request - MessageObj for failed
 * request
 */
public class JsonMealUpdateResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    String strJson;

    JSONObject jsonResponse;

    android.os.Message msg = new android.os.Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p>
     * {"api_response":{"status":"Successful"},"message":[{"body":
     * "Good day! TGIF Going to the movies? What about some popcorn for movie time munchies? Recent studies have shown that popcorn, the hull part, has some good nutritional qualities. It has fiber and polyphenols, antioxidants that prevents damage to the cells. The best kind of popcorn is the air-popped one which has the fewest calories. Plain air-popped popcorn has about 30 calories per cup. Some low fat microwave popcorns are almost as low in calories. Also make it a point to enjoy the sun and the great outdoors this weekend to catch up on some much-needed physical activity!"
     * ,"coach":{"avatar_url":
     * "http://img.hapilabs.com/hapicoach/coach/coach_charmaine_manango_default@2x.png","coach_id":"4","firstname":"Charmaine","lastname":"Manango"},"message_id":112901,"message_timestamp_utc":1425027368,"message_type":4}],"messages_last_timestamp":142624158
     * 1 }
     */

    public JsonMealUpdateResponseHandler(Handler handler, String strJson) {
        super(handler);
        this.handler = handler;

        /**
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
         * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
         */
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

        try {
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            jsonResponse = new JSONObject(strJson);

            // returns the value mapped by name if it exists and is a JSONArray.
            // returns null otherwise.
            if (jsonResponse.has("api_response")) {

                JSONObject api_response = jsonResponse.getJSONObject("api_response");
                String requestStatus = api_response.optString("status");

                // for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {

                    MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, jsonResponse);
                    setResponseObj(msgObj);
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    handler.handleMessage(msg);

                    return;
                }

                JSONObject meal_res = jsonResponse.getJSONObject("meal");
                List<Comment> comments = new ArrayList<Comment>();
                List<HAPI4U> hapi4Us = new ArrayList<HAPI4U>();

                Meal meal = new Meal();
                if (ApplicationEx.getInstance().tempList.containsKey("meal_res.optString(\"meal_id\")")) {
                    meal = ApplicationEx.getInstance().tempList.get(meal_res.optString("meal_id"));
                }

                if (meal_res.has("commentgroup")) {
                    JSONObject commentGroupObj = meal_res.getJSONObject("commentgroup");
                    if (commentGroupObj != null && commentGroupObj.length() > 0) {

                        Object commentsObj = new JSONTokener(commentGroupObj.getString("comment")).nextValue();
                        if (commentsObj instanceof JSONArray) {
                            JSONArray commentArr = commentGroupObj.optJSONArray("comment");
                            for (int i = 0; i < commentArr.length(); i++) {
                                JSONObject commentgroup = commentArr.getJSONObject(i);
                                Comment comment = JsonUtil.getComment(commentgroup, Comment.STATUS.SYNC_COMMENT);
                                comments.add(comment);
                            }
                        }
                    }

                    Object hapi4uObj = new JSONTokener(commentGroupObj.getString("hapi4u")).nextValue();

                   System.out.println("JsonMealUpdateResponseHandler: " + hapi4uObj);
                    if (hapi4uObj instanceof JSONArray) {
                        JSONArray hapi4uArr = commentGroupObj.optJSONArray("hapi4u");
                        meal.hapi4Us = hapi4Us;

                        for (int i = 0; i < hapi4uArr.length(); i++) {
                            JSONObject hapi4ugroup = hapi4uArr.getJSONObject(i);
                            meal.hapi4Us.add(getHAPI4U(hapi4ugroup, Comment.STATUS.SYNC_COMMENT));
                        }
                    }
                }

                setResponseObj(comments);

                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);
                return;
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
