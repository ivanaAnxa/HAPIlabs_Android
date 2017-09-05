package com.anxa.hapilabs.controllers.messages;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.GetMessagesListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPostMessageResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.Message;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

public class PostMessageImplementer {

	JsonPostMessageResponseHandler jsonResponseHandler;

	Context context;
	GetMessagesListener listener;

	public PostMessageImplementer(Context context, String userId, String message, GetMessagesListener listener) {
		this.context = context;
		this.listener = listener;
		jsonResponseHandler = new JsonPostMessageResponseHandler(mainHandler, "");
		Message msg = new Message();
		msg.message_body = message;
		String data = JsonRequestWriter.getInstance().createMessageJson(msg);
		System.out.println("data: " + data);
		syncServices(userId, data, jsonResponseHandler);

	}
	
	//where data = xml string format post data
		public void syncServices(String userID,String data,Handler responseHandler){
			
			String url = WebServices.getURL(SERVICES.POST_MESSAGES);
			
			Connection connection = new Connection(responseHandler);
			
			connection.addParam("signature",connection.createSignature(WebServices.getCommand(SERVICES.POST_MESSAGES)+userID) );
		    connection.addParam("userid",userID );
			
		    connection.addHeader("Content-Type", "application/json");
			
		    connection.addHeader("charset", "utf-8");
			
		    connection.addHeader("Accept", "application/json");
			
		    connection.create(Connection.POST, url,data);
		 
		}

		

		
		final  Handler mainHandler = new Handler(){
			@Override
	        public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
	                case JsonDefaultResponseHandler.START:
	                	break;
	                case JsonDefaultResponseHandler.COMPLETED:
	                	listener.postMessagesSuccess("success");
	                	break;
	                case JsonDefaultResponseHandler.ERROR:	
	                	//stop progress here
	                	MessageObj mesObj = new MessageObj();
	                	mesObj.setMessage_id(jsonResponseHandler.getResultCode());
	                	mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
	                	mesObj.setType(MESSAGE_TYPE.FAILED);

	                	listener.postMessagesError(mesObj);
	                	
	                	//dismiss progress here
	                  	 break;
	              }//end Switch
			}
		};


}
