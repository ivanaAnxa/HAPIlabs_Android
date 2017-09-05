package com.anxa.hapilabs.controllers.addmeal;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonGetGraphResponseHandler;

public class MealGraphImplementer {

	
	JsonGetGraphResponseHandler jsonResponseHandler;
	
	Context context;
	
	public MealGraphImplementer(Context context,String username) {
		this.context = context;
		
		
		jsonResponseHandler = new JsonGetGraphResponseHandler(jsonResponseHandler);
		
		getGraph(username,jsonResponseHandler);
		
}	
	
	public void getGraph(String username, Handler responseHandler){

	 
	}

	

	
	final  Handler graphHandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                
                	break;
                case JsonDefaultResponseHandler.COMPLETED:
              
                	break;
                case JsonDefaultResponseHandler.ERROR:
                	 break;
              }//end Switch
		}
	};




}
