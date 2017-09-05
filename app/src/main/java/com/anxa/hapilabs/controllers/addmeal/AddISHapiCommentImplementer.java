package com.anxa.hapilabs.controllers.addmeal;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.common.connection.Connection;
import com.anxa.hapilabs.common.connection.WebServices;
import com.anxa.hapilabs.common.connection.WebServices.SERVICES;
import com.anxa.hapilabs.common.connection.listener.MealISHapiCommentListener;
import com.anxa.hapilabs.common.connection.listener.ProgressChangeListener;
import com.anxa.hapilabs.common.handlers.reader.JsonDefaultResponseHandler;
import com.anxa.hapilabs.common.handlers.reader.JsonHapi4UResponseHandler;
import com.anxa.hapilabs.common.handlers.writer.JsonRequestWriter;
import com.anxa.hapilabs.models.Comment;
import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.MessageObj.MESSAGE_TYPE;
//import com.google.android.gms.internal.hn;

public class AddISHapiCommentImplementer {

	
	JsonHapi4UResponseHandler jsonResponseHandler;
	
	Handler responseHandler;
	protected ProgressChangeListener progresslistener;
	
	MealISHapiCommentListener listener;
	
	Context context;
	
	String mealID = "";
	String tempCommentID;
	Comment comment;
	public AddISHapiCommentImplementer(Context context,String username,String mealid,Comment comment,ProgressChangeListener progresslistener,MealISHapiCommentListener listener){
		
		this.context = context;
		this.listener = listener;
		this.progresslistener = progresslistener;
		mealID = mealid;
		
		tempCommentID = comment.comment_id;
		
		this.comment = comment;
		
		jsonResponseHandler = new JsonHapi4UResponseHandler(handler);
		
		String data = JsonRequestWriter.getInstance().createAddHapi4UJson(comment);
		
		
		addCommentService(username, data, jsonResponseHandler);
		
	
}
	//where data = xml string format post data
	public void addCommentService(String username, String data, Handler responseHandler){
		String url = WebServices.getURL(SERVICES.UPLOAD_COMMENT_HAPI);
		
		Connection connection = new Connection(responseHandler);
		
		connection.addParam("userid", username);
		
		connection.addParam("comment_id", comment.comment_id);
		
		connection.addParam("signature",connection.createSignature(WebServices.getCommand(SERVICES.UPLOAD_COMMENT_HAPI)+username+comment.comment_id) );
		
		
	    connection.addHeader("Content-Type", "application/json");
		
	    connection.addHeader("charset", "utf-8");
		
	    connection.addHeader("Accept", "application/json");
	    
	    connection.create(Connection.POST, url,data);
	    
	 
	}

	

	
	final  Handler handler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
                case JsonDefaultResponseHandler.START:
                
                	break;
                case JsonDefaultResponseHandler.COMPLETED:
                	
                	
                	//update listener
                	listener.uploadISHapiCommentSuccess("SUCCESS");
                	
                	
                	break;
                case JsonDefaultResponseHandler.ERROR:
                	
                
                		
                	
                	//stop progress here
                	MessageObj mesObj = new MessageObj();
                	mesObj.setMessage_id(jsonResponseHandler.getResultCode());
                	mesObj.setMessage_string(jsonResponseHandler.getResultMessage());
                	mesObj.setType(MESSAGE_TYPE.FAILED);
                	
                	listener.uploadISHapiCommentFailedWithError(mesObj,mealID);
                	
                	//update listener
                	
                  	 break;
              }//end Switch
		}
	};




}
