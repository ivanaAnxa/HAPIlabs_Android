package com.anxa.hapilabs.controllers.anxamats;


import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.SyncListener;
import com.anxa.hapilabs.common.handlers.reader.JsonAnxaMatsResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;

import android.os.Message;

public class AnxaMatsJobImplementer {

	
	JsonAnxaMatsResponseHandler jsonResponseHandler;
	Context context;
	SyncListener syncListener;

	public AnxaMatsJobImplementer(Context context,String userId,int deltaTime) {
		this.context = context;
		
		jsonResponseHandler = new JsonAnxaMatsResponseHandler(mainHandler);
	
		String data = JsonRequestWriter.getInstance().createAnxaMats(106, userId, "1", deltaTime, new Date() );
		
		sendSession(data,jsonResponseHandler);
}

	//where data = xml string format post data
	public void sendSession(String data, Handler responseHandler){
		
		String url = WebServices.getURL(SERVICES.ANXAMATS);
		
		Connection connection = new Connection(responseHandler);
		
		connection.addHeader("Content-Type", "application/json");
		
	    connection.addHeader("charset", "utf-8");
		
	    connection.addHeader("Accept", "application/json");
	    
	    connection.create(Connection.POST, url,data);
	    
	  
	
	}
	
	final  Handler mainHandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                	break;
                case JsonDefaultResponseHandler.COMPLETED:
                	//STEP 1: stop progress here
                	if (jsonResponseHandler!=null  && jsonResponseHandler.getResponseObj() != null && jsonResponseHandler.getResponseObj() instanceof List<?>){
                	//do nothing here	
                    }
                	break;
                case JsonDefaultResponseHandler.ERROR:	
                	//stop progress here
                //	do nothing here
                	//dismiss progress here
                  	 break;
              }//end Switch
		}
	};
}
