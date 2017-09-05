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
public class JsonForgotResponseHandler extends JsonDefaultResponseHandler {

	protected Handler handler;
	protected boolean isError = false;

	String OutputData = "";
	String strJson;

	JSONObject jsonResponse;

	Message msg = new Message();

	public JsonForgotResponseHandler(Handler handler) {
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

			jsonResponse = new JSONObject(strJson);

			// returns the value mapped by name if it exists and is a JSONArray.
			// returns null otherwise.
			if (jsonResponse.has("api_response")) {

				Log.i("api_response", strJson);
				JSONObject api_response = jsonResponse.getJSONObject("api_response");
				String requestStatus = api_response.optString("status");
				String message = api_response.optString("message");

				// for failed request
				if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed") || message.equalsIgnoreCase("Failed")) {
					String messageDetail = api_response.optString("message_detail");
					MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.FAILED, api_response);
					msgObj.setMessage_string(messageDetail);
					msgObj.setMessage_id(requestStatus);
					setResponseObj(msgObj);
					msg.what = JsonDefaultResponseHandler.ERROR;
					handler.handleMessage(msg);
					return;
				}else if(requestStatus.equalsIgnoreCase("Successful")){
					// if success
					MessageObj msgObj = JsonUtil.getMessageObj(MESSAGE_TYPE.SUCCESS, api_response);
					msgObj.setMessage_id(requestStatus);
					setResponseObj(msgObj);
					msg.what = JsonDefaultResponseHandler.COMPLETED;
					handler.handleMessage(msg);
					return;
				}
			}

			String error_count = jsonResponse.optString("error_count");
			if(error_count != null && Integer.parseInt(error_count) > 0)
			{
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
