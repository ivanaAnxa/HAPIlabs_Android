package com.anxa.hapilabs.controllers.login;

import android.content.Context;
import android.os.Handler;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.connection.listener.PushRegListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonPushRegResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.UserProfile;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;

import android.os.Message;
public class PushRegImplementer {

	
	JsonPushRegResponseHandler jsonResponseHandler;
	
	//Handler responseHandler;
	protected ProgressChangeListener progresslistener;
	
	PushRegListener pushRegListener;
	
	String regID;
	Context context;
	
	public PushRegImplementer(Context context,String appVersion,String sharedKey,String senderID, String deviceToken,PushRegListener pushRegListener,UserProfile userprofile) {
		this.regID = userprofile.getRegID();
		
		this.context = context;
		
		this.pushRegListener = pushRegListener;

		String data = JsonRequestWriter.getInstance().createPushRegRequest(appVersion, true, userprofile.getFirstname(), userprofile.getRegID(), userprofile.getLastname());
		
		jsonResponseHandler = new JsonPushRegResponseHandler(pushHandler);
		
		//this.progresslistener.startProgress();
		
		
		pushServices(senderID,deviceToken, data,sharedKey, jsonResponseHandler);
		
	
}
	//where data = xml string format post data
	public void pushServices(String senderID, String deviceToken, String data, String sharedKey,Handler responseHandler){
		String url = WebServices.getURL(SERVICES.PUSH_REG);
		
		String signatureParam = senderID+deviceToken;
		
		Connection connection = new Connection(responseHandler);
	    connection.addParam("appId", senderID);
		connection.addParam("deviceToken", deviceToken);
	    connection.addParam("signature",connection.createSignature(signatureParam,sharedKey) );
	    connection.addHeader("Content-Type", "application/json");
		connection.addHeader("charset", "utf-8");
		connection.addHeader("Accept", "application/json");
	   
	    connection.create(Connection.POST, url,data);
	 
	}

	

	
	final  Handler pushHandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                
                	break;
                case JsonDefaultResponseHandler.COMPLETED:
                	//STEP 1: stop progress here
                
                	
                	//STEP 3:
                	
                	pushRegListener.pushRegSuccess(regID);
                	
                	
                	break;
                case JsonDefaultResponseHandler.ERROR:
                	//stop progress here
                	MessageObj mesObj = new MessageObj();
                	mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                	mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                	mesObj.setType(MESSAGE_TYPE.FAILED);
                	
                	pushRegListener.pushRegFailedWithError(mesObj);
                	
                	//dismiss progress here
                  	 break;
              }//end Switch
		}
	};




}
