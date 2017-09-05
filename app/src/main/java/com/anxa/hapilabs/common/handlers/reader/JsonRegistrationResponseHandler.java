package com.anxa.hapilabs.common.handlers.reader;

import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Handler;
import android.os.Message;

/****
 * Notes: - Return = UserProfileObj for successful request - MessageObj for
 * failed request
 */
public class JsonRegistrationResponseHandler extends JsonDefaultResponseHandler {

    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    JSONObject jsonResponse;
    String strJson;

    Message msg = new Message();

    /******
     * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
     * <p/>
     * {"api_response"
     * :{"status":"Successful"},"user":{"plan_profile":{"current_weight"
     * :160,"eating_habits":null,"goal_type":"1","height":175,"membership":"1",
     * "membership_expiry_utc"
     * :null,"start_weight":160,"target_weight":152},"user_profile"
     * :{"birthday":"1986-10-26"
     * ,"contact_number":"989 982928 98","country":"Philippines"
     * ,"date_joined_utc"
     * :"2014-10-22","email":"rj@hapitest2.com","firstname":"Angela"
     * ,"gender":"Female"
     * ,"language":"en","lastname":"Cabonce","picture_url_large"
     * :null,"timezone":"UTC","user_id":"1058510","user_login":{"password":
     * "f192f62db06e89e04d66a9c721f066883fca1079"
     * ,"username":"rj@hapitest2.com"}}}}
     */
    public JsonRegistrationResponseHandler(Handler handler) {
        super(handler);
        this.handler = handler;
        //	this.strJson = strJson;
        /******
         * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE on
         * strJson variable
         * */

        //strJson = "{\"api_response\":{\"status\":\"Successful\"},\"user\":{\"plan_profile\":{\"current_weight\":160,\"eating_habits\":null,\"goal_type\":\"1\",\"height\":175,\"membership\":\"1\",\"membership_expiry_utc\":null,\"start_weight\":160,\"target_weight\":152},\"user_profile\":{\"birthday\":\"1986-10-26\",\"contact_number\":\"989 982928 98\",\"country\":\"Philippines\",\"date_joined_utc\":\"2014-10-22\",\"email\":\"rj@hapitest2.com\",\"firstname\":\"Angela\",\"gender\":\"Female\",\"language\":\"en\",\"lastname\":\"Cabonce\",\"picture_url_large\":null,\"timezone\":\"UTC\",\"user_id\":\"1058510\",\"user_login\":{\"password\":\"f192f62db06e89e04d66a9c721f066883fca1079\",\"username\":\"rj@hapitest2.com\"}}}}";

        //	this.strJson = strJson;

    }
//
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

        /******
         * STEP3: START JSON PARSING HERE:
         *
         *
         *
         * */
        try {
            /******
             * inform handler that the jsn processing has started; this is
             * optional
             *********/
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            /******
             * Creates a new JSONObject with name/value mappings from the JSON
             * string.
             ********/
            jsonResponse = new JSONObject(strJson);
            System.out.println("JsonRegistrationResponseHandler " + strJson);
            /*****
             * Returns the value mapped by name if it exists and is a JSONArray.
             ***/
            /******* Returns null otherwise. *******/

            if(jsonResponse.has("error_count")) {

                String error_count = jsonResponse.optString("error_count");
                if (error_count != null && Integer.parseInt(error_count) > 0) {

                    System.out.println("JsonRegistrationResponseHandler " + jsonResponse.optString("error_count"));

                    MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, jsonResponse);
                    messageObj.setMessage_string(jsonResponse.optString("message_detail"));
                    // transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    // set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.ERROR;
                    // set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;

                }
            }

            JSONObject api_response = jsonResponse.getJSONObject("api_response");
            String requestStatus = api_response.optString("status");
            String message = api_response.optString("message");

            // for failed request
            if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed") || message.equalsIgnoreCase("Failed")) {

                // set message obj for the failed request
                MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
                messageObj.setMessage_string(api_response.optString("message_detail"));
                // transfer the message obj to the response handler
                setResponseObj(messageObj);
                // set json response handler to completed
                msg.what = JsonDefaultResponseHandler.ERROR;
                // set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            }

            //if success
            JSONObject data = jsonResponse.getJSONObject("user");

            if (data != null) {
                // set message obj for the successfull request
                UserProfile userProfile = JsonUtil.getUserProfile(data);
                // transfer the message obj to the response handler
                setResponseObj(userProfile);
                // set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);
                return;

            }


        } catch (JSONException e) {
            // set message obj for the failed request
            try {
                JSONObject api_response1 = jsonResponse.getJSONObject("api_response");
                MessageObj messageObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response1);
                // transfer the message obj to the response handler
                setResponseObj(messageObj);
                // set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;
                // set the handler for the waiting class
                handler.handleMessage(msg);
            } catch (JSONException ee) {
            }
        }
    }

    @Override
    public void start(String strJson, String id) {
        // TODO Auto-generated method stub
    }
}