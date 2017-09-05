package com.anxa.hapilabs.controllers.addmeal;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.MealAddListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonUploadMealResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.common.util.ApplicationEx;
import com.anxa.hapilabs.models.Meal;
import com.anxa.hapilabs.models.Meal.STATE;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;

public class AddMealHapiImplementer {

	
	JsonUploadMealResponseHandler jsonResponseHandler;
	
	Handler responseHandler;
	protected ProgressChangeListener progresslistener;
	
	MealAddListener mealAddlistener;
	
	Context context;
	
	String mealID = "";
	public AddMealHapiImplementer(Context context,String username,Meal meal,ProgressChangeListener progresslistener,MealAddListener mealAddlistener){
		
		this.context = context;
		this.mealAddlistener = mealAddlistener;
		this.progresslistener = progresslistener;
		mealID = meal.meal_id;
		
		jsonResponseHandler = new JsonUploadMealResponseHandler(mealAddHandler);
		
		String data = JsonRequestWriter.getInstance().createUploadMealJson(meal,Meal.MEALSTATE_EDIT);
		
		addMealService(username, data, jsonResponseHandler);
		
	
}
	//where data = xml string format post data
	public void addMealService(String username, String data, Handler responseHandler){
		String url = WebServices.getURL(SERVICES.UPLOAD_MEAL);
		
		
		Connection connection = new Connection(responseHandler);
		
		connection.addParam("userid", username);
		
	    connection.addParam("signature",connection.createSignature(WebServices.getCommand(SERVICES.UPLOAD_MEAL)+username) );
		
	    connection.addHeader("Content-Type", "application/json");
		
	    connection.addHeader("charset", "utf-8");
		
	    connection.addHeader("Accept", "application/json");
	    
	    
	    connection.create(Connection.POST, url,data);
	    
	 
	}

	

	
	final  Handler mealAddHandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                
                	break;
                case JsonDefaultResponseHandler.COMPLETED:
                	//STEP 1: stop progress here
                	List<Meal> mealList = new ArrayList<Meal>();

                	mealList = (List<Meal>)jsonResponseHandler.getResponseObj();
                	
                	String newID = mealList.get(0).meal_id;
                	//remove from meal add  hasttable
                	ApplicationEx.getInstance().mealsToAdd.remove(mealID);
                	
                	
                	//update status from meal list hashtable
                	Meal meal = ApplicationEx.getInstance().tempList.get(mealID);
                	
                	meal.state = STATE.SYNC;
                	//update meal id
                	meal.meal_id = newID;
                	ApplicationEx.getInstance().tempList.put(newID,meal);
                	
                	
                	break;
                case JsonDefaultResponseHandler.ERROR:
                	//update the UI for error display
                	ApplicationEx.getInstance().mealsToAdd.remove(mealID);
                	
                	Meal meal_failed = ApplicationEx.getInstance().tempList.get(mealID);
                	meal_failed.state = STATE.FAILED;
                	ApplicationEx.getInstance().tempList.put(mealID,meal_failed);
                	
                	
                	//stop progress here
                	MessageObj mesObj = new MessageObj();
                	mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                	mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                	mesObj.setType(MESSAGE_TYPE.FAILED);
                	
                	mealAddlistener.uploadMealFailedWithError(mesObj,mealID);
                	
                	//dismiss progress here
                  	 break;
              }//end Switch
		}
	};




}
