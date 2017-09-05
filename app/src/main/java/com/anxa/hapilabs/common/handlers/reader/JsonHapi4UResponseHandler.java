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
public class JsonHapi4UResponseHandler extends JsonDefaultResponseHandler {

	protected Handler handler;

	protected boolean isError = false;

	String OutputData = "";
	String strJson;

	JSONObject jsonResponse;

	Message msg = new Message();

	/******
	 * STEP1: PLEASE ADD FORMAT OF THE FORGOT PASSOWRD RESPONSE JSON DATA
	 * 
	 * {"api_response":
	 * {	"message_detail":" Successfully liked comment",
	 *		 "status":  "Successful"
	 * },
	 * "comment":
	 * {	"comment_id":null,
	 * 		"comment_message":" Great!!! ",
	 * 		"comment_timestamp_utc":1413442601,
	 * 		"comment_type":null,
	 * 		"ishapi":"true"
	 * }
	 * }
	 * 
	 * 
	 * Failed:
	 * 
	 * {"api_response":{"message_detail":"Invalid meal","status":"Failed"}}
	 * 
	 * 
	 * */

	public JsonHapi4UResponseHandler(Handler handler) {
		super(handler);
		this.handler = handler;

		/**
		 * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
		 * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
		 */
//		if (strJson.isEmpty()) {
//			strJson = "{\"api_response\":{\"message_detail\":\"Successfully liked comment\",\"status\":\"Successful\"},\"comment\":{\"comment_id\":null,\"comment_message\":\" Great!!! \",\"comment_timestamp_utc\":1413442601,\"comment_type\":null,\"ishapi\":\"true\"}}";
//		}
//
//		this.strJson = strJson;

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

			// returns the value mapped by name if it exists and is a JSONArray.
			// returns null otherwise.
			if (jsonResponse.has("api_response")) {

				JSONObject api_response = jsonResponse.getJSONObject("api_response");
				String requestStatus = api_response.optString("status");

				// for failed request
				if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
					MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
					setResponseObj(msgObj);
					msg.what = JsonDefaultResponseHandler.COMPLETED;
					handler.handleMessage(msg);
					return;
				}
				
				// if success
				JSONObject commentJson = jsonResponse.getJSONObject("comment");
				
				if(commentJson != null)
				{
					setResponseObj(JsonUtil.getComment(commentJson));
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
