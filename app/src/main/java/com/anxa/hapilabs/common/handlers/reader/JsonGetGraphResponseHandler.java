package com.anxa.hapilabs.common.handlers.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.anxa.hapilabs.models.GraphMeal;

import android.os.Handler;
import android.os.Message;




public class JsonGetGraphResponseHandler extends JsonDefaultResponseHandler {

	protected Handler handler;
	protected boolean isError = false;
	String OutputData = "";
	String strJson;
	JSONObject jsonResponse;
	Message msg = new Message();

	public JsonGetGraphResponseHandler(Handler handler/*, String strJson*/) {
		super(handler);
		this.handler = handler;
		/**
		 * STEP2: FOR TESTING PURPOSE, PLEASE HARD CODE A SAMPLE RESPONSE n
		 * strJson variable NOTE OF THE SAMPLE BELOW AND HOW IT IS CODED.
		 */
	}

	@Override
	public void start(String strJson) {
		this.strJson = strJson;
		start();
	
	}

	@Override
	public void start() {
		/** STEP3: START JSON PARSING HERE: */
		/*"meal_log": {
	    "meal_post": [
	      {
	        "date": "7\/24\/2015",
	        "posted_count": 3
	      },
	      {
	        "date": "7\/25\/2015",
	        "posted_count": 1
	      }
	    ],
	    "total_meals": 50
	  }*/
		
		try {
			msg.what = JsonDefaultResponseHandler.START;
			handler.handleMessage(msg);
			jsonResponse = new JSONObject(strJson);

			if (jsonResponse.has("meal_log")){
				JSONArray api_respose = jsonResponse.getJSONArray("meal_post");
				List<GraphMeal> graphList = new ArrayList<GraphMeal>();

				if (api_respose != null && api_respose.length() > 0) {
					for (int i = 0; i < api_respose.length(); i++) {
						graphList.add(JsonUtil.getGraphMeal(api_respose.getJSONObject(i)));
					}
					setResponseObj(graphList);
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
